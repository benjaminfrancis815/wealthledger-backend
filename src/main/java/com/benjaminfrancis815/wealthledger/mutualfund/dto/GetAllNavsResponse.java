package com.benjaminfrancis815.wealthledger.mutualfund.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetAllNavsResponse(List<MutualFund> mutualFunds) {

	public static record MutualFund(Long id, BigDecimal nav, LocalDate navDate, boolean isLatest, Long schemeCode) {

	}

}
