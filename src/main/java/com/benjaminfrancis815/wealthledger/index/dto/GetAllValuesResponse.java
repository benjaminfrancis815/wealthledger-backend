package com.benjaminfrancis815.wealthledger.index.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record GetAllValuesResponse(List<IndexValue> indexValues) {

	public static record IndexValue(Long id, BigDecimal highIndexValue, BigDecimal lowIndexValue,
			BigDecimal closingIndexValue, LocalDate indexDate, boolean isLatest, String indexName) {

	}

}
