var host = "http://localhost:8080/AdapterMotor/api/";

var wsUri = getRootUri() + "/AdapterMotor/websocket";
var restOutput;
var wsOutput;
var websocket;

var wsSerialization = "ttl";
var restSerialization = "ttl";

var currentInstanceID = 1;

var restMotors = [];
var wsMotors = [];

google.load('visualization', '1', {
	packages : [ 'gauge' ]
});
// google.setOnLoadCallback(drawChart);

var wsGaugeData;


window.addEventListener("load", init, false);

var gaugeOptions = {
	// width: 400,
	height : 200,
	min : 0,
	max : 1000,
	redFrom : 900,
	redTo : 1000,
	yellowFrom : 750,
	yellowTo : 900,
	minorTicks : 50,
    animation:{
        duration: 30000,
        easing: 'out',
      }

};

String.prototype.escape = function() {
	var tagsToReplace = {
		'&' : '&amp;',
		'<' : '&lt;',
		'>' : '&gt;'
	};
	return this.replace(/[&<>]/g, function(tag) {
		return tagsToReplace[tag] || tag;
	});
};

function init() {
	restOutput = document.getElementById("restOutput");
	wsOutput = document.getElementById("wsOutput");

	websocket = new WebSocket(wsUri);
	websocket.onopen = function(evt) {
		onOpen(evt);
	};
	websocket.onmessage = function(evt) {
		onMessage(evt);
	};
	websocket.onerror = function(evt) {
		onError(evt);
	};
	
	wsGaugeData = new google.visualization.DataTable();
	wsGaugeData.addColumn('string', 'Motor');
	wsGaugeData.addColumn('number', 'RPM');

	refresGUIInstanceIDs();
}

function drawChart(data, element) {
	// var data = google.visualization.arrayToDataTable([
	// ['Label', 'Value'],
	// ['Memory', 80],
	// ['CPU', 55],
	// ['Network', 68]
	// ]);

	// var data = new google.visualization.DataTable();
	// data.addColumn('string', 'Motor');
	// data.addColumn('number', 'RPM');
	// data.addRow(['V', 200]);

	var chart = new google.visualization.Gauge(document.getElementById(element));
	chart.draw(data, gaugeOptions);
}

function getRootUri() {
	return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" + (document.location.port == "" ? "8080" : document.location.port);
}

function wsGetDescription() {
	wsDoSend("description." + wsSerialization);
}

function wsGetInstances() {
	wsDoSend("instances." + wsSerialization);
}

function onOpen(evt) {
	wsWriteToScreen(false, "Connected to Endpoint!");
	// doSend(textID.value);
}

function onMessage(evt) {
	wsWriteToScreen(false, "Message Received: <br/>" + evt.data);
	
	if(evt.data.indexOf("Event Notification:") > -1){
		wsRefreshInstanceGraphics(evt.data, true);
	} else {
		wsRefreshInstanceGraphics(evt.data, false);
	}
}

