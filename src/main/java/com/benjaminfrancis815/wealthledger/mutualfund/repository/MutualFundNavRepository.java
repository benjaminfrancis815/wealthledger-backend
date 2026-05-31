package com.benjaminfrancis815.wealthledger.mutualfund.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFundNav;

public interface MutualFundNavRepository extends JpaRepository<MutualFundNav, Long> {

	List<MutualFundNav> findAllByIsLatest(boolean isLatest);

}
