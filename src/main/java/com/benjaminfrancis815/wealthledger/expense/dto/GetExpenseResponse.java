package com.benjaminfrancis815.wealthledger.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetExpenseResponse(Long id, LocalDate expenseDate, BigDecimal amount, String description) {

}
