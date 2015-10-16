package com.smsgt.roamer_locator.struts2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.smsgt.roamer_locator.struts2.bean.RoamerLocationBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;

public class RoamwareFileReaderAndDBAccessor {
	
	private List<RoamerLocationBean> roamerLocationList = new ArrayList<RoamerLocationBean>();
	private static final Logger logger = Logger.getLogger(RoamwareFileReaderAndDBAccessor.class);
	
	public List<RoamerLocationBean> retrieveAllInfo(String file, String msisdn) {
		
		try {

			FileReader fReader = new FileReader(new File(file));
			BufferedReader bReader = new BufferedReader(fReader);
			RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		    sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
			
		    String next, line = bReader.readLine();
		    for (boolean first = true, last = (line == null); !last; first = false, line = next) {
              
		    	last = ((next = bReader.readLine()) == null);

                if(line != null && !line.trim().isEmpty()) {
                	
					String[] split = line.split(",",-1);
					String timestamp = split[0].replace("\"", "");
					String cellLac = split[4];
					String cellId = split[5];
					
						if(!cellId.equalsIgnoreCase("No returned cell id") && !cellLac.equalsIgnoreCase("No returned cell lac")) {
								RoamerLocationBean rBean = rDBAccess.getCellIdCoordinatesFromDB(cellLac, cellId);
								if(rBean != null) {
									rBean.setTimestamp(sdf.format(Long.parseLong(timestamp) * 1000));
									if(first) {
										rBean.setPosition("start point");
										first = false;
									} else if (last) {
										rBean.setPosition("end point");
										last = false;
									} 
								} else {
									rBean = new RoamerLocationBean();
									rBean.setPosition("");
									rBean.setCellName("Nothing found");
									rBean.setCellId(cellId.replaceAll("\"", ""));
									rBean.setCellLac(cellLac.replaceAll("\"", ""));
									rBean.setLatitude("Nothing found");
									rBean.setLongitude("Nothing found");
									rBean.setBarangay("Nothing found");
									rBean.setTown("Nothing found");
									rBean.setSite_address("Nothing found");
									rBean.setTimestamp(sdf.format(Long.parseLong(timestamp) * 1000));
								}
								
								roamerLocationList.add(rBean);
							
						} else {
							RoamerLocationBean rBean = new RoamerLocationBean();
							rBean.setPosition("");
							rBean.setCellName("Nothing found");
							rBean.setCellId("No returned cell id");
							rBean.setCellLac("No returned cell lac");
							rBean.setLatitude("Nothing found");
							rBean.setLongitude("Nothing found");
							rBean.setBarangay("Nothing found");
							rBean.setTown("Nothing found");
							rBean.setSite_address("Nothing found");
							rBean.setTimestamp(sdf.format(Long.parseLong(timestamp) * 1000));
							roamerLocationList.add(rBean);
						}
                }     
            }
			
			bReader.close();
			fReader.close();
		} catch(Exception x) {
			logger.error("retrieveAllInfo() Exception=>" + x.getMessage(), x);
			
		}
		
		
		return roamerLocationList;
	}

}
