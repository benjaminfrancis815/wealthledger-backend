package com.benjaminfrancis815.wealthledger.mutualfund.repository;

import java.util.List;

import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFundNavStaging;

public interface MutualFundBatchRepository {

	void cleanupMutualFundNavsStaging();

	int[] saveMutualFundNavsStaging(final List<MutualFundNavStaging> mutualFundNavsStaging);

	int saveMutualFundNavs();

	int updateMutualFundNavs();

}
