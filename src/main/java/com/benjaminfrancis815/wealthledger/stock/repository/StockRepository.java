package com.benjaminfrancis815.wealthledger.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.stock.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
