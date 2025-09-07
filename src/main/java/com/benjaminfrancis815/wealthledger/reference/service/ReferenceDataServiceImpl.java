package com.benjaminfrancis815.wealthledger.reference.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.benjaminfrancis815.wealthledger.reference.dto.GetExpenseCategoriesResponse;
import com.benjaminfrancis815.wealthledger.reference.dto.GetPaymentModesResponse;
import com.benjaminfrancis815.wealthledger.reference.model.ExpenseCategory;
import com.benjaminfrancis815.wealthledger.reference.model.PaymentMode;
import com.benjaminfrancis815.wealthledger.reference.repository.ExpenseCategoryRepository;
import com.benjaminfrancis815.wealthledger.reference.repository.PaymentModeRepository;

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService {

	private final PaymentModeRepository paymentModeRepository;
	private final ExpenseCategoryRepository expenseCategoryRepository;

	public ReferenceDataServiceImpl(final PaymentModeRepository paymentModeRepository,
			final ExpenseCategoryRepository expenseCategoryRepository) {
		this.paymentModeRepository = paymentModeRepository;
		this.expenseCategoryRepository = expenseCategoryRepository;
	}

	@Override
	public GetPaymentModesResponse getPaymentModes() {
		final List<PaymentMode> paymentModes = this.paymentModeRepository.findAll();
		final List<GetPaymentModesResponse.PaymentMode> transformedPaymentModes = paymentModes.stream()
				.map(this::toGetPaymentModesResponsePaymentMode).collect(Collectors.toCollection(ArrayList::new));
		return new GetPaymentModesResponse(transformedPaymentModes);
	}

	@Override
	public GetExpenseCategoriesResponse getExpenseCategories() {
		final List<ExpenseCategory> expenseCategories = this.expenseCategoryRepository.findAll();
		final List<GetExpenseCategoriesResponse.ExpenseCategory> transformedExpenseCategories = expenseCategories
				.stream().map(this::toGetExpenseCategoriesResponseExpenseCategory)
				.collect(Collectors.toCollection(ArrayList::new));
		return new GetExpenseCategoriesResponse(transformedExpenseCategories);
	}

	private GetPaymentModesResponse.PaymentMode toGetPaymentModesResponsePaymentMode(final PaymentMode paymentMode) {
		return new GetPaymentModesResponse.PaymentMode(paymentMode.getId(), paymentMode.getName());
	}

	private GetExpenseCategoriesResponse.ExpenseCategory toGetExpenseCategoriesResponseExpenseCategory(
			final ExpenseCategory expenseCategory) {
		return new GetExpenseCategoriesResponse.ExpenseCategory(expenseCategory.getId(), expenseCategory.getName());
	}

}
