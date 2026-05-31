package com.benjaminfrancis815.wealthledger.mutualfund.service;

import java.io.IOException;
import java.nio.file.Path;

import com.benjaminfrancis815.wealthledger.mutualfund.dto.GetAllNavsResponse;

public interface MutualFundService {

	GetAllNavsResponse getAllNavs();

	Path saveNavFile();

	int stageNavFile(final Path path) throws IOException;

	void saveMutualFundNavs(final int stagedRecords);

}
