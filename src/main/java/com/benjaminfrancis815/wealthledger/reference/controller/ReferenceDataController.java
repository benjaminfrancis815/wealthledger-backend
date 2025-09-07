package com.benjaminfrancis815.wealthledger.reference.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.reference.dto.GetExpenseCategoriesResponse;
import com.benjaminfrancis815.wealthledger.reference.dto.GetPaymentModesResponse;
import com.benjaminfrancis815.wealthledger.reference.service.ReferenceDataService;

@RestController
public class ReferenceDataController {

	final ReferenceDataService referenceDataService;

	public ReferenceDataController(final ReferenceDataService referenceDataService) {
		this.referenceDataService = referenceDataService;
	}

	@GetMapping(value = "/v1/payment-modes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetPaymentModesResponse> getPaymentModes() {
		final GetPaymentModesResponse getPaymentModesResponse = this.referenceDataService.getPaymentModes();
		return new ResponseEntity<>(getPaymentModesResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/v1/expense-categories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetExpenseCategoriesResponse> getExpenseCategories() {
		final GetExpenseCategoriesResponse getExpenseCategoriesResponse = this.referenceDataService
				.getExpenseCategories();
		return new ResponseEntity<>(getExpenseCategoriesResponse, HttpStatus.OK);
	}

}
