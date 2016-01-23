#Webgenekfca

This is a Beta web based tool which helps the researcher to group similar genes based on their expression profiles for different experiments.

The genes are grouped based on the concepts obtained from a K-Formal Concept Analysis.

#Installation

##Requirements:

-Java Development Kit 1.7.x or higher
-Sprint Roo 1.2.5 or higher: http://projects.spring.io/spring-roo/
-Mysql server 5.5
-Apache Tomcat 7.x or higher

##Database preparation

1- Edit the file src/main/resources/META-INF/spring/database.properties
   a) Define the host where the mysql server is installed and the db name:
      database.url=jdbc\:mysql\://localhost\:3306/databasename
   b) Define the username:
      database.username=username
   c) Define the password:
      database.password=password

2- Enter in mysql and create the database:
mysql> create database databasename;

3- Create the user:
mysql> create user 'username' identified by 'password';

4- Grant priviledges:
mysql> grant all privileges on databasename.* to 'username';

5- Fill database with gene info:
   a) Enter in the geneinfo folder:
      $ cd src/main/resources/geneinfo
   b) Generate the database file: 
      $ python populate_genedescription.py . > db_dump.sql
   c) Insert into database:
      $ mysql -uusername -ppassword databasename < db_dump.sql

##Install Affymetrix Tools

The Affymetrix tools are needed to upload .CEL files into the web application.
1- Download the last version of Affymetrix Tools
http://www.affymetrix.com/estore/partners_programs/programs/developer/tools/powertools.affx#1_2

2- Unzip into the destination folder, and grant it execution priviledges

##Configure ApplicationContext.xml

The applicationContext.xml file is located in src/main/resources/META-INF/spring
Here inside <bean class="es.uc3m.tsc.file.ProcessUploadedFiles" id="processCELFiles"> three arguments have to be changed:
The first <constructor-arg/> tag tells a temporary file needed when uploading data from different experiments
The second <constructor-arg/> points to the Affymetrix Tools bin directory
The third <constructor-arg/> includes the complete address where the CDF files are located. These files contain microarry information
and are used by the Affymetrix Tools to generate the proper gene expression matrix. These files are in this project under src/main/resources/cdf/

##Building

In the home project folder execute roo.sh
$ roo.sh

The first time you execute it will take longer because it has to create all .aj from
the java files. Once it has finished loading the data building can start, write:
roo> perform package

This will generate the war file: target/GeneAnalyzer-0.1.0.BUILD-SNAPSHOT.war

##Deploy

The deploy has to be done in Apache Tomcat.
The JAVA_HOME environmet variable has to be set to the jdk directory
To start the tomcat:
$ apache-tomcat-7.0.57/bin/start.sh
To stop the tomcat:
$ apache-tomcat-7.0.57/bin/stop.sh

To deploy copy the war into the webapps dir:
$ mv GeneAnalyzer-0.1.0.BUILD-SNAPSHOT.war ~/apache-tomcat-7.0.57/webapps/webgenekfca.war

In your browser load the url:
http://localhost:8080/webgenekfca

The logs will be shown in:
- apache-tomcat-7.0.57/logs/webgenekfca.log
- apache-tomcat-7.0.57/logs/catalina.out

##Debug

After creating the database and building, it is possible to debug the application.
The debug can be done with the Sprint Tool Suite (STS).

1- Open the STS
2- Import the project:
    File->Import->General->Existing Projects into Workspace
3- Write the project folder in the box titled: 'Select root directory'
4- Create the web server:
    Window->Preferences->Server->Runtime Environment->Add
5- Add the Apache Tomcat server
    Apache->Apache Tomcat v7.0->Next
6- In 'Tomcat installation directory' select the folder where the Tomcat is installed
7- Click Finish
8- If the 'Server' tab view is not present then add it: 
    Window->Show View->Other->Server->Servers
9- In the 'Server' tab view right click:
    New->Server->Apache->Tomcat v7.0 Server->Next
10-Add the available project to the configured and click Finish
11-To launch right click on the newly created Tomcat server and click on Debug
12-Open a browser and go to: http://localhost:8080/[projectName]


