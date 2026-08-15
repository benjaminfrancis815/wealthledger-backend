package com.benjaminfrancis815.wealthledger.index.model;

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
@Table(name = "index_values")
public class IndexValue extends AuditableEntity {

	@Id
	private Long id;

	@Column(name = "high_index_value")
	private BigDecimal highIndexValue;

	@Column(name = "low_index_value")
	private BigDecimal lowIndexValue;

	@Column(name = "closing_index_value")
	private BigDecimal closingIndexValue;

	@Column(name = "index_date")
	private LocalDate indexDate;

	@Column(name = "is_latest")
	private boolean isLatest;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "index_id")
	private Index index;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public BigDecimal getHighIndexValue() {
		return this.highIndexValue;
	}

	public void setHighIndexValue(final BigDecimal highIndexValue) {
		this.highIndexValue = highIndexValue;
	}

	public BigDecimal getLowIndexValue() {
		return this.lowIndexValue;
	}

	public void setLowIndexValue(final BigDecimal lowIndexValue) {
		this.lowIndexValue = lowIndexValue;
	}

	public BigDecimal getClosingIndexValue() {
		return this.closingIndexValue;
	}

	public void setClosingIndexValue(final BigDecimal closingIndexValue) {
		this.closingIndexValue = closingIndexValue;
	}

	public LocalDate getIndexDate() {
		return this.indexDate;
	}

	public void setIndexDate(final LocalDate indexDate) {
		this.indexDate = indexDate;
	}

	public boolean isLatest() {
		return this.isLatest;
	}

	public void setLatest(final boolean isLatest) {
		this.isLatest = isLatest;
	}

	public Index getIndex() {
		return this.index;
	}

	public void setIndex(final Index index) {
		this.index = index;
	}

}
