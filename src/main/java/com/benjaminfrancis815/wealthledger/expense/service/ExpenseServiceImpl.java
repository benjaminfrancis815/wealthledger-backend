package com.benjaminfrancis815.wealthledger.expense.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;
import com.benjaminfrancis815.wealthledger.expense.model.Expense;
import com.benjaminfrancis815.wealthledger.expense.repository.ExpenseRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	private final ExpenseRepository expenseRepository;

	public ExpenseServiceImpl(final ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}

	@Override
	public CreateExpenseResponse createExpense(final CreateExpenseRequest request) {
		final Expense expense = new Expense();
		expense.setExpenseDate(request.expenseDate());
		expense.setDescription(request.description());
		expense.setAmount(request.amount());
		final Expense savedExpense = expenseRepository.save(expense);
		final CreateExpenseResponse response = new CreateExpenseResponse(savedExpense.getId(),
				savedExpense.getExpenseDate(), savedExpense.getAmount(), savedExpense.getDescription());
		return response;
	}

	@Override
	public GetAllExpensesResponse getAllExpenses() {
		final List<Expense> expenses = expenseRepository.findAll();
		final List<GetAllExpensesResponse.Expense> fetchedExpenses = expenses.stream()
				.map(this::toGetAllExpensesResponseExpense).collect(Collectors.toCollection(ArrayList::new));
		return new GetAllExpensesResponse(fetchedExpenses);
	}

	private GetAllExpensesResponse.Expense toGetAllExpensesResponseExpense(final Expense expense) {
		return new GetAllExpensesResponse.Expense(expense.getId(), expense.getExpenseDate(), expense.getAmount(),
				expense.getDescription());
	}

}
