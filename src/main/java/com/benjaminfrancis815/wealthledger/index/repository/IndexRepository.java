package com.benjaminfrancis815.wealthledger.index.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.index.model.Index;

public interface IndexRepository extends JpaRepository<Index, Long> {

}
