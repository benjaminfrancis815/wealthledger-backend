package com.benjaminfrancis815.wealthledger.index.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.index.dto.GetAllMetricValuesResponse;
import com.benjaminfrancis815.wealthledger.index.dto.GetAllValuesResponse;
import com.benjaminfrancis815.wealthledger.index.service.IndexService;

@RestController
public class IndexController {

	private final IndexService indexService;

	@Autowired
	public IndexController(final IndexService indexService) {
		this.indexService = indexService;
	}

	@GetMapping(value = "/v1/indices/values", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetAllValuesResponse> getAllValues() {
		final GetAllValuesResponse response = this.indexService.getAllValues();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(value = "/v1/indices/metrics/values", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetAllMetricValuesResponse> getAllMetricValues() {
		final GetAllMetricValuesResponse response = this.indexService.getAllMetricValues();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
