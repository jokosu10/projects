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
<title>Roamer Locator Map Tracker [ Welcome Page ] </title>
<head>

<link rel= "stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/roamer_page_element.css">

<s:head/>
<script type="text/javascript">
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	if(sessionId === "" || sessionId == null) {
		location.href="${pageContext.request.contextPath}/jsp/admin/CreateCampaign.jsp";
	} 
</script>
</head>
<body>
	<div id="spinner" class="spinner" style="display:none;"></div>
	<div id="main">
		<div id="map_header">
			<table>
				<tr>
					<td colspan="1"><img src="${pageContext.request.contextPath}/images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;"></td>
				</tr>
			</table>
		</div>
		
		<div id = "map_content" style="background-color: #058cbb; height: 500px;">
			<div id="loginDiv" align="center" style="margin-top: 50px;">
				<table>
					<tr>
						<td>
							<s:form id="loginForm2" name="loginForm2" action="validateUser2" theme="simple" method="post">
									<s:actionerror />
									<s:fielderror fieldName="userName"/>
									<s:fielderror fieldName="password"/>
																	<table>
									<tr>
										<td>
											<label style="margin-left: 5px;">Username:</label>
										</td>
										<td>
											<s:textfield name="userName" cssStyle="width: 200px; margin-top: 20px; margin-bottom: 10px; margin-left: 5px;"/>
										</td>
									</tr>
									<tr>
										<td>
											<label style="margin-left: 5px;">Password:</label>
										</td>
										<td>
											<s:password name="password" cssStyle="width: 200px; margin-left: 5px;" />	
										</td>
									</tr>
									<tr>
										<td>
											<div style="height: 25px;"></div>
										</td>
									</tr>
									<tr>
										<td></td>
										<td>
											<s:submit value="    Login   " style="margin-left: 135px;" cssClass="button-color" />		
										</td>
									</tr>
								</table>
							</s:form>
							<br>
							<!--<div align="center"><label>Click&nbsp;<a href="${pageContext.request.contextPath}/jsp/admin/InboundRoamersMapTracker.jsp" style="color: white;">HERE</a> to go back!!</label><br/><br/></div>-->
						</td>
					</tr>
				</table>	
			</div>
		</div>
		<div id="poweredByDiv"><img src="${pageContext.request.contextPath}/images/poweredby.png"></div>
	</div>
</body>
</html>