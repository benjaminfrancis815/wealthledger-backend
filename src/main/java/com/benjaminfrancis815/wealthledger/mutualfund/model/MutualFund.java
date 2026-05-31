package com.benjaminfrancis815.wealthledger.mutualfund.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mutual_funds")
public class MutualFund {

	@Id
	private Long id;

	@Column(name = "scheme_code")
	private Long schemeCode;

	@Column(name = "name")
	private String name;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Long getSchemeCode() {
		return this.schemeCode;
	}

	public void setSchemeCode(final Long schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
