package com.benjaminfrancis815.wealthledger.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.stock.dto.GetAllLtpsResponse;
import com.benjaminfrancis815.wealthledger.stock.service.StockService;

@RestController
public class StockController {

	private final StockService stockService;

	@Autowired
	public StockController(final StockService stockService) {
		this.stockService = stockService;
	}

	@GetMapping(value = "/v1/stocks/ltps", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetAllLtpsResponse> getAllLtps() {
		final GetAllLtpsResponse response = this.stockService.getAllLtps();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