function onError(evt) {
	wsWriteToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function wsDoSend(message) {
	wsWriteToScreen(true, "Message Sent: " + message);
	websocket.send(message);
}

function wsRefreshInstanceGraphics(ttlString, isEvent) {
	wsMotors = [];
	
	if(isEvent){
		if(ttlString.indexOf("terminated:") > 0){
			var pos = ttlString.indexOf("terminated:");
			var pos2 = ttlString.indexOf(";;");
			var instanceIdToTerminate = ttlString.slice(pos+11,pos2);
			
			for(var i = 0; i < wsGaugeData.getNumberOfRows(); i++){
				if(wsGaugeData.getValue(i, 0) == instanceIdToTerminate){
					wsGaugeData.removeRow(i);
				}
			}
			
			
		} else if(ttlString.indexOf("provisioned:") > 0){
			var pos = ttlString.indexOf("provisioned:");
			var pos2 = ttlString.indexOf("::");
			var instanceIdToProvision = ttlString.slice(pos+12,pos2);
			var pos3 = ttlString.indexOf(";;");
			var rpmToProvision = parseInt(ttlString.slice(pos2+2,pos3));
			wsGaugeData.addRow([ instanceIdToProvision, rpmToProvision ]);
			
			
		} else if(ttlString.indexOf("changedRPM:") > 0){
			var pos = ttlString.indexOf("changedRPM:");
			var pos2 = ttlString.indexOf("::");
			var instanceIdToChange = ttlString.slice(pos+11,pos2);
			var pos3 = ttlString.indexOf(";;");
			var rpmToChange = parseInt(ttlString.slice(pos2+2,pos3));
			
			for(var i = 0; i < wsGaugeData.getNumberOfRows(); i++){
				if(wsGaugeData.getValue(i, 0) == instanceIdToChange){
					//wsGaugeData.removeRow(i);
					wsGaugeData.setValue(i, 1, rpmToChange);
				}
			}
			
			//var newValue = 1000 - data.getValue(0, 1);
			//wsGaugeData.setValue(0, 1, rpmToChange);
		    //  drawChart();

			//wsGaugeData.addRow([ "M" + instanceIdToProvision, rpmToChange ]);
			
		}
		
	} else {

		if (wsSerialization == "ttl") {
			parseTTL(ttlString, wsMotors);
		}
	
		wsGaugeData = new google.visualization.DataTable();
		wsGaugeData.addColumn('string', 'Motor');
		wsGaugeData.addColumn('number', 'RPM');
	
		for ( var index = 0; index < wsMotors.length; ++index) {
			// $("#restGraphics").append(restMotors[index].instanceID + " -> " +
			// restMotors[index].rpm + "<br/>");
			wsGaugeData.addRow([ wsMotors[index].instanceID, parseInt(wsMotors[index].rpm) ]);
		}
	}

	drawChart(wsGaugeData, 'wsGraphics');

}

function wsWriteToScreen(isCommand, message) {
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";

	if (isCommand) {
		pre.style.color = "blue";
	} else {
		message = formatInput(message);
	}

	pre.innerHTML = message;
	wsOutput.appendChild(pre);
	wsOutput.scrollTop = wsOutput.scrollHeight;
}

function restWriteToScreen(isCommand, message) {
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";
	if (isCommand) {
		pre.style.color = "blue";
	} else {
		message = formatInput(message);
	}
	pre.innerHTML = message;
	restOutput.appendChild(pre);
	restOutput.scrollTop = restOutput.scrollHeight;
}

function formatInput(inputString) {

	if (restSerialization != "ttl") {
		inputString = inputString.escape();
	}
	inputString = inputString.split('\n').join('<br/>');

	return inputString;
}

//function insertLiteral(literalString, targetElement) {
//	var textNode = document.createTextNode(literalString);
//	targetElement.appendChild(textNode);
//	return textNode;
//}
//
//function getXmlString(xml) {
//	if (window.ActiveXObject) {
//		return xml.xml;
//	}
//	return new XMLSerializer().serializeToString(xml);
//}


function refresGUIInstanceIDs() {
	$("#restInstanceNumber").val(currentInstanceID);
}

function restGetDescription() {
	var restURL = host + "description." + restSerialization;
	restGET(restURL);
}

function restGetInstances() {
	var restURL = host + "instances." + restSerialization;
	restGETInstances(restURL);
}

function restRefreshInstanceGraphics(ttlString) {
	restMotors = [];

	if (restSerialization == "ttl") {
		parseTTL(ttlString, restMotors);
	}

	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Motor');
	data.addColumn('number', 'RPM');
	// data.addRow(['Motor 1', 200]);

	// $("#restGraphics").html("");
	for ( var index = 0; index < restMotors.length; ++index) {
		// $("#restGraphics").append(restMotors[index].instanceID + " -> " +
		// restMotors[index].rpm + "<br/>");
		data.addRow([ "M" + restMotors[index].instanceID + " RPM", parseInt(restMotors[index].rpm) ]);
	}

	drawChart(data, 'restGraphics');
}

function parseTTL(ttlString, motors) {

	var index = 0;

	var pos = ttlString.indexOf("rdfs:label");
	while (pos > 0) {

		ttlString = ttlString.slice(pos);
		pos = ttlString.indexOf("\"");
		ttlString = ttlString.slice(pos + 1);
		pos = ttlString.indexOf("\"");
		var currentInstanceID = ttlString.slice(0, pos);

		pos = ttlString.indexOf(":rpm");
		ttlString = ttlString.slice(pos);
		pos = ttlString.indexOf("\"");
		ttlString = ttlString.slice(pos + 1);
		pos = ttlString.indexOf("\"");
		var currentRPM = ttlString.slice(0, pos);

		var motor = {
			instanceID : currentInstanceID,
			rpm : currentRPM
		};

		motors[index] = motor;

		index++;
		pos = ttlString.indexOf("rdfs:label");
	}
}

function restProvisionInstance() {
	var restURL = host + "instance/" + $("#restInstanceNumber").val();
	restPOST(restURL);
	currentInstanceID++;
	refresGUIInstanceIDs();
}

function restMonitorInstance() {
	var restURL = host + "instance/" + $("#restInstanceNumber").val() + "/description." + restSerialization;
	restGET(restURL);
}

function restTerminateInstance() {
	var restURL = host + "instance/" + $("#restInstanceNumber").val();
	restDELETE(restURL);
	currentInstanceID--;
	refresGUIInstanceIDs();
}

function restChangeRPM() {
	var restURL = host + "instance/" + $("#restInstanceNumber").val() + "/rpm/" + $("#restChangeRPMValue").val();
	restPUT(restURL);
}

function restGET(restURL) {
	restWriteToScreen(true, "Sending GET " + restURL);
	$.ajax({
		url : restURL,
		type : 'GET',
		// data : 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
		success : function(data) {
			restWriteToScreen(false, data);
		}
	});
}

function restGETInstances(restURL) {
	restWriteToScreen(true, "Sending GET " + restURL);
	$.ajax({
		url : restURL,
		type : 'GET',
		// data : 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
		success : function(data) {
			restWriteToScreen(false, data);
			restRefreshInstanceGraphics(data);
		}
	});
}

function restPOST(restURL) {
	restWriteToScreen(true, "Sending POST " + restURL);
	$.ajax({
		url : restURL,
		type : 'POST',
		// data : 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
		success : function(data) {
			restWriteToScreen(false, data);
		}
	});

}

function restDELETE(restURL) {
	restWriteToScreen(true, "Sending DELETE " + restURL);
	$.ajax({
		url : restURL,
		type : 'DELETE',
		// data : 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
		success : function(data) {
			restWriteToScreen(false, data);
		}
	});
}

function restPUT(restURL) {
	restWriteToScreen(true, "Sending PUT " + restURL);
	$.ajax({
		url : restURL,
		type : 'PUT',
		// data : 'ID=1&Name=John&Age=10', // or $('#myform').serializeArray()
		success : function(data) {
			restWriteToScreen(false, data);
		}
	});
}

function setRestSerialization(fileEnding) {
	restSerialization = fileEnding;
}

function setWsSerialization(fileEnding) {
	wsSerialization = fileEnding;
}