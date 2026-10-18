package com.benjaminfrancis815.wealthledger.stock.schedule;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.benjaminfrancis815.wealthledger.stock.service.StockService;
import com.benjaminfrancis815.wealthledger.stock.type.LtpFile;

@Component
public class StockSchedule {

	private static final Logger log = LoggerFactory.getLogger(StockSchedule.class);

	private final StockService stockService;

	@Autowired
	public StockSchedule(final StockService stockService) {
		this.stockService = stockService;
	}

	@Scheduled(cron = "${stock.save-ltp-schedule-cron}")
	public void saveLtp() {
		try {
			final List<LtpFile> ltpFiles = this.stockService.saveLtpFiles();
			final int stagedRecords = this.stockService.stageLtpFiles(ltpFiles);
			this.stockService.saveStockLtps(stagedRecords);
		} catch (final Exception exc) {
			log.error("Schedule failed...!", exc);
			throw new RuntimeException("Schedule failed...!");
		}
	}

}
