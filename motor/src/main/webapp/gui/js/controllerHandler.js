/**
 * Created by vju on 22.06.14.
 */


var getDescriptionURL = "http://localhost:8080/AdapterMotor/api/description.ttl";
var getInstancesURL = "http://localhost:8080/AdapterMotor/api/instances.ttl";
var provisonInstanceURL = "http://localhost:8080/AdapterMotor/api/instance/"


var ControllerHandler = function(){
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


}
var controllerHandler;
$( document ).ready( function(){
     controllerHandler = new ControllerHandler();
    $("#contentDiv").load("description.html");
    $.get(getDescriptionURL, function(data){
        $("#adapterDescription").html(data);
    })
} )


function deactivateMenu() {
    $("#ul_sidebar").children().each(function(){
        $(this).removeClass("active");
    });
}
