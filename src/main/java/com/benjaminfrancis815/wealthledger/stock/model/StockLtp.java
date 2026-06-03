package com.benjaminfrancis815.wealthledger.stock.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.benjaminfrancis815.wealthledger.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_ltps")
public class StockLtp extends AuditableEntity {

	@Id
	private Long id;

	@Column(name = "ltp")
	private BigDecimal ltp;

	@Column(name = "ltp_date")
	private LocalDate ltpDate;

	@Column(name = "is_latest")
	private boolean isLatest;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "stock_id")
	private Stock stock;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public BigDecimal getLtp() {
		return this.ltp;
	}

	public void setLtp(final BigDecimal ltp) {
		this.ltp = ltp;
	}

	public LocalDate getLtpDate() {
		return this.ltpDate;
	}

	public void setLtpDate(final LocalDate ltpDate) {
		this.ltpDate = ltpDate;
	}

	public boolean isLatest() {
		return this.isLatest;
	}

	public void setLatest(final boolean isLatest) {
		this.isLatest = isLatest;
	}

	public Stock getStock() {
		return this.stock;
	}

	public void setStock(final Stock stock) {
		this.stock = stock;
	}

}
