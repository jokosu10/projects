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
<title>Roamer Locator Map Tracker [ Inbound Roamers Map ]</title>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>

<script type="text/javascript" src="../js/leaflet.js"></script>
<script type="text/javascript" src="../js/jquery-1.10.2.js"></script>

<link rel="stylesheet" type="text/css" href="../css/leaflet.css">
<link rel= "stylesheet" type="text/css" href="../css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="../css/roamer_page_element.css">


<script type="text/javascript">
$("#document").ready(function() {
	
	var sessionId = "<s:property value='%{#session.loginId}'/>";
	var sessionType = "<s:property value='%{#session.loginType}'/>";
	
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
    	var geojsonPath = "../map_json/";
    	var host = "10.8.72.12/roamware/Tiles";
    	
    	var sessionName2 = decodeURIComponent(getParamsFromUrl("loginName"));
    	var sessionType2 = decodeURIComponent(getParamsFromUrl("loginType"));
    	var sessionId2 = decodeURIComponent(getParamsFromUrl("loginId"));
    	
    	rewriteUrl();
    	
    	$.ajax({
         	type:'POST',
             url:'insertToUserLogs.action?userId='+sessionId2+'&fullName='+ sessionName2 +'&action=View&page=InboundRoamersMapTracker.jsp',
             contentType: 'application/json; charset=utf-8',
             dataType: 'json'
    	});
             
    	$("#spinner").hide();
    	var geojson = null, geojsonOld = null;
    	
     	var bounds = new L.LatLngBounds(
    			new L.LatLng(4.22816513512253, 116.389103446625),
    			new L.LatLng(21.6156671213737, 126.979526808346));
    	
    	var map = L.map('map', {
    		minZoom: 6,
    		maxZoom: 14,
    		zoom: 6,
    		maxBounds: bounds, 
    		zoomControl: true,
    	    center: [12.6326,122.7457]
    	});

    	L.tileLayer('http://'+ host +'/{z}/{x}/{y}.png', {
    		scheme: "TMS",
    		continousWorld: "true",
    	    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    	}).addTo(map); 	
    	
    	var geojsonToLoad = geojsonPath + "philippines_geojson.json";
    	
    	$("#spinner").show();
    	
    	$.get("getLatestInboundMapDate.action", function(data) {
    		var res = (JSON.parse(JSON.stringify(data)).inboundMapDate).replace(/"/g,'');
    		$("#dateId").html(res);
    	});
    	
    	
    	var allRes = 0, luzRes = 0, visRes = 0, minRes = 0, ncrRes = 0, cebuRes = 0, iloiloRes = 0,
    			davaoRes = 0, baguioRes = 0, boracayRes = 0, coronRes = 0, elNidoRes = 0, pPrincesaRes = 0, 
    				pGaleraRes = 0, subicRes = 0, tagaytayRes = 0, unknownRes = 0;
    	
    	var allHover = "", luzHover = "", visHover = "", minHover = "", ncrHover = "", cebuHover = "", iloiloHover = "",
    			davaoHover = "", baguioHover = "", boracayHover = "", coronHover = "", elNidoHover = "", pPrincesaHover = "", 
    				pGaleraHover = "", subicHover = "", tagaytayHover = "", unknownHover = "";	
    	
    	
    	var locationArray = ['unknown','All','LUZON','VISAYAS','MINDANAO','NCR','CEBU','ILOILO','DAVAO','BAGUIO','BORACAY','CORON','EL NIDO','PUERTO PRINCESA','PUERTO GALERA','SUBIC','TAGAYTAY'];
    	for(var lookUp = 0; lookUp < locationArray.length; lookUp++) {
    		$.ajax({
    	     	type:'POST',
    	         url:'getInboundRoamersCount.action?location='+ locationArray[lookUp] +'&column=',
    	         contentType: 'application/json; charset=utf-8',
    	         dataType: 'json',
    	         success: function(response){
    	        	 	 
    	        		 var jsonRes = JSON.parse(JSON.stringify(response)).jsonResponse;
    	        			if(jsonRes != null && jsonRes !== "[]") {
    	        				var obj = eval(jsonRes);
    		     				for(var i = 0; i < obj.length; i++) {

    		     					 if(obj[i].inboundRoamersLocation === "LUZON") {
    		     						 luzRes = Number(luzRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 luzHover = luzHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "VISAYAS") {
    		     						 visRes = Number(visRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 visHover = visHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "MINDANAO") {
    		     						 minRes = Number(minRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 minHover = minHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "NCR") {
    		     						 ncrRes = Number(ncrRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 ncrHover = ncrHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "CEBU") {
    		     						 cebuRes = Number(cebuRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 cebuHover = cebuHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "ILOILO") {
    		     						 iloiloRes = Number(iloiloRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 iloiloHover = iloiloHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "DAVAO") {
    		     						 davaoRes = Number(davaoRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 davaoHover = davaoHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "BAGUIO") {
    		     						 baguioRes = Number(baguioRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 baguioHover = baguioHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "BORACAY") {
    		     						 boracayRes = Number(boracayRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 boracayHover = boracayHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "CORON") {
    		     						 coronRes = Number(coronRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 coronHover = coronHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "EL NIDO") {
    		     						 elNidoRes = Number(elNidoRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 elNidoHover = elNidoHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "PUERTO PRINCESA") {
    		     						 pPrincesaRes = Number(pPrincesaRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 pPrincesaHover = pPrincesaHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "PUERTO GALERA") {
    		     						 pGaleraRes = Number(pGaleraRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 pGaleraHover = pGaleraHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "SUBIC") {
    		     						 subicRes = Number(subicRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 subicHover = subicHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "TAGAYTAY") {
    		     						 tagaytayRes = Number(tagaytayRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 tagaytayHover = tagaytayHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "All") {
    		     						 allHover = allHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     						 allRes = Number(allRes) + Number(obj[i].inboundRoamersTotalCount);
    		     					 } else if(obj[i].inboundRoamersLocation === "unknown") {
    		     						 unknownHover = unknownHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     						 unknownRes = Number(unknownRes) + Number(obj[i].inboundRoamersTotalCount);
    		     					 }
    		     				}
    		     				
    	        			}
    	     					
    	        		 $("#allId").html((allRes == 0 ? 0 : allRes));	
    	        		 $("#luzonId").html((luzRes == 0 ? 0 : luzRes));
    	        		 $("#visayasId").html((visRes == 0 ? 0 : visRes));
    	        		 $("#mindanaoId").html((minRes == 0 ? 0 : minRes));
    	        		 $("#ncrId").html((ncrRes == 0 ? 0 : ncrRes));
    	        		 $("#cebuId").html((cebuRes == 0 ? 0 : cebuRes));
    	        		 $("#iloiloId").html((iloiloRes == 0 ? 0 : iloiloRes));
    	        		 $("#davaoId").html((davaoRes == 0 ? 0 : davaoRes));
    	        		 $("#baguioId").html((baguioRes == 0 ? 0 : baguioRes));
    	        		 $("#boracayId").html((boracayRes == 0 ? 0 : boracayRes));
    	        		 $("#coronId").html((coronRes == 0 ? 0 : coronRes));
    	        		 $("#elNidoId").html((elNidoRes == 0 ? 0 : elNidoRes));
    	        		 $("#pPrincesaId").html((pPrincesaRes == 0 ? 0 : pPrincesaRes));
    	        		 $("#pGaleraId").html((pGaleraRes == 0 ? 0 : pGaleraRes));
    	        		 $("#subicId").html((subicRes == 0 ? 0 : subicRes));
    	        		 $("#tagaytayId").html((tagaytayRes == 0 ? 0 : tagaytayRes));
    	        		 $("#unknownId").html((unknownRes == 0 ? 0 : unknownRes));
    	        		 
    	          return false;
    	         }
    	    });
    	}

       	// control that shows state info on hover
    	var info = L.control();

    	info.onAdd = function (map) {
    		this._div = L.DomUtil.create('div', 'info');
    		this.update();
    		return this._div;
    	};

    	info.update = function (count,loc) {
    		if(loc === undefined && count === undefined ) {
    			loc = "";
    			count = "Hover over a place";
    		} else if(count === "") {
    			count = "No values found";
    		}
    		this._div.innerHTML = '<div style="overflow-y: auto; text-align: center; height: 250px;">' +
    		  '<h4>Roamer Count</h4>' + 
    		  '<label style="color: black;"><strong>'+ loc +'</strong></label><br>'+
    		  '<label style="color: black;">'+ count +'</label><br></div>';	
    		  
    		  if(!! info2) {
    			  info2.update("reset");
    		  }
    	};

    	info.addTo(map);
    	
    	var info2 = L.control();
    	
    	info2.onAdd = function (map) {
    		this._div = L.DomUtil.create('div', 'info');
    		this.update();
    		return this._div;
    	};

    	info2.update = function (stringval) {
    		if(stringval === undefined || stringval === "reset") {
    			stringval = "Click the table on the left side";
    		} else {
    			if(stringval === "") {
    				stringval = "<br>No values found";
    			}
    		}
    		
    		this._div.innerHTML = '<div style="overflow-y: auto; text-align: center; height: 250px;">' +
    							  '<h4>Details</h4>' + 
    							  '<label style="color: black;">'+ stringval +'</label><br></div>';				  
    	};

    	info2.addTo(map);
    	// end info    	
    	
    	
      	$.getJSON(geojsonToLoad, function(data) {
    	    
      		geojson = L.geoJson(data, { 	
      	      onEachFeature: function (feature, layer) {
      	    	 layer.on({
      	 			//mouseover: highlightFeature,
      	 			mouseover: function(e) {
      	 				var layer = e.target;

      	 				var count = "";
      	 				var loc = "";
      	 				
      	 				loc = layer.feature.properties.PROVINCE;
      	 				count = searchInJson(loc);

      	 				layer.setStyle({
      	 					weight: 5,
      	 					color: '#666',
      	 					fillColor: getColor(count),
      	 					dashArray: '',
      	 					fillOpacity: 0.7
      	 				});

      	 				if (!L.Browser.ie && !L.Browser.opera) {
      	 					layer.bringToFront();
      	 				}
      	 				info.update(count,loc);
      	 			},
      	 			//mouseout: resetHighlight
      	 			mouseout: function(e) {
      	 				geojson.resetStyle(e.target);
      	 			}
      	 		});
      	      },
      	      style: style
      	    }); 
    	    
    	    geojson.addTo(map);
    	    geojsonOld = geojson;
    	    $("#spinner").hide();
    	});	
    	
    	
    	$("#tableMap tr").click(function() {
    	    var tableSelection = $(this).find("a").attr("href");
    	   
    	    	if(tableSelection) {
                 		switch(tableSelection) {
                 			case "All": info2.update("<strong>All</strong><br>" + allHover); break; 
    						case "Luzon": info2.update("<strong>Luzon</strong><br>" + luzHover); break; 
    						case "Visayas": info2.update("<strong>Visayas</strong><br>" + visHover); break;
    						case "Mindanao": info2.update("<strong>Mindanao</strong><br>" + minHover); break;
    						case "NCR": info2.update("<strong>NCR</strong><br>" + ncrHover); break;
    						case "Cebu": info2.update("<strong>Cebu</strong><br>" + cebuHover); break;
    						case "Iloilo": info2.update("<strong>Iloilo</strong><br>" + iloiloHover); break;
    						case "Davao": info2.update("<strong>Davao</strong><br>" + davaoHover); break;
    						case "Baguio": info2.update("<strong>Baguio</strong><br>" + baguioHover); break;
    						case "Boracay": info2.update("<strong>Boracay</strong><br>" + boracayHover); break;
    						case "Coron": info2.update("<strong>Coron</strong><br>" + coronHover); break;
    						case "El Nido": info2.update("<strong>El Nido</strong><br>" + elNidoHover); break;
    						case "Puerto Princesa": info2.update("<strong>Puerto Princesa</strong><br>" + pPrincesaHover); break;
    						case "Puerto Galera": info2.update("<strong>Puerto Galera</strong><br>" + pGaleraHover); break;
    						case "Subic": info2.update("<strong>Subic</strong><br>" + subicHover); break;
    						case "Tagaytay": info2.update("<strong>Tagaytay</strong><br>" + tagaytayHover); break;
    						case "Unknown": info2.update("<strong>Unknown Locations</strong><br>" + unknownHover); break;
    						
    					}
    		    }; return false;		
    		}); 	
      		
 
    	$("#report_link").click(function() {
    		window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId2
    				+"&loginType="+ sessionType2
    				+"&loginName="+ sessionName2, "_blank");
    	});
    }
    
    function processPage() {
    	
    	//var host = "localhost/Tiles/Tiles";
    	var geojsonPath = "../map_json/";
    	var host = "10.8.72.12/roamware/Tiles";
    	
    	var sessionName = "<s:property value='%{#session.loginName}'/>";
    	$.ajax({
         	type:'POST',
             url:'insertToUserLogs.action?userId='+sessionId+'&fullName='+ sessionName +'&action=View&page=InboundRoamersMapTracker.jsp',
             contentType: 'application/json; charset=utf-8',
             dataType: 'json'
    	});
             
    	$("#spinner").hide();
    	var geojson = null, geojsonOld = null;
    	
     	var bounds = new L.LatLngBounds(
    			new L.LatLng(4.22816513512253, 116.389103446625),
    			new L.LatLng(21.6156671213737, 126.979526808346));
    	
    	var map = L.map('map', {
    		minZoom: 6,
    		maxZoom: 14,
    		zoom: 6,
    		maxBounds: bounds, 
    		zoomControl: true,
    	    center: [12.6326,122.7457]
    	});

    	L.tileLayer('http://'+ host +'/{z}/{x}/{y}.png', {
    		scheme: "TMS",
    		continousWorld: "true",
    	    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
    	}).addTo(map); 	
    	
    	var geojsonToLoad = geojsonPath + "philippines_geojson.json";
    	
    	$("#spinner").show();
    	
    	$.get("getLatestInboundMapDate.action", function(data) {
    		var res = (JSON.parse(JSON.stringify(data)).inboundMapDate).replace(/"/g,'');
    		$("#dateId").html(res);
    	});
    	
    	
    	var allRes = 0, luzRes = 0, visRes = 0, minRes = 0, ncrRes = 0, cebuRes = 0, iloiloRes = 0,
    			davaoRes = 0, baguioRes = 0, boracayRes = 0, coronRes = 0, elNidoRes = 0, pPrincesaRes = 0, 
    				pGaleraRes = 0, subicRes = 0, tagaytayRes = 0, unknownRes = 0;
    	
    	var allHover = "", luzHover = "", visHover = "", minHover = "", ncrHover = "", cebuHover = "", iloiloHover = "",
    			davaoHover = "", baguioHover = "", boracayHover = "", coronHover = "", elNidoHover = "", pPrincesaHover = "", 
    				pGaleraHover = "", subicHover = "", tagaytayHover = "", unknownHover = "";	
    	
    	
    	var locationArray = ['unknown','All','LUZON','VISAYAS','MINDANAO','NCR','CEBU','ILOILO','DAVAO','BAGUIO','BORACAY','CORON','EL NIDO','PUERTO PRINCESA','PUERTO GALERA','SUBIC','TAGAYTAY'];
    	for(var lookUp = 0; lookUp < locationArray.length; lookUp++) {
    		$.ajax({
    	     	type:'POST',
    	         url:'getInboundRoamersCount.action?location='+ locationArray[lookUp] +'&column=',
    	         contentType: 'application/json; charset=utf-8',
    	         dataType: 'json',
    	         success: function(response){
    	        	 	 
    	        		 var jsonRes = JSON.parse(JSON.stringify(response)).jsonResponse;
    	        			if(jsonRes != null && jsonRes !== "[]") {
    	        				var obj = eval(jsonRes);
    		     				for(var i = 0; i < obj.length; i++) {

    		     					 if(obj[i].inboundRoamersLocation === "LUZON") {
    		     						 luzRes = Number(luzRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 luzHover = luzHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "VISAYAS") {
    		     						 visRes = Number(visRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 visHover = visHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "MINDANAO") {
    		     						 minRes = Number(minRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 minHover = minHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "NCR") {
    		     						 ncrRes = Number(ncrRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 ncrHover = ncrHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "CEBU") {
    		     						 cebuRes = Number(cebuRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 cebuHover = cebuHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "ILOILO") {
    		     						 iloiloRes = Number(iloiloRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 iloiloHover = iloiloHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "DAVAO") {
    		     						 davaoRes = Number(davaoRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 davaoHover = davaoHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "BAGUIO") {
    		     						 baguioRes = Number(baguioRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 baguioHover = baguioHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "BORACAY") {
    		     						 boracayRes = Number(boracayRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 boracayHover = boracayHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "CORON") {
    		     						 coronRes = Number(coronRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 coronHover = coronHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "EL NIDO") {
    		     						 elNidoRes = Number(elNidoRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 elNidoHover = elNidoHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "PUERTO PRINCESA") {
    		     						 pPrincesaRes = Number(pPrincesaRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 pPrincesaHover = pPrincesaHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "PUERTO GALERA") {
    		     						 pGaleraRes = Number(pGaleraRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 pGaleraHover = pGaleraHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "SUBIC") {
    		     						 subicRes = Number(subicRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 subicHover = subicHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     					 } else if(obj[i].inboundRoamersLocation === "TAGAYTAY") {
    		     						 tagaytayRes = Number(tagaytayRes) + Number(obj[i].inboundRoamersTotalCount);
    		     						 tagaytayHover = tagaytayHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 
    		     					 } else if(obj[i].inboundRoamersLocation === "All") {
    		     						 allHover = allHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     						 allRes = Number(allRes) + Number(obj[i].inboundRoamersTotalCount);
    		     					 } else if(obj[i].inboundRoamersLocation === "unknown") {
    		     						 unknownHover = unknownHover + "<br>" + ("( " + obj[i].operatorCountry + " ) " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount));
    		     						 unknownRes = Number(unknownRes) + Number(obj[i].inboundRoamersTotalCount);
    		     					 }
    		     				}
    		     				
    	        			}
    	     					
    	        		 $("#allId").html((allRes == 0 ? 0 : allRes));	
    	        		 $("#luzonId").html((luzRes == 0 ? 0 : luzRes));
    	        		 $("#visayasId").html((visRes == 0 ? 0 : visRes));
    	        		 $("#mindanaoId").html((minRes == 0 ? 0 : minRes));
    	        		 $("#ncrId").html((ncrRes == 0 ? 0 : ncrRes));
    	        		 $("#cebuId").html((cebuRes == 0 ? 0 : cebuRes));
    	        		 $("#iloiloId").html((iloiloRes == 0 ? 0 : iloiloRes));
    	        		 $("#davaoId").html((davaoRes == 0 ? 0 : davaoRes));
    	        		 $("#baguioId").html((baguioRes == 0 ? 0 : baguioRes));
    	        		 $("#boracayId").html((boracayRes == 0 ? 0 : boracayRes));
    	        		 $("#coronId").html((coronRes == 0 ? 0 : coronRes));
    	        		 $("#elNidoId").html((elNidoRes == 0 ? 0 : elNidoRes));
    	        		 $("#pPrincesaId").html((pPrincesaRes == 0 ? 0 : pPrincesaRes));
    	        		 $("#pGaleraId").html((pGaleraRes == 0 ? 0 : pGaleraRes));
    	        		 $("#subicId").html((subicRes == 0 ? 0 : subicRes));
    	        		 $("#tagaytayId").html((tagaytayRes == 0 ? 0 : tagaytayRes));
    	        		 $("#unknownId").html((unknownRes == 0 ? 0 : unknownRes));
    	        		 
    	          return false;
    	         }
    	    });
    	}
 
    	// control that shows state info on hover
    	var info = L.control();

    	info.onAdd = function (map) {
    		this._div = L.DomUtil.create('div', 'info');
    		this.update();
    		return this._div;
    	};

    	info.update = function (count,loc) {
    		if(loc === undefined && count === undefined ) {
    			loc = "";
    			count = "Hover over a place";
    		} else if(count === "") {
    			count = "No values found";
    		}
    		this._div.innerHTML = '<div style="overflow-y: auto; text-align: center; height: 250px;">' +
    		  '<h4>Roamer Count</h4>' + 
    		  '<label style="color: black;"><strong>'+ loc +'</strong></label><br>'+
    		  '<label style="color: black;">'+ count +'</label><br></div>';	
    		  
    		  if(!! info2) {
    			  info2.update("reset");
    		  }
    	};

    	info.addTo(map);
    	
    	
    	var info2 = L.control();

    	info2.onAdd = function (map) {
    		this._div = L.DomUtil.create('div', 'info');
    		this.update();
    		return this._div;
    	};

    	info2.update = function (stringval) {
    		if(stringval === undefined || stringval === "reset") {
    			stringval = "Click the table on the left side";
    		} else {
    			if(stringval === "") {
    				stringval = "<br>No values found";
    			}
    		}
    		
    		this._div.innerHTML = '<div style="overflow-y: auto; text-align: center; height: 250px;">' +
    							  '<h4>Details</h4>' + 
    							  '<label style="color: black;">'+ stringval +'</label><br></div>';				  
    	};

    	info2.addTo(map);
    	// end info    	
    	
    	
      	$.getJSON(geojsonToLoad, function(data) {
    	    
      		geojson = L.geoJson(data, { 	
    	      onEachFeature: function (feature, layer) {
    	    	 layer.on({
    	 			//mouseover: highlightFeature,
    	 			mouseover: function(e) {
    	 				var layer = e.target;

    	 				var count = "";
    	 				var loc = "";
    	 				
    	 				loc = layer.feature.properties.PROVINCE;
    	 				count = searchInJson(loc);

    	 				layer.setStyle({
    	 					weight: 5,
    	 					color: '#666',
    	 					fillColor: getColor(count),
    	 					dashArray: '',
    	 					fillOpacity: 0.7
    	 				});

    	 				if (!L.Browser.ie && !L.Browser.opera) {
    	 					layer.bringToFront();
    	 				}
    	 				info.update(count,loc);
    	 			},
    	 			//mouseout: resetHighlight
    	 			mouseout: function(e) {
    	 				geojson.resetStyle(e.target);
    	 			}
    	 		});
    	      },
    	      style: style
    	    }); 
    	    
    	    geojson.addTo(map);
    	  //  geojsonOld = geojson;
    	    $("#spinner").hide();
    	});	
    	
    	$("#tableMap tr").click(function() {
    	    var tableSelection = $(this).find("a").attr("href");
    	   
    	    	if(tableSelection) {
                 		switch(tableSelection) {
                 			case "All": info2.update("<strong>All</strong><br>" + allHover); break; 
    						case "Luzon": info2.update("<strong>Luzon</strong><br>" + luzHover); break; 
    						case "Visayas": info2.update("<strong>Visayas</strong><br>" + visHover); break;
    						case "Mindanao": info2.update("<strong>Mindanao</strong><br>" + minHover); break;
    						case "NCR": info2.update("<strong>NCR</strong><br>" + ncrHover); break;
    						case "Cebu": info2.update("<strong>Cebu</strong><br>" + cebuHover); break;
    						case "Iloilo": info2.update("<strong>Iloilo</strong><br>" + iloiloHover); break;
    						case "Davao": info2.update("<strong>Davao</strong><br>" + davaoHover); break;
    						case "Baguio": info2.update("<strong>Baguio</strong><br>" + baguioHover); break;
    						case "Boracay": info2.update("<strong>Boracay</strong><br>" + boracayHover); break;
    						case "Coron": info2.update("<strong>Coron</strong><br>" + coronHover); break;
    						case "El Nido": info2.update("<strong>El Nido</strong><br>" + elNidoHover); break;
    						case "Puerto Princesa": info2.update("<strong>Puerto Princesa</strong><br>" + pPrincesaHover); break;
    						case "Puerto Galera": info2.update("<strong>Puerto Galera</strong><br>" + pGaleraHover); break;
    						case "Subic": info2.update("<strong>Subic</strong><br>" + subicHover); break;
    						case "Tagaytay": info2.update("<strong>Tagaytay</strong><br>" + tagaytayHover); break;
    						case "Unknown": info2.update("<strong>Unknown Locations</strong><br>" + unknownHover); break;
    						
    					}
    		    }; return false;		
    		}); 	
      		

    	$("#report_link").click(function() {
    		window.location.replace("http://10.8.72.12/RoamerLocatorMapTracker/index.php/report?loginId="+ sessionId
    				+"&loginType="+ sessionType
    				+"&loginName="+ sessionName, "_blank");
    	});
    }
    
	function getColor(val) {
		return val > 100000 ? '#800026' :
	           val > 50000  ? '#BD0026' :
	           val > 20000  ? '#E31A1C' :
	           val > 15000  ? '#FC4E2A' :
	           val > 5000   ? '#FD8D3C' :
	           val > 2000   ? '#FEB24C' :
	           val > 100   ? '#FED976' : 
	        	   '#FFEDA0';
	}
	
	function style(feature) {
		return {
	        fillColor: "#1e6ea6",
	        weight: 2,
	        opacity: 1,
	        color: 'white',
	        dashArray: '3',
	        fillOpacity: 0.7
	    };
	}

/*	function highlightFeature(e) {
		var layer = e.target;

		var count = "";
		var loc = "";
		
		loc = layer.feature.properties.PROVINCE;
		count = searchInJson(loc);

		layer.setStyle({
			weight: 5,
			color: '#666',
			fillColor: getColor(count),
			dashArray: '',
			fillOpacity: 0.7
		});

		if (!L.Browser.ie && !L.Browser.opera) {
			layer.bringToFront();
		}
		info.update(count,loc);
	}

	function resetHighlight(e) {
		geojson.resetStyle(e.target);
		//info.update();
	} */
	
	function searchInJson(location) {
		
		// ajax call na dapat
		// almost same lang nung upon load na ajax call
		var column = "";
		var ncrCityArray = ["Quezon City","Manila","Kalookan City","Valenzuela","Malabon","Navotas","Marikina","San Juan","Pasig City","Mandaluyong","Makati City","Pateros","Pasay City","Taguig", "Para\u00f1aque", "Las Pi\u00f1as", "Muntinlupa"];
		var match = $.inArray(location,ncrCityArray);
		var resHover = "";
		
		if(match < 0) {
			column = "province";
		} else if(match >= 0) {
			column = "town";
		}
		
		$.ajax({
	     	 type:'GET',
	         url:'getInboundRoamersCount.action?location='+ location +'&column='+column,
	         async: false,		 
	         contentType: 'application/json; charset=utf-8',
	         dataType: 'json',
	         success: function(response){
	        	 	 
	        		 var jsonRes = JSON.parse(JSON.stringify(response)).jsonResponse;
	        		
	        			if(jsonRes != null && jsonRes !== "[]") {
	        				var obj = eval(jsonRes);
		     				console.log(jsonRes);
		     				for(var i = 0; i < obj.length; i++) {

		     					resHover = resHover + "<br>" + ("(" + obj[i].operatorCountry + ") " + obj[i].operatorName + " : " + Number(obj[i].inboundRoamersTotalCount)); 	 
		     				}	
	        			}
	     	   	        		 
	          return false;
	         }
	    });
		
		return resHover;

	}	
	
	function getRandomColor() {
		var letters = '0123456789ABCDEF'.split('');
        var color = '#';
        for (var i = 0; i < 6; i++ ) {
            color += letters[Math.round(Math.random() * 15)];
        }      
        return color;
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
		
			<div class="inboundMap_reg">
				<a onclick="location.href='InboundRoamersMapTracker.jsp'" style="text-decoration: none;" /><img src= "../images/2.png" style="margin-top: 50px; margin-left: 20px;"><br><label style="font-family: sans-serif; font-size: small; color: white; background-color: #ed145b; border-radius: 5px;">Inbound Map</label></a>
			</div>
			<div class="reporting_reg">
				<a id="report_link" style="text-decoration: none;"/><img src= "../images/3.png" style="margin-top: 50px; margin-left: 15px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Reporting</label></a>			
			</div>
			<div class="ad-hoc_reg">
			<br>
				<a onclick="location.href='logoutUser'" style="text-decoration: none;"><label>Logout</label></a><br>
				<a onclick="location.href='InboundRoamersAdHocMapTracker.jsp'" style="text-decoration: none;" /><img src= "../images/4.png" style="margin-top: 10px; margin-left: 5px;"><br><label style="font-family: sans-serif; font-size: small; color: white;">Ad-Hoc</label></a>
			</div>	
		</div>
		
		<div id = "map_content" style="overflow-y: auto; height: 600px;">
			     <div id="tableMapDiv" style="float: left; height: 70%; width: 20%; margin-top: 10px; margin-left: 10px; margin-right: 5px; border: 2px solid; border-radius: 8px;">
			     	<table id="tableMap" style="margin-left: auto; margin-right: auto; margin-top: 10px; ">
			     		<tr><td><label style="color: black; margin-left: 5px;">DATE</label></td><td style="text-align: center;"><label id="dateId" style="color: black; font-size: x-small;"></label></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">LOCATION</label></td><td style="text-align: center;"><label style="color: black;">COUNT</label></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">ALL</label></td><td style="text-align: center;"><a href="All"><label id="allId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">LUZON</label></td><td style="text-align: center;"><a href="Luzon"><label id="luzonId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">VISAYAS</label></td><td style="text-align: center;"><a href="Visayas"><label id="visayasId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">MINDANAO</label></td><td style="text-align: center;"><a href="Mindanao"><label id="mindanaoId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">NCR</label></td><td style="text-align: center;"><a href="NCR"><label id="ncrId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">CEBU</label></td><td style="text-align: center;"><a href="Cebu"><label id="cebuId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">ILOILO</label></td><td style="text-align: center;"><a href="Iloilo"><label id="iloiloId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">DAVAO</label></td><td style="text-align: center;"><a href="Davao"><label id="davaoId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">BAGUIO</label></td><td style="text-align: center;"><a href="Baguio"><label id="baguioId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">BORACAY</label></td><td style="text-align: center;"><a href="Boracay"><label id="boracayId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">CORON</label></td><td style="text-align: center;"><a href="Coron"><label id="coronId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">EL NIDO</label></td><td style="text-align: center;"><a href="El Nido"><label id="elNidoId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">PUERTO GALERA</label></td><td style="text-align: center;"><a href="Puerto Galera"><label id="pGaleraId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">PUERTO PRINCESA</label></td><td style="text-align: center;"><a href="Puerto Princesa"><label id="pPrincesaId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">SUBIC</label></td><td style="text-align: center;"><a href="Subic"><label id="subicId" style="color: black;"></label></a></td></tr>			     		
			     		<tr><td><label style="color: black; margin-left: 5px;">TAGAYTAY</label></td><td style="text-align: center;"><a href="Tagaytay"><label id="tagaytayId" style="color: black;"></label></a></td></tr>
			     		<tr><td><label style="color: black; margin-left: 5px;">UNKNOWN</label></td><td style="text-align: center;"><a href="Unknown"><label id="unknownId" style="color: black;"></label></a></td></tr>
			     	</table>
			     </div>
				 <div id="map" name="map" style="margin-top: 10px; height: 97%; width: 78%; display: inline-block; border: 2px solid; border-radius: 8px;"></div>
		</div>
		<div id="poweredByDiv"><img src="../images/poweredby.png"></div>
	</div>
</body>
</html>