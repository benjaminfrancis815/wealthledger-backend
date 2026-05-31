package com.benjaminfrancis815.wealthledger.mutualfund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.mutualfund.dto.GetAllNavsResponse;
import com.benjaminfrancis815.wealthledger.mutualfund.service.MutualFundService;

@RestController
public class MutualFundController {

	private final MutualFundService mutualFundService;

	@Autowired
	public MutualFundController(final MutualFundService mutualFundService) {
		this.mutualFundService = mutualFundService;
	}

	@GetMapping(value = "/v1/mutual-funds/navs", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetAllNavsResponse> getAllNavs() {
		final GetAllNavsResponse response = this.mutualFundService.getAllNavs();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
