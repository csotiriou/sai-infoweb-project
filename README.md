# Preface
Project developed for Msc Software Engineering for Ambient Intelligence, 2nd semester. 

The goal of the project is to create an internet based notification system between professors and students (push and normal messaging). The professors can use a web based service to send messages to the smartphones of selected registered students. The initial requirements do not cover sending notifications from students to professors.

**Estimated completion time:** 90 hours of development work.

Project completed and documented. This is my first encounter with web service oriented development, and I used plain JSP and JDBC, with a custom API for cleaning open JDBC connections.


I posted this project so that other students/developers can benefit from the work I have done in this project.

#### Project Features / Focus

* Extensible API for adding new capabilities
* Efficient MySQL connection re-use (DataPool)
* Support for re-using the same connections for multiple statements, and providing automatic cleanup of open connections (see AbstractDAO documentation)
* Highly extensible JSON serialising methods (based on GSON but added new interfaces and methods for extensibility). See APIResponse.java .
* Support for plain JDBC connections
* Configurability
* Clear documentation

#### Public release limitations

Contrary to the internal release, the public release is stripped from the iOS push notification certificates, and the mail messaging system supporting sending e-mails from Oramind's web service (my own website), due to security concerns.

Since this was a school project that should be completed into a small amount of time, security is not taken into account.

#Prerequisites:

#### ECLIPSE REQUIREMENTS:

* Recommended version: Eclipse Juno EE.
* Necessary plugins: m2e, m2e-wtp, WTP tools for eclipse.
* Java version: 1.6+ 


#### XCode REQUIREMENTS

* Recommended version: 4.5+
* Minimum system version: OS X Lion 10.7+ (not tested).
* Recommended system version: OS X Lion 10.8+ (tested).

#### Server & MySQL requirements
Tomcat version 7+ & MySQL server 5.2+


## Getting started:


##### DEPLOYING THE SERVER


**FOR THIS PUBLIC RELEASE (WHERE MUCH OF THE INFORMATION IS STRIPPED) TAKE A LOOK AT THE `DB.properties` FILE TO ALSO SEE WHAT YOU NEED TO SET UP THE SERVICE CORRECTLY**

* Import the project "webappMAVEN" into Eclipse
* Make sure you have a MySQL server up and running.
* Make sure that you have a tomcat environment set up correctly.
* In the Eclipse project, run the contents of **ConfigScript.sql** file to your MySQL server. This will create all the necessary database structure for the project to run.
* In the Eclipse project, open DB.properties. This is the file containing all important configuration options for the project. What is really interesting up to this point is the following options that specify the database connectivity.

```
#!text

dbname=oradb
dbpass=
dbuser=root
dbport=3306
dbhost=localhost

```


Change the values of these properties to suit your environment.

That's it. Now you can produce a .war file by right-clicking on the project in Eclipse, and click Export->WAR file… . Then install the resulting .war file to your tomcat installation.

##### LOGGING INTO THE SERVICE
After you install the server, visit the address of the server at `http://<server-address>:<port>/webapp/` and you will be redirected to the login page. You can use the following credentials

```
#!text

username: root
passord: <leave the field blank>

```

to log into the service and start using the service.

##### RUNNING THE iOS APPLICATION:
Open the InfowebTests.xcodeproj file with Xcode, and run the application. You may need to tackle with Definitions.h file to change the location and the port of the server (to tell the application where to connect when running the service).

##### IMPORTANT NOTE: 
Push notifications do not work when running on iOS Simulator. This is expected, as push notifications on the Simulator are not supported by Apple at the time of this writing. 

##### 'PREF' DATABASE TABLE
Information stripped for public released

If you don't know how to use this table, then set the `useOramindService` in DB.properties to `false`.

## TESTING
Testing is performed by running the testing functions of the bundled iOS application. In order to do so, MySQL must be pre-populated with some test data in advance.

##### Server Setup:

Open the DB.properties file inside the eclipse project.
Set the property `external3rdPartyTestsAllowed` to `true`
Reinstall the web application to the web server to ensure that the updates have happened


##### Pre-Populating the Database to prepare for testing
On the server visit the address `http:///<your-server>/webapp/StressTests?req=setup` just **once** so that the database can be prepopulated.

##### Running the Tests from the client
This is done through the normal procedure of running unit tests from the database. 

* Open the InfoWeb.xcodeproj project.
* Hit command-U in the keyboard (shortcut for testing the project)
* Alternatively, you can select the UnitTests scheme, and run it as you would run a normal application

### Testing notes:
For locahost servers, the expected results are 100% success of iOS unit tests. For Internet servers, the results may vary depending on the Internet connection.