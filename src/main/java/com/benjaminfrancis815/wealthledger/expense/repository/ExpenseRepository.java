package com.benjaminfrancis815.wealthledger.expense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.benjaminfrancis815.wealthledger.expense.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

	boolean existsByIdAndCreatedBy(Long id, Long userId);

	Optional<Expense> findByIdAndCreatedBy(Long id, Long userId);

	List<Expense> findAllByCreatedBy(Sort sort, Long userId);

}
