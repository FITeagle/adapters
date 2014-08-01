FITeagle :: Adapter :: Motor
=============================
- Level: Demonstration
- Technologies: REST
- Summary: Test implementation of Motor Adapter with REST using RestEasy in Wildfly Environment
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. Type this command to build and deploy the archive:

        mvn clean install wildfly:deploy

4. This will deploy `target/AdapterMotor.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

###JMS Delivery Mechanism

**JMS Adapter Admin Console**

First start the logger and commander webservice at: core/bus via mvn wildfly:deploy

Control the Adapter via the Adapter Console GUI Interface in native/src/main/webapp/gui/admin/console2.html

Discover, listResources, Provision, Monitor, Terminate via Buttons
Create Control Input Strings via the box on the right and submit via Control button.

Instances are visualized as gauge charts at the bottom.

**Dynamic Motor Instances**

To create a dynamic motor that automatically changes its RPM every 5 seconds:
- Provision a motor instance
- Change this instance's property "isDynamic" to true via Control box.


The JMS DM listens at the Topic: topic/core

Send the following messages:

 * Describe:
 
`request:::description,,,serialization:::[SERIALIZATION]`

 * Instances:
 
`request:::listResources,,,serialization:::[SERIALIZATION]`

 * Provision (create instance):
 * 
`request:::provision,,,instanceID:::[INSTANCE_ID]`

 * Monitor:

`request:::monitor,,,instanceID:::[INSTANCE_ID],,,serialization:::[SERIALIZATION]`

 * Control (file is path to local input ttl file)

`request:::terminate,,,instanceID:::[INSTANCE_ID]`

 * Terminate:
 
`request:::control,,,control:::[CONTROL_INPUT],,,serialization:::[SERIALIZATION]`


- [INSTANCE_ID] - Integer
- [CONTROL_INPUT] - Control input string as ttl, rdf or n-triple
- [SERIALIZATION] - TURTLE, RDF/XML or N-TRIPLE


**Example control input string:**

To change the RPM of motor instance number 3 to 500:


```
@prefix :      <http://fiteagle.org/ontology/adapter/motor#> .
@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .

:m3     a             :MotorResource ;
        rdfs:label    "3" ;
        :rpm          "500"^^xsd:long .`

```



### EJB Delivery Mechanism

Access the Adapter as EJB:

`(IMotorAdapterEJB) new InitialContext().lookup("java:module/MotorAdapter");`



### WebSocket Delivery Mechanism 

Connet to WebSocket at:

`ws://localhost:8080/AdapterMotor/websocket`

Javascript Test Client is located at:

 * Instances:

<http://localhost:8080/AdapterMotor/test.html>

 * Describe:
 
`description.ttl`

`description.rdf`

`description.ntriple`


 * Instances:
 
`instances.ttl`

`instances.rdf`

`instances.ntriple`


 * Monitor, Provision, Control, Terminate
 
Not implemented so far.



### REST Delivery Mechanism

The REST DM is located at:

<http://localhost:8080/AdapterMotor/api/>

Javascript Test Client is located at:

<http://localhost:8080/AdapterMotor/test.html>


The adapter will be running at the following URL in the Browser: <http://localhost:8080/AdapterMotor/api/description.ttl> and will send some test TTL file.

 * Describe:

`curl -X GET http://localhost:8080/AdapterMotor/api/description.ttl`

 * Instances:

`curl -X GET http://localhost:8080/AdapterMotor/api/instances.ttl`

 * Provision (create instance):

`curl -X POST http://localhost:8080/AdapterMotor/api/instance/1`

 * Monitor:

`curl -X GET http://localhost:8080/AdapterMotor/api/instance/1/description.ttl`

 * Control (file is path to local input ttl file)

`curl -v -X PUT -F file=@"input.ttl" http://localhost:8080/AdapterMotor/api/instance/1/description.ttl`

 * Terminate:

`curl -X DELETE http://localhost:8080/AdapterMotor/api/instance/1`


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

