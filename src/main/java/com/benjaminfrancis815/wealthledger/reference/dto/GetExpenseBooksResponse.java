package com.benjaminfrancis815.wealthledger.reference.dto;

import java.util.List;

public record GetExpenseBooksResponse(List<ExpenseBook> expenseBooks) {

	public static record ExpenseBook(Long id, String name) {

	}

}
