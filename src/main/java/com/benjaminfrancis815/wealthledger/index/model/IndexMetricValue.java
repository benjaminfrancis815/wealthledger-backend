package com.benjaminfrancis815.wealthledger.index.model;

import java.math.BigDecimal;

import com.benjaminfrancis815.wealthledger.model.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "index_metric_values")
public class IndexMetricValue extends AuditableEntity {

	@Id
	private Long id;

	@Column(name = "ath")
	private BigDecimal ath;

	@Column(name = "dma_50")
	private BigDecimal dma50;

	@Column(name = "dma_200")
	private BigDecimal dma200;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "index_id")
	private Index index;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public BigDecimal getAth() {
		return this.ath;
	}

	public void setAth(final BigDecimal ath) {
		this.ath = ath;
	}

	public BigDecimal getDma50() {
		return this.dma50;
	}

	public void setDma50(final BigDecimal dma50) {
		this.dma50 = dma50;
	}

	public BigDecimal getDma200() {
		return this.dma200;
	}

	public void setDma200(final BigDecimal dma200) {
		this.dma200 = dma200;
	}

	public Index getIndex() {
		return this.index;
	}

	public void setIndex(final Index index) {
		this.index = index;
	}

}
