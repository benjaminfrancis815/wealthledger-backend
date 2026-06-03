package com.benjaminfrancis815.wealthledger.stock.service;

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

import com.benjaminfrancis815.wealthledger.security.repository.UserRepository;
import com.benjaminfrancis815.wealthledger.stock.dto.GetAllLtpsResponse;
import com.benjaminfrancis815.wealthledger.stock.model.StockLtp;
import com.benjaminfrancis815.wealthledger.stock.model.StockLtpStaging;
import com.benjaminfrancis815.wealthledger.stock.repository.StockBatchRepository;
import com.benjaminfrancis815.wealthledger.stock.repository.StockLtpRepository;
import com.benjaminfrancis815.wealthledger.stock.repository.StockRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;

@Service
public class StockServiceImpl implements StockService {

	private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

	private static final DateTimeFormatter LTP_FILE_NAME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

	private static final DateTimeFormatter LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN = DateTimeFormatter
			.ofPattern("ddMMyyyy");

	private final WebClient webClient;
	private final StockRepository stockRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockLtpRepository stockLtpRepository;
	private final UserRepository userRepository;

	@Value("${stock.ltp-file-download-url}")
	private String ltpFileDownloadUrl;

	@Value("${stock.ltp-file-path}")
	private String ltpFilePath;

	@Value("${stock.ltp-staging-batch-size}")
	private int ltpStagingBatchSize;

	@Autowired
	public StockServiceImpl(final WebClient webClient, final StockRepository stockRepository,
			final StockBatchRepository stockBatchRepository, final StockLtpRepository stockLtpRepository,
			final UserRepository userRepository) {
		this.webClient = webClient;
		this.stockRepository = stockRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockLtpRepository = stockLtpRepository;
		this.userRepository = userRepository;
	}

	@Override
	public GetAllLtpsResponse getAllLtps() {
		final List<StockLtp> stockLtps = this.stockLtpRepository.findAllByIsLatest(true);
		final List<GetAllLtpsResponse.Stock> transformedStocks = stockLtps.stream().map(this::toGetAllLtpsResponse)
				.collect(Collectors.toCollection(ArrayList::new));
		return new GetAllLtpsResponse(transformedStocks);
	}

	private GetAllLtpsResponse.Stock toGetAllLtpsResponse(final StockLtp stockLtp) {
		return new GetAllLtpsResponse.Stock(stockLtp.getId(), stockLtp.getLtp(), stockLtp.getLtpDate(),
				stockLtp.isLatest(), stockLtp.getStock().getSymbol());
	}

	@Override
	public Path saveLtpFile() {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String id = currentTime.format(LTP_FILE_NAME_PATTERN);
		final String computedPath = this.ltpFilePath + "ltp-" + id;
		log.info("Downloading file to the path [{}].", computedPath);
		final Path targetPath = Path.of(computedPath);
		final String computedLtpFileDownloadUrl = this.ltpFileDownloadUrl.replace("date",
				currentTime.format(LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN));
		log.info("Downloading file from the URL [{}].", this.ltpFileDownloadUrl);
		final Flux<DataBuffer> flux = this.webClient.get().uri(computedLtpFileDownloadUrl).retrieve()
				.bodyToFlux(DataBuffer.class);
		final Path path = DataBufferUtils
				.write(flux, targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
				.thenReturn(targetPath).block();
		log.info("File downloaded successfully.");
		return path;
	}

	@Override
	@Transactional
	public int stageLtpFile(final Path path) throws IOException {
		final Long userId = this.userRepository.findByUsername("admin").map(x -> x.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found...!"));
		final Set<String> requiredSymbols = this.stockRepository.findAll().stream().map(x -> x.getSymbol())
				.collect(Collectors.toCollection(HashSet::new));
		this.stockBatchRepository.cleanupStockLtpsStaging();
		int validRows = 0;
		log.info("Reading file from the path [{}].", path);
		try (final Reader reader = Files.newBufferedReader(path);
				CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get()
						.parse(reader)) {
			log.info("ltpStagingBatchSize [{}].", this.ltpStagingBatchSize);
			List<StockLtpStaging> stockLtpsStaging = null;
			for (final CSVRecord csvRecord : csvParser) {
				final String[] values = csvRecord.values();
				if (values.length == 15) {
					final String symbol = values[0];
					if (requiredSymbols.contains(symbol)) {
						if (stockLtpsStaging == null) {
							stockLtpsStaging = new ArrayList<>();
						}
						final StockLtpStaging stockLtpStaging = new StockLtpStaging();
						stockLtpStaging.setSymbol(symbol);
						stockLtpStaging.setLtp(values[8]);
						stockLtpStaging.setLtpDate(values[2]);
						stockLtpStaging.setCreatedBy(userId);
						stockLtpStaging.setModifiedBy(userId);
						stockLtpsStaging.add(stockLtpStaging);
						++validRows;
						if (validRows % this.ltpStagingBatchSize == 0) {
							this.stockBatchRepository.saveStockLtpsStaging(stockLtpsStaging);
							stockLtpsStaging = null;
						}
					}
				}
			}
			if (stockLtpsStaging != null && !stockLtpsStaging.isEmpty()) {
				this.stockBatchRepository.saveStockLtpsStaging(stockLtpsStaging);
			}
		}
		log.info("Total valid rows [{}].", validRows);
		return validRows;
	}

	@Override
	@Transactional
	public void saveStockLtps(final int stagedRecords) {
		log.info("Saving records [{}].", stagedRecords);
		if (stagedRecords > 0) {
			this.stockBatchRepository.saveStockLtps();
			this.stockBatchRepository.updateStockLtps();
		}
	}

}
