package com.benjaminfrancis815.wealthledger.index.model;

public class IndexValueStaging {

	private String name;

	private String highIndexValue;

	private String lowIndexValue;

	private String closingIndexValue;

	private String indexDate;

	private Long createdBy;

	private Long modifiedBy;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getHighIndexValue() {
		return this.highIndexValue;
	}

	public void setHighIndexValue(final String highIndexValue) {
		this.highIndexValue = highIndexValue;
	}

	public String getLowIndexValue() {
		return this.lowIndexValue;
	}

	public void setLowIndexValue(final String lowIndexValue) {
		this.lowIndexValue = lowIndexValue;
	}

	public String getClosingIndexValue() {
		return this.closingIndexValue;
	}

	public void setClosingIndexValue(final String closingIndexValue) {
		this.closingIndexValue = closingIndexValue;
	}

	public String getIndexDate() {
		return this.indexDate;
	}

	public void setIndexDate(final String indexDate) {
		this.indexDate = indexDate;
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
