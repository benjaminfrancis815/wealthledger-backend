package com.benjaminfrancis815.wealthledger.expense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.benjaminfrancis815.wealthledger.expense.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	boolean existsByIdAndCreatedBy(Long id, Long userId);

	Optional<Expense> findByIdAndCreatedBy(Long id, Long userId);

	List<Expense> findAllByCreatedBy(Sort sort, Long userId);

}
