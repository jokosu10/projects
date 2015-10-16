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
	
	var uploadMarkersLayerGroup = L.layerGroup(); 
	var uploadMarkersArray = new Array();
	var totalRecords = 0;
	
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	if(sessionId === "" || sessionId == null) {
		$("#blankDiv").show();
		alert("Your current logged session has expired or is not logged-in. Please login to view this page. Redirecting to Login page..");
		location.href="${pageContext.request.contextPath}/jsp/Login.jsp";
		$("#blankDiv").hide();
	} else if(decodeURIComponent(getParamsFromUrl("loginId")) !== "null") {
		processPageFromVar();
	} else {
		processPage();
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
		
		//var host = "localhost/Tiles/Tiles";
		var host = "10.8.72.12/roamware/Tiles"; // public of .53	

		var sessionName2 = decodeURIComponent(getParamsFromUrl("loginName"));
		var sessionId2 = decodeURIComponent(getParamsFromUrl("loginId"));
		var sessionType2 = decodeURIComponent(getParamsFromUrl("loginType"));
		
		rewriteUrl();
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=InboundRoamersAdHocMapTracker.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
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
			continousWorld: "true",
		    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
		}).addTo(map); 	
		
		$("#dropDownInputDiv2").show();
		
		/** ---------- DROP DOWN  INPUT -------- **/
		
		$("#plotOnMapDropDownInputBtn").click(function() {
			
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
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
		/**--------- END DROPDOWN INPUT --------- **/
		$(".exportToCSV").click(function(event) {
		    exportTableToCSV.apply(this, [$('#tableDiv>table'), 'adhoc_export.csv']);
		    
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Download_AdHoc_Report&page=InboundRoamersAdHocMapTracker.jsp',
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
		var sessionType = "<s:property value='%{#session.loginType}'/>";
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=InboundRoamersAdHocMapTracker.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
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
			continousWorld: "true",
		    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
		}).addTo(map); 	
		
		$("#dropDownInputDiv2").show();

		
		/** ---------- DROP DOWN  INPUT -------- **/
		
		$("#plotOnMapDropDownInputBtn").click(function() {
			
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Plot_On_Map&page=InboundRoamersAdHocMapTracker.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			alert("Checking database for updated values! Please wait....");
			$("#spinner").show();
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
		/**--------- END DROPDOWN INPUT --------- **/
		$(".exportToCSV").click(function(event) {
		    exportTableToCSV.apply(this, [$('#tableDiv>table'), 'adhoc_export.csv']);
		    
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Download_AdHoc_Report&page=InboundRoamersAdHocMapTracker.jsp',
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
     	    console.log(k,v);
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
	         	length: 10,
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
	
	function exportTableToCSV($table, filename) {

        var $rows = $table.find('tr:has(td),tr:has(th)'),
		
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



  //}

});  
</script>
</head>
<body>
	<div id="spinner" class="spinner" style="display:none;"></div>
	<div id="main">
		<div id="map_header">
			<div class="logo">
				<img src="../images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;">
			</div>
		
			<div class="inboundMap_reg">
				<a onclick="location.href='InboundRoamersMapTracker.jsp'" style="text-decoration: none;"/><img src= "../images/2.png" style="margin-top: 50px; margin-left: 20px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Inbound Map</label></a>
			</div>
			<div class="reporting_reg">
				<a id="report_link" style="text-decoration: none;"/><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>			
			</div>
			<div class="ad-hoc_reg">
			<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none;"><label>Logout</label></a><br>
				<a onclick="location.href='InboundRoamersAdHocMapTracker.jsp'" style="text-decoration: none;" /><img src= "../images/4.png" style="margin-top: 10px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white; background-color: #ed145b; border-radius: 5px;">Ad-Hoc</label></a>
			</div>	
		</div>
		
		<div id = "map_content" style="overflow-y: auto; height: 600px;">
			<div id="uploadDiv" style="margin-left: 20px; margin-right: 20px; font-size: 15px; height: 20%;"><br/>
					<label style="color: black;">View Ad-Hoc Results:&nbsp;&nbsp;</label>
					
					<div style="float: right; margin-top: 5px;">
						<input type="button" class="button-color" value="Plot on Map" id="plotOnMapDropDownInputBtn" />
						<div style="display: inline-block; margin-left: 15px;">
							<a href="#" class="exportToCSV" style="border-radius: 8px; font-size: x-small; font-family: sans-serif; background-color: #ed145b; color: white; text-decoration: none !important; ">  Export To CSV  </a>
						</div>
					</div>	
					<br>
			</div>
			<br/>	
			<div id="map" name="map" style="overflow: hidden; margin-top: 10px; height:100%; width: 100%; margin-left: 2px; margin-right: 2px; border: 1px solid #AAA;"></div>
			<div id="tableDiv" style="margin-top: 10px;" align="center"></div>
			<div id="smartpaginator"></div><br/>	
		</div>
		<div id="poweredByDiv"><img src="../images/poweredby.png"></div>
	</div>
</body>
</html>