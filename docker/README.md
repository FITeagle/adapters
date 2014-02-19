FITeagle :: Adapter :: Docker
=============================
Author: Alexander Willner
Level: Demonstration
Technologies: JMS, EJB, MDB
Summary: Demonstrates the use of JMS 1.1 and EJB 3.1 Message-Driven Bean to implement a FITeagle Adapter for Docker
Target Product: FITeagle
Source: <https://github.com/fiteagle/adapters/>

What is it?
-----------

This example demonstrates the use of *JMS 1.1* and *EJB 3.1 Message-Driven Bean* in WildFly to implement a FITeagle Adapter.

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/adapter-docker.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

The adapter will be running at the following URL: <http://localhost:8080/adapter-docker/> and will send some debug messages to the queue.

Investigate the Server Console Output
-------------------------------------

Look at the WildFly console or server log and you should see log messages like the following:

    08:11:22,540 INFO  [class org.fiteagle.adapter.docker.mdb.DockerAdapter] (Thread-114 (HornetQ-client-global-threads-476550772)) The Docker adapter received a message from adapter topic: Text: This is message 1; Propery 'status': started
    08:11:22,543 INFO  [class org.fiteagle.adapter.docker.mdb.DockerAdapter] (Thread-128 (HornetQ-client-global-threads-476550772)) The Docker adapter received a message from adapter topic: Text: This is message 2; Propery 'status': started
    08:11:22,545 INFO  [class org.fiteagle.adapter.docker.mdb.DockerAdapter] (Thread-125 (HornetQ-client-global-threads-476550772)) The Docker adapter received a message from adapter topic: Text: This is message 4; Propery 'status': stopped
    08:11:22,546 INFO  [class org.fiteagle.adapter.docker.mdb.DockerAdapter] (Thread-124 (HornetQ-client-global-threads-476550772)) The Docker adapter received a message from adapter topic: Text: This is message 3; Propery 'status': stopped
    08:11:22,548 INFO  [class org.fiteagle.adapter.docker.mdb.DockerAdapter] (Thread-127 (HornetQ-client-global-threads-476550772)) The Docker adapter received a message from adapter topic: Text: This is message 5; Propery 'status': stopped


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFyl).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy
