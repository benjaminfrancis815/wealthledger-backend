package com.benjaminfrancis815.wealthledger.mutualfund.model;

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
@Table(name = "mutual_fund_navs")
public class MutualFundNav extends AuditableEntity {

	@Id
	private Long id;

	@Column(name = "nav")
	private BigDecimal nav;

	@Column(name = "nav_date")
	private LocalDate navDate;

	@Column(name = "is_latest")
	private boolean isLatest;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mutual_fund_id")
	private MutualFund mutualFund;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public BigDecimal getNav() {
		return this.nav;
	}

	public void setNav(final BigDecimal nav) {
		this.nav = nav;
	}

	public LocalDate getNavDate() {
		return this.navDate;
	}

	public void setNavDate(final LocalDate navDate) {
		this.navDate = navDate;
	}

	public boolean isLatest() {
		return this.isLatest;
	}

	public void setLatest(final boolean isLatest) {
		this.isLatest = isLatest;
	}

	public MutualFund getMutualFund() {
		return this.mutualFund;
	}

	public void setMutualFund(final MutualFund mutualFund) {
		this.mutualFund = mutualFund;
	}

}
