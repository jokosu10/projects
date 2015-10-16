package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class InboundRoamersReportBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String operatorCountry;
	private String operatorName;
	private String imsi;
	private String msisdn;
	private String vlr;
	private String cellLac;
	private String cellId;
	private String geographicalArea;
	private String regionalArea;
	private String region;
	private String province;
	private String town;
	private String barangay;
	private String siteAddress;
	private String date;
	
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getCellLac() {
		return cellLac;
	}
	public void setCellLac(String cellLac) {
		this.cellLac = cellLac;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getGeographicalArea() {
		return geographicalArea;
	}
	public void setGeographicalArea(String geographicalArea) {
		this.geographicalArea = geographicalArea;
	}
	public String getRegionalArea() {
		return regionalArea;
	}
	public void setRegionalArea(String regionalArea) {
		this.regionalArea = regionalArea;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getBarangay() {
		return barangay;
	}
	public void setBarangay(String barangay) {
		this.barangay = barangay;
	}
	public String getSiteAddress() {
		return siteAddress;
	}
	public void setSiteAddress(String siteAddress) {
		this.siteAddress = siteAddress;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getVlr() {
		return vlr;
	}
	public void setVlr(String vlr) {
		this.vlr = vlr;
	}
	public String getOperatorCountry() {
		return operatorCountry;
	}
	public void setOperatorCountry(String operatorCountry) {
		this.operatorCountry = operatorCountry;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
