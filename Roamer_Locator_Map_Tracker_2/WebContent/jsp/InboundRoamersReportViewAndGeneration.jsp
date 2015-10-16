 <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
 
<!DOCTYPE html>
<html>
<title>Roamer Locator Map Tracker [ Reports Viewer And Generator ]</title>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="../js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="../js/smartpaginator.js"></script>

<link rel= "stylesheet" type="text/css" href="../css/datepicker.css">
<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../css/smartpaginator.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">


<script type="text/javascript">
$("#document").ready(function() {
	
	$("#smartpaginator").hide();
	$("#spinner").hide();
	
	$.get("inboundRoamerSelectOperator.action?mode=all", function(data) {
		
		$("#searchBaseOnNetwork").append($('<option>', {
			    value: "",
			    text: "Select Network"
			}));
	
		$("#searchBaseOnNetwork").append($('<option>', {
			    value: "All",
			    text: "All"
			}));
		
			var json = JSON.stringify(data);
			var jsonArray = eval('('+JSON.parse(json).jsonResponse.split(",") + ')');
			
			for(var j = 0; j < jsonArray.length; j++) {
				$("#searchBaseOnNetwork").append($('<option>', {
				    value: jsonArray[j].mncMcc,
				    text: jsonArray[j].destination + " - " + jsonArray[j].mncMcc + " - " + jsonArray[j].networkOpName
				}));
			}
	});
	
	var dateFrom = "", dateTo = "", totalRecords = 0;
	
	var dpFrom = $("#datepickerFrom").datepicker();
	dpFrom.on("changeDate", function(e) {
	    dpFrom.datepicker("hide");
	});
	
	var dpTo = $("#datepickerTo").datepicker();
	dpTo.on("changeDate", function(e) {
	    dpTo.datepicker("hide");
	});
		
	var whatServiceToCall = "getInboundRoamersReportPerNetwork.action?operator=";
	var searchQuery = "", isNetwork = true, searchPlaceVal = "";
	
	$("#searchIMSI").prop("disabled", true);
	$("#searchByNetworkRadioButton").attr('checked', true);
	
	$("#selectionForm input[name='selectionRadio']").change(function () {
	    if ($(this).val() === "byNetwork" && $(this).is(":checked")) {
	    	$("#searchBaseOnNetwork").removeAttr("disabled");
	    	$("#searchIMSI").prop("disabled", true);
	    	$("#searchIMSI").val("");
	    	whatServiceToCall = "getInboundRoamersReportPerNetwork.action?operator=";
	    	isNetwork = true;
	    } else {
	    	$("#searchBaseOnNetwork").prop("disabled",true);
	    	$("#searchIMSI").removeAttr("disabled");
	    	$("#searchIMSI").val("");
	    	$("#searchBaseOnNetwork").prop("selectedIndex",0);
	    	whatServiceToCall = "getInboundRoamersReportUsingImsi.action?imsi=";
	    	isNetwork = false;
	    }
	});
	
	$("#searchBaseOnNetwork").change(function() {
		searchQuery =  $("#searchBaseOnNetwork").val();
	});
	
	$("#selectPlace").change(function () {
		searchPlaceVal = $("#selectPlace").val();
	});
	
	$("input[type='text']").keyup(function() {
	    var raw_text =  jQuery(this).val();
	    var return_text = raw_text.replace(/[^0-9]/g,'');
	    jQuery(this).val(return_text);
	});
	
	
	$("#search").click(function() {
		
		var promptString = "";
		dateFrom = $("#datepickerFrom").val();
		dateTo = $("#datepickerTo").val();
		
		$("#inboundRoamersTable").remove();
		$("#smartpaginator").hide();
		var input =  $("#searchIMSI").val();
		
		if(!isNetwork) {
			searchQuery = input;
		} 
	    
		if(!(searchQuery === "") && !(searchPlaceVal === "") && !(dateFrom === "") && !(dateTo === "")) {
			 // fetch all info from inbound roamers table based on selection and display on table
			 var isProvincial = null, isTouristDestination = false;
			 if(searchPlaceVal === "Luzon" || searchPlaceVal === "Visayas" || searchPlaceVal === "Mindanao" || searchPlaceVal === "All") {
				 isProvincial = true;
			 } else {
				 isProvincial = false;
				 if(searchPlaceVal !== "NCR" && searchPlaceVal !== "Cebu" && searchPlaceVal !== "Iloilo" && searchPlaceVal !== "Davao") {
					 isTouristDestination = true;
				 } else {
					 isTouristDestination = false;
				 }	 
			 }

		  $("#spinner").show();	 
		  $.ajax({
		         	type:'POST',
		             url: whatServiceToCall + searchQuery + '&dateFrom=' + dateFrom + '&dateTo=' + dateTo + '&location=' + searchPlaceVal + '&isProvincialSelected=' + isProvincial +'&isTouristDestination=' +isTouristDestination,
		             contentType: 'application/json; charset=utf-8',
		             dataType: 'json',
		             success: function(response){
		            		 displayOnTable(response);
		             return false;
		             }
			    });
			 
		} else {
			if(searchQuery === "") {
				 promptString += "Operator/IMSI/MSISDN is mandatory \n";
			} 
			
			if (searchPlaceVal === "") {
				promptString  += "Location is mandatory \n";
			}
			
			if(dateFrom === "") {
				promptString += "From date is mandatory \n";
			} 
			
			if (dateTo === "") {
				promptString += "To date is mandatory \n";
			}
			
			alert(promptString);
		}
		
		return false;
	});	
	
	function displayOnTable(response) {
		
		var rec = 0;
		$("#spinner").hide();
		if(JSON.stringify(response) === "{\"jsonResponse\":\"[]\"}") {
			alert("No data to display!");
		} else {
			var parse = $.parseJSON(JSON.stringify(response));
		
			$("#smartpaginator").show();
			$("#tableDiv").append("<table id='inboundRoamersTable'>");
			$("#inboundRoamersTable").append("<tr><th style='text-align:center'>LOCATION</th>" +
											"<th style='text-align:center'>TOTAL ROAMER COUNT</th>" +
											"<th style='text-align:center'>ROAMER COUNT PER OPERATOR</th>" + 
											"<th style='text-align:center'>OPERATOR COUNTRY</th>" +
											"<th style='text-align:center'>OPERATOR NAME</th>" +
											"<th style='text-align:center'>IMSI</th>" + 
											"<th style='text-align:center'>MSISDN</th>" + 
											"<th style='text-align:center'>VLR</th>" + 
											"<th style='text-align:center'>LAC</th>" + 
											"<th style='text-align:center'>CELL ID</th>" + 
											"<th style='text-align:center'>GEOGRAPHICAL AREA</th>" + 
											"<th style='text-align:center'>REGIONAL AREA</th>" + 
											"<th style='text-align:center'>REGION</th>" + 
											"<th style='text-align:center'>PROVINCE</th>" + 
											"<th style='text-align:center'>TOWN</th>" + 
											"<th style='text-align:center'>BARANGAY</th>" + 
											"<th style='text-align:center'>SITE ADDRESS</th>"+ 
											"<th style='text-align:center'>DATE</th></tr>");
			
			$.each(parse,function(k,v) {
				
				var obj = eval(v);
				for(var i = 0; i < obj.length; i++) {
					
					/*$("#inboundRoamersTable").append("<tr class='header' style='background-color: #23AFFA;'><td colspan='2'>LOCATION:</td><td colspan='11' style='text-align: center;'>"+obj[i].inboundRoamersLocation + "</td></tr>");
					$("#inboundRoamersTable").append("<tr class='header' style='background-color: #888;'><td colspan='2'>ROAMER COUNT:</td><td colspan='11' style='text-align: center;'>"+obj[i].inboundRoamersTotalCount + "</td></tr>");
					$("#inboundRoamersTable").append("<tr class='header' style='background-color: #5F9EA0;'><td>IMSI</td><td>MSISDN</td><td>VLR</td><td>LAC</td><td>CELL ID</td><td>GEOGRAPHICAL AREA</td><td>REGIONAL AREA</td><td>REGION</td><td>PROVINCE</td><td>TOWN</td><td>BARANGAY</td><td>SITE ADDRESS</td><td>DATE</td></tr>");
					rec+= 3; */
					
					var obj2 = obj[i].inboundRoamersBeanList;
					for(var j = 0; j < obj2.length; j++) {
						
						$("#inboundRoamersTable").append("<tr style='font-size: xx-small'><td style='text-align:center'>"+obj[i].inboundRoamersLocation+"</td><td style='text-align:center'>"
														+ obj[i].inboundRoamersTotalCount +"</td><td style='text-align:center'>" 
														+ obj[i].inboundRoamersCountPerOperator +"</td><td style='text-align:center'>" 
														+ obj2[j].operatorCountry +"</td><td style='text-align:center'>" 
														+ obj2[j].operatorName +"</td><td style='text-align:center'>" 
														+ obj2[j].imsi+"</td><td style='text-align:center'>" 
														+ obj2[j].msisdn+"</td><td style='text-align:center'>"
														+ obj2[j].vlr+"</td><td style='text-align:center'>"
														+ obj2[j].cellLac+"</td><td style='text-align:center'>" 
														+ obj2[j].cellId+"</td><td style='text-align:center'>" 
														+ obj2[j].geographicalArea +"</td><td style='text-align:center'>" 
														+ obj2[j].regionalArea + "</td><td style='text-align:center'>"
														+ obj2[j].region + "</td><td style='text-align:center'>" 
														+ obj2[j].province + "</td><td style='text-align:center'>" 
														+ obj2[j].town + "</td><td style='text-align:center'>" 
														+ obj2[j].barangay +"</td><td style='text-align:center'>"
														+ obj2[j].siteAddress +"</td><td style='text-align:center'>"
														+ obj2[j].date +"</td></tr>");
							
						rec++;
					}
					//$("#inboundRoamersTable").append("<tr><td></td></tr>");
					//rec += 1;
				}	
			});
			
		}
		totalRecords = rec;
		
		if(!!$("#inboundRoamersTable")) {
	    	 $("#smartpaginator").smartpaginator({ totalrecords: totalRecords,
	    		initVal: 0,
	         	recordsperpage: 100,
	         	length: 10,
	         	datacontainer: 'inboundRoamersTable', 
	         	dataelement: 'tr',
	         	theme: 'red' });     
		 }
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

    $(".exportToCSV").click(function(event) {
        exportTableToCSV.apply(this, [$('#tableDiv>table'), 'export_' + searchPlaceVal + '-' + dateFrom.replace('/','_') + '-' + dateTo.replace('/','-') +'.csv']);
    });     
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
				<a onclick="location.href='InboundRoamersReportViewAndGeneration.jsp'" style="text-decoration: none;"/><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white; background-color: #ed145b; border-radius: 5px;">Reporting</label></a>
			</div>
			<div class="ad-hoc_reg">
			<br>
				<a onclick="location.href='logoutUser.action'" style="text-decoration: none;"><label>Logout</label></a><br>
				<a onclick="location.href='InboundRoamersAdHocMapTracker.jsp'" style="text-decoration: none;"/><img src= "../images/4.png" style="margin-top: 10px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Ad-Hoc</label></a>
			</div>		
			<!--<table style="width: 100%;">
				<tr>
					<td colspan="1">
						<a onclick="location.href='Login.jsp'" /><img src="../images/logo_final.png" style="margin-left: 35px; margin-top: 10px; margin-bottom: 10px;"></a>
					</td>
					<td colspan="1"><div style="width: 50px;"></td>
					<td colspan="1"><div style="width: 50px;"></td>
					<td colspan="1">
						<a onclick="location.href='InboundRoamersMapTracker.jsp'" /><img src= "../images/2.png" style="margin-top: 50px; margin-left: 20px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Inbound Map</label></a>
					</td>
					<td colspan="1"><div style="width: 50px;"></td>
					<td colspan="1">
						<a onclick="location.href='InboundRoamersReportViewAndGeneration.jsp'" /><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>
					</td>
					<td colspan="1"><div style="width: 50px;"></td>
					<td colspan="1">
						<a onclick="location.href='InboundRoamersAdHocMapTracker.jsp'" /><img src= "../images/4.png" style="margin-top: 50px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Ad-Hoc</label></a>
					</td>
				</tr>
			</table>-->
		</div>
		
		<div id = "map_content" style="height: 700px;">
			<div style="text-align: left; margin-left: 100px; margin-top: 30px;">
				<br>
				<form id="selectionForm" name="selectionForm">
					<input type="radio" name="selectionRadio" id="searchByNetworkRadioButton" value="byNetwork" style="vertical-align: middle; margin-top: -1px;"><label style="color: black;">&nbsp;&nbsp;By Network/Operator</label></input></br>
					<input type="radio" name="selectionRadio" id="searchByIMSIRadioButton" value="byImsi" style="vertical-align: middle; margin-top: -1px;"><label style="color: black;">&nbsp;&nbsp;By Single IMSI</label></input></br>
				</form>
				<br>
				<div style="width: inherit;">
				<div style="width: inherit;">
					<div>
						<div style="position: absolute;">
							<label style="color: black;">Operator:&nbsp;</label><select id="searchBaseOnNetwork" name="searchBaseOnNetwork" style="max-width: 310px; width: 310px;"></select>
						</div>
						<div style="float: right; display: inline-block; margin-right: 100px;">
							<label style="color: black;">&nbsp;From:&nbsp;</label><input class="datepicker" type="text"  name="dateFrom" id="datepickerFrom" style="margin-bottom: 5px; width: 200px; "/>
						</div>
					</div>
					<br><br>
					<div>
						<div style="position: absolute;">
							<label style="color: black;">IMSI/MSISDN:&nbsp;</label><input type="text" style="margin-left: 10px; width: 270px;" id="searchIMSI" name="searchIMSI" />
						</div>
						<div style="float: right; display: inline-block; margin-right: 100px;">
							<label style="margin-left: 18px; color: black;">&nbsp;To:&nbsp;</label><input class="datepicker" type="text" name="dateTo" id="datepickerTo" style="margin-bottom: 5px; width: 200px; " />
						</div>
					</div>
					<br><br>
					<div>
						<div style="position: absolute;">
							<label style="color: black; margin-top: 10px;">Location:</label>
							<select id="selectPlace" name="selectPlace" style="margin-top: 10px; width: 310px; max-width: 310px;">
								<option value="" selected="selected">Select Location</option>
								<option value="All">All</option>
								<option value="Luzon">Luzon</option>
								<option value="Visayas">Visayas</option>
								<option value="Mindanao">Mindanao</option>
								<option value="NCR">NCR</option>
								<option value="Cebu">Cebu</option>
								<option value="Iloilo">Iloilo</option>
								<option value="Davao">Davao</option>
								<option value="Baguio">Baguio</option>
								<option value="Boracay">Boracay</option>
								<option value="Coron">Coron</option>
								<option value="El Nido">El Nido</option>
								<option value="Puerto Galera">Puerto Galera</option>
								<option value="Puerto Princesa">Puerto Princesa</option>
								<option value="Subic">Subic</option>
								<option value="Tagaytay">Tagaytay</option>
							</select>
						</div>
					</div>
				
				</div>	
				</div>		
				<!--<table style="width: 100%;">
					<tr>
						<td><label style="color: black;">Operator:&nbsp;</label><select id="searchBaseOnNetwork" name="searchBaseOnNetwork"></select></td>
						<td><label style="color: black;">&nbsp;From:&nbsp;</label><input class="datepicker" type="text"  name="dateFrom" id="datepickerFrom" style="margin-bottom: 5px; width: 200px; "/></td>
					</tr>
					
					<tr>
						<td><label style="color: black;">IMSI/MSISDN:&nbsp;</label><input type="text" style="margin-left: 10px; width: 270px;" id="searchIMSI" name="searchIMSI" /></td>
						<td><label style="margin-left: 18px; color: black;">&nbsp;To:&nbsp;</label><input class="datepicker" type="text" name="dateTo" id="datepickerTo" style="width: 200px;" /></td>
					</tr>
					
					<tr>
						<td><label style="color: black; margin-top: 10px;">Location:</label>
							<select id="selectPlace" name="selectPlace" style="margin-top: 10px; width: 310px;">
								<option value="" selected="selected">Select Location</option>
								<option value="All">All</option>
								<option value="Luzon">Luzon</option>
								<option value="Visayas">Visayas</option>
								<option value="Mindanao">Mindanao</option>
								<option value="NCR">NCR</option>
								<option value="Cebu">Cebu</option>
								<option value="Iloilo">Iloilo</option>
								<option value="Davao">Davao</option>
								<option value="Baguio">Baguio</option>
								<option value="Boracay">Boracay</option>
								<option value="Coron">Coron</option>
								<option value="El Nido">El Nido</option>
								<option value="Puerto Galera">Puerto Galera</option>
								<option value="Puerto Princesa">Puerto Princesa</option>
								<option value="Subic">Subic</option>
								<option value="Tagaytay">Tagaytay</option>
							</select></td>	
					</tr>
				</table>-->							
			</div>	
			
			<div style="float: right;  display: inline-block; margin-top: 10px; margin-right: 100px;"><input type="button" id="search" value="Search" style="height: 30px; width: 100px; border-radius: 8px; font-size: small; font-family: sans-serif; background-color: #ed145b; color: white;"/></div><br><br>
			<hr align="center" style="background-color: black; padding-left: 10px; padding-right: 10px; width: 600px height: 2px;"><br>
			
			<div style="float: right; margin-top: 5px; margin-right: 100px;">
				<a href="#" class="exportToCSV" style="border-radius: 8px; font-size: small; font-family: sans-serif; background-color: #ed145b; color: white;">Export To CSV</a>
			</div>
			
			<div id="tableDiv" style="margin-top: 50px; margin-left: 10px; margin-right: 10px; overflow-y: auto; height: 300px;" align="center"></div>
			<div id="smartpaginator"></div>

		</div>
		<div id="poweredByDiv"><img src="../images/poweredby.png"></div>
		
	</div>
</body>
</html>