<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 

<% 
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires",0);
response.setHeader("Pragma","no-cache");
%>

<!DOCTYPE html>
<html>
<title>Roamer Locator Map Tracker [ Create Campaign ]</title>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />

<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../js/date.js"></script>
<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">

<script type="text/javascript">
$("#document").ready(function() {

	var sessionId = "<s:property value='%{#session.loginId}'/>";
	if(sessionId === "" || sessionId == null) {
		$("#blankDiv").show();
		alert("Your current logged session has expired or is not logged-in. Please login to view this page. Redirecting to Login page..");
		location.href="logoutUser.action";
		$("#blankDiv").hide();
	} else if (decodeURIComponent(getParamsFromUrl("loginId")) !== "null") {
		processPageFromVar();
	} else {
		var sessionType = "<s:property value='%{#session.loginType}'/>";
		if(sessionType !== "2" && sessionType !== "1") {
			$("#blankDiv").show();
			alert("Your current logged session is not intended for this page. Please provide the right credentials. Redirecting to Login page..");
			location.href="logoutUser.action";
			$("#blankDiv").hide();
		} else {
			processPage();
	    }
	} 

	
   // functions here
   function getParamsFromUrl(name) {
		
	    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
	    if (results==null) {
	       return null;
	    } else{
	       return results[1] || 0;
	    }
	}
   
   function rewriteUrl() {
	   
		// rewrite url so that params are not visible in address bar
		var uri = window.location.toString();
		if (uri.indexOf("?") > 0) {
		    var clean_uri = uri.substring(0, uri.indexOf("?"));
		    window.history.replaceState({}, document.title, clean_uri);
		}
		// end
   }	
	
	function processPageFromVar() {
		
		var sessionName2 = decodeURIComponent(getParamsFromUrl("loginName"));
		var sessionType2 = decodeURIComponent(getParamsFromUrl("loginType"));
		var sessionId2 = decodeURIComponent(getParamsFromUrl("loginId"));
		
		rewriteUrl();
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=CreateCampaign.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
					+"&loginType="+ sessionType2
					+"&loginName="+ sessionName2, "_blank");
		});
		
		var timeIntervalToSetSched5Hr = null;
		checkPSIStatus(); // call upon document load to get & display status
	
		// ajax call
		$.ajax({
				 type:'POST',
	             url:'checkIfCampaignIsRunning.action',
	             contentType: 'application/json; charset=utf-8',
	             dataType: 'json',
	             success: function(response){
	            	 var rSchedBean =  JSON.parse(JSON.stringify(response)).rSchedBean;
	            	 if(rSchedBean === null) {
	            		 $("#campaignTimeLabel").html("No current campaign schedule");
	            	 } else {
	            		 $("#campaignTimeLabel").html(rSchedBean.date + " " + rSchedBean.time + " | " + rSchedBean.status);
	            	 }
	               return false; 
	             } 
		});       
		
		$.get("getScheduleInterval.action", function(data) {
			
			var json = (JSON.parse(JSON.stringify(data)).scheduleInterval).replace(/"/g,'');
			timeIntervalToSetSched5Hr = (json * 60 * 60 * 1000);
	  		console.log(timeIntervalToSetSched5Hr);
		});	
		
		$('#editableDiv').hide(); // hidden by default
		
		var allNetworks = true;
		
		$("#campaignForm input[name='networkRadio']").change(function () {
		    if ($(this).val() == "All" && $(this).is(":checked")) {
		    	$('#editableDiv :input').prop('disabled', true);
		    	$('#editableDiv :input').prop('checked',false);
		    	$('#editableDiv').hide();
		    	allNetworks = true;
		    } else {
		    	$('#editableDiv').show();
		    	$('#editableDiv :input').removeAttr('disabled');
		    	allNetworks = false;
		    }
		});
		
		for(var a = 0; a < 24; a++) {
			$("#selectTimeHour").append($('<option>',{
				value: convertToTwoDigit(a),
				text: convertToTwoDigit(a)
			}));
		}
		
		for(var b = 0; b < 60; (b += 5)) {
			$("#selectTimeMin").append($('<option>',{
				value: convertToTwoDigit(b),
				text: convertToTwoDigit(b)
			}));
		}
		
		$("#offCampaign").click(function() {

			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Pause_Campaign&page=CreateCampaign.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			$.ajax({
				type:'POST',
	            url:'pauseResumeCampaign.action?pauseType=pause',
	            contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
	            success: function(response){
	            	var json = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
	            	if(json.indexOf("Cannot") >= 0) {
	            		alert(json);
	            	} else {
	            		alert("Current Campaign schedule succesfully turned-off!");
	            		location.reload();
	            	}
	            return false;
	            } 
			});   
			
		});
		
		$("#onCampaign").click(function() {

			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Resume_Campaign&page=CreateCampaign.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});				
			
			$.ajax({
				type:'POST',
	            url:'pauseResumeCampaign.action?pauseType=resume',
	            contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
	            success: function(response){
	            	var json = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
	            	if(json.indexOf("Cannot") >= 0) {
	            		alert(json);
	            	} else {
	            		alert("Current Campaign schedule succesfully turned-on!");
	            		location.reload();
	            	}
	            return false;
	            } 
			}); 
		});
		

		
		$("#createCampaign").click(function() {	
			
			var time = $("#selectTimeHour").val() + ":" + $("#selectTimeMin").val();
			var checkedValues = "";
			if(allNetworks) {
				checkedValues = "all";
			} else {
				// get first all checked network operators
				checkedValues = $('#editableDiv :input:checkbox:checked').map(function() {
				    return this.value;
				}).get();
			}
				
			// ajax call
			$.ajax({
					 type:'POST',
		             url:'checkForExistingCampaign.action',
		             contentType: 'application/json; charset=utf-8',
		             dataType: 'json',
		             success: function(response){
		            	 	
		            	 	 var dateCreated = $("#serverTimeLabel").html().substring(0,10).trim(), timeCreated = $("#serverTimeLabel").html().substring(11,16).trim(), id = "", triggerType = "";
		            	 	 var serverDate = Date.parse(dateCreated + " " + timeCreated);
		            	 	 
		            			var rSchedBean =  JSON.parse(JSON.stringify(response)).rSchedBean;
		            			 
		            			if(rSchedBean === null) {
		            				triggerType = "insert";
		            				
		            				// check if setted time is greater than, less than or equal to current time
		            				// if greater, set 'dateCreated' for today, else set 'dateCreated' for tomorrow (dateCreated + 1)
		            				var testDate = Date.parse(dateCreated + " " + time);
		            				
		            				if(Date.compare(testDate,serverDate) < 0) {
		            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
		            					testDate = Date.today().add(1).days();
		            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
		            					insertViaAJAX(checkedValues, testDateCreated, time, id, dateCreated, timeCreated, triggerType);
		            					
		            					$.ajax({
		            				     	type:'POST',
		            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
		            				         contentType: 'application/json; charset=utf-8',
		            				         dataType: 'json'
		            					});
		            					
		            				} else if (Date.compare(testDate,serverDate) >= 0) {
		            					// to be run today
		            					insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
		            					
		            					$.ajax({
		            				     	type:'POST',
		            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
		            				         contentType: 'application/json; charset=utf-8',
		            				         dataType: 'json'
		            					});
		            				}
		            				
		            			} else {
		            				
		            				if(rSchedBean.status === "IN USE" || rSchedBean.status === "PAUSED") {
		            					// get previous date and time sched then validate if 5 hours was
		            					// var prevSchedDate = rSchedBean.date, prevSchedTime = rSchedBean.time;
		            					
		            					// pwede mag-insert diba?
		            					triggerType = "insert";
		            					var testDate = Date.parse(dateCreated + " " + time);
		            					
		            					var rSchedBeanDate = rSchedBean.date, rSchedBeanTime = rSchedBean.time;
		            					var rSchedBeanFinalDate = Date.parse(rSchedBeanDate + " " + rSchedBeanTime);
		            					
		            						if(Date.compare(testDate,serverDate) < 0) {
				            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
				            					testDate = Date.today().add(1).days();
				            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
				            					insertViaAJAX(checkedValues, testDateCreated, time, id, dateCreated, timeCreated, triggerType);
				            					
				            					$.ajax({
				            				     	type:'POST',
				            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
				            				         contentType: 'application/json; charset=utf-8',
				            				         dataType: 'json'
				            					});
				            					
				            				} else if (Date.compare(testDate,serverDate) >= 0) {
				            					// to be run today
				            					// to do: compare testDate to campaignDate
				            					
				            					var difference = testDate.getTime() - rSchedBeanFinalDate.getTime();
				            					if(Date.compare(testDate,rSchedBeanFinalDate) < 0) {
				            						insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
				            						
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
				            						
				            					} else {
				            						// should be greater than min. interval
				            						if(difference >= timeIntervalToSetSched5Hr) {
				            							insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
				            							
						            					$.ajax({
						            				     	type:'POST',
						            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
						            				         contentType: 'application/json; charset=utf-8',
						            				         dataType: 'json'
						            					});
				            							
				            						} else {
				            							alert("Unable to create schedule. The time you've entered is within the duration of Map Tracker back-end execution.");
				            						}
				            					}	
				            				}
		            					
		            				} else { // with 'PENDING' status
			            				// for update
			            				// check if date_created is today . if yes, update. if no, do not allow to insert new 
			            				if(dateCreated === rSchedBean.date_created) {
			            					// check if set time is 5 hours greater than previous time
			            					var settedDateTime = Date.parse(time);
			            					
			            					triggerType = "update";
			            						
			    	            				if(Date.compare(settedDateTime,serverDate) < 0) {
			    	            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
			    	            					testDate = Date.today().add(1).days();
			    	            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
			    	            					insertViaAJAX(checkedValues, testDateCreated, time, rSchedBean.id, dateCreated, timeCreated, triggerType);
			    	            					
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
			    	            					
			    	            				} else if (Date.compare(settedDateTime,serverDate) >= 0) {
			    	            					// to be run today
			    	            					insertViaAJAX(checkedValues, dateCreated, time, rSchedBean.id, dateCreated, timeCreated, triggerType);
			    	            					
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
			    	            				
			    	            				}
			            				} else {
			            					alert("Unable to create new schedule, an existing campaign is running. Please try again after " + (((timeIntervalToSetSched5Hr/1000)/60)/60) + " hrs.");
			            				}
		            				}
		            			}
		             return false;
		             }
			
			    });
			
			return false;
		  });
	}
	
	function processPage() {
		
		var sessionName = "<s:property value='%{#session.loginName}'/>";
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=CreateCampaign.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		var timeIntervalToSetSched5Hr = null;
		checkPSIStatus(); // call upon document load to get & display status
	
		// ajax call
		$.ajax({
				 type:'POST',
	             url:'checkIfCampaignIsRunning.action',
	             contentType: 'application/json; charset=utf-8',
	             dataType: 'json',
	             success: function(response){
	            	 var rSchedBean =  JSON.parse(JSON.stringify(response)).rSchedBean;
	            	 if(rSchedBean === null) {
	            		 $("#campaignTimeLabel").html("No current campaign schedule");
	            	 } else {
	            		 $("#campaignTimeLabel").html(rSchedBean.date + " " + rSchedBean.time + " | " + rSchedBean.status);
	            	 }
	               return false; 
	             } 
		});       
		
		$.get("getScheduleInterval.action", function(data) {
			
			var json = (JSON.parse(JSON.stringify(data)).scheduleInterval).replace(/"/g,'');
			timeIntervalToSetSched5Hr = (json * 60 * 60 * 1000);
	  		console.log(timeIntervalToSetSched5Hr);
		});	
		
		$('#editableDiv').hide(); // hidden by default
		
		var allNetworks = true;
		
		$("#campaignForm input[name='networkRadio']").change(function () {
		    if ($(this).val() == "All" && $(this).is(":checked")) {
		    	$('#editableDiv :input').prop('disabled', true);
		    	$('#editableDiv :input').prop('checked',false);
		    	$('#editableDiv').hide();
		    	allNetworks = true;
		    } else {
		    	$('#editableDiv').show();
		    	$('#editableDiv :input').removeAttr('disabled');
		    	allNetworks = false;
		    }
		});
		
		for(var a = 0; a < 24; a++) {
			$("#selectTimeHour").append($('<option>',{
				value: convertToTwoDigit(a),
				text: convertToTwoDigit(a)
			}));
		}
		
		for(var b = 0; b < 60; (b += 5)) {
			$("#selectTimeMin").append($('<option>',{
				value: convertToTwoDigit(b),
				text: convertToTwoDigit(b)
			}));
		}
		
		$("#offCampaign").click(function() {

			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Pause_Campaign&page=CreateCampaign.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			$.ajax({
				type:'POST',
	            url:'pauseResumeCampaign.action?pauseType=pause',
	            contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
	            success: function(response){
	            	var json = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
	            	if(json.indexOf("Cannot") >= 0) {
	            		alert(json);
	            	} else {
	            		alert("Current Campaign schedule succesfully turned-off!");
	            		location.reload();
	            	}
	            return false;
	            } 
			});   
			
		});
		
		$("#onCampaign").click(function() {

			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Resume_Campaign&page=CreateCampaign.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});				
			
			$.ajax({
				type:'POST',
	            url:'pauseResumeCampaign.action?pauseType=resume',
	            contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
	            success: function(response){
	            	var json = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
	            	if(json.indexOf("Cannot") >= 0) {
	            		alert(json);
	            	} else {
	            		alert("Current Campaign schedule succesfully turned-on!");
	            		location.reload();
	            	}
	            return false;
	            } 
			}); 
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
					+"&loginType="+ sessionType
					+"&loginName="+ sessionName, "_blank");
		});
		
		$("#createCampaign").click(function() {	
			
			var time = $("#selectTimeHour").val() + ":" + $("#selectTimeMin").val();
			var checkedValues = "";
			if(allNetworks) {
				checkedValues = "all";
			} else {
				// get first all checked network operators
				checkedValues = $('#editableDiv :input:checkbox:checked').map(function() {
				    return this.value;
				}).get();
			}
				
			// ajax call
			$.ajax({
					 type:'POST',
		             url:'checkForExistingCampaign.action',
		             contentType: 'application/json; charset=utf-8',
		             dataType: 'json',
		             success: function(response){
		            	 	
		            	 	 var dateCreated = $("#serverTimeLabel").html().substring(0,10).trim(), timeCreated = $("#serverTimeLabel").html().substring(11,16).trim(), id = "", triggerType = "";
		            	 	 var serverDate = Date.parse(dateCreated + " " + timeCreated);
		            	 	 
		            			var rSchedBean =  JSON.parse(JSON.stringify(response)).rSchedBean;
		            			 
		            			if(rSchedBean === null) {
		            				triggerType = "insert";
		            				
		            				// check if setted time is greater than, less than or equal to current time
		            				// if greater, set 'dateCreated' for today, else set 'dateCreated' for tomorrow (dateCreated + 1)
		            				var testDate = Date.parse(dateCreated + " " + time);
		            				
		            				if(Date.compare(testDate,serverDate) < 0) {
		            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
		            					testDate = Date.today().add(1).days();
		            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
		            					insertViaAJAX(checkedValues, testDateCreated, time, id, dateCreated, timeCreated, triggerType);
		            					
		            					$.ajax({
		            				     	type:'POST',
		            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
		            				         contentType: 'application/json; charset=utf-8',
		            				         dataType: 'json'
		            					});
		            					
		            				} else if (Date.compare(testDate,serverDate) >= 0) {
		            					// to be run today
		            					insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
		            					
		            					$.ajax({
		            				     	type:'POST',
		            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
		            				         contentType: 'application/json; charset=utf-8',
		            				         dataType: 'json'
		            					});
		            				}
		            				
		            			} else {
		            				
		            				if(rSchedBean.status === "IN USE" || rSchedBean.status === "PAUSED") {
		            					// get previous date and time sched then validate if 5 hours was
		            					// var prevSchedDate = rSchedBean.date, prevSchedTime = rSchedBean.time;
		            					
		            					// pwede mag-insert diba?
		            					triggerType = "insert";
		            					var testDate = Date.parse(dateCreated + " " + time);
		            					
		            					var rSchedBeanDate = rSchedBean.date, rSchedBeanTime = rSchedBean.time;
		            					var rSchedBeanFinalDate = Date.parse(rSchedBeanDate + " " + rSchedBeanTime);
		            					
		            						if(Date.compare(testDate,serverDate) < 0) {
				            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
				            					testDate = Date.today().add(1).days();
				            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
				            					insertViaAJAX(checkedValues, testDateCreated, time, id, dateCreated, timeCreated, triggerType);
				            					
				            					$.ajax({
				            				     	type:'POST',
				            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
				            				         contentType: 'application/json; charset=utf-8',
				            				         dataType: 'json'
				            					});
				            					
				            				} else if (Date.compare(testDate,serverDate) >= 0) {
				            					// to be run today
				            					// to do: compare testDate to campaignDate
				            					
				            					var difference = testDate.getTime() - rSchedBeanFinalDate.getTime();
				            					if(Date.compare(testDate,rSchedBeanFinalDate) < 0) {
				            						insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
				            						
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
				            						
				            					} else {
				            						// should be greater than min. interval
				            						if(difference >= timeIntervalToSetSched5Hr) {
				            							insertViaAJAX(checkedValues, dateCreated, time, id, dateCreated, timeCreated, triggerType);
				            							
						            					$.ajax({
						            				     	type:'POST',
						            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
						            				         contentType: 'application/json; charset=utf-8',
						            				         dataType: 'json'
						            					});
				            							
				            						} else {
				            							alert("Unable to create schedule. The time you've entered is within the duration of Map Tracker back-end execution.");
				            						}
				            					}	
				            				}
		            					
		            				} else { // with 'PENDING' status
			            				// for update
			            				// check if date_created is today . if yes, update. if no, do not allow to insert new 
			            				if(dateCreated === rSchedBean.date_created) {
			            					// check if set time is 5 hours greater than previous time
			            					var settedDateTime = Date.parse(time);
			            					
			            					triggerType = "update";
			            						
			    	            				if(Date.compare(settedDateTime,serverDate) < 0) {
			    	            					alert("The campaign execution time you've entered is earlier than the current time, this will be effective starting tomorrow.");
			    	            					testDate = Date.today().add(1).days();
			    	            					var testDateCreated = (testDate.getMonth() + 1) + "/" + testDate.getDate() + "/" + testDate.getFullYear();
			    	            					insertViaAJAX(checkedValues, testDateCreated, time, rSchedBean.id, dateCreated, timeCreated, triggerType);
			    	            					
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
			    	            					
			    	            				} else if (Date.compare(settedDateTime,serverDate) >= 0) {
			    	            					// to be run today
			    	            					insertViaAJAX(checkedValues, dateCreated, time, rSchedBean.id, dateCreated, timeCreated, triggerType);
			    	            					
					            					$.ajax({
					            				     	type:'POST',
					            				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Create_Campaign&page=CreateCampaign.jsp',
					            				         contentType: 'application/json; charset=utf-8',
					            				         dataType: 'json'
					            					});
			    	            				
			    	            				}
			            				} else {
			            					alert("Unable to create new schedule, an existing campaign is running. Please try again after " + (((timeIntervalToSetSched5Hr/1000)/60)/60) + " hrs.");
			            				}
		            				}
		            			}
		             return false;
		             }
			
			    });
			
			return false;
		  });		
	}
		
	function insertViaAJAX(checkedValues,date,time, id, dateCreated, timeCreated, triggerType) {
		$.ajax({
			type:'POST',
            url:'createCampaign.action?selectedNetworks='+checkedValues +'&date='+date+'&time='+time +'&triggerType='+triggerType+'&dateCreated='+dateCreated+'&timeCreated='+timeCreated+'&id='+id,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(response){
           		 if(JSON.parse(JSON.stringify(response)).jsonResponse === "success" && triggerType === "insert") {
           			 alert("Successfully created a campaign!");
           		 } else if(JSON.parse(JSON.stringify(response)).jsonResponse === "success" && triggerType === "update") {
           			 alert("Successfully updated a campaign!");
           		 } else {
           			 alert("Failed to create a campaign!");
           		 }
            return false;
            }
	   	}); 
	}
	
	function convertToTwoDigit(param) {
		if(param < 10) {
			val = "0" + param;
		} else {
			val = param;
		}
		return val;
	}
	
	window.setInterval(timeClock, 1000); // function to update clock every second
	
    function timeClock() {
    	/* code block to display server time */	
    	$.ajax({
			type:'GET',
            url:'getServerDate.action',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(response){
            	$("#serverTimeLabel").html(JSON.parse(JSON.stringify(response)).serverDate.replace(/"/g,''));
            return false;
            }
	   	});
	} 
	
	window.setInterval(checkPSIStatus,60000); // function to check PSI status every minute
	
	function checkPSIStatus() {
		/* code block to display PSI status */
    	$.ajax({
			type:'GET',
            url:'checkIfPSIIsRunning.action',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(response){
            	$("#psiStatusLabel").html(JSON.parse(JSON.stringify(response)).psiStatus.replace(/"/g,''));
            return false;
            }
	   	});
	}
	
});
</script>
</head>
<body>
	<div id="blankDiv" style="display:none;"></div>
	<div id="main">
		<div id="map_header">
			<div class="logo">
				<img src="../images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;">
			</div>
			<div class="scheduler">
				<a onclick="location.href='CreateCampaign.jsp'" style="text-decoration: none;"/><img src= "../images/1.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="background-color: #ed145b; border-radius: 5px;">Scheduler</label></a>
			</div>
			<div class="inboundMap">
				<a onclick="location.href='InboundRoamersMapTracker_Admin.jsp'" style="text-decoration: none;"/><img src= "../images/2.png" style="margin-top: 50px; margin-left: 25px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Inbound Map</label></a>
			</div>
			<div class="reporting">
				<a id="report_link" style="text-decoration: none;"/><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>			
			</div>
			<div class="ad-hoc">
				<a onclick="location.href='InboundRoamersAdHocMapTracker_Admin.jsp'" style="text-decoration: none;"/><img src= "../images/4.png" style="margin-top: 50px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Ad-Hoc</label></a>
			</div>	
			<div class="userOperations">
				<a onclick="location.href='UserOperations.jsp'" style="text-decoration: none;"/><img src= "../images/view_user.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">View Users</label></a>
			</div>
			<div class="userLogs">
				<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none; margin-left: 25px;"><label>Logout</label></a><br>
				<a onclick="location.href='UserLogs.jsp'" style="text-decoration: none;"/><img src= "../images/view.png" style="margin-top: 10px; margin-left: 25px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">View User Logs</label></a>
			</div>									
		</div>
		
		<div id = "map_content" style="height: 600px;">
			<br><br><br><br><br>
			<div id= "scheduler" style="margin: auto; vertical-align: middle; width: 500px; ">
				</br></br>
				<div style="text-align: left; margin-left: 50px; margin-top: 20px;">
					<label style="color: black;">Current Campaign Schedule:&nbsp;</label><label id="campaignTimeLabel" style="color: black; margin-left: 40px;"></label><br/>
					<label style="color: black;">Server Time:&nbsp;</label><label id="serverTimeLabel" style="color: black; margin-left: 170px;"></label><br/>
					<label style="color: black;">PSI Application Status:&nbsp;</label><label id="psiStatusLabel" style="color: black; margin-left: 140px;"></label><br/>
					<label style="color: black;">Select Network:&nbsp;</label>
				</div>
				<div style="text-align: left; margin-left: 160px; margin-top: 10px;">		
					<form action="campaignForm" id="campaignForm">
						<input type="radio" name="networkRadio" id="allNetworkRadioButton" checked="checked" value="All"><label style="color: black; margin-left: 10px;">ALL NETWORKS</label></input><br>
						<!--<input type="radio" name="networkRadio" id="selectNetworkRadioButton" value="Select">Select Below</input><br/><br/>-->
						<!--<div id="editableDiv" name="editableDiv" style="border: 1px solid; overflow: auto; width: 400px; height: 100px; font-size: 15px;  margin-bottom: 10px; padding: 2%;"></div><br/><br/>-->
					</form>
				</div>
				<div style="text-align: left; margin-left: 50px; margin-top: 20px;">
					<label style="color: black;">Select Time:&nbsp;</label></br><label style="margin-left: 120px; color: black;">Hour:&nbsp;</label><select id="selectTimeHour" name="selectTimeHour"></select>&nbsp;&nbsp;<label style="color: black;">Minute:&nbsp;</label><select id="selectTimeMin" name="selectTimeMin"></select>&nbsp;&nbsp;<br><br>
					<input type="button" class="button-color" id="createCampaign" name="createCampaign" value="   Save   " style="width: 85px;"/>
					<input type="button" class="button-color" id="offCampaign" name="offCampaign" value="Turn OFF Campaign" style="margin-left: 15px;" />
					<input type="button" class="button-color" id="onCampaign" name="onCampaign" value="Turn ON Campaign" style="margin-left: 15px;" />
				</div>							
			</div>			
		</div>
		<div id="poweredByDiv"><img src="../images/poweredby.png"><a onclick="location.href='Login.jsp'" style="text-decoration: none; cursor: default;" /><label style="color: #058cbb;">..</label></a></div>
	</div>
</body>
</html>