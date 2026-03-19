package com.benjaminfrancis815.wealthledger.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.reference.model.ExpenseBook;

public interface ExpenseBookRepository extends JpaRepository<ExpenseBook, Long> {

}
