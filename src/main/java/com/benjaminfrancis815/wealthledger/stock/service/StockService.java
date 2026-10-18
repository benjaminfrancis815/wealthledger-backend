package com.benjaminfrancis815.wealthledger.stock.service;

import java.io.IOException;
import java.util.List;

import com.benjaminfrancis815.wealthledger.stock.dto.GetAllLtpsResponse;
import com.benjaminfrancis815.wealthledger.stock.type.LtpFile;

public interface StockService {

	GetAllLtpsResponse getAllLtps();

	List<LtpFile> saveLtpFiles();

	int stageLtpFiles(final List<LtpFile> ltpFiles) throws IOException;

	void saveStockLtps(final int stagedRecords);

}
