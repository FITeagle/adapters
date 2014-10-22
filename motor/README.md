FITeagle :: Adapter :: Motor
=============================
- Level: Demonstration
- Technologies: REST
- Summary: Test implementation of Motor Adapter with REST in a Wildfly Environment
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

The adapter can be accessed via different delivery mechanisms: direct REST, Websocket, EJB or JMS (via Northbound REST or Web GUI)


JMS/MDB Delivery Mechanism
--------------------------------

### Open the at the adapter manager

Open the adapter manager to see a list of the deployed adapters and manage them:

[http://localhost:8080/native/gui/admin/adapter-manager.html](http://localhost:8080/native/gui/admin/adapter-manager.html)

Monitor, create, terminate and configure resource adapters.


### Experimenting via curl REST calls


The example RDF files that are used in some calls can be found in the adapters/motor directory.

* Create a single new motor instance using an attached, detailed RDF description:
  * ```curl -i -X PUT -d @createMotor.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1```
  * Response should be HTTP 201 + New motor instance RDF description
  
  
* Create four new motor instances at once using an attached, detailed RDF description:
  * ```curl -i -X PUT -d @createManyMotors.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1```
  * Response should be HTTP 201 + New motor instance RDF description
   
* Create a single new motor resource instance named "ARunningMotor01" with no attached RDF description and default parameters (just using the path):
  * ```curl -i -X PUT http://localhost:8080/native/api/resources/ADeployedMotorAdapter1/ARunningMotor01```
  * Response should be HTTP 201 + New motor instance RDF description

* To get a description of all resources instances managed by the adapter:
  * ```curl -i -X GET http://localhost:8080/native/api/resources/ADeployedMotorAdapter1```

* To get a description of the properties of a single resource instance managed by the adapter:
  * ```curl -i -X GET http://localhost:8080/native/api/resources/ADeployedMotorAdapter1/ARunningMotor01```
  
* Release a single motor resource instance (just using the path):
  * ```curl -i -X DELETE http://localhost:8080/native/api/resources/ADeployedMotorAdapter1/ARunningMotor01```
  * Response should be HTTP 200 + motor instance release RDF description
  
* Configure a single motor resource instance using attached RDF description:
  * ```curl -i -X POST -d @configureMotor.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1```
  * Response should be HTTP 200 + motor instance updated properties RDF description

* Configure a two motor resource instances at the same time using attached RDF description:
  * ```curl -i -X POST -d @configureManyMotors.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1 ```
  * Response should be HTTP 200 + motor instances updated properties RDF description
 
* Configure "Motor1" instance so it becomes dynamic:
  * ```curl -i -X POST -d @configureDynamicMotorTrue.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1 ```
  * Response should be HTTP 200 + motor instances updated properties RDF description
  * A motor resource instance that is configured with the property isDynamic = true will randomly change its RPM property every 5 seconds and send a corresponding notification (fitealge:inform Message).
  * Open the log viewer to see those notifications. Alternatively keep requesting the motor instances details using GET (see above) to see the updated RPM values. Also keep refreshing the FUSEKI web interface to see the live updates made in the repository.
  
* Configure "Motor1" instance so it it no longer dynamic:
  * ```curl -i -X POST -d @configureDynamicMotorFalse.ttl http://localhost:8080/native/api/resources/ADeployedMotorAdapter1 ```

**Dynamic Motor Instances**

To create a dynamic motor that automatically changes its RPM every 5 seconds:
- Provision a motor instance
- Change this instance's property "isDynamic" to true via Control box.


**Example configure input string:**

To change the RPM of motor resource instance named "Motor1" to 500:


```

@prefix motorgarage: <http://open-multinet.info/ontology/resource/motorgarage#> .
@prefix motor: <http://open-multinet.info/ontology/resource/motor#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix av:    <http://federation.av.tu-berlin.de/about#> .

av:ADeployedMotorAdapter1 a motorgarage:MotorGarageAdapter .

av:Motor1 a motor:Motor ;
                            motor:rpm 500 .
                            
```


EJB Delivery Mechanism
-----------------------------

Access the Adapter as EJB:

`(IMotorAdapterEJB) new InitialContext().lookup("java:module/MotorAdapter");`



WebSocket Delivery Mechanism 
-----------------------------

Connect to WebSocket at:

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



REST Delivery Mechanism
----------------------------

IMPORTANT: REST DM is not working at the moment together with wildfly 8 + resteasy + beans.xml because of:

https://developer.jboss.org/thread/242654?_sscc=t

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

`curl -v -X PUT -d @"input.ttl" http://localhost:8080/AdapterMotor/api/instance/1/description.ttl`

 * Terminate:

`curl -X DELETE http://localhost:8080/AdapterMotor/api/instance/1`


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

