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

<script type="text/javascript" src="../js/leaflet.js"></script>
<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="../js/date.js"></script>
<script type="text/javascript" src="../js/smartpaginator.js"></script>

<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../css/leaflet.css">
<link rel="stylesheet" type="text/css" href="../css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">

<script type="text/javascript">
$("#document").ready(function() {
	
	var isCampaignRunning = false, isAdhocRunning = false;
	var timeIntervalToSetSched5Hr = null, serverClock = null;
	var uploadMarkersLayerGroup = L.layerGroup(); 
	var uploadMarkersArray = new Array();
	
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	var sessionType = "<s:property value='%{#session.loginType}'/>";
	
	if(sessionId === "" || sessionId == null) {
		$("#blankDiv").show();
		alert("Your current logged session has expired or is not logged-in. Please login to view this page. Redirecting to Login page..");
		location.href="logoutUser.action";
		$("#blankDiv").hide();
	} else if (decodeURIComponent(getParamsFromUrl("loginId")) !== "null") {
		processPageFromVar();
	} else {
		
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
	   
		//var host = "localhost/Tiles/Tiles";
		var host = "10.8.72.12/roamware/Tiles"; // public of .53	
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=InboundRoamersAdHocMapTracker_Admin.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});			
		
		getScheduleInterval();
		checkPSIStatus(); // call upon document load to get & display status
		
		// get all operators
		$.get("inboundRoamerSelectOperator.action?mode=international", function(data) {
			
			$("#selectNetwork").append($('<option>', {
	 			    value: "----",
	 			    text: "Select Network"
	 			}));
			
	 		var json = JSON.stringify(data);
	  		var jsonArray = eval('('+JSON.parse(json).jsonResponse.split(",") + ')');
	  		
			for(var j = 0; j < jsonArray.length; j++) {
	 			$("#selectNetwork").append($('<option>', {
	 			    value: jsonArray[j].mncMcc,
	 			   text: jsonArray[j].destination + " - " + jsonArray[j].mncMcc + " - " + jsonArray[j].networkOpName
	 			}));
	 		}
		});	
		
		
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
			continousWorld: "false",
		    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
		}).addTo(map); 	
		
		var isInternationalRP = true;
		var totalRecords = 0;
		
		$("#dropDownInputRadioButton").attr('checked', true);
		$("#textInputDiv2").hide();
		$("#uploadInputDiv2").hide();
		$("#dropDownInputDiv2").show();
		
		$("#fileUpload input[name='selectionRadio']").change(function () {
		    if ($(this).val() === "dropDownInput" && $(this).is(":checked")) {
		    
		    	$("#textInputDiv2").hide();
		    	$("#uploadInputDiv2").hide();
		    	$("#dropDownInputDiv2").show();
		    	
		    } else if ($(this).val() === "textInput" && $(this).is(":checked")) {
		    	
		    	$("#textInputDiv2").show();
		    	$("#uploadInputDiv2").hide();
		    	$("#dropDownInputDiv2").hide();
		    	
		    } else {
		    	
		    	$("#textInputDiv2").hide();
		    	$("#uploadInputDiv2").show();
		    	$("#dropDownInputDiv2").hide();
		    	
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

			if(isInternationalRP) {
				var yes = confirm("Please note that only international imsis' (those not starting with '515') \nand msisdns not starting with '63917' prefix or any Globe local msisdn prefix will only be processed");
				if(yes) {
					
					$.ajax({
				     	type:'POST',
				         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Upload_File&page=InboundRoamersAdHocMapTracker_Admin.jsp',
				         contentType: 'application/json; charset=utf-8',
				         dataType: 'json'
					});
					
					$("#spinner").show(); // show spinner 
					$.ajax({
						url: "roamerLocatorFileUpload.action?isInternationalRP="+isInternationalRP,
						type: "POST",
						data: fd,
						enctype: "multipart/form-data",
						processData: false,
						contentType: false,
						success: function(response){
							$("#spinner").hide();
							var result = JSON.parse(JSON.stringify(response)).jsonResult.replace(/"/g,'');
							
							if(result.indexOf("failed") > 0) {
								// if success, disable buttons
								alert(result);
							} else {
								alert(result);
								$("#uploadBtn").prop("disabled", true);
								$("#triggerAdHocTextInputBtn").prop("disabled",true);
								$("#triggerAdHocDropDownBtn").prop("disabled",true);
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
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$.ajax({
				url: "checkAdHocData.action",
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
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$("#smartpaginator").hide();
			$.ajax({
				url: "checkAdHocData.action",
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
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Trigger_Adhoc&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			// call ajax
			$("#spinner").show();
			$("#smartpaginator").hide();
			var values = $.map($('#textInputMultiple option'), function(e) { return e.value; });
			$.ajax({
				url: "triggerAdHoc.action?inputMode=textInput&stringInput="+ values.join("-"),
				type: "POST",
				contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
				success: function(response){
					$("#spinner").hide();
					var result = JSON.parse(JSON.stringify(response)).jsonResponse.replace(/"/g,'');
					
					if(result.indexOf("failed") >= 0) {
						alert(result);
					} else {
						alert(result);
						$("#textInputMultiple").children().remove();
						$("#uploadBtn").prop("disabled", true);
						$("#triggerAdHocTextInputBtn").prop("disabled",true);
						$("#triggerAdHocDropDownBtn").prop("disabled",true);
					}
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
				
				var localMsisdnListArr =  ["0813","0907","0908","0909","0910","0912","0918","0919","0920","0921","0928","0929","0930","0938","0939","0946","0947","0948","0949","0989","0998","0999","0922","0923","0925","0932","0933","0934","0942","0943","0817","0905","0906","0915","0916","0917","0926","0927","0935","0936","0937","0994","0996","0997","0977","0979","0973","0974","0975"
					     					,"63913","63907","63908","63909","63910","63912","63918","63919","63920","63921","63928","63929","63930","63938","63939","63946","63947","63948","63949","63989","63998","63999","63922","63923","63925","63932","63933","63934","63942","63943","63817","63905","63906","63915","63916","63917","63926","63927","63935","63936","63937","63994","63996","63997","63977","63979","63973","63974","63975"];
				
				if(imsi !== "" && msisdn === "") {
					if(imsi.substring(0,3) === "515") {
						alert("Cannot input local imsi starting with '515'");
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
								validMsisdn = false;
								break;
							} else {
								if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = false;
									break;
								} else {
									validMsisdn = true;
								}
							}
						}
						
						if(validMsisdn) {
							$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
							$("#addIMSI").focus();
							$("#addIMSI").val("");
						} else {
							alert("Cannot input local msisdn starting with '" + localMsisdnListArr[i] + "'");
							$("#addIMSI").focus();
						}
					}
				} else if(imsi !== "" && msisdn !== "") {
					if(imsi.substring(0,3) === "515") {
						alert("Cannot input local imsi starting with '515'");
						$("#addIMSI").focus();
					} else {
						var validMsisdn = false;
						for(var i = 0; i < localMsisdnListArr.length; i++) {
							if(msisdn.substring(0,5).indexOf(localMsisdnListArr[i]) >= 0) {
								validMsisdn = false;
								break;
							} else {
								if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = false;
									break;
								} else {
									validMsisdn = true;
								}
							}
						}
						
						if(validMsisdn) {
							$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
							$("#addIMSI").focus();
							$("#addIMSI").val("");
						} else {
							alert("Cannot input local msisdn starting with '" + localMsisdnListArr[i] + "'");
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
		
		/** ---------- DROP DOWN  INPUT -------- **/
		
		$("#plotOnMapDropDownInputBtn").click(function() {
			
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$("#smartpaginator").hide();
			$.ajax({
				url: "checkAdHocData.action",
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
		
		$("#triggerAdHocDropDownBtn").click(function() {
			var val  = confirm("Selection from 'Drop-Down Input' will generate many points\n and could clutter the map. Do you want to proceed?");
			$("#smartpaginator").hide();
			if(val) {
				
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Trigger_Adhoc&page=InboundRoamersAdHocMapTracker_Admin.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				// ajax call for adhoc
				$("#spinner").show();
				$.ajax({
					url: "triggerAdHoc.action?inputMode=dropDownInput&stringInput="+ $("#selectNetwork").val(),
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						$("#spinner").hide();
						var result = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
						if(result.indexOf("Success") >= 0) {
							// if success, disable buttons
							alert(result);
							$("#uploadBtn").prop("disabled", true);
							$("#triggerAdHocTextInputBtn").prop("disabled",true);
							$("#triggerAdHocDropDownBtn").prop("disabled",true);
						} else {
							alert(result);
						}
					}
				}); 
				return false;
			}
		});	
		
		$(".exportToCSV").click(function(event) {
		    exportTableToCSV.apply(this, [$('#tableDiv>table'), 'adhoc_export.csv']);
		    
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Download_AdHoc_Report&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
		}); 
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
					+"&loginType="+ sessionType2
					+"&loginName="+ sessionName2, "_blank");
		});
	   
   }
   
   function processPage() {
	   
		//var host = "localhost/Tiles/Tiles";
		var host = "10.8.72.12/roamware/Tiles"; // public of .53	
		
		var sessionName = "<s:property value='%{#session.loginName}'/>";
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=InboundRoamersAdHocMapTracker_Admin.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});			
		
		getScheduleInterval();
		checkPSIStatus(); // call upon document load to get & display status
		
		// get all operators
		$.get("inboundRoamerSelectOperator.action?mode=international", function(data) {
			
			$("#selectNetwork").append($('<option>', {
	 			    value: "----",
	 			    text: "Select Network"
	 			}));
			
	 		var json = JSON.stringify(data);
	  		var jsonArray = eval('('+JSON.parse(json).jsonResponse.split(",") + ')');
	  		
			for(var j = 0; j < jsonArray.length; j++) {
	 			$("#selectNetwork").append($('<option>', {
	 			    value: jsonArray[j].mncMcc,
	 			   text: jsonArray[j].destination + " - " + jsonArray[j].mncMcc + " - " + jsonArray[j].networkOpName
	 			}));
	 		}
		});	
		
		
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
			continousWorld: "false",
		    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
		}).addTo(map); 	
		
		var isInternationalRP = true;
		var totalRecords = 0;
		
		$("#dropDownInputRadioButton").attr('checked', true);
		$("#textInputDiv2").hide();
		$("#uploadInputDiv2").hide();
		$("#dropDownInputDiv2").show();
		
		$("#fileUpload input[name='selectionRadio']").change(function () {
		    if ($(this).val() === "dropDownInput" && $(this).is(":checked")) {
		    
		    	$("#textInputDiv2").hide();
		    	$("#uploadInputDiv2").hide();
		    	$("#dropDownInputDiv2").show();
		    	
		    } else if ($(this).val() === "textInput" && $(this).is(":checked")) {
		    	
		    	$("#textInputDiv2").show();
		    	$("#uploadInputDiv2").hide();
		    	$("#dropDownInputDiv2").hide();
		    	
		    } else {
		    	
		    	$("#textInputDiv2").hide();
		    	$("#uploadInputDiv2").show();
		    	$("#dropDownInputDiv2").hide();
		    	
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

			if(isInternationalRP) {
				var yes = confirm("Please note that only international imsis' (those not starting with '515') \nand msisdns not starting with '63917' prefix or any Globe local msisdn prefix will only be processed");
				if(yes) {
					
					//var sessionName = "<s:property value='%{#session.loginName}'/>";
					$.ajax({
				     	type:'POST',
				         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Upload_File&page=InboundRoamersAdHocMapTracker_Admin.jsp',
				         contentType: 'application/json; charset=utf-8',
				         dataType: 'json'
					});
					
					$("#spinner").show(); // show spinner 
					$.ajax({
						url: "roamerLocatorFileUpload.action?isInternationalRP="+isInternationalRP,
						type: "POST",
						data: fd,
						enctype: "multipart/form-data",
						processData: false,
						contentType: false,
						success: function(response){
							$("#spinner").hide();
							var result = JSON.parse(JSON.stringify(response)).jsonResult.replace(/"/g,'');
							
							if(result.indexOf("failed") > 0) {
								// if success, disable buttons
								alert(result);
							} else {
								alert(result);
								$("#uploadBtn").prop("disabled", true);
								$("#triggerAdHocTextInputBtn").prop("disabled",true);
								$("#triggerAdHocDropDownBtn").prop("disabled",true);
							}
							
						}
					}); 
				}
			}
			return false;
		});
		
		$("#plotOnMapUploadBtn").click(function() {
			
			//var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$.ajax({
				url: "checkAdHocData.action",
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
		
			//var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$("#smartpaginator").hide();
			$.ajax({
				url: "checkAdHocData.action",
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
			
			//var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Trigger_Adhoc&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			// call ajax
			$("#spinner").show();
			$("#smartpaginator").hide();
			var values = $.map($('#textInputMultiple option'), function(e) { return e.value; });
			$.ajax({
				url: "triggerAdHoc.action?inputMode=textInput&stringInput="+ values.join("-"),
				type: "POST",
				contentType: 'application/json; charset=utf-8',
	            dataType: 'json',
				success: function(response){
					$("#spinner").hide();
					var result = JSON.parse(JSON.stringify(response)).jsonResponse.replace(/"/g,'');
					
					if(result.indexOf("failed") >= 0) {
						alert(result);
					} else {
						alert(result);
						$("#textInputMultiple").children().remove();
						$("#uploadBtn").prop("disabled", true);
						$("#triggerAdHocTextInputBtn").prop("disabled",true);
						$("#triggerAdHocDropDownBtn").prop("disabled",true);
					}
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
				
				var localMsisdnListArr =  ["0813","0907","0908","0909","0910","0912","0918","0919","0920","0921","0928","0929","0930","0938","0939","0946","0947","0948","0949","0989","0998","0999","0922","0923","0925","0932","0933","0934","0942","0943","0817","0905","0906","0915","0916","0917","0926","0927","0935","0936","0937","0994","0996","0997","0977","0979","0973","0974","0975"
					     					,"63913","63907","63908","63909","63910","63912","63918","63919","63920","63921","63928","63929","63930","63938","63939","63946","63947","63948","63949","63989","63998","63999","63922","63923","63925","63932","63933","63934","63942","63943","63817","63905","63906","63915","63916","63917","63926","63927","63935","63936","63937","63994","63996","63997","63977","63979","63973","63974","63975"];
				
				if(imsi !== "" && msisdn === "") {
					if(imsi.substring(0,3) === "515") {
						alert("Cannot input local imsi starting with '515'");
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
								validMsisdn = false;
								break;
							} else {
								if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = false;
									break;
								} else {
									validMsisdn = true;
								}
							}
						}
						
						if(validMsisdn) {
							$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
							$("#addIMSI").focus();
							$("#addIMSI").val("");
						} else {
							alert("Cannot input local msisdn starting with '" + localMsisdnListArr[i] + "'");
							$("#addIMSI").focus();
						}
					}
				} else if(imsi !== "" && msisdn !== "") {
					if(imsi.substring(0,3) === "515") {
						alert("Cannot input local imsi starting with '515'");
						$("#addIMSI").focus();
					} else {
						var validMsisdn = false;
						for(var i = 0; i < localMsisdnListArr.length; i++) {
							if(msisdn.substring(0,5).indexOf(localMsisdnListArr[i]) >= 0) {
								validMsisdn = false;
								break;
							} else {
								if(msisdn.substring(0,4).indexOf(localMsisdnListArr[i]) >= 0) {
									validMsisdn = false;
									break;
								} else {
									validMsisdn = true;
								}
							}
						}
						
						if(validMsisdn) {
							$("#textInputMultiple").append(new Option(imsiVal, imsiVal, false, false));
							$("#addIMSI").focus();
							$("#addIMSI").val("");
						} else {
							alert("Cannot input local msisdn starting with '" + localMsisdnListArr[i] + "'");
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
		
		/** ---------- DROP DOWN  INPUT -------- **/
		
		$("#plotOnMapDropDownInputBtn").click(function() {
			
			//var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
			$("#smartpaginator").hide();
			$.ajax({
				url: "checkAdHocData.action",
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
		
		$("#triggerAdHocDropDownBtn").click(function() {
			var val  = confirm("Selection from 'Drop-Down Input' will generate many points\n and could clutter the map. Do you want to proceed?");
			$("#smartpaginator").hide();
			if(val) {
				
				//var sessionName = "<s:property value='%{#session.loginName}'/>";
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Trigger_Adhoc&page=InboundRoamersAdHocMapTracker_Admin.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				// ajax call for adhoc
				$("#spinner").show();
				$.ajax({
					url: "triggerAdHoc.action?inputMode=dropDownInput&stringInput="+ $("#selectNetwork").val(),
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						$("#spinner").hide();
						var result = (JSON.parse(JSON.stringify(response)).jsonResponse).replace(/"/g,'');
						if(result.indexOf("Success") >= 0) {
							// if success, disable buttons
							alert(result);
							$("#uploadBtn").prop("disabled", true);
							$("#triggerAdHocTextInputBtn").prop("disabled",true);
							$("#triggerAdHocDropDownBtn").prop("disabled",true);
						} else {
							alert(result);
						}
					}
				}); 
				return false;
			}
		});	
		
		$(".exportToCSV").click(function(event) {
		    exportTableToCSV.apply(this, [$('#tableDiv>table'), 'adhoc_export.csv']);
		    
			//var sessionName = "<s:property value='%{#session.loginName}'/>";
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Download_AdHoc_Report&page=InboundRoamersAdHocMapTracker_Admin.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
		}); 
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
					+"&loginType="+ sessionType
					+"&loginName="+ sessionName, "_blank");
		});
   }
   
	// add markers and polylines
	function plotOnMap(response) {
		
		$("#inboundRoamersTable").remove();
		var responseData = JSON.stringify(response);
     	var result = $.parseJSON(responseData);
		var rec = 0;
     	
		$("#smartpaginator").hide();
     	$("#tableDiv").append("<table class='tablesorter' id='inboundRoamersTable'><thead><tr>"+ 
				  "<th style='text-align: center;'>DATE</th>"+
				  "<th style='text-align: center;'>COUNTRY</th>"+
				  "<th style='text-align: center;'>OPERATOR</th>"+				  
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
		//display the key and value pair
		//console.log(k,v);
		var returnedObjects = eval(v);
				
		if(returnedObjects != null) {
		
			for(var i = 0; i < returnedObjects.length; i++) {
				
					var imsi = returnedObjects[i].imsi;
					var msisdn = returnedObjects[i].msisdn;
  	  				var operatorCountry = returnedObjects[i].operatorCountry;
  	  				var operatorName = returnedObjects[i].operatorName;					
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
			       	        	
			       	     		$("#inboundRoamersTable").append("<tr><td style='text-align: center;'>"+timestamp+
	     										 				 "</td><td style='text-align: center;'>"+operatorCountry+
	       	     										 		 "</td><td style='text-align: center;'>"+operatorName+
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
	 							
	 							if(difference >= timeIntervalToSetSched5Hr) {
	 								isCampaignRunning = false;
	 							}
	 							checkForAdHoc();					
	 						} else  {
	 							
	 							isCampaignRunning = true;
	 							if(isCampaignRunning) {
		 							alert("Cannot perform Ad-Hoc because Campaign is running or has not yet finished...");
		 				 			
		 							$("#uploadBtn").prop("disabled", true);
		 							$("#triggerAdHocTextInputBtn").prop("disabled",true);
		 							$("#triggerAdHocDropDownBtn").prop("disabled",true);
		 							
		 							/*$("#textInputDiv2").hide();
		 				 			$("#textInputDiv").children().prop("disabled",true);
		 				 			$("#textInputDiv2").children().prop("disabled",true);
		 				 			$("#uploadInputDiv2").hide();
		 				 			$("#uploadInputDiv").children().prop("disabled", true);
		 				 			$("#uploadInputDiv2").children().prop("disabled",true);
		 				 			$("#dropDownInputDiv2").children().prop("disabled",true);*/
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
    	 			alert("Cannot perform Ad-Hoc because previous one has not yet finished...");
    	 			
					$("#uploadBtn").prop("disabled", true);
					$("#triggerAdHocTextInputBtn").prop("disabled",true);
					$("#triggerAdHocDropDownBtn").prop("disabled",true);
    	 			
    	 			/*$("#textInputDiv2").hide();
    	 			$("#textInputDiv").children().prop("disabled",true);
    	 			$("#textInputDiv2").children().prop("disabled",true);
    	 			$("#uploadInputDiv2").hide();
    	 			$("#uploadInputDiv").children().prop("disabled", true);
    	 			$("#uploadInputDiv2").children().prop("disabled",true);
    	 			$("#dropDownInputDiv2").children().prop("disabled",true); */
    	 		}
            	return false;
            }
	    });	
	}
	
	function exportTableToCSV($table, filename) {

        var $rows = $table.find('tr:has(td), tr:has(th)'),
		
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
                    $cols = $row.find('td,th');

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
				<img src="../images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;" />
			</div>
			<div class="scheduler">
				<a onclick="location.href='CreateCampaign.jsp'" style="text-decoration: none;"/><img src= "../images/1.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Scheduler</label></a>
			</div>
			<div class="inboundMap">
				<a onclick="location.href='InboundRoamersMapTracker_Admin.jsp'" style="text-decoration: none;"/><img src= "../images/2.png" style="margin-top: 50px; margin-left: 25px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Inbound Map</label></a>
			</div>
			<div class="reporting">
				<a id="report_link" style="text-decoration: none;"/><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>			
			</div>
			<div class="ad-hoc">
				<a onclick="location.href='InboundRoamersAdHocMapTracker_Admin.jsp'" style="text-decoration: none;"/><img src= "../images/4.png" style="margin-top: 50px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; background-color: #ed145b; border-radius: 5px;">Ad-Hoc</label></a>
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
		
		<div id = "map_content" style="overflow-y: auto; height: 600px;">
		
							<div id="uploadDiv" style="margin-left: 20px; margin-right: 20px; font-size: 15px; height: 45%;"><br/>
									<form method="post" accept="" id="fileUpload" name="fileUpload" enctype="multipart/form-data" style="margin-left: 20px;">
										<label style="color: black; font-size: 15px;">PSI Application Status:&nbsp;</label><label id="psiStatusLabel" style="color: black; margin-left: 50px; font-size: 15px;"></label><br/>
										<label style="color: black;">Choose one method for Ad-Hoc:&nbsp;&nbsp;</label>
										<div style="float: right; display: inline-block; ">
											<a href="#" class="exportToCSV" style="border-radius: 8px; font-size: x-small; font-family: sans-serif; background-color: #ed145b; color: white; text-decoration: none !important; ">  Export To CSV  </a>
										</div>
										<div>
											<table>
												<tr>
													<td>
														<div id="dropDownInput">
															<input type="radio" name="selectionRadio" id="dropDownInputRadioButton" value="dropDownInput" style="vertical-align: middle; margin-top: -1px;"/><label style="color: black;">&nbsp;Selection Input</label><br>
															<div id="dropDownInputDiv2"><label style="font-size: small; margin-left: 20px; color: black;">Operator:</label><select id="selectNetwork" name="selectNetwork" style="margin-left: 20px; color: white; width: 310px; max-width: 310px;"></select>
																<input type="button" class="button-color" value="Trigger Ad-Hoc" id="triggerAdHocDropDownBtn" style="margin-left: 10px; "/>&nbsp;&nbsp;&nbsp;<input type="button" class="button-color" value="Plot on Map" id="plotOnMapDropDownInputBtn" /><br>
															</div>
														</div>
													</td>
												</tr>
												<tr>
													<td>
														<div id="textInputDiv" style="margin-top: 5px;">
															<input type="radio" name="selectionRadio" id="textInputRadioButton" value="textInput" style="vertical-align: middle; margin-top: -1px;"/><label style="color: black;">&nbsp;Text Input</label><br>
															<div id="textInputDiv2">
																<label style="font-size: x-small; margin-left: 20px; color: black;">Format: [IMSI],[MSISDN]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">If IMSI is only available, the format should be [IMSI],[blank]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">example: 5150289056789, (place a comma after the value)</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">If MSISDN is only available, the format should be [blank],[MSISDN]</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">example: ,639056781345 (place a comma before the value)</label><br>
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
																<label style="font-size: x-small; margin-left: 20px; color: black;">No IMSI: ,639056781345 (place a comma before the value)</label><br>
																<label style="font-size: x-small; margin-left: 20px; color: black;">No MSISDN: 5150289056789, (place a comma after the value)</label><br>
																
																<label style="margin-left: 10px;"/><input type="file" name="uploadFile" accept=".csv" required="required" style="margin-top: 5px; margin-left: 10px; background-color: #058cbb;" />
																<input type="button" class="button-color" value="Upload" id="uploadBtn" style="margin-left: 10px; margin-top: 10px;"/>&nbsp;&nbsp;<input type="button" class="button-color" value="Plot On Map" id="plotOnMapUploadBtn" />
															</div>
														</div>
													</td>
												</tr>											
											</table>
										</div>
									</form>
							</div>
			<br/>	
			<div id="map" name="map" style="overflow: hidden; margin-top: 120px; height:100%; width: 100%; margin-left: 2px; margin-right: 2px; border: 1px solid #AAA;"></div>
			<div id="tableDiv" style="margin-top: 10px;" align="center"></div>
			<br/>
			<div id="smartpaginator"></div><br/>
		</div>
		<div id="poweredByDiv"><img src="../images/poweredby.png"><a onclick="location.href='admin/Login.jsp'" style="text-decoration: none; cursor: default;" /><label style="color: #058cbb;">..</label></a></div>
	</div>
</body>
</html>