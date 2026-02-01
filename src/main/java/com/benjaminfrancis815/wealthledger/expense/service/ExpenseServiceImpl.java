package com.benjaminfrancis815.wealthledger.expense.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.CreateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetAllExpensesResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.GetExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseRequest;
import com.benjaminfrancis815.wealthledger.expense.dto.UpdateExpenseResponse;
import com.benjaminfrancis815.wealthledger.expense.model.Expense;
import com.benjaminfrancis815.wealthledger.expense.repository.ExpenseRepository;
import com.benjaminfrancis815.wealthledger.expense.specification.ExpenseSpecifications;
import com.benjaminfrancis815.wealthledger.security.model.User;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	private final ExpenseRepository expenseRepository;

	public ExpenseServiceImpl(final ExpenseRepository expenseRepository) {
		this.expenseRepository = expenseRepository;
	}

	@Override
	public void deleteExpense(final Long id) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		final Long userId = user.getId();
		if (!this.expenseRepository.existsByIdAndCreatedBy(id, userId)) {
			throw new RuntimeException("Expense not found...!");
		}
		this.expenseRepository.deleteById(id);
	}

	@Override
	public CreateExpenseResponse createExpense(final CreateExpenseRequest request) {
		final Expense expense = new Expense();
		expense.setExpenseDate(request.expenseDate());
		expense.setDescription(request.description());
		expense.setAmount(request.amount());
		expense.setExpenseCategoryId(request.expenseCategoryId());
		expense.setPaymentModeId(request.paymentModeId());
		final Expense savedExpense = this.expenseRepository.save(expense);
		final CreateExpenseResponse response = new CreateExpenseResponse(savedExpense.getId(),
				savedExpense.getExpenseDate(), savedExpense.getAmount(), savedExpense.getDescription(),
				savedExpense.getExpenseCategoryId(), savedExpense.getPaymentModeId());
		return response;
	}

	@Override
	public UpdateExpenseResponse updateExpense(final Long id, final UpdateExpenseRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		final Long userId = user.getId();
		final Expense expense = this.expenseRepository.findByIdAndCreatedBy(id, userId)
				.orElseThrow(() -> new RuntimeException("Expense not found...!"));
		expense.setExpenseDate(request.expenseDate());
		expense.setDescription(request.description());
		expense.setAmount(request.amount());
		expense.setExpenseCategoryId(request.expenseCategoryId());
		expense.setPaymentModeId(request.paymentModeId());
		final Expense updatedExpense = this.expenseRepository.save(expense);
		final UpdateExpenseResponse response = new UpdateExpenseResponse(updatedExpense.getId(),
				updatedExpense.getExpenseDate(), updatedExpense.getAmount(), updatedExpense.getDescription(),
				updatedExpense.getExpenseCategoryId(), updatedExpense.getPaymentModeId());
		return response;
	}

	@Override
	public GetAllExpensesResponse getAllExpenses(final LocalDate expenseStartDate, final LocalDate expenseEndDate) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		final Long userId = user.getId();
		final Sort sort = Sort.by(Sort.Order.asc("expenseDate"), Sort.Order.asc("id"));
		final Specification<Expense> specification = ExpenseSpecifications.getAllExpenses(userId, expenseStartDate,
				expenseEndDate);
		final List<Expense> expenses = this.expenseRepository.findAll(specification, sort);
		final List<GetAllExpensesResponse.Expense> transformedExpenses = expenses.stream()
				.map(this::toGetAllExpensesResponseExpense).collect(Collectors.toCollection(ArrayList::new));
		return new GetAllExpensesResponse(transformedExpenses);
	}

	@Override
	public GetExpenseResponse getExpense(final Long id) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final User user = (User) authentication.getPrincipal();
		final Long userId = user.getId();
		final Expense expense = this.expenseRepository.findByIdAndCreatedBy(id, userId)
				.orElseThrow(() -> new RuntimeException("Expense not found...!"));
		return new GetExpenseResponse(expense.getId(), expense.getExpenseDate(), expense.getAmount(),
				expense.getDescription(), expense.getExpenseCategoryId(), expense.getPaymentModeId());
	}

	private GetAllExpensesResponse.Expense toGetAllExpensesResponseExpense(final Expense expense) {
		return new GetAllExpensesResponse.Expense(expense.getId(), expense.getExpenseDate(), expense.getAmount(),
				expense.getDescription(), expense.getExpenseCategoryId(), expense.getPaymentModeId());
	}

}
