package com.benjaminfrancis815.wealthledger.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetAllExpensesResponse(List<Expense> expenses) {

	public static record Expense(Long id, LocalDate expenseDate, BigDecimal amount, String description) {

	}

}
