/**
 * Created by vju on 22.06.14.
 */


var getDescriptionURL = "http://localhost:8080/mightyrobot/api/description.ttl";
var getInstancesURL = "http://localhost:8080/mightyrobot/api/instances.ttl";
var provisonInstanceURL = "http://localhost:8080/mightyrobot/api/instance/"

var getInstanceDescriptionURLPre = "http://localhost:8080/mightyrobot/api/instance/";
var getInstanceDescriptionURLSuf = "/description.ttl";

var ControllerHandler = function(){

	this.getInstanceDescriptionPage = function(){
		deactivateMenu();
		$("#a_getInstanceDescription").parent().addClass("active");
        $("#contentDiv").load("getInstanceDescription.html");
    }
	this.getInstanceDescription = function(){
		deactivateMenu();
		var instanceId = $("#input_getInstanceDescriptionInstanceName").val();
		var target = getInstanceDescriptionURLPre + instanceId + getInstanceDescriptionURLSuf;
       $.get(target, function(data){
            $("#instanceDescription").html(data);
        }).fail(function(e){
            alert(e.statusText)
        });

        return false;		
	}
    this.getDescription = function(){
        deactivateMenu();
        $("#a_getDescription").parent().addClass("active");
        $("#contentDiv").load("description.html");
        $.get(getDescriptionURL, function(data){
            $("#adapterDescription").html(data);
        }).fail(function(e){
            alert(e.statusText)
        });

        return false;
    }
    this.getInstances = function(){
        deactivateMenu();
        $("#a_getInstances").parent().addClass("active");
        $("#contentDiv").load("getInstances.html");
        $.get(getInstancesURL, function(data){
            $("#currentInstances").html(data);
        }).fail(function(e){
            alert(e.statusText)
        });
        return false;
    }
    this.provisionPage = function(){
        deactivateMenu();
        $("#a_createInstance").parent().addClass("active");
        $("#contentDiv").load("createInstance.html");

    }
    this.provisionInstance = function(){
        var instanceId = $("#input_instanceName").val();
        $.post( provisonInstanceURL + instanceId, function( data ) {
            alert(data);
        });

    }
	this.deletePage = function(){
        deactivateMenu();
        $("#a_deleteInstance").parent().addClass("active");
        $("#contentDiv").load("deleteInstance.html");

    }
    this.deleteInstance = function(){
        var instanceId = $("#input_deleteInstanceName").val();
        $.ajax({
		    url: provisonInstanceURL + instanceId,
		    type: 'DELETE',
		    success: function(result) {
		        alert(result);
		    }
		}); 
    }  
    this.putInstanceDescriptionPage = function(){
		deactivateMenu();
		$("#a_putInstanceDescription").parent().addClass("active");
        $("#contentDiv").load("putInstanceDescription.html");
    }
	this.putInstanceDescription = function(){
		deactivateMenu();
		var instanceId = $("#input_putInstanceDescriptionInstanceName").val();
		var instanceDesc = $("#input_putInstanceDescriptionInstanceDescription").val();
		var target = getInstanceDescriptionURLPre + instanceId + getInstanceDescriptionURLSuf;
		$.ajax({
		    url: target,
		    type: 'PUT',
		    data: [{name: "Description", value: instanceDesc}],
		    success: function(result) {
		        alert(result);
		    }
		});			
	}
}

var controllerHandler;
$( document ).ready( function(){
     controllerHandler = new ControllerHandler();
    $("#contentDiv").load("description.html");
    $.delete(getDescriptionURL, function(data){
        $("#adapterDescription").html(data);
    })
} )


function deactivateMenu() {
    $("#ul_sidebar").children().each(function(){
        $(this).removeClass("active");
    });
}
