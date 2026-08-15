package com.benjaminfrancis815.wealthledger.index.repository;

import java.util.List;

import com.benjaminfrancis815.wealthledger.index.model.IndexValueStaging;

public interface IndexBatchRepository {

	void cleanupIndexValuesStaging();

	int[] saveIndexValuesStaging(final List<IndexValueStaging> indexValuesStaging);

	int saveIndexValues();

	int updateIndexValues();

	int updateIndexMetric50DayMovingAverageValues();

	int updateIndexMetric200DayMovingAverageValues();

	int updateIndexMetricAllTimeHighValues();

}
