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
<title>Roamer Locator Map Tracker [ Ad-Hoc ]</title>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>

<script type="text/javascript" src="../../js/leaflet.js"></script>
<script type="text/javascript" src="../../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../../js/jquery-ui-1.10.4.js"></script>
<script type="text/javascript" src="../../js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="../../js/date.js"></script>
<script type="text/javascript" src="../../js/smartpaginator.js"></script>


<link rel="stylesheet" type="text/css" href="../../css/leaflet.css">
<link rel="stylesheet" type="text/css" href="../../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../../css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="../../css/roamer_page_element.css">

<script type="text/javascript">
$("#document").ready(function() {
	
	var uploadMarkersLayerGroup = L.layerGroup(); 
	var uploadMarkersArray = new Array();
	var isCampaignRunning = false, isAdhocRunning = false;
	var timeIntervalToSetSched5Hr = null, serverClock = null;
	var sessionUserId = "";
	
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	if(sessionId === "" || sessionId == null) {
		$("#blankDiv").show();
		alert("Your current logged session has expired or is not logged-in. Please login to view this page. Redirecting to Login page..");
		location.href="logoutUser.action";
		$("#blankDiv").hide();
		
	} else {			
		var sessionType = "<s:property value='%{#session.loginType}'/>";
		if(sessionType !== "2") {
			$("#blankDiv").show();
			alert("Your current logged session was not intended for this page. Redirecting to Campaign page..");
			location.href="${pageContext.request.contextPath}/jsp/CreateCampaign.jsp";
			$("#blankDiv").hide();
		} else {
						
			var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=InboundRoamersAdHocMapTracker_2.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			sessionUserId = "<s:property value='%{#session.loginType}'/>";
			//var host = "localhost/Tiles/Tiles";
			var host = "10.8.72.12/roamware/Tiles"; // public of .53	
			
			getScheduleInterval();
			checkPSIStatus(); // call upon document load to get & display status
			
			$("#spinner").hide();
			$("#smartpaginator").hide();
						
		 	var bounds = new L.LatLngBounds(
					new L.LatLng(4.22816513512253, 116.389103446625),
					new L.LatLng(21.6156671213737, 126.979526808346));
			
			var map = L.map('map', {
				minZoom: 6,
				maxZoom: 14,
				zoom: 6,
				maxBounds: bounds, 
				zoomControl: false,
			    center: [12.3,122.7457]
			});

			L.tileLayer('http://'+ host +'/{z}/{x}/{y}.png', {
				scheme: "TMS",
				continousWorld: "true",
			    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
			}).addTo(map); 	
			
			var isInternationalRP = false;
			var totalRecords = 0;
			
			$("#textInputRadioButton").attr('checked', true);
			$("#uploadInputDiv2").hide();
			$("#textInputDiv2").show();
			
			
			$("#fileUpload input[name='selectionRadio']").change(function () {
			    if ($(this).val() === "textInput" && $(this).is(":checked")) {
			    	$("#textInputDiv2").show();
			    	$("#uploadInputDiv2").hide();    	
			    } else {
			    	isTextInput = false;
			    	$("#textInputDiv2").hide();
			    	$("#uploadInputDiv2").show();
			    	
			    }
			});
			
			/** ---------- UPLOAD INPUT ---------- **/
			
			// process uploaded file
			
			$("#uploadBtn").click(function() {
			
				uploadMarkersLayerGroup.clearLayers();	// clear map for previously added layers (markers, polylines)
				$("#inboundRoamersTable").remove(); // remove cellTables from uploadDiv
				$("#imsiTableDiv").remove(); // remove imsiTableDiv from uploadDiv
				$("#smartpaginator").hide();
				
				var fd = new FormData($("#fileUpload")[0]);

				if(!isInternationalRP) {
					var yes = confirm("Please note that only local imsis' (those starting with '515') \nand msisdns starting with '63917' or any Globe local msisdn prefix will only be processed");
					if(yes) {
						
						$.ajax({
					     	type:'POST',
					         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Upload_File&page=InboundRoamersAdHocMapTracker_2.jsp',
					         contentType: 'application/json; charset=utf-8',
					         dataType: 'json'
						});
						
						$.ajax({
					     	type:'POST',
					         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_2.jsp',
					         contentType: 'application/json; charset=utf-8',
					         dataType: 'json'
						});
						
						$("#spinner").show(); // show spinner 
						$.ajax({
							url: "roamerLocatorFileUpload2.action?isInternationalRP="+isInternationalRP+"&sessionUserId="+sessionUserId,
							type: "POST",
							data: fd,
							enctype: "multipart/form-data",
							processData: false,
							contentType: false,
							success: function(response){
								$("#spinner").hide();
								
								var result = JSON.parse(JSON.stringify(response)).jsonResult.replace(/"/g,'');
								
								if(result.indexOf("failed") > 0) {
									alert(result);
								} else {
									// if success, disable buttons
									alert(result);
									//$("#uploadBtn").prop("disabled", true);
									//$("#triggerAdHocTextInputBtn").prop("disabled",true);
									//$("#triggerAdHocDropDownBtn").prop("disabled",true);
								}
							}
						}); 
					}
				}
				return false;
			});
			
			$("#plotOnMapUploadBtn").click(function() {
				
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_2.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				alert("Checking database for updated values! Please wait....");
				$("#spinner").show();
				$("#smartpaginator").hide();
				
				$.ajax({
					url: "checkAdHocData2.action?sessionUId="+sessionUserId,
					type: "GET",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						$("#spinner").hide();
						var result = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
						if(result.indexOf("PSI") >= 0 || result.indexOf("No AdHoc trigger") >= 0) {
							alert(result);
						} else {
							plotOnMap(response);
						}
					}
				}); 
				return false;	
			});	
			
			/** ---------- END UPLOAD INPUT ---------- **/
			
			
			/** ---------- TEXT INPUT ---------- **/
			
			$("#plotOnMapTextInputBtn").click(function() {
			
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_2.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				alert("Checking database for updated values! Please wait....");
				$("#spinner").show();
				$("#smartpaginator").hide();
				
				$.ajax({
					url: "checkAdHocData2.action?sessionUId="+sessionUserId,
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						$("#spinner").hide();
						var result = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
						if(result.indexOf("PSI") >= 0 || result.indexOf("No AdHoc trigger") >= 0) {
							alert(result);
						} else {
							plotOnMap(response);
						}
					}
				}); 
				return false;	
			});
			
			$("#triggerAdHocTextInputBtn").click(function() {
				
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Trigger_Adhoc&page=InboundRoamersAdHocMapTracker_2.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				// call ajax
				$("#spinner").show();
				$("#smartpaginator").hide();
				
				var values = $.map($('#textInputMultiple option'), function(e) { return e.value; });
				$.ajax({
					url: "triggerAdHoc2.action?inputMode=textInput&sessionId="+sessionUserId+"&stringInput="+ values.join("-"),
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						$("#spinner").hide();
						alert(JSON.parse(JSON.stringify(response)).jsonResponse.replace(/"/g,''));
						$("#textInputMultiple").children().remove();
						
						// if success, disable buttons
						//$("#uploadBtn").prop("disabled", true);
						//$("#triggerAdHocTextInputBtn").prop("disabled",true);
						//$("#triggerAdHocDropDownBtn").prop("disabled",true);
					}
				}); 
				return false;
				
			});	
			
			$("input[type='text']").keyup(function() {
			    var raw_text =  jQuery(this).val();
			    var return_text = raw_text.replace(/[^0-9,]/g,'');
			    jQuery(this).val(return_text);
			});
			
			$("#addImsiBtn").click(function() {
				
				var imsiVal = $("#addIMSI").val();
				$("#smartpaginator").hide();
				
				if(imsiVal === "") {
					alert("IMSI/MSISDN input is empty!");
					$("#addIMSI").focus();
					
				} else {
					var splitted = imsiVal.split(",");
					var imsi = splitted[0];
					var msisdn = splitted[1];
					
					var localMsisdnListArr =  ["0905","0906","0915","0916","0917","0926","0927","0935","0936","0975", "0977", "63905","63906","63915","63916","63917","63926","63927","63935","63936","63975", "63977"];
					
					if(imsi !== "" && msisdn === "") {
						if(imsi.substring(0,3) !== "515") {
							alert("Cannot input non-local imsi starting with '515'");
							$("#addIMSI").focus();
						} else {
							$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
							$("#addIMSI").focus();
							$("#addIMSI").val("");
						}
					} else if(imsi === "" && msisdn !== "") {
						if(msisdn !== "") {
							var validMsisdn = false;
							for(var i = 0; i < localMsisdnListArr.length; i++) {
								if(msisdn.substring(0,5).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = true;
									break;
								} else {
									if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
										validMsisdn = true;
										break;
									} else {
										validMsisdn = false;	
									}
								}
							}
							
							if(validMsisdn) {
								$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
								$("#addIMSI").focus();
								$("#addIMSI").val("");
							} else {
								alert("Cannot input non-local msisdn. Should start with '63917' or any Globe local msisdn prefix");
								$("#addIMSI").focus();
							}
						}
					} else if(imsi !== "" && msisdn !== "") {
						if(imsi.substring(0,3) !== "515") {
							alert("Cannot input non-local imsi starting with '515'");
							$("#addIMSI").focus();
						} else {
							var validMsisdn = false;
							for(var i = 0; i < localMsisdnListArr.length; i++) {
								if(msisdn.substring(0,5).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = true;
									break;
								} else {
									if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
										validMsisdn = true;
										break;
									} else {
										validMsisdn = false;
									}
								}
							}
							
							if(validMsisdn) {
								$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
								$("#addIMSI").focus();
								$("#addIMSI").val("");
							} else {
								 var msisdnLength = msisdn.length;
		                         if(msisdnLength > 0) {
		                           if(msisdnLength >= 5) {
		                                 alert("Cannot input non-local msisdn starting with '" + msisdn.substring(0,5) + "'");
		                           } else {
		                                 alert("Cannot input non-local msisdn starting with '" + msisdn.substring(0,msisdnLength) + "'");
		                           }
		                         }
		                         $("#addIMSI").focus();

							}
						}
					} else {
						alert("Invalid input! Try again!");
						$("#addIMSI").focus();
					}
					
				}	
			});
			
			$("#removeImsiBtn").click(function() {
				 $("#smartpaginator").hide();
				 if ($('#textInputMultiple option:selected').val() != null) {
		     
		             $('#textInputMultiple option:selected').remove();
		             $("#textInputMultiple").attr('selectedIndex', '-1').find("option:selected").removeAttr("selected");
		             
		         } else {
		             alert("Before removing, please select any position.");
		         }
			});
			/** ---------- END TEXT INPUT ---------- **/
			$(".exportToCSV").click(function(event) {
			    exportTableToCSV.apply(this, [$('#tableDiv>table'), 'adhoc_export.csv']);
			    
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Download_Report&page=InboundRoamersAdHocMapTracker_2.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
			}); 					
		}
	
	}	

