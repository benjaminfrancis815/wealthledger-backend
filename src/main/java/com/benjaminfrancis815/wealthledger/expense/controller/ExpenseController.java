package com.benjaminfrancis815.wealthledger.expense.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;
import com.benjaminfrancis815.wealthledger.expense.service.ExpenseService;

@RestController
public class ExpenseController {

	final ExpenseService expenseService;

	public ExpenseController(final ExpenseService expenseService) {
		this.expenseService = expenseService;
	}

	@GetMapping(value = "/v1/expenses", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetAllExpensesResponse> getAllExpenses() {
		final GetAllExpensesResponse response = this.expenseService.getAllExpenses();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/v1/expenses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CreateExpenseResponse> createExpense(@RequestBody final CreateExpenseRequest request) {
		final CreateExpenseResponse response = this.expenseService.createExpense(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
