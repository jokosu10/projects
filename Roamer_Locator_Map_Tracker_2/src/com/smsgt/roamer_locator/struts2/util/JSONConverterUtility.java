package com.smsgt.roamer_locator.struts2.util;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONConverterUtility {
	
	private static final Logger logger = Logger.getLogger(JSONConverterUtility.class);
	
	public static String convertToJSONFormat(Object obj) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String resultStr = "";
		try {
			resultStr = objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("convertToJSONFormat() Exception=>" + e.getMessage(), e);
		}
		return resultStr;	
	}
}
