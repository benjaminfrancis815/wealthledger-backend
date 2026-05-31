package com.benjaminfrancis815.wealthledger.mutualfund.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFund;

public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {

}
