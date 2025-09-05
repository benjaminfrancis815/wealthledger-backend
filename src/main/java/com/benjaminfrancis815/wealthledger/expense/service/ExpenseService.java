package com.benjaminfrancis815.wealthledger.expense.service;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;

public interface ExpenseService {

	GetAllExpensesResponse getAllExpenses();

	CreateExpenseResponse createExpense(final CreateExpenseRequest request);

}
