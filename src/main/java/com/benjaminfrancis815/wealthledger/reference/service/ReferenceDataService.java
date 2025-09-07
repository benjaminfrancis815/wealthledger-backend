package com.benjaminfrancis815.wealthledger.reference.service;

import com.benjaminfrancis815.wealthledger.reference.dto.GetExpenseCategoriesResponse;
import com.benjaminfrancis815.wealthledger.reference.dto.GetPaymentModesResponse;

public interface ReferenceDataService {

	GetPaymentModesResponse getPaymentModes();

	GetExpenseCategoriesResponse getExpenseCategories();

}
