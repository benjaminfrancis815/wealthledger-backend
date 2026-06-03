package com.benjaminfrancis815.wealthledger.stock.repository;

import java.util.List;

import com.benjaminfrancis815.wealthledger.stock.model.StockLtpStaging;

public interface StockBatchRepository {

	void cleanupStockLtpsStaging();

	int[] saveStockLtpsStaging(final List<StockLtpStaging> stockLtpsStaging);

	int saveStockLtps();

	int updateStockLtps();

}
