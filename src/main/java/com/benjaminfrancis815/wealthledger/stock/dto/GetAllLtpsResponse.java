package com.benjaminfrancis815.wealthledger.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetAllLtpsResponse(List<Stock> stocks) {

	public static record Stock(Long id, BigDecimal ltp, LocalDate ltpDate, boolean isLatest, String symbol) {

	}

}
