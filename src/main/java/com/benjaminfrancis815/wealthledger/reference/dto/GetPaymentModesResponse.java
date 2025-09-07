package com.benjaminfrancis815.wealthledger.reference.dto;

import java.util.List;

public record GetPaymentModesResponse(List<PaymentMode> paymentModes) {

	public static record PaymentMode(Long id, String name) {

	}

}
