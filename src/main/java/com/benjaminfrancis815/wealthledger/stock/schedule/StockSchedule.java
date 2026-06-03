package com.benjaminfrancis815.wealthledger.stock.schedule;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.benjaminfrancis815.wealthledger.stock.service.StockService;

@Component
public class StockSchedule {

	private static final Logger log = LoggerFactory.getLogger(StockSchedule.class);

	private final StockService stockService;

	@Autowired
	public StockSchedule(final StockService stockService) {
		this.stockService = stockService;
	}

	@Scheduled(cron = "${stock.save-ltp-schedule-cron}")
	public void saveNav() {
		try {
			final Path path = this.stockService.saveLtpFile();
			final int stagedRecords = this.stockService.stageLtpFile(path);
			this.stockService.saveStockLtps(stagedRecords);
		} catch (final Exception exc) {
			log.error("Schedule failed...!", exc);
			throw new RuntimeException("Schedule failed...!");
		}
	}

}
