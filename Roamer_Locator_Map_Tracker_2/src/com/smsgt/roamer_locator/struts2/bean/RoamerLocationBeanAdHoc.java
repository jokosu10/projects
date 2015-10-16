package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;
import java.util.List;

public class RoamerLocationBeanAdHoc implements Serializable {

	private static final long serialVersionUID = 1L;
	private String operatorCountry;
	private String operatorName;
	private String imsi;
	private String msisdn;
	private List<RoamerLocationBean> roamerLocationBean;
	
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public List<RoamerLocationBean> getRoamerLocationBean() {
		return roamerLocationBean;
	}
	public void setRoamerLocationBean(List<RoamerLocationBean> roamerLocationBean) {
		this.roamerLocationBean = roamerLocationBean;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
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
