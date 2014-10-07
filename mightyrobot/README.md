FITeagle :: Adapter :: MightyRobot
=============================
- Dummy MightyRobot Adapter, low-feature descendant from Motor
- Level: Demonstration
- Technologies: REST, Websockets, JMS
- Summary: Prototype implementation of MightyRobot Adapter with REST using RestEasy in Wildfly Environment, as well as websockets and JMS messaging, working based on fiteagle ontology model
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. Type this command to build and deploy the archive:

        mvn clean verify wildfly:deploy

4. This will deploy `target/AdapterMightyRobot.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the package.

Access the Adapter
------------------

Javascript Client is at:

<http://localhost:8080/AdapterMightyRobot/test.html>

WebSocket is at:

`ws://localhost:8080/AdapterMightyRobot/websocket`

REST is at:

<http://localhost:8080/AdapterMightyRobot/api/>


Test the adapter with the following commands

 * Describe:
`curl -X GET http://localhost:8080/AdapterMightyRobot/api/description.ttl`

 * Provision:
`curl -X GET http://localhost:8080/AdapterMightyRobot/api/instances.ttl`

 * Provision (create instance):
`curl -X POST http://localhost:8080/AdapterMightyRobot/api/instance/1`

 * Monitor:
`curl -X GET http://localhost:8080/AdapterMightyRobot/api/instance/1/description.ttl`

 * Control (file is path to local input ttl file that needs to be supplied)
`curl -v -X PUT -F file=@"input.ttl" http://localhost:8080/AdapterMightyRobot/api/instance/1/description.ttl`

 * Terminate:
`curl -X DELETE http://localhost:8080/AdapterMightyRobot/api/instance/1`


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFyl).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

