package com.benjaminfrancis815.wealthledger.mutualfund.service;

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

import com.benjaminfrancis815.wealthledger.mutualfund.dto.GetAllNavsResponse;
import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFundNav;
import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFundNavStaging;
import com.benjaminfrancis815.wealthledger.mutualfund.repository.MutualFundBatchRepository;
import com.benjaminfrancis815.wealthledger.mutualfund.repository.MutualFundNavRepository;
import com.benjaminfrancis815.wealthledger.mutualfund.repository.MutualFundRepository;
import com.benjaminfrancis815.wealthledger.security.repository.UserRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;

@Service
public class MutualFundServiceImpl implements MutualFundService {

	private static final Logger log = LoggerFactory.getLogger(MutualFundServiceImpl.class);

	private static final DateTimeFormatter NAV_FILE_NAME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

	private final WebClient webClient;
	private final MutualFundRepository mutualFundRepository;
	private final MutualFundBatchRepository mutualFundBatchRepository;
	private final MutualFundNavRepository mutualFundNavRepository;
	private final UserRepository userRepository;

	@Value("${mf.nav-file-download-url}")
	private String navFileDownloadUrl;

	@Value("${mf.nav-file-path}")
	private String navFilePath;

	@Value("${mf.nav-staging-batch-size}")
	private int navStagingBatchSize;

	@Autowired
	public MutualFundServiceImpl(final WebClient webClient, final MutualFundRepository mutualFundRepository,
			final MutualFundBatchRepository mutualFundBatchRepository,
			final MutualFundNavRepository mutualFundNavRepository, final UserRepository userRepository) {
		this.webClient = webClient;
		this.mutualFundRepository = mutualFundRepository;
		this.mutualFundBatchRepository = mutualFundBatchRepository;
		this.mutualFundNavRepository = mutualFundNavRepository;
		this.userRepository = userRepository;
	}

	@Override
	public GetAllNavsResponse getAllNavs() {
		final List<MutualFundNav> mutualFundNavs = this.mutualFundNavRepository.findAllByIsLatest(true);
		final List<GetAllNavsResponse.MutualFund> transformedMutualFunds = mutualFundNavs.stream()
				.map(this::toGetAllNavsResponseMutualFund).collect(Collectors.toCollection(ArrayList::new));
		return new GetAllNavsResponse(transformedMutualFunds);
	}

	private GetAllNavsResponse.MutualFund toGetAllNavsResponseMutualFund(final MutualFundNav mutualFundNav) {
		return new GetAllNavsResponse.MutualFund(mutualFundNav.getId(), mutualFundNav.getNav(),
				mutualFundNav.getNavDate(), mutualFundNav.isLatest(), mutualFundNav.getMutualFund().getSchemeCode());
	}

	@Override
	public Path saveNavFile() {
		final String id = LocalDateTime.now().format(NAV_FILE_NAME_PATTERN);
		final String computedPath = this.navFilePath + id;
		log.info("Downloading file to the path [{}].", computedPath);
		final Path targetPath = Path.of(computedPath);
		log.info("Downloading file from the URL [{}].", this.navFileDownloadUrl);
		final Flux<DataBuffer> flux = this.webClient.get().uri(this.navFileDownloadUrl).retrieve()
				.bodyToFlux(DataBuffer.class);
		final Path path = DataBufferUtils
				.write(flux, targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
				.thenReturn(targetPath).block();
		log.info("File downloaded successfully.");
		return path;
	}

	@Override
	@Transactional
	public int stageNavFile(final Path path) throws IOException {
		final Long userId = this.userRepository.findByUsername("admin").map(x -> x.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found...!"));
		final Set<Long> requiredSchemeCodes = this.mutualFundRepository.findAll().stream().map(x -> x.getSchemeCode())
				.collect(Collectors.toCollection(HashSet::new));
		this.mutualFundBatchRepository.cleanupMutualFundNavsStaging();
		int validRows = 0;
		log.info("Reading file from the path [{}].", path);
		try (final Reader reader = Files.newBufferedReader(path);
				CSVParser csvParser = CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader()
						.setSkipHeaderRecord(true).get().parse(reader)) {
			log.info("navStagingBatchSize [{}].", this.navStagingBatchSize);
			List<MutualFundNavStaging> mutualFundNavsStaging = null;
			for (final CSVRecord csvRecord : csvParser) {
				final String[] values = csvRecord.values();
				if (values.length == 6) {
					final Long schemeCode = Long.parseLong(values[0]);
					if (requiredSchemeCodes.contains(schemeCode)) {
						if (mutualFundNavsStaging == null) {
							mutualFundNavsStaging = new ArrayList<>();
						}
						final MutualFundNavStaging mutualFundNavStaging = new MutualFundNavStaging();
						mutualFundNavStaging.setSchemeCode(values[0]);
						mutualFundNavStaging.setNav(values[4]);
						mutualFundNavStaging.setNavDate(values[5]);
						mutualFundNavStaging.setCreatedBy(userId);
						mutualFundNavStaging.setModifiedBy(userId);
						mutualFundNavsStaging.add(mutualFundNavStaging);
						++validRows;
						if (validRows % this.navStagingBatchSize == 0) {
							this.mutualFundBatchRepository.saveMutualFundNavsStaging(mutualFundNavsStaging);
							mutualFundNavsStaging = null;
						}
					}
				}
			}
			if (mutualFundNavsStaging != null && !mutualFundNavsStaging.isEmpty()) {
				this.mutualFundBatchRepository.saveMutualFundNavsStaging(mutualFundNavsStaging);
			}
		}
		log.info("Total valid rows [{}].", validRows);
		return validRows;
	}

	@Override
	@Transactional
	public void saveMutualFundNavs(final int stagedRecords) {
		log.info("Saving records [{}].", stagedRecords);
		if (stagedRecords > 0) {
			this.mutualFundBatchRepository.saveMutualFundNavs();
			this.mutualFundBatchRepository.updateMutualFundNavs();
		}
	}

}
