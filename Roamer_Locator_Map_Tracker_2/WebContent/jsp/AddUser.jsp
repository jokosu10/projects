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
<title>Roamer Locator Map Tracker [ Add User Page ] </title>
<head>

<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="../js/smartpaginator.js"></script>

<link rel= "stylesheet" type="text/css" href="../css/datepicker.css">
<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">

<s:head/>
<script type="text/javascript">
$("#document").ready(function() {
	
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
	function processPageFromVar() {
		
	    var sessionId2 = decodeURIComponent(getParamsFromUrl("loginId"));
		var sessionName2 = decodeURIComponent(getParamsFromUrl("loginName"));
		var sessionType2 = decodeURIComponent(getParamsFromUrl("loginType"));
		
		rewriteUrl();
		
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=AddUser.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		if(sessionType2 === "2") {
			$("#userTypeDropdown").append($("<option></option>")
			         .attr("value","Super Admin User")
			         .text("Super Admin User")); 
		}
		
		$("#addUserBtn").click(function() {
			
			if($("#password").val().length < 8) {
				alert("Password should have a minimum of 8 characters");
			} else {
				if($("#password").val() !== $("#confirmPassword").val()) {
					alert("Passwords don't matched! Try again!");
				} else {
					
					var missingParam = "";
					if($('#userTypeDropdown').val() === "") {
						missingParam += "UserType is mandatory \n";
					} 
					
					if($('#userName').val() === "") {
						missingParam += "UserName is mandatory \n";
					}
					
					if($('#fullName').val() === "") {
						missingParam += "FullName is mandatory \n";
					}
					
					if(missingParam !== "") {
						alert(missingParam);
					} else {
						$.ajax({
							url: "createUser.action?userType="+ $('#userTypeDropdown').val() +"&userName="+$('#userName').val()+"&password="+$('#password').val()+"&fullName="+$('#fullName').val()+"&createdBy="+sessionName2,
							type: "POST",
							contentType: 'application/json; charset=utf-8',
				            dataType: 'json',
							success: function(response){
								var res = JSON.parse(JSON.stringify(response)).createUserReturn;
								if(res.indexOf("exist") >= 0) {
									alert(res.replace(/"/g,''));
								} else {
									if(Boolean(res) == true) {
										alert("Successfully added user");
										$("#userName").val("");
										$("#password").val("");
										$("#fullName").val("");
										$("#confirmPassword").val("");
										$('#userTypeDropdown').prop('selectedIndex',0);
										
										$.ajax({
									     	type:'POST',
									         url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=Add_User&page=AddUser.jsp',
									         contentType: 'application/json; charset=utf-8',
									         dataType: 'json'
										});
										
									} else {
										alert("Failed to create user. Please try again!!");
									}
								}
							}	
						});	
					}
				}
			}
		});

		$("#cancelBtn").click(function() {
			window.location.replace("UserOperations.jsp","_blank");
		});		
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
					+"&loginType="+ sessionType2
					+"&loginName="+ sessionName2, "_blank");
		});
		
	}
	
	function processPage() {
		
		var sessionName = "<s:property value='%{#session.loginName}'/>";
		$.ajax({
	     	type:'POST',
	         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=AddUser.jsp',
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json'
		});
		
		if(sessionType === "2") {
			$("#userTypeDropdown").append($("<option></option>")
			         .attr("value","Super Admin User")
			         .text("Super Admin User")); 
		}
		
		$("#addUserBtn").click(function() {
			
			if($("#password").val().length < 8) {
				alert("Password should have a minimum of 8 characters");
			} else {
				if($("#password").val() !== $("#confirmPassword").val()) {
					alert("Passwords don't matched! Try again!");
				} else {
					
					var missingParam = "";
					if($('#userTypeDropdown').val() === "") {
						missingParam += "UserType is mandatory \n";
					} 
					
					if($('#userName').val() === "") {
						missingParam += "UserName is mandatory \n";
					}
					
					if($('#fullName').val() === "") {
						missingParam += "FullName is mandatory \n";
					}
					
					if(missingParam !== "") {
						alert(missingParam);
					} else {
						$.ajax({
							url: "createUser.action?userType="+ $('#userTypeDropdown').val() +"&userName="+$('#userName').val()+"&password="+$('#password').val()+"&fullName="+$('#fullName').val()+"&createdBy="+sessionName,
							type: "POST",
							contentType: 'application/json; charset=utf-8',
				            dataType: 'json',
							success: function(response){
								var res = JSON.parse(JSON.stringify(response)).createUserReturn;
								if(res.indexOf("exist") >= 0) {
									alert(res.replace(/"/g,''));
								} else {
									if(Boolean(res) == true) {
										alert("Successfully added user");
										$("#userName").val("");
										$("#password").val("");
										$("#fullName").val("");
										$("#confirmPassword").val("");
										$('#userTypeDropdown').prop('selectedIndex',0);
										
										$.ajax({
									     	type:'POST',
									         url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=Add_User&page=AddUser.jsp',
									         contentType: 'application/json; charset=utf-8',
									         dataType: 'json'
										});
										
									} else {
										alert("Failed to create user. Please try again!!");
									}
								}
							}	
						});	
					}
				}
			}
		});

		$("#cancelBtn").click(function() {
			window.location.replace("UserOperations.jsp","_blank");
		});		
		
		$("#report_link").click(function() {
			window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
					+"&loginType="+ sessionType
					+"&loginName="+ sessionName, "_blank");
		});
	}

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
			    <div style="float: left; display: inline-block; width: 30%; height: 500px;"></div>
				<div style="float: left; display: inline-block; width: 40%; height: 500px;">
				    <br/>
				    <div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="font-size: x-large; color: black; margin-left: 25%;">View Users&nbsp;</label><label style="font-size: x-large; color: black; margin-left: 5px;">>>&nbsp;</label><label style="font-size: x-large; color: blue; margin-left: 5px;">Add User&nbsp;</label>
					</div>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 25%;">Username:&nbsp;</label><input type="text" style="margin-left: 70px; width: 200px; height: 30px;" id="userName" name="userName" size="20%" />
					</div>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 25%;">Password:&nbsp;</label><input type="password" style="margin-left: 76px; width: 200px; height: 30px;" id="password" name="password" size="20%" />
					</div>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 25%;">Confirm Password:&nbsp;</label><input type="password" style="margin-left: 20px; width: 200px; height: 30px;" id="confirmPassword" name="confirmPassword" size="20%" />
					</div>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 25%;">UserType:&nbsp;</label>
						<select id="userTypeDropdown" style="margin-left: 75px; width: 200px; color: white; ">
							<option value="">--Select Type--</option>
							<option value="Regular User">Regular User</option>
							<option value="Admin User">Admin User</option>
						</select>
					</div>
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="color: black; margin-left: 25%;">Full Name:&nbsp;</label><input type="text" style="margin-left: 70px; width: 200px; height: 30px;" id="fullName" name="fullName" size="20%" />
					</div>	
					<br/>
					<br/>
					<div style="position: absolute; margin: 0 auto; width: inherit;">
						<label style="margin-left: 25%"></label>
						<input type="button" class="button-color" value="     Submit     " id="addUserBtn" style = "margin-left: 140px;"/>
						<input type="button" class="button-color" value="     Cancel     " id="cancelBtn" style = "margin-left: 5px; "/>
						<label style="margin-left: 25%"></label>
					</div>																						
				</div>
				
				<div style="float: right; display: inline-block; width: 30%; height: 500px;"></div>		
			<br/>
		</div>
		<div id="poweredByDiv"><img src="${pageContext.request.contextPath}/images/poweredby.png"></div>
	</div>
</body>
</html>