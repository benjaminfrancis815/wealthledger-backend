package com.benjaminfrancis815.wealthledger.stock.model;

public class StockLtpStaging {

	private String symbol;

	private String ltp;

	private String ltpDate;

	private Long createdBy;

	private Long modifiedBy;

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(final String symbol) {
		this.symbol = symbol;
	}

	public String getLtp() {
		return this.ltp;
	}

	public void setLtp(final String ltp) {
		this.ltp = ltp;
	}

	public String getLtpDate() {
		return this.ltpDate;
	}

	public void setLtpDate(final String ltpDate) {
		this.ltpDate = ltpDate;
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
