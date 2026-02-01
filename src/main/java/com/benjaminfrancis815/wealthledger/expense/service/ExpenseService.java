package com.benjaminfrancis815.wealthledger.expense.service;

import java.time.LocalDate;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseResponse;

public interface ExpenseService {

	GetAllExpensesResponse getAllExpenses(final LocalDate expenseStartDate, final LocalDate expenseEndDate);

	GetExpenseResponse getExpense(final Long id);

	CreateExpenseResponse createExpense(final CreateExpenseRequest request);

	UpdateExpenseResponse updateExpense(final Long id, final UpdateExpenseRequest request);

	void deleteExpense(final Long id);

}
