package com.benjaminfrancis815.wealthledger.stock.service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.benjaminfrancis815.wealthledger.stock.type.LtpFile;
import com.benjaminfrancis815.wealthledger.stock.type.LtpFileConfig;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;

@Service
public class StockServiceImpl implements StockService {

	private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

	private static final DateTimeFormatter LTP_FILE_NAME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

	private static final DateTimeFormatter NSE_LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN = DateTimeFormatter
			.ofPattern("ddMMyyyy");

	private static final DateTimeFormatter BSE_LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN = DateTimeFormatter
			.ofPattern("yyyyMMdd");

	private static final long NSE_STOCK_EXCHANGE_ID = 1l;

	private static final long BSE_STOCK_EXCHANGE_ID = 2l;

	private static final Map<Long, LtpFileConfig> LTP_FILE_CONFIG_MAPPINGS = getLtpFileConfigMappings();

	private final WebClient webClient;
	private final StockRepository stockRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockLtpRepository stockLtpRepository;
	private final UserRepository userRepository;

	@Value("${stock.nse-ltp-file-download-url}")
	private String nseLtpFileDownloadUrl;

	@Value("${stock.bse-ltp-file-download-url}")
	private String bseLtpFileDownloadUrl;

	@Value("${stock.ltp-file-path}")
	private String ltpFilePath;

	@Value("${stock.ltp-staging-batch-size}")
	private int ltpStagingBatchSize;

	private static Map<Long, LtpFileConfig> getLtpFileConfigMappings() {
		final Map<Long, LtpFileConfig> ltpFileConfigMappings = new HashMap<>();
		ltpFileConfigMappings.put(NSE_STOCK_EXCHANGE_ID, new LtpFileConfig(15, 0, 8, 2));
		ltpFileConfigMappings.put(BSE_STOCK_EXCHANGE_ID, new LtpFileConfig(34, 7, 17, 1));
		return ltpFileConfigMappings;
	}

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
				stockLtp.isLatest(), stockLtp.getStock().getSymbol(), stockLtp.getStockExchange().getName());
	}

	@Override
	public List<LtpFile> saveLtpFiles() {
		final Path nseLtpFilePath = saveNseLtpFile();
		final Path bseLtpFilePath = saveBseLtpFile();
		final List<LtpFile> ltpFiles = new ArrayList<>();
		ltpFiles.add(new LtpFile(NSE_STOCK_EXCHANGE_ID, nseLtpFilePath));
		ltpFiles.add(new LtpFile(BSE_STOCK_EXCHANGE_ID, bseLtpFilePath));
		return ltpFiles;
	}

	private Path saveNseLtpFile() {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String id = currentTime.format(LTP_FILE_NAME_PATTERN);
		final String computedPath = this.ltpFilePath + "nse-ltp-" + id;
		log.info("Downloading file to the path [{}].", computedPath);
		final Path targetPath = Path.of(computedPath);
		final String computedLtpFileDownloadUrl = this.nseLtpFileDownloadUrl.replace("date",
				currentTime.format(NSE_LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN));
		log.info("Downloading file from the URL [{}].", computedLtpFileDownloadUrl);
		final Flux<DataBuffer> flux = this.webClient.get().uri(computedLtpFileDownloadUrl).retrieve()
				.bodyToFlux(DataBuffer.class);
		final Path path = DataBufferUtils
				.write(flux, targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
				.thenReturn(targetPath).block();
		log.info("File downloaded successfully.");
		return path;
	}

	private Path saveBseLtpFile() {
		final LocalDateTime currentTime = LocalDateTime.now();
		final String id = currentTime.format(LTP_FILE_NAME_PATTERN);
		final String computedPath = this.ltpFilePath + "bse-ltp-" + id;
		log.info("Downloading file to the path [{}].", computedPath);
		final Path targetPath = Path.of(computedPath);
		final String computedLtpFileDownloadUrl = this.bseLtpFileDownloadUrl.replace("date",
				currentTime.format(BSE_LTP_FILE_DOWNLOAD_URL_PLACEHOLDER_PATTERN));
		log.info("Downloading file from the URL [{}].", computedLtpFileDownloadUrl);
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
	public int stageLtpFiles(final List<LtpFile> ltpFiles) throws IOException {
		final Long userId = this.userRepository.findByUsername("admin").map(x -> x.getId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found...!"));
		final Set<String> requiredSymbols = this.stockRepository.findAll().stream().map(x -> x.getSymbol())
				.collect(Collectors.toCollection(HashSet::new));
		this.stockBatchRepository.cleanupStockLtpsStaging();
		int stagedRecords = 0;
		for (final LtpFile ltpFile : ltpFiles) {
			final Long stockExchangeId = ltpFile.stockExchangeId();
			final LtpFileConfig ltpFileConfig = LTP_FILE_CONFIG_MAPPINGS.get(stockExchangeId);
			final Path path = ltpFile.path();
			int validRows = 0;
			log.info("Reading file from the path [{}].", path);
			try (final Reader reader = Files.newBufferedReader(path);
					CSVParser csvParser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get()
							.parse(reader)) {
				log.info("ltpStagingBatchSize [{}].", this.ltpStagingBatchSize);
				List<StockLtpStaging> stockLtpsStaging = null;
				for (final CSVRecord csvRecord : csvParser) {
					final String[] values = csvRecord.values();
					if (values.length == ltpFileConfig.maxRowLength()) {
						final String symbol = values[ltpFileConfig.symbolIndex()];
						if (requiredSymbols.contains(symbol)) {
							if (stockLtpsStaging == null) {
								stockLtpsStaging = new ArrayList<>();
							}
							final StockLtpStaging stockLtpStaging = new StockLtpStaging();
							stockLtpStaging.setSymbol(symbol);
							stockLtpStaging.setLtp(values[ltpFileConfig.ltpIndex()]);
							stockLtpStaging.setLtpDate(values[ltpFileConfig.ltpDateIndex()]);
							stockLtpStaging.setStockExchangeId(stockExchangeId);
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
				log.info("Total valid rows [{}].", validRows);
				stagedRecords += validRows;
			}
		}
		log.info("Total staged rows [{}].", stagedRecords);
		return stagedRecords;
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
