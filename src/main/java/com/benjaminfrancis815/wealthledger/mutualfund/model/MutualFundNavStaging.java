package com.benjaminfrancis815.wealthledger.mutualfund.model;

public class MutualFundNavStaging {

	private String schemeCode;

	private String nav;

	private String navDate;

	private Long createdBy;

	private Long modifiedBy;

	public String getSchemeCode() {
		return this.schemeCode;
	}

	public void setSchemeCode(final String schemeCode) {
		this.schemeCode = schemeCode;
	}

	public String getNav() {
		return this.nav;
	}

	public void setNav(final String nav) {
		this.nav = nav;
	}

	public String getNavDate() {
		return this.navDate;
	}

	public void setNavDate(final String navDate) {
		this.navDate = navDate;
	}

	public Long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(final Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
