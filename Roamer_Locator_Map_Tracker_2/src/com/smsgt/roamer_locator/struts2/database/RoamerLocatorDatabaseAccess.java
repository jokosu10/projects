package com.smsgt.roamer_locator.struts2.database;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.smsgt.roamer_locator.struts2.bean.ImsiMsisdnForAdhocBean;
import com.smsgt.roamer_locator.struts2.bean.InboundRoamersBean;
import com.smsgt.roamer_locator.struts2.bean.InboundRoamersReportBean;
import com.smsgt.roamer_locator.struts2.bean.InboundRoamersReportFinalBean;
import com.smsgt.roamer_locator.struts2.bean.NetworkOperatorBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocationBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorScheduleBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUser;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUserLogs;
import com.smsgt.roamer_locator.struts2.util.MapTrackerPropertiesReader;

public class RoamerLocatorDatabaseAccess {
	
	private MapTrackerPropertiesReader mapTrackerPropertiesReader = new MapTrackerPropertiesReader("maptracker.properties");
	private static final Logger logger = Logger.getLogger(RoamerLocatorDatabaseAccess.class);
	
	/** @description create connection to database
	 *  @return Connection object
	 */
	
	public Connection createDBConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
		    String connectionUrl = mapTrackerPropertiesReader.getValueFromProperty("databaseString");
		    conn = DriverManager.getConnection(connectionUrl); 
		} catch(Exception e) {
			logger.error("createDBConnection() Exception => " + e.getMessage(), e);
		}
		return conn;
	}

	/** @description check if PSI is running
	 *  @return status if running or not
	 */
	public String checkIfPSIIsRunning() {
		Connection conn = null;
		String status = "NOT RUNNING";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.createDBConnection();
			String sql = "SELECT psi_status FROM roamer_locator_config";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				status = rs.getString(1).toUpperCase();
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("checkIfPSIIsRunning() SQLException => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkIfPSIIsRunning() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		
		logger.error("checkIfPSIIsRunning() result: " + status);	
		return status;
	}

	/** @description create a user
	 *  @return boolean true or false (success or failed)
	 */	
	public String createUser(String username, String password, String userType, String adminType, String createdBy, String fullName, String dateTime) {
		
		Connection conn = null;
		PreparedStatement ps = null, ps2 = null;
		String hashedPw = "", alreadyExist = "";
		boolean successfulCreate = false;
		try {
			conn = this.createDBConnection();
			
			// check first if username already exist
			String sqlCheckUname = "SELECT username FROM roamer_locator_users WHERE username = ?";
			ps = conn.prepareStatement(sqlCheckUname);
			ps.setString(1, username);
			ResultSet r = ps.executeQuery();
			if(r.next()) {
				alreadyExist = "Username already exist. Please try a new one!";
				successfulCreate = false;
			} else { // username is available
				String sql = "INSERT INTO roamer_locator_users (username, password, user_type, admin_type, created_by, orig_password, name, date_time_created, date_time_updated) VALUES (?,?,?,?,?,?,?,?,?)";
				hashedPw = md5(password);
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, username);
				ps2.setString(2, hashedPw);
				ps2.setString(3, userType);
				ps2.setString(4, adminType);
				ps2.setString(5, createdBy);
				ps2.setString(6, password);
				ps2.setString(7, fullName);
				ps2.setString(8, dateTime);
				ps2.setString(9, dateTime);
				ps2.executeUpdate();
				ps2.close();
				successfulCreate = true;
			}
			r.close();
			ps.close();
		} catch(Exception e) {
			logger.error("createUser() Exception => " + e.getMessage(), e);
			successfulCreate = false;
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("createUser() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		
		if(!alreadyExist.trim().isEmpty()) {
			return alreadyExist;
		} else {
			return String.valueOf(successfulCreate);
		}
	}
	
	/** @description update user details
	 *  @return boolean true or false (success or failed)
	 */
	public String updateUser(RoamerLocatorUser user) {
		boolean successfulUpdate = false;
		Connection conn = null;
		PreparedStatement ps = null, ps2 = null;
		String alreadyExist = "";
		try {
			conn = this.createDBConnection();
			
			// check first if username already exist
			String sqlCheckUname = "SELECT username,id FROM roamer_locator_users WHERE username = ?";
			ps = conn.prepareStatement(sqlCheckUname);
			ps.setString(1, user.getUserName());
			ResultSet r = ps.executeQuery();
			if(r.next()) {
				// check if same userid, if same allow edit of same username, else prompt user already exists
				if(r.getLong("id") == user.getId()) {
					
					String sqlUpdate = "UPDATE roamer_locator_users SET date_time_updated = ?, username = ?, password = ?, orig_password = ?, name = ? , admin_type = ?, user_type = ? , updated_by = ? WHERE id = ?";
					ps2 = conn.prepareStatement(sqlUpdate);
					
					ps2.setString(1, user.getDateTimeUpdated());
					ps2.setString(2, user.getUserName());
					ps2.setString(3, md5(user.getOrigPassword()));
					ps2.setString(4, user.getOrigPassword());
					ps2.setString(5, user.getName());
					ps2.setString(6, user.getAdminType());
					ps2.setString(7, user.getUserType());
					ps2.setString(8, user.getUpdatedBy());
					ps2.setLong(9, user.getId());
					ps2.executeUpdate();
					successfulUpdate = true;
					ps2.close();
				} else {
					alreadyExist = "Username already exist. Please try a new one!";
					successfulUpdate = false;
				}
			} else { // username is available
				String sqlUpdate = "UPDATE roamer_locator_users SET date_time_updated = ?, username = ?, password = ?, orig_password = ?, name = ? , admin_type = ?, user_type = ? , updated_by = ? WHERE id = ?";
				ps2 = conn.prepareStatement(sqlUpdate);
				
				ps2.setString(1, user.getDateTimeUpdated());
				ps2.setString(2, user.getUserName());
				ps2.setString(3, md5(user.getOrigPassword()));
				ps2.setString(4, user.getOrigPassword());
				ps2.setString(5, user.getName());
				ps2.setString(6, user.getAdminType());
				ps2.setString(7, user.getUserType());
				ps2.setString(8, user.getUpdatedBy());
				ps2.setLong(9, user.getId());
				ps2.executeUpdate();
				successfulUpdate = true;
				ps2.close();
			}
			r.close();
			ps.close();

		} catch(Exception e) {
			logger.error("updateUser() Exception => " + e.getMessage(), e);
			successfulUpdate = false;
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("updateUser() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		
		if(!alreadyExist.trim().isEmpty()) {
			return alreadyExist;
		} else {
			return String.valueOf(successfulUpdate);
		}	
	}

	/** @description delete a user
	 *  @return boolean true or false (success or failed)
	 */
	public String deleteUser(String userId) {
		boolean successfulDelete = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.createDBConnection();
			String sqlDelete = "DELETE FROM roamer_locator_users WHERE id = ?";
			ps = conn.prepareStatement(sqlDelete);
			ps.setInt(1, Integer.valueOf(userId));
			ps.executeUpdate();
			ps.close();
			successfulDelete = true;
		} catch(Exception e) {
			logger.error("deleteUser() Exception => " + e.getMessage(), e);
			successfulDelete = false;
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("deleteUser() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		return String.valueOf(successfulDelete);
	}

	/** @description returns all user
	 *  @return List of all users
	 */
	public List<RoamerLocatorUser> getAllUsers(String param, String userId) {
		Connection conn = null;
		PreparedStatement ps = null;
		List<RoamerLocatorUser> roamerLocatorUsersList = new ArrayList<RoamerLocatorUser>();
		try {
			conn = this.createDBConnection();
			String sqlSearch = "";
			if(param.trim().isEmpty()) {
				if(userId.equalsIgnoreCase("2")) {
					sqlSearch = "SELECT * FROM roamer_locator_users WHERE admin_type in (1,2,3)";
				} else {
					sqlSearch = "SELECT * FROM roamer_locator_users WHERE admin_type in (1,3)";
				}
				
				ps = conn.prepareStatement(sqlSearch);
			} else {
				if(userId.equalsIgnoreCase("2")) {
					sqlSearch = "SELECT * FROM roamer_locator_users WHERE admin_type in (1,2,3) AND ( username LIKE ? OR name LIKE ? )";
				} else {
					sqlSearch = "SELECT * FROM roamer_locator_users WHERE admin_type in (1,3) AND ( username LIKE ? OR name LIKE ? )";
				}
				ps = conn.prepareStatement(sqlSearch);
				String likeSearch = "%" + param;
				ps.setString(1, likeSearch);
				ps.setString(2, likeSearch);
			}
					
		
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				RoamerLocatorUser rUser = new RoamerLocatorUser();
				rUser.setId(rs.getLong("id"));
				rUser.setUserName(rs.getString("username"));
				rUser.setOrigPassword(rs.getString("orig_password"));
				rUser.setUserType(rs.getString("user_type"));
				rUser.setName(rs.getString("name"));
				roamerLocatorUsersList.add(rUser);
			}
			rs.close();
			ps.close();
		} catch(Exception e) {
			logger.error("searchUser() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("searchUser() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		return roamerLocatorUsersList;
	}	
	
	/** @description view all logs
	 *  @return list of RoamerLocatorUserLogs object
	 */
	public List<RoamerLocatorUserLogs> viewUserLogs(String adminType, String username, String action, String date) {
		
		Connection conn = null;
		List<RoamerLocatorUserLogs> roamerLocatorUserLogsList = new ArrayList<RoamerLocatorUserLogs>();
		String sql = "", addToQuery = "";
		try {
			conn = this.createDBConnection();
			
			if(username != null && !username.equalsIgnoreCase("")) {
				addToQuery = " AND name like '%" + username + "%'";
			} 
			
			if(action != null && !action.equalsIgnoreCase("")) {
				addToQuery += " AND action = '" + action + "'";
			}
			
			if(date != null && !date.equalsIgnoreCase("")) {
				addToQuery += " AND date_time_accessed like '%" + date + "%'";
			}
			
			if(adminType.equalsIgnoreCase("1")) {			
				sql = "SELECT * FROM roamer_locator_user_logs WHERE user_id IN (1, 3) " + addToQuery + " ORDER BY id DESC";
			} else if(adminType.equalsIgnoreCase("2")){
				sql = "SELECT * FROM roamer_locator_user_logs WHERE user_id IN (1, 2, 3) " + addToQuery + " ORDER BY id DESC";
			}
			
			Statement s = conn.prepareStatement(sql);
			ResultSet rs = s.executeQuery(sql);
			while(rs.next()) {
				RoamerLocatorUserLogs roamerLocatorUserLogs = new RoamerLocatorUserLogs();
				roamerLocatorUserLogs.setId(Long.valueOf(rs.getString("id")));
				roamerLocatorUserLogs.setUserId(rs.getString("user_id"));
				roamerLocatorUserLogs.setName(rs.getString("name"));
				roamerLocatorUserLogs.setAccessedPage(rs.getString("accessed_page"));
				roamerLocatorUserLogs.setAction(rs.getString("action"));
				roamerLocatorUserLogs.setDateTime(rs.getString("date_time_accessed"));
				roamerLocatorUserLogsList.add(roamerLocatorUserLogs);
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			logger.error("viewUserLogs() SQLException => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("viewUserLogs() Exception => " + e.getMessage(), e);
	             }
	          }	
		}	
		logger.error("viewUserLogs() params => adminType: " + adminType + ", username: " + username + ", action: " + action + ", date: " + date+ ", result: " + roamerLocatorUserLogsList.size());
		return roamerLocatorUserLogsList;
	}
	
	public void insertToUserLogs(String userId, String name, String action, String accessedPage, String dateTime) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.createDBConnection();
			String sqlInsert = "INSERT INTO roamer_locator_user_logs(user_id, name, action, accessed_page, date_time_accessed) VALUES(?,?,?,?,?)";
			ps = conn.prepareStatement(sqlInsert);
			ps.setString(1, userId);
			ps.setString(2, name);
			ps.setString(3, action);
			ps.setString(4, accessedPage);
			ps.setString(5, dateTime);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			logger.error("insertToUserLogs() SQLException => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("insertToUserLogs() Exception => " + e.getMessage(), e);
	             }
	          }	
		}	
	}
	
	public RoamerLocatorUser getUserType(String username, String password) {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		RoamerLocatorUser rUser = new RoamerLocatorUser();
		String md5Password = md5(password);
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT * FROM roamer_locator_users WHERE username = ? AND password = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,username);
			ps.setString(2, md5Password);
			rs = ps.executeQuery();
			if(rs.next()) {
				rUser.setAdminType(rs.getString("admin_type"));
				rUser.setId(rs.getLong("id"));
				rUser.setName(rs.getString("name"));
			} else {
				rUser.setAdminType("");
				rUser.setId(Long.valueOf(0));
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("getUserType() SQLException => " + e.getMessage(), e);
			rUser.setAdminType("");
			rUser.setId(Long.valueOf(0));
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getUserType() Exception => " + e.getMessage(), e);
	             }
	          }	
		}
		
		logger.error("getUserType() params username: " + username + ", password: " + password + ", result: " + rUser.getId() + ", " + rUser.getAdminType());
		return rUser;
	}
	
	public int getScheduleInterval() {
		
		Connection conn = null;
		int interval = 5; // default
		try {
			conn = this.createDBConnection();
			String sql = "SELECT time_interval FROM roamer_locator_config";
			Statement s = conn.prepareStatement(sql);
			ResultSet rs = s.executeQuery(sql);
			while(rs.next()) {
				interval = rs.getInt(1);
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			logger.error("getScheduleInterval() SQLException => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getScheduleInterval() Exception => " + e.getMessage(), e);
	             }
	          }	
		}	
		logger.error("getScheduleInterval() result: " + interval);
		return interval;
	}
	
	
	/**@description gets the equivalent IMSI from database for the MSISDN input
	 * @param msisdn
	 * @return String object
	 */		
	public String getImsiForMSISDNInput(String msisdn) {
		
		Connection conn = null;
		String imsi = "";
		try {
			conn = this.createDBConnection();
			String query = "SELECT IMSI FROM roamer_locator_table WHERE MSISDN = ?";
			PreparedStatement p = conn.prepareStatement(query);
			p.setString(1, msisdn);
			ResultSet r = p.executeQuery();
			logger.error(p.toString());
			while(r.next()) {
				imsi = r.getString(1);
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("getImsiForMSISDNInput() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getImsiForMSISDNInput() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("getImsiForMSISDNInput() params msisdn: " + msisdn + ", result imsi: " + imsi); 
		return imsi;
	}

	/**@description gets the equivalent MSISDN from database for the IMSI input
	 * @param imsi
	 * @return String object
	 */		
	public String getMSISDNForIMSIInput(String imsi) {
		
		Connection conn = null;
		String msisdn = "";
		try {
			conn = this.createDBConnection();
			String query = "SELECT MSISDN FROM roamer_locator_table WHERE IMSI = ?";
			PreparedStatement p = conn.prepareStatement(query);
			p.setString(1, imsi);
			logger.error(p.toString());
			ResultSet r = p.executeQuery();
			while(r.next()) {
				msisdn = r.getString(1);
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("getMSISDNForIMSIInput() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getMSISDNForIMSIInput() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getMSISDNForIMSIInput() params imsi: " + imsi + ", result msisdn: " + msisdn);
		return msisdn;
	}	
	
	/**@description gets the equivalent IMSI from database for the MSISDN input
	 * @param msisdn
	 * @return String object
	 */		
	public String getImsiForMSISDNInput2(String msisdn) {
		
		Connection conn = null;
		String imsi = "";
		try {
			conn = this.createDBConnection();
			String query = "SELECT IMSI FROM roamer_locator_table2 WHERE MSISDN = ?";
			PreparedStatement p = conn.prepareStatement(query);
			p.setString(1, msisdn);
			logger.error(p.toString());
			ResultSet r = p.executeQuery();
			
			while(r.next()) {
				imsi = r.getString(1);
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("getImsiForMSISDNInput2() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getImsiForMSISDNInput2() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("getImsiForMSISDNInput2() params msisdn: " + msisdn + ", result imsi: " + imsi);
		return imsi;
	}	
	
	/**@description gets the equivalent MSISDN from database for the IMSI input
	 * @param imsi
	 * @return String object
	 */		
	public String getMSISDNForIMSIInput2(String imsi) {
		
		Connection conn = null;
		String msisdn = "";
		try {
			conn = this.createDBConnection();
			String query = "SELECT MSISDN FROM roamer_locator_table2 WHERE IMSI = ?";
			PreparedStatement p = conn.prepareStatement(query);
			p.setString(1, imsi);
			logger.error(p.toString());
			ResultSet r = p.executeQuery();
			
			while(r.next()) {
				msisdn = r.getString(1);
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("getMSISDNForIMSIInput2() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getMSISDNForIMSIInput2() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("getMSISDNForIMSIInput2() params imsi: " + imsi + ", result msisdn: " + msisdn);
		return msisdn;
	}	
	
	/**@description gets all the operator from database and store it in a ArrayList object
	 * @param none
	 * @return ArrayList<String> operators
	 */
	public ArrayList<NetworkOperatorBean> returnAllOperator(String mode) {
		
		Connection conn = null;
		ArrayList<NetworkOperatorBean> operatorsFromDB = new ArrayList<NetworkOperatorBean>();
		try {
			conn = this.createDBConnection();
			String sql = "";
			if(mode.equalsIgnoreCase("all")) {
				sql = "SELECT * FROM roamer_locator_operators_table ORDER BY destination ASC";
			} else if (mode.equalsIgnoreCase("international")) {
				sql = "SELECT * FROM roamer_locator_operators_table ORDER BY destination ASC";
			} else if(mode.equalsIgnoreCase("local")) {
				sql = "SELECT * FROM roamer_locator_operators_table ORDER BY destination ASC";
			}
			
	        PreparedStatement ps = conn.prepareStatement(sql);
	        logger.error(ps.toString());
	        ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				NetworkOperatorBean nBean = new NetworkOperatorBean();
				nBean.setTadicCode(rs.getString(1).replaceAll("\"",""));
				nBean.setMncMcc(rs.getString(2));
				nBean.setDestination(rs.getString(3).replaceAll("\"",""));
				nBean.setNetworkOpName(rs.getString(4).replaceAll("\"",""));
				operatorsFromDB.add(nBean);
	        }
			rs.close();
			ps.close();
		} catch(Exception e) {
			logger.error("returnAllOperator() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("returnAllOperator() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		
		logger.error("returnAllOperator() params mode: " + mode +", result arraysize: " + operatorsFromDB.size());
		return operatorsFromDB;
	}	
		
	/**@description gets the filename from database
	 * @param imsi
	 * @return filename
	 */
	public String getRoamerLocationFromDB(String imsi, String msisdn) {
		
		String fileName = "";
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT filename FROM roamer_locator_table WHERE IMSI = ? AND MSISDN = ? ORDER BY time_stamp DESC LIMIT 1";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, imsi);
	        ps.setString(2, msisdn);
	        logger.error(ps.toString());
	        ResultSet rs = ps.executeQuery();
	        
			while (rs.next()) {
		           fileName = rs.getString(1);
	        }
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("getRoamerLocationFromDB() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("getRoamerLocationFromDB() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getRoamerLocationFromDB() params imsi: " + imsi + ", msisdn: " + msisdn + ", result : " + fileName);
		return fileName;
	}

	/**@description gets the filename from database
	 * @param imsi
	 * @return filename
	 */
	public String getRoamerLocationFromDB2(String imsi, String msisdn) {
		
		String fileName = "";
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT filename FROM roamer_locator_table2 WHERE IMSI = ? AND MSISDN = ? ORDER BY time_stamp DESC LIMIT 1";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, imsi);
	        ps.setString(2, msisdn);
	        logger.error(ps.toString());
	        ResultSet rs = ps.executeQuery();
	        
			while (rs.next()) {
		           fileName = rs.getString(1);
	        }
			rs.close();
			ps.close();
		} catch (SQLException e) {
			logger.error("getRoamerLocationFromDB2() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("getRoamerLocationFromDB2() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getRoamerLocationFromDB2() params imsi: " + imsi + ", msisdn: "+ msisdn +", result : " + fileName);
		return fileName;
	}	
	

	/**@description gets the values from database and store it in a RoamerLocationBean object
	 * @param cellId
	 * @return RoamerLocationBean object
	 */
	public RoamerLocationBean getCellIdCoordinatesFromDB(String cellLac, String cellId) {
		
		RoamerLocationBean roamerLocatorBean = null;
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT CELL_ID, CELL_NAME, CELL_LAC, LONGITUDE, LATITUDE, TOWN, BARANGAY, SITE_ADDRESS from roamer_locator_cellid_lookup_table WHERE CELL_ID = ? AND CELL_LAC = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, cellId.replaceAll("\"", ""));
	        ps.setString(2, cellLac.replaceAll("\"", ""));
	        logger.error(ps.toString());
	        ResultSet rs = ps.executeQuery();
	        
			while (rs.next()) {
				  
		           roamerLocatorBean = new RoamerLocationBean();
		           roamerLocatorBean.setPosition("");
		           roamerLocatorBean.setCellId(rs.getString("CELL_ID"));
		           roamerLocatorBean.setCellName(rs.getString("CELL_NAME"));
		           roamerLocatorBean.setCellLac(rs.getString("CELL_LAC"));
		           roamerLocatorBean.setLongitude(rs.getString("LONGITUDE"));
		           roamerLocatorBean.setLatitude(rs.getString("LATITUDE"));
		           roamerLocatorBean.setTown(rs.getString("TOWN"));
		           roamerLocatorBean.setBarangay(rs.getString("BARANGAY"));
		           roamerLocatorBean.setSite_address(rs.getString("SITE_ADDRESS"));
	        }
			rs.close();
			ps.close();
		} catch(Exception e) {
			logger.error("getCellIdCoordinatesFromDB() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getCellIdCoordinatesFromDB() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		
		logger.error("getCellIdCoordinatesFromDB() params cellLac : " + cellLac + ", cellid: " + cellId + ", result object: " + (roamerLocatorBean != null? roamerLocatorBean.toString() : null));
		return roamerLocatorBean;
	}

	/**@description gets all the IMSI from database and store it in a ArrayList object
	 * @param none
	 * @return ArrayList<String> imsi
	 */
	public List<ImsiMsisdnForAdhocBean> returnAllImsi(String imsi) {
		
		List<ImsiMsisdnForAdhocBean> imsiFromDB = new ArrayList<ImsiMsisdnForAdhocBean>();
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			imsi = imsi + "%";
			String sql = "SELECT IMSI from roamer_locator_table where IMSI like ? ";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, imsi);
	        logger.error(ps.toString());
	        ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				  ImsiMsisdnForAdhocBean imsiMsisdnForAdhocBean = new ImsiMsisdnForAdhocBean();
				  imsiMsisdnForAdhocBean.setImsi(rs.getString(1));
				  //imsiMsisdnForAdhocBean.setMsisdn(rs.getString(2));
		          imsiFromDB.add(imsiMsisdnForAdhocBean);
	        }
			rs.close();
			ps.close();
		} catch(Exception e) {
			logger.error("returnAllImsi() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("returnAllImsi() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("returnAllImsi() params imsi: " + imsi + ", result arraysize: " + imsiFromDB.size());
		return imsiFromDB;
	}
	
	public void insertCampaignSchedule(String scheduleDate, String scheduleTime, String triggerType, String id, String dateCreated, String timeCreated) {
		
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			
			boolean validDateTimeSchedule = validateDateTimeEntry(scheduleDate, scheduleTime);
			boolean validDateTimeCreation = validateDateTimeEntry(dateCreated, timeCreated);
			
			if(validDateTimeSchedule && validDateTimeCreation) {
				
				if(triggerType.equalsIgnoreCase("insert")) {
					
					String query = "SELECT * FROM roamer_locator_scheduler WHERE status = 'PAUSED' ORDER BY id DESC LIMIT 1";
					PreparedStatement p = conn.prepareStatement(query);
					ResultSet r = p.executeQuery();
					
					while(r.next()) {
						String query2 = "UPDATE roamer_locator_scheduler SET status = 'FINISHED' where id = ?";
						PreparedStatement p2 = conn.prepareStatement(query2);
						p2.setInt(1,r.getInt("id"));
						p2.executeUpdate();
						p2.close();
					}
					
					r.close();
					p.close();
										
					String sql = "INSERT INTO roamer_locator_scheduler(date,time,status,date_created,time_created,tag) VALUES (?,?,?,?,?,?)";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, scheduleDate);
					ps.setString(2, scheduleTime);
					ps.setString(3, "PENDING");
					ps.setString(4, dateCreated);
					ps.setString(5, timeCreated);
					ps.setInt(6, 0);
					ps.executeUpdate();
					ps.close();		
					
				} else if (triggerType.equalsIgnoreCase("update")) {
					
					String sql = "UPDATE roamer_locator_scheduler SET date = ?, time = ? , date_created = ?, time_created = ? , tag = 0 WHERE id = " + id;
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, scheduleDate);
					ps.setString(2, scheduleTime);
					ps.setString(3, dateCreated);
					ps.setString(4, timeCreated);
					ps.executeUpdate();
					ps.close();
				}
			}
		} catch(Exception e) {
			logger.error("insertCampaignSchedule() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("insertCampaignSchedule() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("insertCampaignSchedule() params scheduleDate: " + scheduleDate + ", scheduleTime: " + scheduleTime + ", triggerType: " + triggerType + ", id: " + id +" , dateCreated: "+ dateCreated +", timeCreated: " + timeCreated);
	}
	
	// validates date and time input
	private boolean validateDateTimeEntry(String date, String time) {
		
		boolean validDateTime = false;
		
		String monthDigit = date.substring(0, date.indexOf("/"));
		if(monthDigit.length() < 2) {
			date = "0" + date; 
		}
		
		boolean datematch = date.matches("^[0-9/]+");
		boolean timematch = time.matches("^[0-9:]+");
		
		if(datematch && timematch) {
			// check if valid date
			if(date.length() == 10 && time.length() == 5 ) {
				
				String month = date.substring(0,2);
				String day = date.substring(3,5);
				String year = date.substring(6, date.length());
				
				if(Integer.parseInt(month) > 0 && Integer.parseInt(month) < 13) { // month is 1 to 12
					// valid month
					
					if(Integer.parseInt(day) > 0 && Integer.parseInt(day) <= 32) { // day is from 1 to 31
						// valid day
						
						if(Integer.parseInt(year) == Calendar.getInstance().get(Calendar.YEAR)) { // year is equal to current year
							// valid year
							// check if valid time
							String hr = time.substring(0,2);
							String minute = time.substring(3,5);
							
							if(Integer.parseInt(hr) >= 0 && Integer.parseInt(hr) < 24) { // valid hour
								
								if(Integer.parseInt(minute) >= 0 && Integer.parseInt(minute) < 60) { // valid time
									validDateTime = true;
								}
							}
						}
					}
				}	
			}
		}
		
		return validDateTime;
	}
	
	public String pauseResumeCampaign(String type) {

		Connection conn = null;
		String ret = "";
		
		try {
			conn = this.createDBConnection();
			
			String query = "SELECT count(*) FROM roamer_locator_scheduler WHERE (status = 'IN USE' or status = 'PAUSED')";
			PreparedStatement p = conn.prepareStatement(query);
			ResultSet r = p.executeQuery();
			int cnt = 0;
			while(r.next()) {
				cnt = r.getInt(1);
				if(cnt > 0) {
					if(type.equalsIgnoreCase("pause")) {
						
						String sql = "UPDATE roamer_locator_scheduler SET status = ? WHERE status = 'IN USE' ";
						PreparedStatement ps = conn.prepareStatement(sql);
						ps.setString(1, "PAUSED");
						ps.executeUpdate();
						ps.close();
						
					} else if (type.equalsIgnoreCase("resume")) {
						
						String sql = "UPDATE roamer_locator_scheduler SET status = ? WHERE status = 'PAUSED' ";
						PreparedStatement ps = conn.prepareStatement(sql);
						ps.setString(1, "IN USE");
						ps.executeUpdate();
						ps.close();
					}
				} else {
					ret = "Cannot perform operation as there's no schedule to be pause/resume";
				}
				
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("pauseResumeCampaign() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("pauseResumeCampaign() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("pauseResumeCampaign() params type: " + type + ", result: " + ret);
		return ret;
	}
	
	public RoamerLocatorScheduleBean checkForExistingCampaign() {
		
		Connection conn = null;
		RoamerLocatorScheduleBean rSchedBean = null;
		
		try {
			conn = this.createDBConnection();
			String sqlCheck = "SELECT * FROM roamer_locator_scheduler ORDER BY id DESC LIMIT 1";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlCheck);
			while(rs.next()) {
				rSchedBean = new RoamerLocatorScheduleBean();
				rSchedBean.setId(Integer.toString(rs.getInt(1)));
				rSchedBean.setDate(rs.getString(2));
				rSchedBean.setTime(rs.getString(3));
				rSchedBean.setStatus(rs.getString(4));
				rSchedBean.setDate_executed(rs.getString(5));
				rSchedBean.setDate_created(rs.getString(6));
				rSchedBean.setTime_created(rs.getString(7));
			}
			rs.close();
			st.close();
			
		} catch(Exception e) {
			logger.error("checkForExistingCampaign() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkForExistingCampaign() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("checkForExistingCampaign() result object: " + (rSchedBean != null ? rSchedBean.toString() : null));
		return rSchedBean;
	}
	
	public RoamerLocatorScheduleBean checkForCurrentCampaign() {
		
		Connection conn = null;
		RoamerLocatorScheduleBean rSchedBean = null;
		
		try {
			conn = this.createDBConnection();
			String sqlCheck = "SELECT * FROM roamer_locator_scheduler WHERE (status = 'IN USE' OR status = 'PAUSED') ORDER BY id DESC LIMIT 1";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlCheck);
			while(rs.next()) {
				rSchedBean = new RoamerLocatorScheduleBean();
				rSchedBean.setId(Integer.toString(rs.getInt(1)));
				rSchedBean.setDate(rs.getString(2));
				rSchedBean.setTime(rs.getString(3));
				rSchedBean.setStatus(rs.getString(4));
				rSchedBean.setDate_executed(rs.getString(5));
				rSchedBean.setDate_created(rs.getString(6));
				rSchedBean.setTime_created(rs.getString(7));
			}
			rs.close();
			st.close();
			
		} catch(Exception e) {
			logger.error("checkForCurrentCampaign() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkForCurrentCampaign() Exception => " + e.getMessage(), e);
	             }
	          }
		}	
		logger.error("checkForCurrentCampaign() result object: " + (rSchedBean !=  null ? rSchedBean.toString() : null ));
		return rSchedBean;
	}
	
	public Boolean checkIfAdHocIsRunning(String timestamp) {
		
		Boolean isRunning = false;
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sqlCheck = "SELECT count(*) FROM roamer_locator_adhoc_table WHERE status = 0 AND from_unixtime(time_stamp,'%m/%d/%Y') = ?";
			PreparedStatement st = conn.prepareStatement(sqlCheck);
			st.setString(1,timestamp);
			logger.error(st.toString());
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Integer count = new Integer(rs.getInt(1));
				if(count > 0) {
					isRunning = true;
				}
			}
			rs.close();
			st.close();
			
		} catch(Exception e) {
			logger.error("checkIfAdHocIsRunning() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkIfAdHocIsRunning() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("checkIfAdHocIsRunning() params timestamp: " + timestamp + ", result: " +  isRunning); 
		return isRunning;
	}
	
	public void insertToAdhocTable(String imsi, String msisdn, String vlr, String timestamp) {
		
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			
			String sql = "INSERT INTO roamer_locator_adhoc_table(IMSI,MSISDN,VLR,time_stamp,status) VALUES (?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, imsi);
			ps.setString(2, msisdn);
			ps.setString(3, vlr);
			ps.setString(4, timestamp);
			ps.setInt(5, 0);
			ps.executeUpdate();
			ps.close();
					       			
		} catch(Exception e) {
			logger.error("insertToAdhocTable() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("insertToAdhocTable() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("insertToAdhocTable() params imsi: " + imsi + ", msisdn: "+ msisdn +", vlr: " + vlr+ ", timestamp: " + timestamp);
	}

	public Integer checkAdHocEntryIfToday(String date) {
		
		Connection conn = null;
		Integer count = 0;
		try {
			conn = this.createDBConnection();
			String dateCheckerQuery = "SELECT count(*) FROM roamer_locator_adhoc_table where from_unixtime(time_stamp,'%m/%d/%Y') = ? ";
			PreparedStatement p = conn.prepareStatement(dateCheckerQuery);
			p.setString(1, date);
			ResultSet r = p.executeQuery();
			while(r.next()) {
				count = new Integer(r.getInt(1));
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("checkAdHocEntryIfToday() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkAdHocEntryIfToday() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("checkAdHocEntryIfToday() params date: " + date + ", result count: " + count);
		return count;
	}
	
	public ArrayList<ImsiMsisdnForAdhocBean> viewAdHocResults(String date) {
		
		ArrayList<ImsiMsisdnForAdhocBean> adhocBeanList = new ArrayList<ImsiMsisdnForAdhocBean>();
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT * FROM roamer_locator_adhoc_table WHERE status != 0 AND from_unixtime(time_stamp,'%m/%d/%Y') = ? GROUP BY IMSI, MSISDN";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,date);
			ResultSet rs = ps.executeQuery();
			logger.error(ps.toString());
			while(rs.next()) {
				ImsiMsisdnForAdhocBean imsiMsisdnForAdhocBean = new ImsiMsisdnForAdhocBean();
				imsiMsisdnForAdhocBean.setImsi(rs.getString("IMSI"));
				imsiMsisdnForAdhocBean.setMsisdn(rs.getString("MSISDN"));
				adhocBeanList.add(imsiMsisdnForAdhocBean);
			} 
			rs.close();
			ps.close();
			
		} catch(Exception e) {
			logger.error("viewAdHocResults() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("viewAdHocResults() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("viewAdHocResults() params date: " + date + ", result arraysize: " + adhocBeanList.size());
		return adhocBeanList;
	}

	/*
	 * @description gets the values from database and store it in a InboundRoamersBean object
	 * @param location
	 * @return InboundRoamersBean object
	 */	
	public ArrayList<InboundRoamersBean> getInboundRoamersInformation(String location, String column) {
		
		ArrayList<InboundRoamersBean> inboundRoamersBeanList = new ArrayList<InboundRoamersBean>();
		Connection conn = null;
		String qString = "";
		
		String latestTimestamp = this.returnLatestDateTimeForInboundMapCount();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		latestTimestamp = sdf.format(Long.parseLong(latestTimestamp) * 1000);
		
		try {
			conn = this.createDBConnection();
			String sql = "";
			if(column.trim().isEmpty()) {
				if(location.trim().equalsIgnoreCase("All")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
				} else if(location.trim().equalsIgnoreCase("unknown")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND (a.GEOGRAPHICAL_AREA = '' OR a.GEOGRAPHICAL_AREA IS NULL) AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
				} else if (location.trim().equalsIgnoreCase("LUZON") || location.trim().equalsIgnoreCase("VISAYAS") || location.trim().equalsIgnoreCase("MINDANAO")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.GEOGRAPHICAL_AREA = ? AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location;
				} else if(location.trim().equalsIgnoreCase("NCR") || location.trim().equalsIgnoreCase("CEBU") || location.trim().equalsIgnoreCase("ILOILO") || location.trim().equalsIgnoreCase("DAVAO")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.PROVINCE like ? AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location + "%";
				} else {
					String province = returnProvince(location);
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.PROVINCE = '"+ province +"' AND a.SITE_ADDRESS like ? AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = "%" + location + "%";
				}
			} else {
				if(column.trim().equalsIgnoreCase("province")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.PROVINCE like ? AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = "%" + location + "%";
				} else if(column.trim().equalsIgnoreCase("town")) {
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table a, roamer_locator_operators_table b WHERE (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND a.TOWN like ? AND a.type = 'c' AND from_unixtime(a.timestamp,'%d/%m/%Y') = '"+ latestTimestamp + "' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location + "%";
				}
			}

			PreparedStatement ps = conn.prepareStatement(sql);
			
			if(!qString.trim().isEmpty()) {
				ps.setString(1, qString);
			}
			
			logger.error(ps.toString());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				
		           InboundRoamersBean inboundRoamersBean = new InboundRoamersBean();
		           inboundRoamersBean.setInboundRoamersLocation(location);
		           inboundRoamersBean.setInboundRoamersTotalCount(rs.getString(1));
		           inboundRoamersBean.setOperatorName(rs.getString(2));
		           inboundRoamersBean.setOperatorCountry(rs.getString(3));
		           inboundRoamersBeanList.add(inboundRoamersBean);
		           
	        }
			
			rs.close();
			ps.close();
		
		} catch (SQLException e) {
			logger.error("getInboundRoamersInformation() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersInformation() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getInboundRoamersInformation() params location: " + location + ", column: " + column + ", result arraysize: " + inboundRoamersBeanList.size());
		return inboundRoamersBeanList;
	}  	
	
	public String returnLatestDateTimeForInboundMapCount() {
		
		Connection conn = null;
		String timestamp = "";
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT max(TIMESTAMP) FROM inbound_roamers_table";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				timestamp = rs.getString(1);
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			logger.error("returnLatestDateTimeForInboundMapCount() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("returnLatestDateTimeForInboundMapCount() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("returnLatestDateTimeForInboundMapCount() results: " + timestamp);
		return timestamp;
	}


	public ArrayList<InboundRoamersReportFinalBean> getInboundRoamersReport2(String toQuery, boolean isNetwork, String dateFrom, String dateTo, String location, boolean isProvincial) {
		
		ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportArrayList = new ArrayList<InboundRoamersReportFinalBean>();
		
		String filter1 = "", filter2 = "", columnLookup = "", columnLike ="", filter = "";
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			PreparedStatement p = null;
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			String totalRoamerCount = "", sqlForRoamerCount = "";
			
			if(location.equalsIgnoreCase("all")) {
				// location with all
				if(toQuery.equalsIgnoreCase("all")) {
					
					String query = "", query2 = "";
					boolean isSameDate = false;
					
					if(dateFrom.equalsIgnoreCase(dateTo)) {
						sqlForRoamerCount = "SELECT count(distinct imsi) as COUNT FROM inbound_roamers_history WHERE DATE like ? AND type = 'c' ";
						query = "SELECT count(distinct a.imsi) as COUNT, a.GEOGRAPHICAL_AREA, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a.DATE like ? AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.IMSI,1,6) OR b.mnc_mcc = SUBSTRING(a.IMSI,1,5)) GROUP BY a.GEOGRAPHICAL_AREA, b.mnc_mcc ORDER BY COUNT DESC";
						isSameDate = true;
					} else {
						sqlForRoamerCount = "SELECT count(distinct imsi) as COUNT FROM inbound_roamers_history WHERE ( DATE BETWEEN ? AND ? ) AND type = 'c' ";
						query = "SELECT count(distinct a.imsi) as COUNT, a.GEOGRAPHICAL_AREA, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE ( a.DATE BETWEEN ? AND ? ) AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.IMSI,1,6) OR b.mnc_mcc = SUBSTRING(a.IMSI,1,5)) GROUP BY a.GEOGRAPHICAL_AREA, b.mnc_mcc ORDER BY COUNT DESC";
						isSameDate = false;
					}
					
					p = conn.prepareStatement(sqlForRoamerCount);
					
					if(!isSameDate) {
						
						dateFrom = dateFrom + " 00:00:00";
						dateTo = dateTo + " 23:59:59";
						
						p.setString(1,dateFrom);
						p.setString(2,dateTo);
					} else {
						dateFrom = dateFrom + "%";
						p.setString(1,dateFrom);
					}
					
					ResultSet r = p.executeQuery();
					logger.error(p.toString());
					while(r.next()) {
						totalRoamerCount = r.getString(1);
					}
					
					r.close();
					p.close();
					
					ps = conn.prepareStatement(query);
						
					if(!isSameDate) {
						ps.setString(1,dateFrom);
						ps.setString(2,dateTo);
					} else {
						ps.setString(1,dateFrom);
					}
					
					logger.error(ps.toString());
					ResultSet rs = ps.executeQuery();
					
					while(rs.next()) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
						
						iFinalBean.setInboundRoamersLocation(rs.getString(2));
						iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
			            iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
			            
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						
			            if(isSameDate) {
							query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a.GEOGRAPHICAL_AREA = ? AND a.DATE like ? AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND b.mnc_mcc = ?  GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
						} else {
							query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a.GEOGRAPHICAL_AREA = ? AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) AND b.mnc_mcc = ? GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";	
						}
			            
			            ps2 = conn.prepareStatement(query2);
						ps2.setString(1, rs.getString(2));
						
						if(!isSameDate) {
							ps2.setString(2, dateFrom);
							ps2.setString(3, dateTo);
							ps2.setString(4, rs.getString(3));
						} else {
							ps2.setString(2, dateFrom);
							ps2.setString(3, rs.getString(3));
						}
						
						logger.error(ps2.toString());
						ResultSet rs2 = ps2.executeQuery();
						
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
					        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
					        iBean.setOperatorName(rs2.getString("OPERATOR"));
							inboundRoamersBeanList.add(iBean);
							
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
					}
					
					rs.close();
					ps.close();
												
				} else {
					// either from dropdown or imsi input
					
					String queryAppend = "";
					
					if(toQuery.length() > 5) {
						queryAppend = "(b.mnc_mcc = '" + toQuery.substring(0,6) + "' OR b.mnc_mcc = '" + toQuery.substring(0,5) + "')";
					} else {
						queryAppend = "b.mnc_mcc = '" + toQuery + "'";
					}
					
					if(location.equalsIgnoreCase("All")) {
						
						String query = "", query2 = "";
						boolean isSameDate = false;
						
						if(dateFrom.equalsIgnoreCase(dateTo)) {
							sqlForRoamerCount = "SELECT count(distinct imsi) as COUNT FROM inbound_roamers_history WHERE (IMSI like ? OR MSISDN like ?) AND DATE like ? AND type = 'c' ";
							query = "SELECT count(distinct a.imsi) as COUNT, a.GEOGRAPHICAL_AREA,b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.IMSI like ? OR a.MSISDN like ?) AND a.DATE like ? AND a.type = 'c' AND " + queryAppend + " GROUP BY a.GEOGRAPHICAL_AREA ORDER BY COUNT DESC";
							isSameDate = true;
						} else {
							sqlForRoamerCount = "SELECT count(distinct imsi) as COUNT FROM inbound_roamers_history WHERE (IMSI like ? OR MSISDN like ?) AND ( DATE BETWEEN ? AND ? ) AND type = 'c' ";
							query = "SELECT count(distinct a.imsi) as COUNT, a.GEOGRAPHICAL_AREA, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.IMSI like ? OR a.MSISDN like ?) AND ( a.DATE BETWEEN ? AND ? ) AND a.type = 'c' AND " + queryAppend + " GROUP BY a.GEOGRAPHICAL_AREA ORDER BY COUNT DESC";
							isSameDate = false;
						}
						
						toQuery = toQuery + "%";
						
						p = conn.prepareStatement(sqlForRoamerCount);
						p.setString(1, toQuery);
						p.setString(2, toQuery);
						
						if(!isSameDate) {
							
							dateFrom = dateFrom + " 00:00:00";
							dateTo = dateTo + " 23:59:59";
							
							p.setString(3,dateFrom);
							p.setString(4,dateTo);
						} else {
							dateFrom = dateFrom + "%";
							p.setString(3,dateFrom);
						}
						
						logger.error(p.toString());
						ResultSet r = p.executeQuery();
						
						while(r.next()) {
							totalRoamerCount = r.getString(1);
						}
						
						r.close();
						p.close();
						
						ps = conn.prepareStatement(query);
						ps.setString(1, toQuery);
						ps.setString(2, toQuery);
						
						if(!isSameDate) {
							ps.setString(3, dateFrom);
							ps.setString(4, dateTo);
						} else {
							dateFrom = dateFrom + "%";
							ps.setString(3, dateFrom);
						}
						
						logger.error(ps.toString());
						ResultSet rs = ps.executeQuery();
						
						while(rs.next()) {
							
							InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
							iFinalBean.setInboundRoamersLocation(rs.getString(2));
							iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
							iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
				            
				            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
							if(isSameDate) {
								query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a.GEOGRAPHICAL_AREA = ? AND (a.IMSI like ? OR a.MSISDN like ?)  AND a.DATE like ?  AND a.type = 'c' AND " + queryAppend + "  GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
								
							} else {
								query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a.GEOGRAPHICAL_AREA = ? AND (a.IMSI like ? OR a.MSISDN like ?)  AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND " + queryAppend + " GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
								
							}
				          
							ps2 = conn.prepareStatement(query2);
							
							ps2.setString(1, rs.getString(2));
							ps2.setString(2, toQuery);
							ps2.setString(3, toQuery);
							
							if(!isSameDate) {
								ps2.setString(4, dateFrom);
								ps2.setString(5, dateTo);
							} else {
								ps2.setString(4, dateFrom);
							}
							
							logger.error(ps2.toString());
							ResultSet rs2 = ps2.executeQuery();
							
							while(rs2.next()) {
								
								InboundRoamersReportBean iBean = new InboundRoamersReportBean();
						        iBean.setImsi(rs2.getString(2));
						        iBean.setMsisdn(rs2.getString(3));
						        iBean.setVlr(rs2.getString(4));
						        iBean.setCellLac(rs2.getString(5));
						        iBean.setCellId(rs2.getString(6));
						        iBean.setGeographicalArea(rs2.getString(7));
						        iBean.setRegionalArea(rs2.getString(8));
						        iBean.setRegion(rs2.getString(9));
						        iBean.setProvince(rs2.getString(10));
						        iBean.setTown(rs2.getString(11));
						        iBean.setBarangay(rs2.getString(12));
						        iBean.setSiteAddress(rs2.getString(13));
						        iBean.setDate(rs2.getString(15));
						        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
						        iBean.setOperatorName(rs2.getString("OPERATOR"));
								inboundRoamersBeanList.add(iBean);	
							}
							
							iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
							inboundRoamersReportArrayList.add(iFinalBean);
							
							rs2.close();
							ps2.close();
								
						}
						
						rs.close();
						ps.close();
							

												
					} else {
						// this could be LUZON, VISAYAS, etc
						
						boolean isSameDate = false;
						String sql = "";
						
						if(isProvincial) {
							columnLookup = "GEOGRAPHICAL_AREA like ? ";
							columnLike = "%" + location + "%";
							filter = "PROVINCE";
						} else {
							columnLookup = "PROVINCE like ? ";
							columnLike = "%" + location + "%";
							filter = "TOWN";
						}
						
						if(isNetwork) {
							filter1 = "like ? ";
							filter2 = toQuery + "%";
						} else {
							filter1 = "= ? ";
							filter2 = toQuery;
						}
						
						if(dateFrom.equalsIgnoreCase(dateTo)) {
							sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND DATE like ? AND type = 'c'";
							sql = "SELECT count(distinct imsi)," + filter + " FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND DATE like ? AND type = 'c' GROUP BY " +  filter + " ORDER BY " + filter + " ASC";
							isSameDate = true;
						} else {
							sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND type = 'c'";
							sql = "SELECT count(distinct imsi)," + filter + " FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND type = 'c' GROUP BY " +  filter + " ORDER BY " + filter + " ASC";
							isSameDate = false;
						}
						
						p = conn.prepareStatement(sqlForRoamerCount);
						p.setString(1, columnLike);
						p.setString(2, filter2);
						p.setString(3, filter2);
						
						if(!isSameDate) {
							
							dateFrom = dateFrom + " 00:00:00";
							dateTo = dateTo + " 23:59:59";
							
							p.setString(4,dateFrom);
							p.setString(5,dateTo);
						} else {
							dateFrom = dateFrom + "%";
							p.setString(4,dateFrom);
						}
						
						logger.error(p.toString());
						ResultSet r = p.executeQuery();
						
						while(r.next()) {
							totalRoamerCount = r.getString(1);
						}
						
						r.close();
						p.close();
						
						
						ps = conn.prepareStatement(sql);
						ps.setString(1,columnLike);
						ps.setString(2,filter2);
						ps.setString(3,filter2);
						
						if(!isSameDate) {
							ps.setString(4,dateFrom);
							ps.setString(5,dateTo);
						} else {
							ps.setString(4,dateFrom);
						}
						
						logger.error(ps.toString());
						ResultSet rs = ps.executeQuery();
						
						while (rs.next()) {
							
				            InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
				            iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
				            iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
				            iFinalBean.setInboundRoamersLocation(rs.getString(2));
					         
				           // query all base on filter
				            String sql2 = "";
				            if(isSameDate) {
				            	sql2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR from inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND a.DATE like ? AND a." + filter +"='" + rs.getString(2) + "' AND a.type = 'c' AND " + queryAppend + " GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
				            	//isSameDate = true;
				            } else {
				            	sql2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR from inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND (a.DATE BETWEEN ? AND ?) AND a." + filter +"='" + rs.getString(2) + "' AND a.type = 'c' AND "+ queryAppend +" GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
				            	//isSameDate = false;
				            }
				            
				            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
							ps2 = conn.prepareStatement(sql2);
							ps2.setString(1, columnLike);
							ps2.setString(2, filter2);
							ps2.setString(3, filter2);
							
							if(!isSameDate) {
								ps2.setString(4, dateFrom);
								ps2.setString(5, dateTo);
							} else {
								ps2.setString(4, dateFrom);
							}
							
							logger.error(ps2.toString());
							ResultSet rs2 = ps2.executeQuery();
							
							
							while(rs2.next()) {
								
								InboundRoamersReportBean iBean = new InboundRoamersReportBean();
						        iBean.setImsi(rs2.getString(2));
						        iBean.setMsisdn(rs2.getString(3));
						        iBean.setVlr(rs2.getString(4));
						        iBean.setCellLac(rs2.getString(5));
						        iBean.setCellId(rs2.getString(6));
						        iBean.setGeographicalArea(rs2.getString(7));
						        iBean.setRegionalArea(rs2.getString(8));
						        iBean.setRegion(rs2.getString(9));
						        iBean.setProvince(rs2.getString(10));
						        iBean.setTown(rs2.getString(11));
						        iBean.setBarangay(rs2.getString(12));
						        iBean.setSiteAddress(rs2.getString(13));
						        iBean.setDate(rs2.getString(15));
						        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
						        iBean.setOperatorName(rs2.getString("OPERATOR"));
								inboundRoamersBeanList.add(iBean);
							
							}
							
							rs2.close();
							ps2.close();
							iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
							inboundRoamersReportArrayList.add(iFinalBean);
								
				        }
						rs.close();
						ps.close(); 	
					}
				}
			} else {
				// location without all
				String query = "", query2 = "", queryAppend = "", queryAppend2 = "";
				boolean isSameDate = false;
				
				if(toQuery.equalsIgnoreCase("all")) {
					
					if(isProvincial) {
						columnLookup = "GEOGRAPHICAL_AREA like ? ";
						columnLike = "%" + location + "%";
						filter = "PROVINCE";
					} else {
						columnLookup = "PROVINCE like ? ";
						columnLike = "%" + location + "%";
						filter = "TOWN";
					}
					
					if(dateFrom.equalsIgnoreCase(dateTo)) {
						sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND DATE like ? AND type = 'c'";
						query = "SELECT count(distinct imsi) as COUNT, b.operator_name as OPERATOR,b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND a.DATE like ? AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) GROUP BY OPERATOR ORDER BY a.DATE ASC";
						query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND a.DATE like ? AND a.type = 'c' AND (SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?) AND b.mnc_mcc = ? GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
						isSameDate = true;
					} else {
						sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND (DATE BETWEEN ? AND ?) AND type = 'c'";
						query = "SELECT count(distinct a.imsi) as COUNT, b.operator_name as OPERATOR, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND (b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5)) GROUP BY OPERATOR ORDER BY a.DATE ASC";
						query2 = "SELECT a.*,b.destination as COUNTRY ,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND (SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?) AND b.mnc_mcc = ? GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
						isSameDate = false;
					}
					
					p = conn.prepareStatement(sqlForRoamerCount);
					p.setString(1, columnLike);
					
					if(!isSameDate) {
						
						dateFrom = dateFrom + " 00:00:00";
						dateTo = dateTo + " 23:59:59";
						
						p.setString(2,dateFrom);
						p.setString(3,dateTo);
					} else {
						dateFrom = dateFrom + "%";
						p.setString(2,dateFrom);
					}
					
					logger.error(p.toString());
					ResultSet r = p.executeQuery();
					
					while(r.next()) {
						totalRoamerCount = r.getString(1);
					}
					
					r.close();
					p.close();
					
					ps = conn.prepareStatement(query);
					ps.setString(1, columnLike);
					
					if(!isSameDate) {
						ps.setString(2, dateFrom);
						ps.setString(3, dateTo);
					} else {
						ps.setString(2, dateFrom);
					}
					
					logger.error(ps.toString());
					ResultSet rs = ps.executeQuery();
					
					while(rs.next()) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
						iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
			            iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(location);
						
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						ps2 = conn.prepareStatement(query2);
						ps2.setString(1, columnLike);
						
						if(!isSameDate) {
							ps2.setString(2, dateFrom);
							ps2.setString(3, dateTo);
							ps2.setString(4, rs.getString(3));
							ps2.setString(5, rs.getString(3));
							ps2.setString(6, rs.getString(3));
						} else {
							ps2.setString(2, dateFrom);
							ps2.setString(3, rs.getString(3));
							ps2.setString(4, rs.getString(3));
							ps2.setString(5, rs.getString(3));
						}
						
						logger.error(ps2.toString());
						ResultSet rs2 = ps2.executeQuery();
						
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
					        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
					        iBean.setOperatorName(rs2.getString("OPERATOR"));
							inboundRoamersBeanList.add(iBean);
							
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
						
						
					}
					rs.close();
					ps.close();
					
				} else {
					// either from dropdown or imsi input
					String sql = "";
					
					if(isProvincial) {
						columnLookup = "GEOGRAPHICAL_AREA like ? ";
						columnLike = "%" + location + "%";
						filter = "PROVINCE";
					} else {
						columnLookup = "PROVINCE like ? ";
						columnLike = "%" + location + "%";
						filter = "TOWN";
					}
					
					if(isNetwork) {
						filter1 = "like ? ";
						filter2 = toQuery + "%";
						queryAppend = "b.mnc_mcc = '" + toQuery + "'" ;
						queryAppend2 = "(SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?)";
					} else {
						filter1 = "= ? ";
						filter2 = toQuery;
						queryAppend = "(b.mnc_mcc = '" + toQuery.substring(0,6) + "' OR b.mnc_mcc = ' " + toQuery.substring(0,5)+ "')";
						queryAppend2 = "(SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?)";
					}
					
					if(dateFrom.equalsIgnoreCase(dateTo)) {
						sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND DATE like ?  AND type = 'c'";
						sql = "SELECT count(distinct a.imsi),a." + filter + ", b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND a.DATE like ?  AND a.type = 'c' AND " + queryAppend + " GROUP BY a." +  filter + " ORDER BY a." + filter + " ASC";
						isSameDate = true;
					} else {
						sqlForRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND type = 'c'";
						sql = "SELECT count(distinct a.imsi),a." + filter + ", b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND "+ queryAppend +" GROUP BY a." +  filter + " ORDER BY a." + filter + " ASC";
						isSameDate = false;
					}
					
					p = conn.prepareStatement(sqlForRoamerCount);
					p.setString(1,columnLike);
					p.setString(2,filter2);
					p.setString(3,filter2);
					
					if(!isSameDate) {
						
						dateFrom = dateFrom + " 00:00:00";
						dateTo = dateTo + " 23:59:59";
						
						p.setString(4,dateFrom);
						p.setString(5,dateTo);
					} else {
						dateFrom = dateFrom + "%";
						p.setString(4,dateFrom);
					}
					
					logger.error(p.toString());
					ResultSet r = p.executeQuery();
					
					while(r.next()) {
						totalRoamerCount = r.getString(1);
					}
					
					r.close();
					p.close();
					
					
					ps = conn.prepareStatement(sql);
					ps.setString(1,columnLike);
					ps.setString(2,filter2);
					ps.setString(3,filter2);
					
					if(!isSameDate) {
						ps.setString(4,dateFrom);
						ps.setString(5,dateTo);
					} else {
						ps.setString(4,dateFrom);
					}
					
					logger.error(ps.toString());
					ResultSet rs = ps.executeQuery();
					
					while (rs.next()) {
						
			            InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
			            iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(rs.getString(2));
				         
			           // query all base on filter
			            String sql2 = "";
			            if(isSameDate) {
			            	sql2 = "SELECT a.*, b.destination as COUNTRY ,b.operator_name as OPERATOR from inbound_roamers_history a,roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND a.DATE like ?  AND a.type = 'c' AND a." + filter +"='" + rs.getString(2) + "' AND " + queryAppend + "AND " + queryAppend2 + " AND (b.mnc_mcc = ?) GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
			            	//isSameDate = true;
			            } else {
			            	sql2 = "SELECT a.*, b.destination as COUNTRY ,b.operator_name as OPERATOR from inbound_roamers_history a, roamer_locator_operators_table b WHERE a." + columnLookup + " AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND a." + filter +"='" + rs.getString(2) + "' AND " + queryAppend + " AND "+ queryAppend2 + " AND (b.mnc_mcc = ?) GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
			            	//isSameDate = false;
			            }
			            
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						ps2 = conn.prepareStatement(sql2);
						ps2.setString(1, columnLike);
						ps2.setString(2, filter2);
						ps2.setString(3, filter2);
						
						if(!isSameDate) {
							ps2.setString(4, dateFrom);
							ps2.setString(5, dateTo);
							ps2.setString(6, rs.getString(3));
							ps2.setString(7, rs.getString(3));
							ps2.setString(8, rs.getString(3));
						} else {
							ps2.setString(4, dateFrom);
							ps2.setString(5, rs.getString(3));
							ps2.setString(6, rs.getString(3));
							ps2.setString(7, rs.getString(3));
						}
						
						
						logger.error(ps2.toString());
						ResultSet rs2 = ps2.executeQuery();	
						
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
					        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
					        iBean.setOperatorName(rs2.getString("OPERATOR"));
							inboundRoamersBeanList.add(iBean);
						
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
							
			        }
					rs.close();
					ps.close(); 
					
				}
			}
			
		} catch(Exception e) {
			logger.error("getInboundRoamersReport2() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersReport2() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getInboundRoamersReport2() params toQuery: "+ toQuery +", isNetwork: "+ isNetwork +", dateFrom: "+ dateFrom +", dateTo: "+ dateTo +", location: "+ location +", isProvincial: " + isProvincial + ", results arraysize: " + inboundRoamersReportArrayList.size());
		return inboundRoamersReportArrayList;
		
	}	

	public ArrayList<InboundRoamersReportFinalBean> getInboundRoamersReportForTouristDestination2(
			 String touristSpot, String province, String toQuery, boolean isNetwork, String dateFrom, String dateTo) {
		
		ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportForTouristDestinationArrayList = new ArrayList<InboundRoamersReportFinalBean>();
		Connection conn = null;
		
		try {
			
			String filter1 = "", filter2 = "", site_address = "", queryAppend = "";
			site_address = "%" + touristSpot + "%";
			
			if(isNetwork) {
				filter1 = "like ? ";
				filter2 = toQuery + "%";
				if(toQuery.equalsIgnoreCase("All")) {
					queryAppend = "(b.mnc_mcc = SUBSTRING(a.imsi,1,6) OR b.mnc_mcc = SUBSTRING(a.imsi,1,5))";
				} else {
					queryAppend = "b.mnc_mcc = '" + toQuery + "'" ;
				}
				
			} else {
				filter1 = "= ? ";
				filter2 = toQuery;
				queryAppend = "(b.mnc_mcc = '" + toQuery.substring(0,6) + "' OR b.mnc_mcc = ' " + toQuery.substring(0,5)+ "')";
			}
			
			conn = this.createDBConnection();
			
			String sql = "", sql2 = "";
			boolean isSameDate = false;
			
			PreparedStatement p, ps, ps2;
			
			String totalRoamerCount = "", sqlForTotalRoamerCount = "";
			
			if(toQuery.equalsIgnoreCase("all")) {
				
				if(dateFrom.equalsIgnoreCase(dateTo)) {
					sqlForTotalRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND DATE like ? AND type = 'c'";
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name as OPERATOR, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?) AND a.DATE like ? AND a.type = 'c' AND " + queryAppend + " GROUP BY OPERATOR ORDER BY COUNT DESC";
					
					sql2 = "SELECT a.*,b.destination as COUNTRY,b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?)  AND a.DATE like ?  AND a.type = 'c' AND (SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?) AND b.mnc_mcc = ? GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
					isSameDate = true;
				} else {
					sqlForTotalRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND ( DATE BETWEEN ? AND ? ) AND type = 'c'";
					sql = "SELECT count(distinct a.imsi) as COUNT, b.operator_name as OPERATOR, b.mnc_mcc FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?) AND (a.DATE BETWEEN ? AND ? ) AND a.type = 'c' AND " + queryAppend + " GROUP BY OPERATOR ORDER BY COUNT DESC";
					
					sql2 = "SELECT a.*,b.destination as COUNTRY, b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?)  AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND (SUBSTRING(a.imsi,1,6) = ? OR SUBSTRING(a.imsi,1,5) = ?) AND b.mnc_mcc = ? GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
					isSameDate = false;
				}
				
				p = conn.prepareStatement(sqlForTotalRoamerCount);
				p.setString(1, province);
				p.setString(2, site_address);
				
				
				if(!isSameDate) {
					
					dateFrom = dateFrom + " 00:00:00";
					dateTo = dateTo + " 23:59:59";
					
					p.setString(3, dateFrom);
					p.setString(4, dateTo);
				} else {
					dateFrom = dateFrom + "%";
					p.setString(3, dateFrom);
				}
				
				logger.error(p.toString());
				ResultSet r = p.executeQuery();
				
				while(r.next()) {
					totalRoamerCount = r.getString(1);
				}
				
				r.close();
				p.close();
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, province);
				ps.setString(2, site_address);
				
				ps2 = conn.prepareStatement(sql2);
				ps2.setString(1, province);
				ps2.setString(2, site_address);
				
				
				if(!isSameDate) {
					ps.setString(3, dateFrom);
					ps.setString(4, dateTo);
				} else {
					ps.setString(3, dateFrom);
				}
				
				logger.error(ps.toString());
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					
					if(!rs.getString(1).equalsIgnoreCase("0")) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            
						iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(touristSpot);
			            iFinalBean.setInboundRoamersTotalCount(totalRoamerCount);
			            
				        // query all base on filter
			            ArrayList<InboundRoamersReportBean> inboundRoamersForTouristDestinationBeanList = new ArrayList<InboundRoamersReportBean>();
						
			            if(!isSameDate) {
			            	ps2.setString(3, dateFrom);
							ps2.setString(4, dateTo);
							ps2.setString(5, rs.getString(3));
							ps2.setString(6, rs.getString(3));
							ps2.setString(7, rs.getString(3));
							
			            } else {
			            	ps2.setString(3, dateFrom);
			            	ps2.setString(4, rs.getString(3));
							ps2.setString(5, rs.getString(3));
							ps2.setString(6, rs.getString(3));
			            }
			            
			            logger.error(ps2.toString());
			            ResultSet rs2 = ps2.executeQuery();
						
						while(rs2.next()) {
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString("IMSI"));
					        iBean.setMsisdn(rs2.getString("MSISDN"));
					        iBean.setVlr(rs2.getString("VLR"));
					        iBean.setCellLac(rs2.getString("CELL_LAC"));
					        iBean.setCellId(rs2.getString("CELL_ID"));
					        iBean.setGeographicalArea(rs2.getString("GEOGRAPHICAL_AREA"));
					        iBean.setRegionalArea(rs2.getString("REGIONAL_AREA"));
					        iBean.setRegion(rs2.getString("REGION"));
					        iBean.setProvince(rs2.getString("PROVINCE"));
					        iBean.setTown(rs2.getString("TOWN"));
					        iBean.setBarangay(rs2.getString("BARANGAY"));
					        iBean.setSiteAddress(rs2.getString("SITE_ADDRESS"));
					        iBean.setDate(rs2.getString("DATE"));
					        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
					        iBean.setOperatorName(rs2.getString("OPERATOR"));
					        inboundRoamersForTouristDestinationBeanList.add(iBean);
						
						}
						
						iFinalBean.setInboundRoamersBeanList(inboundRoamersForTouristDestinationBeanList);
						inboundRoamersReportForTouristDestinationArrayList.add(iFinalBean);
						
						rs2.close();
					 }
				   }
				
				ps2.close();
				rs.close();
				ps.close();
				
			} else {
				
				if(dateFrom.equalsIgnoreCase(dateTo)) {
					//sqlForTotalRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND DATE like ? AND type = 'c'";
					sql = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND DATE like ? AND type = 'c'";
					
					sql2 = "SELECT a.*, b.destination as COUNTRY, b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?) AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND a.DATE like ? AND a.type = 'c' AND " + queryAppend + " GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
					isSameDate = true;
				} else {
					//sqlForTotalRoamerCount = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND ( DATE BETWEEN ? AND ? ) AND type = 'c'";
					sql = "SELECT count(distinct imsi) FROM inbound_roamers_history WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ? ) AND type = 'c'";
					
					sql2 = "SELECT a.*, b.destination as COUNTRY, b.operator_name as OPERATOR FROM inbound_roamers_history a, roamer_locator_operators_table b WHERE (a.PROVINCE = ? AND a.SITE_ADDRESS like ?) AND (a.IMSI " + filter1 + " OR a.MSISDN " + filter1 + ") AND (a.DATE BETWEEN ? AND ?) AND a.type = 'c' AND " + queryAppend + " GROUP BY a.imsi, (SELECT max(timestamp) FROM inbound_roamers_history) ORDER BY a.DATE ASC";
					isSameDate = false;
				}
				
				/* p = conn.prepareStatement(sqlForTotalRoamerCount);
				p.setString(1, province);
				p.setString(2, site_address);
				
				
				if(!isSameDate) {
					p.setString(3, dateFrom);
					p.setString(4, dateTo);
				} else {
					dateFrom = dateFrom + "%";
					p.setString(3, dateFrom);
				}
				
				ResultSet r = p.executeQuery();
				logger.error(p.toString());
				
				while(r.next()) {
					totalRoamerCount = r.getString(1);
				} */
				
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, province);
				ps.setString(2, site_address);
				ps.setString(3, filter2);
				ps.setString(4, filter2);
				
					
				ps2 = conn.prepareStatement(sql2);
				ps2.setString(1, province);
				ps2.setString(2, site_address);
				ps2.setString(3, filter2);
				ps2.setString(4, filter2);
				
				
				if(!isSameDate) {
					
					dateFrom = dateFrom + " 00:00:00";
					dateTo = dateTo + " 23:59:59";
					
					ps.setString(5, dateFrom);
					ps.setString(6, dateTo);
					
					ps2.setString(5, dateFrom);
					ps2.setString(6, dateTo);
					
				} else {
					dateFrom = dateFrom + "%";
					ps.setString(5, dateFrom);
					
					ps2.setString(5, dateFrom);
				}
				
				logger.error(ps.toString());
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					
					if(!rs.getString(1).equalsIgnoreCase("0")) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            
						iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
						iFinalBean.setInboundRoamersCountPerOperator(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(touristSpot);
			            
				        // query all base on filter
			            ArrayList<InboundRoamersReportBean> inboundRoamersForTouristDestinationBeanList = new ArrayList<InboundRoamersReportBean>();
						ResultSet rs2 = ps2.executeQuery();
						logger.error(ps2.toString());
						
						while(rs2.next()) {
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
					        iBean.setOperatorCountry(rs2.getString("COUNTRY"));
					        iBean.setOperatorName(rs2.getString("OPERATOR"));
					        inboundRoamersForTouristDestinationBeanList.add(iBean);
						
						}
						
						iFinalBean.setInboundRoamersBeanList(inboundRoamersForTouristDestinationBeanList);
						inboundRoamersReportForTouristDestinationArrayList.add(iFinalBean);
						
						rs2.close();
					 }
				   }
				
				ps2.close();
				rs.close();
				ps.close();
					
			}
				
		} catch(Exception e) {
			logger.error("getInboundRoamersReportForTouristDestination2() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersReportForTouristDestination2() Exception => " + e.getMessage(), e);
	             }
	          }
		}
	
		logger.error("getInboundRoamersReportForTouristDestination2() params touristSpot: " + touristSpot + ", province: " + province + ", toQuery: " + toQuery + ", isNetwork: " + isNetwork + ", dateFrom: " + dateFrom + ", dateTo: " + dateTo + ", result arraysize: " + inboundRoamersReportForTouristDestinationArrayList.size());
		return inboundRoamersReportForTouristDestinationArrayList;
		
	}	
	
	public String[] returnCountryAndOperatorName(String imsi) {
		
		String[] retValue = new String[2];
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String query = "SELECT destination,operator_name FROM roamer_locator_operators_table WHERE (mnc_mcc like concat('%'," + imsi.substring(0,6) +",'%') OR mnc_mcc like concat('%'," + imsi.substring(0,5) +",'%'))";
			PreparedStatement p = conn.prepareStatement(query);
			ResultSet r = p.executeQuery();
			logger.error(p.toString());
			while(r.next()) {
				retValue[0] = r.getString(1);
				retValue[1] = r.getString(2);
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("returnCountryAndOperatorName() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("returnCountryAndOperatorName() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		
		return retValue;
	}
	
	
	/***** FOR LOCAL IMSI QUERY *****/
	
	public void insertToAdhocTableLocal(String imsi, String msisdn, String vlr, String timestamp, String sessionId) {
		
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			
			String sql = "INSERT INTO roamer_locator_adhoc_table2(IMSI,MSISDN,VLR,time_stamp,status,user_id) VALUES (?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, imsi);
			ps.setString(2, msisdn);
			ps.setString(3, vlr);
			ps.setString(4, timestamp);
			ps.setInt(5, 0);
			ps.setInt(6, Integer.parseInt(sessionId));
			ps.executeUpdate();
			ps.close();
					       			
		} catch(Exception e) {
			logger.error("insertToAdhocTableLocal() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("insertToAdhocTableLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("insertToAdhocTableLocal() params imsi: " + imsi + ", msisdn: "+ msisdn +", vlr: " + vlr+ ", timestamp: " + timestamp);
	}

	public Integer checkAdHocEntryIfTodayLocal(String date) {
		
		Connection conn = null;
		int count = 0;
		try {
			conn = this.createDBConnection();
			String dateCheckerQuery = "SELECT count(*) FROM roamer_locator_adhoc_table2 where from_unixtime(time_stamp,'%m/%d/%Y') = ? ";
			PreparedStatement p = conn.prepareStatement(dateCheckerQuery);
			p.setString(1, date);
			ResultSet r = p.executeQuery();
			while(r.next()) {
				count = new Integer(r.getInt(1));
			}
			r.close();
			p.close();
			
		} catch(Exception e) {
			logger.error("checkAdHocEntryIfTodayLocal() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("checkAdHocEntryIfTodayLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("checkAdHocEntryIfTodayLocal() params date: " + date + ", result count: " + count);
		return count;
	}
	
	public ArrayList<ImsiMsisdnForAdhocBean> viewAdHocResultsLocal(String date, String uId) {
		
		ArrayList<ImsiMsisdnForAdhocBean> adhocBeanList = new ArrayList<ImsiMsisdnForAdhocBean>();
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT * FROM roamer_locator_adhoc_table2 WHERE status != 0 AND from_unixtime(time_stamp,'%m/%d/%Y') = ? AND user_id = ? GROUP BY IMSI, MSISDN";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,date);
			ps.setInt(2, Integer.parseInt(uId));
			ResultSet rs = ps.executeQuery();
			logger.error(ps.toString());
			while(rs.next()) {
				ImsiMsisdnForAdhocBean imsiMsisdnForAdhocBean = new ImsiMsisdnForAdhocBean();
				imsiMsisdnForAdhocBean.setImsi(rs.getString("IMSI"));
				imsiMsisdnForAdhocBean.setMsisdn(rs.getString("MSISDN"));
				adhocBeanList.add(imsiMsisdnForAdhocBean);
			} 
			rs.close();
			ps.close();
			
		} catch(Exception e) {
			logger.error("viewAdHocResultsLocal() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("viewAdHocResultsLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("viewAdHocResultsLocal() params date: " + date + ", result arraysize: " + adhocBeanList.size());
		return adhocBeanList;
	}

	/*
	 * @description gets the values from database and store it in a InboundRoamersBean object
	 * @param location
	 * @return InboundRoamersBean object
	 */	
	public ArrayList<InboundRoamersBean> getInboundRoamersInformationLocal(String location, String column) {
		
		ArrayList<InboundRoamersBean> inboundRoamersBeanList = new ArrayList<InboundRoamersBean>();
		Connection conn = null;
		String qString = "";
		
		try {
			conn = this.createDBConnection();
			String sql = "";
			if(column.trim().isEmpty()) {
				if(location.trim().equalsIgnoreCase("All")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
				} else if(location.trim().equalsIgnoreCase("unknown")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND (GEOGRAPHICAL_AREA = '' OR GEOGRAPHICAL_AREA IS NULL) AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
				} else if (location.trim().equalsIgnoreCase("LUZON") || location.trim().equalsIgnoreCase("VISAYAS") || location.trim().equalsIgnoreCase("MINDANAO")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND GEOGRAPHICAL_AREA = ? AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location;
				} else if(location.trim().equalsIgnoreCase("NCR") || location.trim().equalsIgnoreCase("CEBU") || location.trim().equalsIgnoreCase("ILOILO") || location.trim().equalsIgnoreCase("DAVAO")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND PROVINCE like ? AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location + "%";
				} else {
					String province = returnProvince(location);
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND PROVINCE = '"+ province +"' AND SITE_ADDRESS like ? AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = "%" + location + "%";
				}
			} else {
				if(column.trim().equalsIgnoreCase("province")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND PROVINCE like ? AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = "%" + location + "%";
				} else if(column.trim().equalsIgnoreCase("town")) {
					sql = "SELECT count(a.imsi) as COUNT, b.operator_name, b.destination FROM inbound_roamers_table2 a, roamer_locator_operators_table b WHERE a.imsi like concat('%',b.mnc_mcc,'%') AND TOWN like ? AND a.type = 'c' GROUP BY b.operator_name ORDER BY COUNT DESC";
					qString = location + "%";
				}
			}

			PreparedStatement ps = conn.prepareStatement(sql);
			
			if(!qString.trim().isEmpty()) {
				ps.setString(1, qString);
			}
			
			ResultSet rs = ps.executeQuery();
			logger.error(ps.toString());
			
			while (rs.next()) {
				
		           InboundRoamersBean inboundRoamersBean = new InboundRoamersBean();
		           inboundRoamersBean.setInboundRoamersLocation(location);
		           inboundRoamersBean.setInboundRoamersTotalCount(rs.getString(1));
		           inboundRoamersBean.setOperatorName(rs.getString(2));
		           inboundRoamersBean.setOperatorCountry(rs.getString(3));
		           inboundRoamersBeanList.add(inboundRoamersBean);
		           
	        }
			
			rs.close();
			ps.close();
		
		} catch (SQLException e) {
			logger.error("getInboundRoamersInformationLocal() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersInformationLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getInboundRoamersInformationLocal() params location: " + location + ", column: " + column + ", result arraysize: " + inboundRoamersBeanList.size());
		return inboundRoamersBeanList;
	}  	
	
	public String returnLatestDateTimeForInboundMapCountLocal() {
		
		Connection conn = null;
		String timestamp = "";
		
		try {
			conn = this.createDBConnection();
			String sql = "SELECT max(TIMESTAMP) FROM inbound_roamers_table2";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				timestamp = rs.getString(1);
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			logger.error("returnLatestDateTimeForInboundMapCountLocal() SQLException => " + e.getMessage(), e);
		} finally {
	         if (conn != null) {
	             try {
	                conn.close();
	             } catch (Exception e) {
	            	 logger.error("returnLatestDateTimeForInboundMapCountLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("returnLatestDateTimeForInboundMapCountLocal() results: " + timestamp);
		return timestamp;
	}


	public ArrayList<InboundRoamersReportFinalBean> getInboundRoamersReportLocal(String toQuery, boolean isNetwork, String dateFrom, String dateTo, String location, boolean isProvincial) {
		
		ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportArrayList = new ArrayList<InboundRoamersReportFinalBean>();
		
		String filter1 = "", filter2 = "", columnLookup = "", columnLike ="", filter = "";
		Connection conn = null;
		
		try {
			conn = this.createDBConnection();
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			
			if(location.equalsIgnoreCase("all")) {
				// location with all
				if(toQuery.equalsIgnoreCase("all")) {
					
					String query = "SELECT count(*) as COUNT, GEOGRAPHICAL_AREA FROM inbound_roamers_history2 WHERE DATE BETWEEN ? AND ? AND TYPE='c' GROUP BY GEOGRAPHICAL_AREA ORDER BY COUNT DESC";
					String query2 = "SELECT * FROM inbound_roamers_history2 WHERE GEOGRAPHICAL_AREA = ? AND (DATE BETWEEN ? AND ?) AND TYPE='c' ";
					
					ps = conn.prepareStatement(query);
					ps.setString(1,dateFrom);
					ps.setString(2,dateTo);
					
					ResultSet rs = ps.executeQuery();
					
					logger.error(ps.toString());
					
					while(rs.next()) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(rs.getString(2));
						
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						ps2 = conn.prepareStatement(query2);
						ps2.setString(1, rs.getString(2));
						ps2.setString(2, dateFrom);
						ps2.setString(3, dateTo);
						ResultSet rs2 = ps2.executeQuery();
						
						logger.error(ps2.toString());
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
							inboundRoamersBeanList.add(iBean);
							
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
						
					}
					
					rs.close();
					ps.close();
					
				} else {
					// either from dropdown or imsi input
					
					if(location.equalsIgnoreCase("All")) {
						
						String query = "SELECT count(*) as COUNT, GEOGRAPHICAL_AREA FROM inbound_roamers_history2 WHERE (IMSI like ? OR MSISDN like ?) AND (DATE BETWEEN ? AND ?) AND TYPE='c' GROUP BY GEOGRAPHICAL_AREA ORDER BY COUNT DESC";
						String query2 = "SELECT * FROM inbound_roamers_history2 WHERE GEOGRAPHICAL_AREA = ? AND (IMSI like ? OR MSISDN like ?)  AND (DATE BETWEEN ? AND ?) AND TYPE='c' ";
						
						toQuery = toQuery + "%";
						
						ps = conn.prepareStatement(query);
						ps.setString(1, toQuery);
						ps.setString(2, toQuery);
						ps.setString(3, dateFrom);
						ps.setString(4, dateTo);
						
						ResultSet rs = ps.executeQuery();
						logger.error(ps.toString());
						
						while(rs.next()) {
							
							InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
				            iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
				            iFinalBean.setInboundRoamersLocation(rs.getString(2));
							
				            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
							ps2 = conn.prepareStatement(query2);
							ps2.setString(1, rs.getString(2));
							ps2.setString(2, toQuery);
							ps2.setString(3, toQuery);
							ps2.setString(4, dateFrom);
							ps2.setString(5, dateTo);
							ResultSet rs2 = ps2.executeQuery();
							
							logger.error(ps2.toString());
							while(rs2.next()) {
								
								InboundRoamersReportBean iBean = new InboundRoamersReportBean();
						        iBean.setImsi(rs2.getString(2));
						        iBean.setMsisdn(rs2.getString(3));
						        iBean.setVlr(rs2.getString(4));
						        iBean.setCellLac(rs2.getString(5));
						        iBean.setCellId(rs2.getString(6));
						        iBean.setGeographicalArea(rs2.getString(7));
						        iBean.setRegionalArea(rs2.getString(8));
						        iBean.setRegion(rs2.getString(9));
						        iBean.setProvince(rs2.getString(10));
						        iBean.setTown(rs2.getString(11));
						        iBean.setBarangay(rs2.getString(12));
						        iBean.setSiteAddress(rs2.getString(13));
						        iBean.setDate(rs2.getString(15));
								inboundRoamersBeanList.add(iBean);
								
							}
							
							rs2.close();
							ps2.close();
							iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
							inboundRoamersReportArrayList.add(iFinalBean);
							
						}
						
						rs.close();
						ps.close();
						
					} else {
						// this could be LUZON, VISAYAS, etc
						if(isProvincial) {
							columnLookup = "GEOGRAPHICAL_AREA like ? ";
							columnLike = "%" + location + "%";
							filter = "PROVINCE";
						} else {
							columnLookup = "PROVINCE like ? ";
							columnLike = "%" + location + "%";
							filter = "TOWN";
						}
						
						if(isNetwork) {
							filter1 = "like ? ";
							filter2 = toQuery + "%";
						} else {
							filter1 = "= ? ";
							filter2 = toQuery;
						}
						
						String sql = "SELECT count(*)," + filter + " FROM inbound_roamers_history2 WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND TYPE='c' GROUP BY " +  filter + " ORDER BY " + filter + " ASC";
						ps = conn.prepareStatement(sql);
						ps.setString(1,columnLike);
						ps.setString(2,filter2);
						ps.setString(3,filter2);
						ps.setString(4,dateFrom);
						ps.setString(5,dateTo);
						
						ResultSet rs = ps.executeQuery();
						logger.error(ps.toString());
						while (rs.next()) {
							
				            InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
				            iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
				            iFinalBean.setInboundRoamersLocation(rs.getString(2));
					         
				           // query all base on filter
				            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
							String sql2 = "SELECT * from inbound_roamers_history2 WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND TYPE='c' AND " + filter +"='" + rs.getString(2) + "' ORDER BY DATE ASC";
							ps2 = conn.prepareStatement(sql2);
							ps2.setString(1, columnLike);
							ps2.setString(2, filter2);
							ps2.setString(3, filter2);
							ps2.setString(4, dateFrom);
							ps2.setString(5, dateTo);
							ResultSet rs2 = ps2.executeQuery();
							logger.error(ps2.toString());
							
							while(rs2.next()) {
								
								InboundRoamersReportBean iBean = new InboundRoamersReportBean();
						        iBean.setImsi(rs2.getString(2));
						        iBean.setMsisdn(rs2.getString(3));
						        iBean.setVlr(rs2.getString(4));
						        iBean.setCellLac(rs2.getString(5));
						        iBean.setCellId(rs2.getString(6));
						        iBean.setGeographicalArea(rs2.getString(7));
						        iBean.setRegionalArea(rs2.getString(8));
						        iBean.setRegion(rs2.getString(9));
						        iBean.setProvince(rs2.getString(10));
						        iBean.setTown(rs2.getString(11));
						        iBean.setBarangay(rs2.getString(12));
						        iBean.setSiteAddress(rs2.getString(13));
						        iBean.setDate(rs2.getString(15));
								inboundRoamersBeanList.add(iBean);
							
							}
							
							rs2.close();
							ps2.close();
							iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
							inboundRoamersReportArrayList.add(iFinalBean);
								
				        }
						rs.close();
						ps.close(); 	
					}
				}
			} else {
				// location without all
				if(toQuery.equalsIgnoreCase("all")) {
					
					if(isProvincial) {
						columnLookup = "GEOGRAPHICAL_AREA like ? ";
						columnLike = "%" + location + "%";
						filter = "PROVINCE";
					} else {
						columnLookup = "PROVINCE like ? ";
						columnLike = "%" + location + "%";
						filter = "TOWN";
					}
					
					String query = "SELECT count(*) as COUNT FROM inbound_roamers_history2 WHERE " + columnLookup + " AND (DATE BETWEEN ? AND ?) AND TYPE='c' ORDER BY DATE ASC";
					String query2 = "SELECT * FROM inbound_roamers_history2 WHERE " + columnLookup + " AND (DATE BETWEEN ? AND ?) AND TYPE='c' ";
					
					ps = conn.prepareStatement(query);
					ps.setString(1, columnLike);
					ps.setString(2, dateFrom);
					ps.setString(3, dateTo);
					ResultSet rs = ps.executeQuery();
					logger.error(ps.toString());
					
					while(rs.next()) {
						
						InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(location);
						
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						ps2 = conn.prepareStatement(query2);
						ps2.setString(1, columnLike);
						ps2.setString(2, dateFrom);
						ps2.setString(3, dateTo);
						ResultSet rs2 = ps2.executeQuery();
						
						logger.error(ps2.toString());
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
							inboundRoamersBeanList.add(iBean);
							
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
						
						
					}
					rs.close();
					ps.close();
					
				} else {
					// either from dropdown or imsi input
					
					if(isProvincial) {
						columnLookup = "GEOGRAPHICAL_AREA like ? ";
						columnLike = "%" + location + "%";
						filter = "PROVINCE";
					} else {
						columnLookup = "PROVINCE like ? ";
						columnLike = "%" + location + "%";
						filter = "TOWN";
					}
					
					if(isNetwork) {
						filter1 = "like ? ";
						filter2 = toQuery + "%";
					} else {
						filter1 = "= ? ";
						filter2 = toQuery;
					}
					
					String sql = "SELECT count(*)," + filter + " FROM inbound_roamers_history2 WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND TYPE='c' GROUP BY " +  filter + " ORDER BY " + filter + " ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1,columnLike);
					ps.setString(2,filter2);
					ps.setString(3,filter2);
					ps.setString(4,dateFrom);
					ps.setString(5,dateTo);
					
					ResultSet rs = ps.executeQuery();
					logger.error(ps.toString());
					while (rs.next()) {
						
			            InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
			            iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
			            iFinalBean.setInboundRoamersLocation(rs.getString(2));
				         
			           // query all base on filter
			            ArrayList<InboundRoamersReportBean> inboundRoamersBeanList = new ArrayList<InboundRoamersReportBean>();
						String sql2 = "SELECT * from inbound_roamers_history2 WHERE " + columnLookup + " AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND TYPE='c' AND " + filter +"='" + rs.getString(2) + "' ORDER BY DATE ASC";
						ps2 = conn.prepareStatement(sql2);
						ps2.setString(1, columnLike);
						ps2.setString(2, filter2);
						ps2.setString(3, filter2);
						ps2.setString(4, dateFrom);
						ps2.setString(5, dateTo);
						ResultSet rs2 = ps2.executeQuery();
						logger.error(ps2.toString());
						
						while(rs2.next()) {
							
							InboundRoamersReportBean iBean = new InboundRoamersReportBean();
					        iBean.setImsi(rs2.getString(2));
					        iBean.setMsisdn(rs2.getString(3));
					        iBean.setVlr(rs2.getString(4));
					        iBean.setCellLac(rs2.getString(5));
					        iBean.setCellId(rs2.getString(6));
					        iBean.setGeographicalArea(rs2.getString(7));
					        iBean.setRegionalArea(rs2.getString(8));
					        iBean.setRegion(rs2.getString(9));
					        iBean.setProvince(rs2.getString(10));
					        iBean.setTown(rs2.getString(11));
					        iBean.setBarangay(rs2.getString(12));
					        iBean.setSiteAddress(rs2.getString(13));
					        iBean.setDate(rs2.getString(15));
							inboundRoamersBeanList.add(iBean);
						
						}
						
						rs2.close();
						ps2.close();
						iFinalBean.setInboundRoamersBeanList(inboundRoamersBeanList);
						inboundRoamersReportArrayList.add(iFinalBean);
							
			        }
					rs.close();
					ps.close(); 
					
				}
			}
			
		} catch(Exception e) {
			logger.error("getInboundRoamersReportLocal() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersReportLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
		logger.error("getInboundRoamersReportLocal() params toQuery: "+ toQuery +", isNetwork: "+ isNetwork +", dateFrom: "+ dateFrom +", dateTo: "+ dateTo +", location: "+ location +", isProvincial: " + isProvincial + ", results arraysize: " + inboundRoamersReportArrayList.size());
		return inboundRoamersReportArrayList;
		
	}	

	public ArrayList<InboundRoamersReportFinalBean> getInboundRoamersReportForTouristDestinationLocal(
			 String touristSpot, String province, String toQuery, boolean isNetwork, String dateFrom, String dateTo) {
		
		ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportForTouristDestinationArrayList = new ArrayList<InboundRoamersReportFinalBean>();
		Connection conn = null;
		
		try {
			
			String filter1 = "", filter2 = "", site_address = "";
			site_address = "%" + touristSpot + "%";
			
			if(isNetwork) {
				filter1 = "like ? ";
				filter2 = toQuery + "%";
			} else {
				filter1 = "= ? ";
				filter2 = toQuery;
			}
			
			conn = this.createDBConnection();
			
			String sql = "", sql2 = "";
			PreparedStatement ps, ps2;
			
			if(toQuery.equalsIgnoreCase("all")) {
				sql = "SELECT count(*) FROM inbound_roamers_history2 WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND (DATE BETWEEN ? AND ? ) AND TYPE='c' ";
				sql2 = "SELECT * FROM inbound_roamers_history2 WHERE (PROVINCE = ? AND SITE_ADDRESS like ?)  AND (DATE BETWEEN ? AND ?) AND TYPE='c' ORDER BY DATE ASC";
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, province);
				ps.setString(2, site_address);
				ps.setString(3, dateFrom);
				ps.setString(4, dateTo);
				
				ps2 = conn.prepareStatement(sql2);
				ps2.setString(1, province);
				ps2.setString(2, site_address);
				ps2.setString(3, dateFrom);
				ps2.setString(4, dateTo);
			
			} else {
				sql = "SELECT count(*) FROM inbound_roamers_history2 WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ? ) AND TYPE='c' ";
				sql2 = "SELECT * FROM inbound_roamers_history2 WHERE (PROVINCE = ? AND SITE_ADDRESS like ?) AND (IMSI " + filter1 + " OR MSISDN " + filter1 + ") AND (DATE BETWEEN ? AND ?) AND TYPE='c' ORDER BY DATE ASC";
			
				ps = conn.prepareStatement(sql);
				ps.setString(1, province);
				ps.setString(2, site_address);
				ps.setString(3, filter2);
				ps.setString(4, filter2);
				ps.setString(5, dateFrom);
				ps.setString(6, dateTo);
				
				ps2 = conn.prepareStatement(sql2);
				ps2.setString(1, province);
				ps2.setString(2, site_address);
				ps2.setString(3, filter2);
				ps2.setString(4, filter2);
				ps2.setString(5, dateFrom);
				ps2.setString(6, dateTo);
				
			}
			
			ResultSet rs = ps.executeQuery();
			logger.error(ps.toString());
			
			while(rs.next()) {
				
				if(!rs.getString(1).equalsIgnoreCase("0")) {
					
					InboundRoamersReportFinalBean iFinalBean = new InboundRoamersReportFinalBean();
		            
					iFinalBean.setInboundRoamersTotalCount(rs.getString(1));
		            iFinalBean.setInboundRoamersLocation(touristSpot);
		            
			        // query all base on filter
		            ArrayList<InboundRoamersReportBean> inboundRoamersForTouristDestinationBeanList = new ArrayList<InboundRoamersReportBean>();
					ResultSet rs2 = ps2.executeQuery();
					logger.error(ps2.toString());
					
					while(rs2.next()) {
						InboundRoamersReportBean iBean = new InboundRoamersReportBean();
				        iBean.setImsi(rs2.getString(2));
				        iBean.setMsisdn(rs2.getString(3));
				        iBean.setVlr(rs2.getString(4));
				        iBean.setCellLac(rs2.getString(5));
				        iBean.setCellId(rs2.getString(6));
				        iBean.setGeographicalArea(rs2.getString(7));
				        iBean.setRegionalArea(rs2.getString(8));
				        iBean.setRegion(rs2.getString(9));
				        iBean.setProvince(rs2.getString(10));
				        iBean.setTown(rs2.getString(11));
				        iBean.setBarangay(rs2.getString(12));
				        iBean.setSiteAddress(rs2.getString(13));
				        iBean.setDate(rs2.getString(15));
				        inboundRoamersForTouristDestinationBeanList.add(iBean);
					
					}
					
					iFinalBean.setInboundRoamersBeanList(inboundRoamersForTouristDestinationBeanList);
					inboundRoamersReportForTouristDestinationArrayList.add(iFinalBean);
					
					rs2.close();
					ps2.close();
				 }
			   }
				
			rs.close();
			ps.close();
			
		} catch(Exception e) {
			logger.error("getInboundRoamersReportForTouristDestinationLocal() Exception => " + e.getMessage(), e);
		} finally {
			if (conn != null) {
	             try {
	            	 conn.close();
	             } catch (Exception e) {
	            	 logger.error("getInboundRoamersReportForTouristDestinationLocal() Exception => " + e.getMessage(), e);
	             }
	          }
		}
	
		logger.error("getInboundRoamersReportForTouristDestinationLocal() params touristSpot: " + touristSpot + ", province: " + province + ", toQuery: " + toQuery + ", isNetwork: " + isNetwork + ", dateFrom: " + dateFrom + ", dateTo: " + dateTo + ", result arraysize: " + inboundRoamersReportForTouristDestinationArrayList.size());
		return inboundRoamersReportForTouristDestinationArrayList;
		
	}
	
	/***** END LOCAL IMSI QUERY *****/
	
	
	/***** MISCELLANEOUS FUNCTIONS *****/
	
	public String returnProvince(String loc) {
		
		String prov = "";
		if(loc.equalsIgnoreCase("Baguio")) {
			prov = "BENGUET";
		} else if(loc.equalsIgnoreCase("Boracay")) {
			prov = "AKLAN";
		} else if(loc.equalsIgnoreCase("Coron") || loc.equalsIgnoreCase("El Nido") || loc.equalsIgnoreCase("Puerto Princesa")) {
			prov = "PALAWAN";
		} else if(loc.equalsIgnoreCase("Puerto Galera")) {
			prov = "ORIENTAL MINDORO";
		} else if(loc.equalsIgnoreCase("Subic")) {
			prov = "ZAMBALES";
		} else if(loc.equalsIgnoreCase("Tagaytay")) {
			prov = "CAVITE";
		}	
		return prov;
	}
	
	
	public static String md5(String input) {
        
        String md5 = null;
        if(null == input) return null;
        
        String inputLong = "globe_smsgt_ssd_maptracker_webapplication_" + input;
        
        try {
	        //Create MessageDigest object for MD5
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        //Update input string in message digest
	        digest.update(inputLong.getBytes(), 0, inputLong.length());
	        //Converts message digest value in base 16 (hex)
	        md5 = new BigInteger(1, digest.digest()).toString(16);
	         
        } catch (NoSuchAlgorithmException e) {
 
            e.printStackTrace();
        }
        
        return md5;
    }
	
	/***** END MISCELLANEOUS FUNCTIONS *****/
}
