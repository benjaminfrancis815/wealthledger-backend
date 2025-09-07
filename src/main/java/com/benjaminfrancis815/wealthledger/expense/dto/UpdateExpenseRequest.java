package com.benjaminfrancis815.wealthledger.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateExpenseRequest(LocalDate expenseDate, BigDecimal amount, String description, Long expenseCategoryId,
		Long paymentModeId) {

}
