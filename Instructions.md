## Instructions to Run the Smart-Hospital Project and Associated Tests

#### What you need to work with this project:
* Maven
* IntelliJ
* Apache Tomcat
* MySQL Workbench 

#### Update Maven Dependencies
* Open the project in IntelliJ.
* Open the `pom.xml` file and download the necessary dependencies.

#### Set up MySQL Database
* Download MySQL Workbench from the following website: https://www.mysql.com/products/workbench/
* Follow the installation wizard to get it set up on your device.
* Note: you should set your username to 'root' and password to 'rootuser' (these are the credentials that we used to run the app and associated tests).
* Open MySQL workbench and click the "+" button. This should open a menu titled "Setup New Connection".
* Create a connection named "root". It should have the hostname "127.0.0.1", use port "3306", and username "root" and password "rootuser" should be valid credentials for it.
* Open your new connection named "root". 
* Click "File", then "Open SQL Script", and then open the "db-script.sql" file (in the "resources" directory).
* Run all the commands in the file. You can do this by clicking "Ctrl-Enter" on each line.
* If at any point the server is shutdown, you can restart it by clicking on "Startup / Shutdown" under the "Instance" tab on the "Navigator" menu. 

#### Set up Tomcat
* Download Apache Tomcat from the following website: https://tomcat.apache.org/download-11.cgi
* Follow Apache's instructions (https://tomcat.apache.org/tomcat-11.0-doc/setup.html) to set it up on your device.
* To set up the server in the project in IntelliJ, follow these instructions: https://www.jetbrains.com/guide/java/tutorials/working-with-apache-tomcat/using-existing-application/.
* To run the server, go to the "Services" menu (at the bottom by the Git, Run, Terminal, etc. menus).
* Select your Tomcat server and click the "Run" button.
* You should see a new tab open in your browser. It should have the following link: http://localhost:8080/Doctor_Patient_Portal_war/.

#### Running Tests
* Note: for any backend test files, only the database needs to be running (not the Tomcat server). For any frontend test files, both need to be running.
* Backend tests are located in the following directory: 'src/main/test/java/com/hms'.
* Frontend tests are located in the following directory: 'src/main/test/java/webapp'.
* You can run all the tests in any individual test class by opening the file and clicking on the "Run test" button next to the class declaration in the left sidebar.
* You can run any individual test in any individual test class by opening the file and clicking on the "Run test" button next to the test method declaration in the left sidebar.

#### Installing Maven
* To generate code coverage reports with JaCoCo, you will need to have Apache Maven installed on your device
* Follow Apache's tutorial here: https://maven.apache.org/install.html

#### Generating Code Coverage Reports with JaCoCo
* We used JaCoCo to generate code coverage reports on our whitebox tests in the following directories:
  * 'src/main/test/java/com/hms/admin/servlet'
  * 'src/main/test/java/com/hms/doctor/servlet'
  * 'src/main/test/java/com/hms/entity'
  * 'src/main/test/java/com/hms/user/servlet'
* You can generate the report using the following commands:
  * 'mvn clean test'
  * 'mvn jacoco:report'
* You can find the report in the 'target/site/jacoco' directory (it will be named 'index.html')

#### Other notes
* When working with the frontend, the admin login email is "admin@gmail.com" and the password is "admin".
* If the 'mvn' command isn't working on your device, it may not be setup correctly and you may need to use the entire path to it
  * For example, it might be something like this: '/c/ProgramData/chocolatey/lib/maven/apache-maven-3.9.9/bin/mvn.cmd'
