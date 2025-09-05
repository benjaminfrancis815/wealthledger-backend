package com.benjaminfrancis815.wealthledger.expense.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.expense.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
