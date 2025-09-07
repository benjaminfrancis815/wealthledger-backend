package com.benjaminfrancis815.wealthledger.expense.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "expense_date")
	private LocalDate expenseDate;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "description")
	private String description;

	@Column(name = "expense_category_id")
	private Long expenseCategoryId;

	@Column(name = "payment_mode_id")
	private Long paymentModeId;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public LocalDate getExpenseDate() {
		return this.expenseDate;
	}

	public void setExpenseDate(final LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Long getExpenseCategoryId() {
		return this.expenseCategoryId;
	}

	public void setExpenseCategoryId(final Long expenseCategoryId) {
		this.expenseCategoryId = expenseCategoryId;
	}

	public Long getPaymentModeId() {
		return this.paymentModeId;
	}

	public void setPaymentModeId(final Long paymentModeId) {
		this.paymentModeId = paymentModeId;
	}

}
