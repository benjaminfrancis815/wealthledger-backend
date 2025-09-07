package com.benjaminfrancis815.wealthledger.reference.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.reference.model.ExpenseCategory;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

}
