package com.benjaminfrancis815.wealthledger.expense.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.benjaminfrancis815.wealthledger.expense.model.Expense;

import jakarta.persistence.criteria.Predicate;

public class ExpenseSpecifications {

	private ExpenseSpecifications() {

	}

	public static Specification<Expense> getAllExpenses(final Long userId, final LocalDate expenseStartDate,
			final LocalDate expenseEndDate) {
		return (root, query, criteriaBuilder) -> {
			final List<Predicate> predicates = new ArrayList<>();

			predicates.add(criteriaBuilder.equal(root.get("createdBy"), userId));

			if (expenseStartDate != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), expenseStartDate));
			}

			if (expenseEndDate != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), expenseEndDate));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
