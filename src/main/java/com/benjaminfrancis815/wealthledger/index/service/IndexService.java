package com.benjaminfrancis815.wealthledger.index.service;

import java.io.IOException;
import java.nio.file.Path;

import com.benjaminfrancis815.wealthledger.index.dto.GetAllMetricValuesResponse;
import com.benjaminfrancis815.wealthledger.index.dto.GetAllValuesResponse;

public interface IndexService {

	GetAllValuesResponse getAllValues();

	GetAllMetricValuesResponse getAllMetricValues();

	Path saveValueFile();

	int stageValueFile(final Path path) throws IOException;

	void saveIndexValues(final int stagedRecords);

}
