FITeagle :: Adapter :: MightyRobot
=============================
- Author: Alexander Willner & Franco le Herrmann de la Technische Universität in the city of Berlin
- Level: Demonstration
- Technologies: JMS, EJB, MDB
- Summary: Demonstrates the use of JMS 1.1 and EJB 3.1 Message-Driven Bean to implement a FITeagle Adapter for a mighty robot
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>

What is it?
-----------

This example demonstrates the use of *JMS 1.1* and *EJB 3.1 Message-Driven Bean* in WildFly to implement a FITeagle Adapter.
It probably still does this, I don't get everything that's happening, yet. But it also simulates an epic robot now. YEAH!

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly) ( ./bootstrap/fiteagle.sh startJ2EE )
2. Open a command line and navigate to the root directory of this project. ( mightyrobot )
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/mightyrobot.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

The adapter will be running at the following URL: <http://localhost:8080/mightyrobot/> 
If it's deployed it will pop up here http://localhost:9990/console/App.html#deployments

Available ressources:

Show adapter description
curl -X GET http://localhost:8080/mightyrobot/api/description.ttl

List all instances
curl -X GET http://localhost:8080/mightyrobot/api/instances.ttl

Add an instance
curl -X POST http://localhost:8080/mightyrobot/api/instance/INSTANCENAME
Delete an instance
curl -X DELETE http://localhost:8080/mightyrobot/api/instance/INSTANCENAME

Put Description of an instance
curl -X PUT -d "Description=DESCRIPTIONTEXT" http://localhost:8080/mightyrobot/api/instance/INSTANCENAME/description.ttl
Query Description of an instance
curl -X GET http://localhost:8080/mightyrobot/api/instance/INSTANCENAME/description.ttl

Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFyl).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy
