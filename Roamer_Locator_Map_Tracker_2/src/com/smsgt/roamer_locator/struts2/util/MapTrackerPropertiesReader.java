package com.smsgt.roamer_locator.struts2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MapTrackerPropertiesReader {
	
	private static final Logger logger = Logger.getLogger(MapTrackerPropertiesReader.class);
	private String fileName;
	
	public MapTrackerPropertiesReader(String fileName){
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getValueFromProperty(String propertyKey) 
	{
		String propertyValue = "";
		Properties property = new Properties();
		InputStream is = null;
		try{
			is = MapTrackerPropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
			if(is == null){
				logger.error("getValueFromProperty() fileName: " + fileName +" file not found!");
				return null;
			}
			else {
				property.load(is);
				propertyValue = (String) property.get(propertyKey);	
			}
		} catch (IOException e) {
			logger.error("getValueFromProperty() IOException => " + e.getMessage());

		}finally{
			try {
				is.close();
			} catch (IOException e) {
				logger.error("getValueFromProperty() IOException =>" + e.getMessage());
			}
		}
		
		return propertyValue;
	}

}
