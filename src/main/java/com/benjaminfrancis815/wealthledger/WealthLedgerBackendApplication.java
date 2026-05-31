package com.benjaminfrancis815.wealthledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WealthLedgerBackendApplication {

	public static void main(final String[] args) {
		SpringApplication.run(WealthLedgerBackendApplication.class, args);
	}

}
