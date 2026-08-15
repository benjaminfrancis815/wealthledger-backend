package com.benjaminfrancis815.wealthledger.index.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.benjaminfrancis815.wealthledger.index.dto.GetAllMetricValuesResponse;
import com.benjaminfrancis815.wealthledger.index.dto.GetAllValuesResponse;
import com.benjaminfrancis815.wealthledger.index.model.IndexMetricValue;
import com.benjaminfrancis815.wealthledger.index.model.IndexValue;
import com.benjaminfrancis815.wealthledger.index.model.IndexValueStaging;
import com.benjaminfrancis815.wealthledger.index.repository.IndexBatchRepository;
import com.benjaminfrancis815.wealthledger.index.repository.IndexMetricValueRepository;
import com.benjaminfrancis815.wealthledger.index.repository.IndexRepository;
import com.benjaminfrancis815.wealthledger.index.repository.IndexValueRepository;
import com.benjaminfrancis815.wealthledger.security.repository.UserRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;

@Service
public class IndexServiceImpl implements IndexService {

	private static final Logger log = LoggerFactory.getLogger(IndexServiceImpl.class);

	private static final DateTimeFormatter VALUE_FILE_NAME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

	private static final DateTimeFormatter VALUE_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN = DateTimeFormatter
			.ofPattern("ddMMyyyy");

	private final WebClient webClient;
	private final IndexRepository indexRepository;
	private final IndexBatchRepository indexBatchRepository;
	private final IndexValueRepository indexValueRepository;
	private final IndexMetricValueRepository indexMetricValueRepository;
	private final UserRepository userRepository;

	@Value("${index.value-file-download-url}")
	private String valueFileDownloadUrl;

	@Value("${index.value-file-path}")
	private String valueFilePath;

	@Value("${index.value-staging-batch-size}")
	private int valueStagingBatchSize;

	@Autowired
	public IndexServiceImpl(final WebClient webClient, final IndexRepository indexRepository,
			final IndexBatchRepository indexBatchRepository, final IndexValueRepository indexValueRepository,
			final IndexMetricValueRepository indexMetricValueRepository, final UserRepository userRepository) {
		this.webClient = webClient;
		this.indexRepository = indexRepository;
		this.indexBatchRepository = indexBatchRepository;
		this.indexValueRepository = indexValueRepository;
		this.indexMetricValueRepository = indexMetricValueRepository;
		this.userRepository = userRepository;
	}

	@Override
	public GetAllValuesResponse getAllValues() {
		final List<IndexValue> indexValues = this.indexValueRepository.findAllByIsLatest(true);
		final List<GetAllValuesResponse.IndexValue> transformedIndexValues = indexValues.stream()
				.map(this::toGetAllValuesResponseIndexValue).collect(Collectors.toCollection(ArrayList::new));
		return new GetAllValuesResponse(transformedIndexValues);
	}

	@Override
	public GetAllMetricValuesResponse getAllMetricValues() {
		final List<IndexMetricValue> indexMetricValues = this.indexMetricValueRepository.findAll();
		final List<GetAllMetricValuesResponse.IndexMetricValue> transformedIndexMetricValues = indexMetricValues
				.stream().map(this::toGetAllMetricValuesResponseIndexMetricValue)
				.collect(Collectors.toCollection(ArrayList::new));
		return new GetAllMetricValuesResponse(transformedIndexMetricValues);
	}

	private GetAllValuesResponse.IndexValue toGetAllValuesResponseIndexValue(final IndexValue indexValue) {
		return new GetAllValuesResponse.IndexValue(indexValue.getId(), indexValue.getHighIndexValue(),
				indexValue.getLowIndexValue(), indexValue.getClosingIndexValue(), indexValue.getIndexDate(),
				indexValue.isLatest(), indexValue.getIndex().getName());
	}

	private GetAllMetricValuesResponse.IndexMetricValue toGetAllMetricValuesResponseIndexMetricValue(
			final IndexMetricValue indexMetricValue) {
		return new GetAllMetricValuesResponse.IndexMetricValue(indexMetricValue.getId(), indexMetricValue.getAth(),
				indexMetricValue.getDma50(), indexMetricValue.getDma200(), indexMetricValue.getIndex().getName());
	}

	@Override
	public Path saveValueFile() {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String id = currentTime.format(VALUE_FILE_NAME_PATTERN);
		final String computedPath = this.valueFilePath + "value-" + id;
		log.info("Downloading file to the path [{}].", computedPath);
		final Path targetPath = Path.of(computedPath);
		final String computedValueFileDownloadUrl = this.valueFileDownloadUrl.replace("date",
				currentTime.format(VALUE_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN));
		log.info("Downloading file from the URL [{}].", this.valueFileDownloadUrl);
		final Flux<DataBuffer> flux = this.webClient.get().uri(computedValueFileDownloadUrl).retrieve()
				.bodyToFlux(DataBuffer.class);
		final Path path = DataBufferUtils
				.write(flux, targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
				.thenReturn(targetPath).block();
		log.info("File downloaded successfully.");
		return path;
	}

	@Override
	@Transactional
	public int stageValueFile(final Path path) throws IOException {
		final Long userId = this.userRepository.findByUsername("admin").map(x -> x.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found...!"));
		final Set<String> requiredNames = this.indexRepository.findAll().stream().map(x -> x.getName())
				.collect(Collectors.toCollection(HashSet::new));
		this.indexBatchRepository.cleanupIndexValuesStaging();
		int validRows = 0;
		log.info("Reading file from the path [{}].", path);
		try (final Reader reader = Files.newBufferedReader(path);
				CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get()
						.parse(reader)) {
			log.info("valueStagingBatchSize [{}].", this.valueStagingBatchSize);
			List<IndexValueStaging> indexValuesStaging = null;
			for (final CSVRecord csvRecord : csvParser) {
				final String[] values = csvRecord.values();
				if (values.length == 13) {
					final String name = values[0];
					if (requiredNames.contains(name)) {
						if (indexValuesStaging == null) {
							indexValuesStaging = new ArrayList<>();
						}
						final IndexValueStaging indexValueStaging = new IndexValueStaging();
						indexValueStaging.setName(name);
						indexValueStaging.setHighIndexValue(values[3]);
						indexValueStaging.setLowIndexValue(values[4]);
						indexValueStaging.setClosingIndexValue(values[5]);
						indexValueStaging.setIndexDate(values[1]);
						indexValueStaging.setCreatedBy(userId);
						indexValueStaging.setModifiedBy(userId);
						indexValuesStaging.add(indexValueStaging);
						++validRows;
						if (validRows % this.valueStagingBatchSize == 0) {
							this.indexBatchRepository.saveIndexValuesStaging(indexValuesStaging);
							indexValuesStaging = null;
						}
					}
				}
			}
			if (indexValuesStaging != null && !indexValuesStaging.isEmpty()) {
				this.indexBatchRepository.saveIndexValuesStaging(indexValuesStaging);
			}
		}
		log.info("Total valid rows [{}].", validRows);
		return validRows;
	}

	@Override
	@Transactional
	public void saveIndexValues(final int stagedRecords) {
		log.info("Saving records [{}].", stagedRecords);
		if (stagedRecords > 0) {
			this.indexBatchRepository.saveIndexValues();
			this.indexBatchRepository.updateIndexValues();
			this.indexBatchRepository.updateIndexMetric50DayMovingAverageValues();
			this.indexBatchRepository.updateIndexMetric200DayMovingAverageValues();
			this.indexBatchRepository.updateIndexMetricAllTimeHighValues();
		}
	}

}
