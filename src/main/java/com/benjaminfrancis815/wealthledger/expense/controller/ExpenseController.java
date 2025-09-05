package com.benjaminfrancis815.wealthledger.expense.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseResponse;
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

	@GetMapping(value = "/v1/expenses/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetExpenseResponse> getExpense(@PathVariable final Long id) {
		final GetExpenseResponse response = this.expenseService.getExpense(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/v1/expenses", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CreateExpenseResponse> createExpense(@RequestBody final CreateExpenseRequest request) {
		final CreateExpenseResponse response = this.expenseService.createExpense(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping(value = "/v1/expenses/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UpdateExpenseResponse> updateExpense(@PathVariable final Long id,
			@RequestBody final UpdateExpenseRequest request) {
		final UpdateExpenseResponse response = this.expenseService.updateExpense(id, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping(value = "/v1/expenses/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable final Long id) {
		this.expenseService.deleteExpense(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
