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

        mvn clean package wildfly:deploy

4. This will deploy `target/AdapterMotor.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

The adapter will be running at the following URL in the Browser: <http://localhost:8080/AdapterMotor/api/description.ttl> and will send some test TTL file.

 * Describe:
`curl -X GET http://localhost:8080/AdapterMotor/api/description.ttl --header "Content-Type:text/xml"`

 * Provision:
`curl -X GET http://localhost:8080/AdapterMotor/api/instances.ttl --header "Content-Type:text/xml"`

 * Provision (create instance):
`curl -X DELETE http://localhost:8080/AdapterMotor/api/instance/1 --header "Content-Type:text/xml"`

 * Monitor: HTTP GET localhost:8080/ADAPTERNAME/instance/1/descripti...
TODO

 * Control: HTTP PUT localhost:8080/ADAPTERNAME/instance/1/descripti...
TODO

 * Terminate:
`curl -X DELETE http://localhost:8080/AdapterMotor/api/instance/1 --header "Content-Type:text/xml"`



Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFyl).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

