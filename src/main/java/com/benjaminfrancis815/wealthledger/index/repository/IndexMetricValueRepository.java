package com.benjaminfrancis815.wealthledger.index.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.index.model.IndexMetricValue;

public interface IndexMetricValueRepository extends JpaRepository<IndexMetricValue, Long> {

}
