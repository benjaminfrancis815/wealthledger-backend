package com.benjaminfrancis815.wealthledger.mutualfund.schedule;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.benjaminfrancis815.wealthledger.mutualfund.service.MutualFundService;

@Component
public class MutualFundSchedule {

	private static final Logger log = LoggerFactory.getLogger(MutualFundSchedule.class);

	private final MutualFundService mutualFundService;

	@Autowired
	public MutualFundSchedule(final MutualFundService mutualFundService) {
		this.mutualFundService = mutualFundService;
	}

	@Scheduled(cron = "${mf.save-nav-schedule-cron}")
	public void saveNav() {
		try {
			final Path path = this.mutualFundService.saveNavFile();
			final int stagedRecords = this.mutualFundService.stageNavFile(path);
			this.mutualFundService.saveMutualFundNavs(stagedRecords);
		} catch (final Exception exc) {
			log.error("Schedule failed...!", exc);
			throw new RuntimeException("Schedule failed...!");
		}
	}

}
