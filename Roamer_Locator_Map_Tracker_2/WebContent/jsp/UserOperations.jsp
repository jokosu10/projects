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
<title>Roamer Locator Map Tracker [ User Operations Page ] </title>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/smartpaginator.js"></script>
<link rel= "stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/roamer_page_element.css">

<s:head/>
<style type="text/css">
imgDel imgEdit {
    display: block;
    width: 35px;
    height: 35px;
}
</style>

<script type="text/javascript">
$("#document").ready(function() {
	
	$("#tableDiv").hide();
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	var sessionName = "<s:property value='%{#session.loginName}'/>";
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
	   
	    var sessionId2 = decodeURIComponent(getParamsFromUrl("loginId"));
	    var sessionType2 = decodeURIComponent(getParamsFromUrl("loginType"));
	    var sessionName2 = decodeURIComponent(getParamsFromUrl("loginName"));
	   
	    rewriteUrl();
	    
		$("#smartpaginator").hide();
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=UserOperations.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		$("#searchUser").click(function() {
			
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Search_User&page=UserOperations.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			$("#roamerUsersTable").remove();
			$("#smartpaginator").hide();
			$("#spinner").show();
			
			var param = "";
			if($("#usernameSearch").val() !== "") {
				param = 'getAllUsers.action?userId='+sessionType2+'&userName='+$("#usernameSearch").val();
			} else {
				param = 'getAllUsers.action?userId='+sessionType2+'&userName=';
			}
			
			getResults(param);
			
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
					+"&loginType="+ sessionType2
					+"&loginName="+ sessionName2, "_blank");
		});
   }
   
   function processPage() {
	   
		$("#smartpaginator").hide();
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=UserOperations.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		$("#searchUser").click(function() {
			
			$.ajax({
		     	type:'POST',
		         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Search_User&page=UserOperations.jsp',
		         contentType: 'application/json; charset=utf-8',
		         dataType: 'json'
			});
			
			$("#roamerUsersTable").remove();
			$("#smartpaginator").hide();
			$("#spinner").show();
			
			var param = "";
			if($("#usernameSearch").val() !== "") {
				param = 'getAllUsers.action?userId='+sessionType+'&userName='+$("#usernameSearch").val();
			} else {
				param = 'getAllUsers.action?userId='+sessionType+'&userName=';
			}
			
			getResults(param);
			
		});
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
					+"&loginType="+ sessionType
					+"&loginName="+ sessionName, "_blank");
		});
   }
   
	function getResults(param) {
		
		$.ajax({
	     	type:'POST',
	         url: param,
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json',
	         success: function(response) {
	        	 			        	 
	        	    var result = (JSON.stringify(response));
					if(result == null || result === "null" || result === '{"viewUsersReturn":"[]"}') {
						$("#spinner").hide();
						alert("No data to display!!");
					} else {
						showTable(result);
					}
					$("#usernameSearch").val("");
	         }
		});
	}
	
	function showTable(result) {
		
		var totalRec = 0;
		
		$("#tableDiv").show();
		$("#smartpaginator").show();
		$("#tableDiv").append("<div id='logsTable'>");
		$("#logsTable").append("<table class='tablesorter' id='roamerUsersTable' style='width: 80%; margin: 0 auto;'><thead style='background-color: #808080; '><tr>"+ 
				  "<th style='text-align: center; width: 10%;'>ID</th>"+
				  "<th style='text-align: center; width: 10%;'>USERNAME</th>"+
				  "<th style='text-align: center; width: 20%;'>PASSWORD</th>"+
				  "<th style='text-align: center; width: 20%;'>USER TYPE</th>"+
				  "<th style='text-align: center; width: 20%;'>NAME</th>"+
				  "<th style='text-align: center; width: 10%;'>ACTION</th>"+
				  "</tr></thead><tbody>");
		
		var jsonArray = eval('('+JSON.parse(result).viewUsersReturn + ')');
		
		for(var i = 0; i < jsonArray.length; i++) {
			
			$("#roamerUsersTable").append("<tr><td style='text-align: center; width: 10%;'>"+jsonArray[i].id+
	 				 "</td><td style='text-align: center; width: 5%;'>"+jsonArray[i].userName+
				 		 "</td><td style='text-align: center; width: 20%;'><input type='password' value='"+jsonArray[i].origPassword+"' readonly disabled style='border: none; background-color: white; text-align: center;'/>"+
						 "</td><td style='text-align: center; width: 20%;'>"+jsonArray[i].userType+ 
						 "</td><td style='text-align: center; width: 20%;'>"+jsonArray[i].name+
						 "</td><td style='text-align: center; width: 10%;'><img class='imgEdit' src= '../images/edit.png' alt='Edit'><img class='imgDel' src= '../images/delete.png' alt='Delete'>"+
						 "</td><</tr>");
			totalRec++;
		}
		
		$("#roamerUsersTable").append("</tbody></table>");
		$("#spinner").hide();
		
		if(!!$("#roamerUsersTable")) {
	    	 $("#smartpaginator").smartpaginator({ totalrecords: totalRec,
	    		initVal: 0,
	         	recordsperpage: 10,
	         	datacontainer: 'roamerUsersTable', 
	         	dataelement: 'tr',
	         	theme: 'red' });  
	    	 
	    	 
	    	 $(".imgDel").click(function () {
	    		    var thisRow = $(this).closest('tr');
	    		    var userId = $(this).closest('tr').find('td:first').text();
	    		    
	    		    var yes = confirm("Are you sure you want to delete this user?");
	    		    if(yes) {
		    		    $.ajax({
		    		     	type:'POST',
		    		         url: 'deleteUser?userId='+userId,
		    		         contentType: 'application/json; charset=utf-8',
		    		         dataType: 'json',
		    		         success: function(response) {
		    		        	 			        	 
		    		        	    var result = JSON.parse(JSON.stringify(response)).deleteUserReturn;
		    						if(result == null || result === "null" || result === '{"deleteUserReturn":"[]"}') {
		    							$("#spinner").hide();
		    							alert("No data to display!!");
		    						} else {
		    							$("#spinner").hide();
		    							// remove the row in the table
		    							if(Boolean(result) == true) {
		    								alert("Successfully deleted user");
		    								thisRow.remove();
		    								
		    								$.ajax({
		    							     	type:'POST',
		    							         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Delete_User&page=UserOperations.jsp',
		    							         contentType: 'application/json; charset=utf-8',
		    							         dataType: 'json'
		    								});
		    				
		    							} else {
		    								alert("Failed to delete user. Please try again!!");
		    							}
		    						}
		    						
		    		         }
		    			});
	    		    	
	    		    }
	    		    	    		    
	    		}); 

	    	 $(".imgEdit").click(function () {
	    		 
	    		 	var userId = $(this).closest('tr').find('td:first').text();
	    		    var userName = $(this).closest('tr').find('td:nth-child(2)').text();
	    		    var userPass = $(this).closest('tr').find('td:nth-child(3)').text();
	    		    var userType = $(this).closest('tr').find('td:nth-child(4)').text();
	    		    var userFullName = $(this).closest('tr').find('td:nth-child(5)').text();
	    		    
	    		    window.location.replace('EditUser.jsp?userId='+userId+'&userName='+userName+'&userPass='+userPass+'&userType='+userType+'&userFullName='+userFullName, '_blank');
	    		    
	    		}); 	
	    	 
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
				<a onclick="location.href='UserOperations.jsp'" style="text-decoration: none;"/><img src= "../images/view_user.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; background-color: #ed145b; border-radius: 5px;">View Users</label></a>
			</div>
			<div class="userLogs">
				<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none; margin-left: 25px;"><label>Logout</label></a><br>
				<a onclick="location.href='UserLogs.jsp'" style="text-decoration: none;"/><img src= "../images/view.png" style="margin-top: 10px; margin-left: 25px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">View User Logs</label></a>
			</div>									
		</div>
		
		<div id = "map_content" style="overflow-y: auto; height: 600px;">
			<br/>
			<div style="height: 100px;">
				<div style="float: left; display: inline-block; width: 25%; height: 100px;"></div>
				<div style="float: left; display: inline-block; width: 50%; height: 100px;">
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="font-size: x-large; color: black; margin-left: 35%;">View/Search User&nbsp;</label>
					</div>
					<br/>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 20%;">Input UserName:&nbsp;</label>
						<input type="text" style="margin-left: 10px; width: 200px; height: 30px;" id="usernameSearch" name="usernameSearch" size="20%" />
						<input type="button" class="button-color" value="    View/Search     " id="searchUser" style="margin-left: 10px;"/>
					</div>
				</div>
				<div style="float: right; display: inline-block; width: 25%; height: 100px;">
					<div style="float: right; margin-right: 25px;">
							<a href="AddUser.jsp" onmouseover="style='text-decoration: none; '"><img src= "../images/add.png" style="margin-left: 10px;">
								<div style="text-align: center; ">
									<label style="color: black; font-size: small; ">Add User</label>
								</div>
							</a>
					</div>
				</div>					
			</div>
			<br/>
			<div id="tableDiv" style="margin-top: 10px;" ></div>
			<br/>
			<div id="smartpaginator"></div><br/>	
		</div>
		<div id="poweredByDiv"><img src="${pageContext.request.contextPath}/images/poweredby.png"></div>
	</div>
</body>
</html>