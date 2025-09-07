package com.benjaminfrancis815.wealthledger.reference.dto;

import java.util.List;

public record GetExpenseCategoriesResponse(List<ExpenseCategory> expenseCategories) {

	public static record ExpenseCategory(Long id, String name) {

	}

}
