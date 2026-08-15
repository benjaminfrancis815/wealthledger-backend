package com.benjaminfrancis815.wealthledger.index.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.index.model.IndexValue;

public interface IndexValueRepository extends JpaRepository<IndexValue, Long> {

	List<IndexValue> findAllByIsLatest(boolean isLatest);

}
