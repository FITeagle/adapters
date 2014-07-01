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

function getRootUri() {
	return "ws://" + (document.location.hostname == "" ? "localhost" : document.location.hostname) + ":" + (document.location.port == "" ? "8080" : document.location.port);
}

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

	refresGUIInstanceIDs();

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
	wsRefreshInstanceGraphics(evt.data);
}

function onError(evt) {
	wsWriteToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function wsDoSend(message) {
	wsWriteToScreen(true, "Message Sent: " + message);
	websocket.send(message);
}

function wsRefreshInstanceGraphics(ttlString) {
	wsMotors = [];
	wsMotorRPMs = [];
	if (wsSerialization == "ttl") {
		parseTTL(ttlString, wsMotors);
	}
	$("#wsGraphics").html("");
	for ( var index = 0; index < wsMotors.length; ++index) {
		$("#wsGraphics").append(wsMotors[index].instanceID + " -> " + wsMotors[index].rpm + "<br/>");
	}
}

function wsWriteToScreen(isCommand, message) {
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";

	if (isCommand) {
		pre.style.color = "blue";
	} else {

		if (restSerialization == "ttl") {
			message = message.split('\n').join('<br/>');
		} else {
			// message = vkbeautify.xml(message);
			// $('#myText').append(getXmlString(message));
			message = vkbeautify.xml(message);

			// test it (for the example, I assume #targetDiv is there but empty)
		//	var xmlString = "<this><is_some><xml with='attributes' /></is_some></this>";
		//	var targetElement = restOutput;
		//	var xmlTextNode = insertLiteral(xmlString, targetElement);
			// message = xmlString;
		}
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

		if (restSerialization == "ttl") {
			message = message.split('\n').join('<br/>');
		} else {
			 message = getXmlString(message);
			// $('#myText').append(getXmlString(message));
			//message = vkbeautify.xml(message.firstElementChild.innerHTML);

			// test it (for the example, I assume #targetDiv is there but empty)
		//	var xmlString = "<this><is_some><xml with='attributes' /></is_some></this>";
		//	var targetElement = restOutput;
		//	var xmlTextNode = insertLiteral(xmlString, targetElement);
			// message = xmlString;

		}
	}
	pre.innerHTML = message;
	restOutput.appendChild(pre);

	restOutput.scrollTop = restOutput.scrollHeight;
}

function insertLiteral(literalString, targetElement) {
	var textNode = document.createTextNode(literalString);
	targetElement.appendChild(textNode);
	return textNode;
}

function getXmlString(xml) {
	if (window.ActiveXObject) {
		return xml.xml;
	}
	return new XMLSerializer().serializeToString(xml);
}

window.addEventListener("load", init, false);

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
	$("#restGraphics").html("");
	for ( var index = 0; index < restMotors.length; ++index) {
		$("#restGraphics").append(restMotors[index].instanceID + " -> " + restMotors[index].rpm + "<br/>");
	}
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