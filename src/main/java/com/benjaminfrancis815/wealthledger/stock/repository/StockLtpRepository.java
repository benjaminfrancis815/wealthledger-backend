package com.benjaminfrancis815.wealthledger.stock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.stock.model.StockLtp;

public interface StockLtpRepository extends JpaRepository<StockLtp, Long> {

	List<StockLtp> findAllByIsLatest(boolean isLatest);

}
