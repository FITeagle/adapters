FITeagle :: Adapter :: Stopwatch
=============================
- Level: Demonstration
- Technologies: REST
- Summary: Test implementation of Stopwatch Adapter with REST in a Wildfly Environment
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

See Motor Adapter README.md Essentially the same commands as the motor adapter, but instead of the name "ADeployedMotorAdapter1" use "ADeployedStopwatchAdapter1" in the JMS related calls. Also replace AdapterMotor with AdapterStopWatch in the direct REST calls

A stopwatch resource has three properties:

 * currentTime: The time since starting the stopwatch.
 * refreshInterval: The time between notifications of currenTime (while the stopwatch has been started and running).
 * isRunning: Set to true to start the stopwatch and false to stop it. 


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

