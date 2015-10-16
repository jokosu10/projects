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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Roamer Locator Map Tracker [ View Users Log Page ] </title>
<head>

<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="../js/smartpaginator.js"></script>

<link rel= "stylesheet" type="text/css" href="../css/datepicker.css">
<link rel="stylesheet" type="text/css" href="../css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">
<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">

<s:head/>
<script type="text/javascript">
$("#document").ready(function() {
	
	$("#tableDiv").hide();
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	var sessionType = "<s:property value='%{#session.loginType}'/>";
	
	if(sessionId === "" || sessionId == null) {
		$("#blankDiv").show();
		alert("Your current logged session has expired or is not logged-in. Please login to view this page. Redirecting to Login page..");
		location.href="logoutUser.action";
		$("#blankDiv").hide();
	} else if(decodeURIComponent(getParamsFromUrl("loginId")) !== "null") {
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
	   
		// show logs in table
		$("#smartpaginator").hide();
		
		var dpicker = $("#datepicker").datepicker();
		dpicker.on("changeDate", function(e) {
		    dpicker.datepicker("hide");
		});
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=UserLogs.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		$("#searchLogs").click(function() {
			
			if($('#logsUsernameSearch').val() !== "" || $('#selAction').val() !== "" || $('#datepicker').val() !== "") {
				
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Search_User&page=UserLogs.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				$("#inboundRoamerLogsTable").remove();
				$("#smartpaginator").hide();
				$("#spinner").show();
				
				$.ajax({
					url: "viewUserLogs.action?adminType="+ sessionType2+"&userName="+$('#logsUsernameSearch').val()+"&action="+$('#selAction').val()+"&dateSearch="+$('#datepicker').val(),
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						
						var result = (JSON.stringify(response));
						console.log(result);
						if(result == null || result === "null" || result === '{"viewLogsReturn":"[]"}') {
							$("#spinner").hide();
							alert("No data to display!!");
						} else {
							showTable(result);
						}
						$("#logsUsernameSearch").val("");
						$('#selAction').prop('selectedIndex',0);
						
					}	
				});	
			} else {
				alert("Please fill-up one or more of the fields/parameters!");
			}
			
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
					+"&loginType="+ sessionType2
					+"&loginName="+ sessionName2, "_blank");
		});
   }
   
   function processPage() {
	   
		// show logs in table
		$("#smartpaginator").hide();
		
		var dpicker = $("#datepicker").datepicker();
		dpicker.on("changeDate", function(e) {
		    dpicker.datepicker("hide");
		});
		
		var sessionName = "<s:property value='%{#session.loginName}'/>";
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=UserLogs.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		$("#searchLogs").click(function() {
			
			if($('#logsUsernameSearch').val() !== "" || $('#selAction').val() !== "" || $('#datepicker').val() !== "") {
				
				$.ajax({
			     	type:'POST',
			         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Search_User&page=UserLogs.jsp',
			         contentType: 'application/json; charset=utf-8',
			         dataType: 'json'
				});
				
				$("#inboundRoamerLogsTable").remove();
				$("#smartpaginator").hide();
				$("#spinner").show();
				
				$.ajax({
					url: "viewUserLogs.action?adminType="+ sessionType+"&userName="+$('#logsUsernameSearch').val()+"&action="+$('#selAction').val()+"&dateSearch="+$('#datepicker').val(),
					type: "POST",
					contentType: 'application/json; charset=utf-8',
		            dataType: 'json',
					success: function(response){
						
						var result = (JSON.stringify(response));
						console.log(result);
						if(result == null || result === "null" || result === '{"viewLogsReturn":"[]"}') {
							$("#spinner").hide();
							alert("No data to display!!");
						} else {
							showTable(result);
						}
						$("#logsUsernameSearch").val("");
						$('#selAction').prop('selectedIndex',0);
						
					}	
				});	
			} else {
				alert("Please fill-up one or more of the fields/parameters!");
			}
			
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
					+"&loginType="+ sessionType
					+"&loginName="+ sessionName, "_blank");
		});
   }
	
	function showTable(result) {
		
		var totalRec = 0;
		
		$("#tableDiv").show();
		$("#smartpaginator").show();
		$("#tableDiv").append("<div id='logsTable'>");
		$("#logsTable").append("<table class='tablesorter' id='inboundRoamerLogsTable' style='width: 80%; margin-left: 5%;'><thead style='background-color: #808080; '><tr>"+ 
				  "<th style='text-align: center; width: 10%;'>ID</th>"+
				  "<th style='text-align: center; width: 10%;'>USER TYPE</th>"+
				  "<th style='text-align: center; width: 20%;'>NAME</th>"+
				  "<th style='text-align: center; width: 20%;'>ACTION</th>"+
				  "<th style='text-align: center; width: 20%;'>ACCESSED PAGE</th>"+
				  "<th style='text-align: center; width: 20%;'>DATE/TIME</th></tr></thead><tbody>");
		
		var jsonArray = eval('('+JSON.parse(result).viewLogsReturn + ')');
		
		for(var i = 0; i < jsonArray.length; i++) {
			
			var userType = jsonArray[i].userId;
			var usrTyp = "";
			if(userType === "2") {
				usrTyp = "Super Admin";
			} else if (userType === "1") {
				usrTyp = "Admin";
			} else {
				usrTyp = "Reg. User";
			}
			
			$("#inboundRoamerLogsTable").append("<tr><td style='text-align: center; width: 10%;'>"+jsonArray[i].id+
	 				 "</td><td style='text-align: center; width: 5%;'>"+usrTyp+
				 		 "</td><td style='text-align: center; width: 20%;'>"+jsonArray[i].name+
						 "</td><td style='text-align: center; width: 20%;'>"+jsonArray[i].action+ 
						 "</td><td style='text-align: center; width: 20%;'>"+jsonArray[i].accessedPage+
						 "</td><td style='text-align: center; width: 25%;'>"+jsonArray[i].dateTime+"</td></tr>");
			totalRec++;
		}
		
		$("#inboundRoamerLogsTable").append("</tbody></table>");
		$("#spinner").hide();
		
		if(!!$("#inboundRoamerLogsTable")) {
	    	 $("#smartpaginator").smartpaginator({ totalrecords: totalRec,
	    		initVal: 0,
	         	recordsperpage: 100,
	         	datacontainer: 'inboundRoamerLogsTable', 
	         	dataelement: 'tr',
	         	theme: 'red' });     
		 }
	}
	
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
				<a onclick="location.href='InboundRoamersAdHocMapTracker_Admin.jsp'" style="text-decoration: none;"/><img src= "../images/4.png" style="margin-top: 50px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Ad-Hoc</label></a>
			</div>	
			<div class="userOperations">
				<a onclick="location.href='UserOperations.jsp'" style="text-decoration: none;"/><img src= "../images/view_user.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">View Users</label></a>
			</div>
			<div class="userLogs">
				<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none; margin-left: 25px;"><label>Logout</label></a><br>
				<a onclick="location.href='UserLogs.jsp'" style="text-decoration: none;"/><img src= "../images/view.png" style="margin-top: 10px; margin-left: 25px;"><br><label style="font-family: sans-serif; font-size: small; background-color: #ed145b; border-radius: 5px;">View User Logs</label></a>
			</div>									
		</div>
		
		<div id = "map_content" style="height: 600px;">
			<br/>
			<div>
				<div>
					<label style="font-size: x-large; color: black; margin-left: 35%;">View/Search User Activities&nbsp;</label><br/>
					<label style="font-size: x-small; color: black; margin-left: 35%;">[ * ] Fill-up one or more of the fields&nbsp;</label><br/>
				</div>
				<br/>
				<div>
					
						<label style="color: black; margin-left: 35%;">*Search By User:&nbsp;</label><input type="text" style="margin-left: 20px; width: 200px; height: 30px;" id="logsUsernameSearch" name="logsUsernameSearch" size="20%" />
						<label style="margin-left: 10px; color: black;">*Search By Date:</label><input class="datepicker" type="text"  name="date" id="datepicker" style="margin-bottom: 5px; margin-left: 10px; width: 200px; "/>
					
					<br/>
				</div>
				<br/>
				<div>
						<label style="color: black; margin-left: 35%;">*Search By Action:&nbsp;</label>
						<select id="selAction" style="margin-left: 2px; width: 200px; color: white; ">
							<option value="">--Select Action--</option>
							<option value="Create Campaign">Create Campaign</option>
							<option value="View">View</option>
							<option value="Login">Login</option>
							<option value="Pause Campaign">Pause Campaign</option>
							<option value="Resume Campaign">Resume Campaign</option>
							<option value="Search User">Search User</option>
							<option value="Add User">Add User</option>
							<option value="Edit User">Edit User</option>
							<option value="Delete User">Delete User</option>
							<option value="Trigger Adhoc">Trigger Adhoc</option>
							<option value="Upload File">Upload File</option>
							<option value="Plot On Map">Plot On Map</option>
							<option value="Download Report">Download Report</option>
						</select>
						<input type="button" class="button-color" value="    View/Search     " id="searchLogs" style="margin-left: 210px;"/>
				</div>			
			</div>
			<div id="tableDiv" style="margin-top: 50px; margin-left: 10px; margin-right: 10px; overflow-y: auto; height: 300px;" align="center"></div>
			<br/>
			<div id="smartpaginator"></div><br/>	
		</div>
		<div id="poweredByDiv"><img src="${pageContext.request.contextPath}/images/poweredby.png"></div>
	</div>
</body>
</html>