package com.benjaminfrancis815.wealthledger.index.dto;

import java.math.BigDecimal;
import java.util.List;

public record GetAllMetricValuesResponse(List<IndexMetricValue> indexMetricValues) {

	public static record IndexMetricValue(Long id, BigDecimal ath, BigDecimal dma50, BigDecimal dma200,
			String indexName) {

	}

}
