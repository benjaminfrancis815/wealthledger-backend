package com.benjaminfrancis815.wealthledger.stock.service;

import java.io.IOException;
import java.nio.file.Path;

import com.benjaminfrancis815.wealthledger.stock.dto.GetAllLtpsResponse;

public interface StockService {

	GetAllLtpsResponse getAllLtps();

	Path saveLtpFile();

	int stageLtpFile(final Path path) throws IOException;

	void saveStockLtps(final int stagedRecords);

}