/* FUNCTIONS */

	// add markers and polylines
	function plotOnMap(response) {
		
		var failedList = [];
		var successList = [];
		
		$("#inboundRoamersTable").remove();
		var responseData = JSON.stringify(response);
     	var result = $.parseJSON(responseData);
		var rec = 0;
     	
		$("#smartpaginator").hide();
     	$("#tableDiv").append("<table class='tablesorter' id='inboundRoamersTable'><thead><tr>"+ 
				  "<th style='text-align: center;'>DATE</th>"+
				  "<th style='text-align: center;'>IMSI</th>"+
				  "<th style='text-align: center;'>MSISDN</th>"+
				  "<th style='text-align: center;'>CELL ID</th>"+
				  "<th style='text-align: center;'>CELL LAC</th>"+
				  "<th style='text-align: center;'>CELL NAME</th>"+ 
				  "<th style='text-align: center;'>TOWN</th>"+
				  "<th style='text-align: center;'>BARANGAY</th>"+
				  "<th style='text-align: center;'>SITE ADDRESS</th>"+
				  "<th style='text-align: center;'>LONGITUDE</th>"+
				  "<th style='text-align: center;'>LATITUDE</th>"+
				  "<th style='text-align: center;'>TAG</th></tr></thead><tbody>");

		$.each(result, function(k, v) {
		var returnedObjects = eval(v);
				
		if(returnedObjects != null) {
		
			for(var i = 0; i < returnedObjects.length; i++) {
				
					var imsi = returnedObjects[i].imsi;
					var msisdn = returnedObjects[i].msisdn;
					var returnedObjects2 = JSON.stringify(returnedObjects[i].roamerLocationBean);
					var tracker = null;
					
					if(returnedObjects2 !== "[null]") {
						
						returnedObjects2 = eval(returnedObjects2);
						
							if(returnedObjects2 != null) {
								
								for(var j = 0; j < returnedObjects2.length; j++) {
									
									var timestamp=returnedObjects2[j].timestamp;
									var uploadPosition=returnedObjects2[j].position;
									var uploadCellId=returnedObjects2[j].cellId;
				       	        	var uploadCellName=returnedObjects2[j].cellName;
				       	        	var uploadCellLac=returnedObjects2[j].cellLac;
				       	   			var uploadLongVal = returnedObjects2[j].longitude;
				       	   			var uploadLatVal = returnedObjects2[j].latitude;
				       	   			var townVal = returnedObjects2[j].town;
				       	   			var barangayVal = returnedObjects2[j].barangay;
				       	   			var siteAddressVal = returnedObjects2[j].site_address;
				       	   			
				       	        	if(uploadLongVal !== "Nothing found") {
				       	       			uploadLongVal=parseFloat(uploadLongVal);
				       	        	} 
				       	        	
				       	   			if(uploadLatVal !== "Nothing found") {
			   	       				uploadLatVal=parseFloat(uploadLatVal);
			   	        		}  	  	  	       	        	
				       	        	
			  	  	       	    var tag = uploadPosition;
			 	       	     	if(tag === "") {
			 	       	     		tag = "none";
			 	       	     	}
			       	        	
			 	       	     	if(uploadCellId === "No returned cell id" && uploadCellLac === "No returned cell lac") {
			 	       	     		failedList.push("IMSI: " + imsi + "  MSISDN: " + msisdn);
			 	       	     	} else {
				       	     		$("#inboundRoamersTable").append("<tr><td style='text-align: center;'>"+timestamp+
	     										 "</td><td style='text-align: center;'>"+imsi+ 
	     								 		 "</td><td style='text-align: center;'>"+msisdn+
	     										 "</td><td style='text-align: center;'>"+uploadCellId+
	     										 "</td><td style='text-align: center;'>"+uploadCellLac+
	     										 "</td><td style='text-align: center;'>"+uploadCellName+
	     										 "</td><td style='text-align: center;'>"+townVal+
	     										 "</td><td style='text-align: center;'>"+barangayVal+
	     										 "</td><td style='text-align: center;'>"+siteAddressVal+
	     										 "</td><td style='text-align: center;'>"+uploadLongVal+
	     										 "</td><td style='text-align: center;'>"+uploadLatVal+
	     										 "</td><td style='text-align: center;'>"+ tag +"</td></tr>");
					       	     	rec++;
					       	     	
									if(uploadLatVal !== "Nothing found" && uploadLongVal !== "Nothing found") {
										var	marker = new L.Marker([uploadLatVal,uploadLongVal]).bindPopup("<h4 align='center'>Details</h4><label style='color: black;'>Tag:&nbsp;&nbsp;" 
	  	       	     							+ tag + "</label><br><label style='color: black;'>Cell-ID:&nbsp;&nbsp;" 
	  	       	     							+ uploadCellId + "</label><br><label style='color: black;'>Cell LAC:&nbsp;&nbsp;" 
	  	       	     							+ uploadCellLac + "</label><br><label style='color: black;'>Date:&nbsp;&nbsp;" 
	  	       	     							+ timestamp + "</label>");
	  	  	  	       	     		
	  	            		    		uploadMarkersLayerGroup.addLayer(marker);
	  	            		    		uploadMarkersArray.push(marker.getLatLng());
									}
									
									successList.push("IMSI: " + imsi + "  MSISDN: " + msisdn);
									
			 	       	     	}
			 	       	     	
  	  	  					}	
  			  	  			
							if(uploadMarkersArray.length > 0) {
								tracker = new L.Polyline(uploadMarkersArray, {
	  			  	  				color: getRandomColor(),
	  			  	  				weight: 3,
	  			  	  				opacity: 4,
	  			  	  				smoothFactor: 1
	  			  	  			});
	  	  	  					
	  	  	  					uploadMarkersLayerGroup.addLayer(tracker);
	  	  	  					uploadMarkersLayerGroup.addTo(map);
	  	  	  					uploadMarkersArray.length = 0;
							}
  	  	  				 	
  	  	  					
  	  	  				}
  	  				} 
					
					
					
  				}
     		}	
     	});	
     	
     	totalRecords = rec;
     	
     	if(!!$("#inboundRoamersTable")) {
     		 $("#inboundRoamersTable").tablesorter({sortList: [[0,1]]}); // sort descending
     		 $("#smartpaginator").show();
	    	 $("#smartpaginator").smartpaginator({ totalrecords: totalRecords,
	    		initVal: 0,
	         	recordsperpage: 100,
	         	datacontainer: 'inboundRoamersTable', 
	         	dataelement: 'tr',
	         	theme: 'red' });  
	    	
	    	 $("#inboundRoamersTable").append("</tbody></table>");
	    	 
		 } 
     	
     	alert("****** Successful Transaction ******\n\n" 
			+ successList.join("\n") + "\n\n****** Failed Transaction ****** \nCaused by: Server timed-out. Please try again!!\n\n" 
				+ failedList.join("\n"));
     	
     	
	}	
	
	
	function getRandomColor() {
		var letters = '0123456789ABCDEF'.split('');
        var color = '#';
        for (var i = 0; i < 6; i++ ) {
            color += letters[Math.round(Math.random() * 15)];
        } 
        
        return color;
	}	
	
	function getScheduleInterval() {
		
		return $.ajax({
         	type:'GET',
             url:'getScheduleInterval.action',
             contentType: 'application/json; charset=utf-8',
             dataType: 'json',
             success: function(data){
            	 	var json = (JSON.parse(JSON.stringify(data)).scheduleInterval).replace(/"/g,'');;
     	 			timeIntervalToSetSched5Hr = (json * 60 * 60 * 1000);
             return false;
             }
	    }).done(getServerDateFirstRun());
	}
	
	function getServerDateFirstRun() {
		
		return $.ajax({
         	type:'GET',
             url:'getServerDate.action',
             contentType: 'application/json; charset=utf-8',
             dataType: 'json',
             success: function(data){
            	 	var json = JSON.parse(JSON.stringify(data)).serverDate;
      				serverClock = json.replace(/"/g,'');
             return false;
             }
	    }).done(checkIfCampaignIsRunning());
	}	
	
	function checkIfCampaignIsRunning() {
		
		return $.ajax({
         	type:'GET',
             url:'checkIfCampaignIsRunning.action',
             contentType: 'application/json; charset=utf-8',
             dataType: 'json',
             success: function(data){
            	 
            	 var rSchedBean =  JSON.parse(JSON.stringify(data)).rSchedBean;
	 				if(rSchedBean === null) {
	 					isCampaignRunning = false;
	 					checkForAdHoc();
	 				} else {
	 					if(rSchedBean.status === "IN USE") {
	 						// compare date/time
	 						var dateNow = serverClock.substring(0,10).trim(), timeNow = serverClock.substring(11,16).trim();
	 						var dbDate = rSchedBean.date, dbTime = rSchedBean.time;
	 						
	 						var thisDateFinal = Date.parse(dateNow + " " + timeNow);
	 						var dbDateFinal = Date.parse(dbDate + " " + dbTime);
	 						
	 						if(Date.compare(thisDateFinal,dbDateFinal) < 0) {
	 							// check if greater than or equal to 1 hour interval
	 							var difference = Math.abs(thisDateFinal.getTime() - dbDateFinal.getTime());
	 							if(difference >= 3600000) {
	 								isCampaignRunning = false;
	 							}
	 							checkForAdHoc();
	 								
	 						} else if(Date.compare(thisDateFinal,dbDateFinal) > 0) {
	 							// check if within set interval from database
	 							var difference = Math.abs(thisDateFinal.getTime() - dbDateFinal.getTime());
	 							//alert(difference + "  " + timeIntervalToSetSched5Hr);
	 							
	 							if(difference >= timeIntervalToSetSched5Hr) {
	 								isCampaignRunning = false;
	 							}
	 							checkForAdHoc();					
	 						} else  {
	 							
	 							isCampaignRunning = true;
	 							if(isCampaignRunning) {
		 							// alert("Cannot perform Ad-Hoc because Campaign is running or has not yet finished...");
		 				 			
		 							// $("#uploadBtn").prop("disabled", true);
		 							// $("#triggerAdHocTextInputBtn").prop("disabled",true);
		 							// $("#triggerAdHocDropDownBtn").prop("disabled",true);
		 							
	 							}	
	 						}
	 					} 
 					}
             	return false;
             }
	    });	
	}	
		
	function checkForAdHoc() {
		
		return $.ajax({
         	type:'GET',
            url:'checkIfAdHocIsRunning.action',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(data){
            	
    	 		isAdhocRunning = JSON.parse(JSON.stringify(data)).isAdhocRunning;
    	 	   	 		
    	 		if(isAdhocRunning) {
    	 			// alert("Cannot perform Ad-Hoc because previous one has not yet finished...");
    	 			
					// $("#uploadBtn").prop("disabled", true);
					// $("#triggerAdHocTextInputBtn").prop("disabled",true);
					// $("#triggerAdHocDropDownBtn").prop("disabled",true);
    	 			
    	 		}
            	return false;
            }
	    });	
	}
	
	function exportTableToCSV($table, filename) {

        var $rows = $table.find('tr:has(td)'),
		
            // Temporary delimiter characters unlikely to be typed by keyboard
            // This is to avoid accidentally splitting the actual contents
            tmpColDelim = String.fromCharCode(11), // vertical tab character
            tmpRowDelim = String.fromCharCode(0), // null character

            // actual delimiter characters for CSV format
            colDelim = '","',
            rowDelim = '"\r\n"',

            // Grab text from table into CSV formatted string
            csv = '"' + $rows.map(function (i, row) {
                var $row = $(row),
                    $cols = $row.find('td');

                return $cols.map(function (j, col) {
                    var $col = $(col),
                        text = $col.text();

                    return text.replace('"', '""'); // escape double quotes

                }).get().join(tmpColDelim);

            }).get().join(tmpRowDelim)
                .split(tmpRowDelim).join(rowDelim)
                .split(tmpColDelim).join(colDelim) + '"',

            // Data URI
            csvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csv);

        $(this)
            .attr({
            'download': filename,
                'href': csvData,
              'target': '_blank'
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
	<div id="spinner" class="spinner" style="display:none;"></div>
	<div id="blankDiv" style="display:none;"></div>
	<div id="main">
		<div id="map_header">
			<div class="logo">
				<img src="../../images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;" />
			</div>
			<div class="scheduler">
				<!--<a onclick="location.href='CreateCampaign_2.jsp'" style="text-decoration: none;"/><img src= "../../images/1.png" style="margin-top: 50px; margin-left: 10px;"><br><label>Scheduler</label></a>-->
			</div>
			<div class="inboundMap">
				<!--<a onclick="location.href='InboundRoamersMapTracker_2.jsp'" style="text-decoration: none;"/><img src= "../../images/2.png" style="margin-top: 50px; margin-left: 20px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Inbound Map</label></a>-->
			</div>
			<div class="reporting">
				<!--<a onclick="location.href='InboundRoamersReportViewAndGeneration_2.jsp'" style="text-decoration: none;"/><img src= "../../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>-->
			</div>
			<div class="ad-hoc2">
				<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none; margin-top: 10px;"><label>Logout</label></a><br>
				<!--<a onclick="location.href='InboundRoamersAdHocMapTracker_2.jsp'" style="text-decoration: none;"/><img src= "../../images/4.png" style="margin-top: 10px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white; background-color: #ed145b; border-radius: 5px;">Ad-Hoc</label></a>-->
			</div>		

		</div>
		
		<div id = "map_content" style="overflow-y: auto; height: 600px;">
		
							<div id="uploadDiv" style="margin-left: 20px; margin-right: 20px; font-size: 15px; height: 45%;"><br/>
									<form method="post" accept="" id="fileUpload" name="fileUpload" enctype="multipart/form-data" style="margin-left: 20px;">
										<label style="color: black; font-size: 15px;">PSI Application Status:&nbsp;</label><label id="psiStatusLabel" style="color: black; margin-left: 50px; font-size: 15px;"></label><br/>
										<label style="color: black;">Choose one method for Ad-Hoc:&nbsp;&nbsp;</label>
										<div style="float: right; display: inline-block; ">
											<a href="#" class="exportToCSV" style="border-radius: 8px; font-size: x-small; font-family: sans-serif; background-color: #ed145b; color: white; text-decoration: none !important; ">  Export To CSV  </a>
										</div>
										<!--<input type="button" value="Export Ad-Hoc Report" id="exportAdHocBtn" class="button-color" style="margin-left: 785px; margin-top: 5px;"/>--><br>
										<div>
											<table>
												<tr>
													<td>
														<div id="textInputDiv" style="margin-top: 5px;">
															<input type="radio" name="selectionRadio" id="textInputRadioButton" value="textInput" style="vertical-align: middle; margin-top: -1px;"/><label style="color: black;">&nbsp;Text Input</label><br>
															<div id="textInputDiv2">
																<label style="font-size: x-small; margin-left: 20px; color: black;">Format: [IMSI],[MSISDN]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">If IMSI is only available, the format should be [IMSI],[blank]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">example: XXXXXXXXXXXX, (place a comma after the value)</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">If MSISDN is only available, the format should be [blank],[MSISDN]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">example: ,XXXXXXXXXXXX (place a comma before the value)</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">Record(s) will not be processed for any deviation on the required format.</label><br>
																<input type="text" style="margin-left: 20px; width: 300px; height: 30px;" id="addIMSI" name="addIMSI" size="20%" />
																<input type="button" class="button-color" value="Add" id="addImsiBtn" style="margin-left: 10px;"/>&nbsp;&nbsp;&nbsp;<input type="button" class="button-color" value="Remove" id="removeImsiBtn" /><br>
																<select id="textInputMultiple" multiple="multiple" style="width: 300px; margin-left: 20px; margin-top: 10px;"/>&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="button-color" value="Trigger Ad-Hoc" id="triggerAdHocTextInputBtn" style="margin-left: 10px; "/>&nbsp;&nbsp;&nbsp;<input type="button" class="button-color" value="Plot on Map" id="plotOnMapTextInputBtn" /><br>
															</div>
														</div>
													</td>
												</tr>
												<tr>
													<td>
														<div id="uploadInputDiv" style="margin-top: 10px;">
															<input type="radio" name="selectionRadio" id="uploadRadioButton" value="uploadInput" style="vertical-align: middle; margin-top: -1px;"/><label style="color: black;">&nbsp;Upload</label><br>
															<div id="uploadInputDiv2">
																<label style="font-size: x-small; margin-left: 20px; color: black;">Upload CSV file with .csv extension only</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">Record format: [IMSI],[MSISDN]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">At least one value is required. If one of the values is not available, just replace with a blank value.</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">No IMSI: ,XXXXXXXXXXXX (place a comma before the value)</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">No MSISDN: XXXXXXXXXXXX, (place a comma after the value)</label><br>
																
																<label style="margin-left: 10px;"/><input type="file" name="uploadFile" required="required" style="margin-top: 5px; margin-left: 10px; background-color: #058cbb;" />
																<input type="button" class="button-color" value="Upload" id="uploadBtn" style="margin-left: 10px; margin-top: 10px;"/>&nbsp;&nbsp;<input type="button" class="button-color" value="Plot On Map" id="plotOnMapUploadBtn" />
															</div>
														</div>
													</td>
												</tr>											
											</table>
										</div>
									</form>
							</div>
<!-- 			<div style="margin-top: 20px; margin-right: 10px; margin-bottom: 50px; float: right;"><br/>
				<input type="submit" value="Upload" id="uploadBtn" class="button" style="border-radius: 8px;"/>
			</div>--><br/>	
			<div id="map" name="map" style="overflow: hidden; margin-top: 100px; height:100%; width: 100%; margin-left: 2px; margin-right: 2px; border: 1px solid #AAA;"></div>
			<div id="tableDiv" style="margin-top: 10px;" align="center"></div>
			<div id="smartpaginator"></div><br/>
		</div>
		<div id="poweredByDiv"><img src="../../images/poweredby.png"></div>
	</div>
</body>
</html>