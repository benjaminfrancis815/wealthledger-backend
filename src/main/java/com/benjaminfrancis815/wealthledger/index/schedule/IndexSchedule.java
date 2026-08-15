package com.benjaminfrancis815.wealthledger.index.schedule;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.benjaminfrancis815.wealthledger.index.service.IndexService;

@Component
public class IndexSchedule {

	private static final Logger log = LoggerFactory.getLogger(IndexSchedule.class);

	private final IndexService indexService;

	@Autowired
	public IndexSchedule(final IndexService indexService) {
		this.indexService = indexService;
	}

	@Scheduled(cron = "${index.save-value-schedule-cron}")
	public void saveValue() {
		try {
			final Path path = this.indexService.saveValueFile();
			final int stagedRecords = this.indexService.stageValueFile(path);
			this.indexService.saveIndexValues(stagedRecords);
		} catch (final Exception exc) {
			log.error("Schedule failed...!", exc);
			throw new RuntimeException("Schedule failed...!");
		}
	}

}
