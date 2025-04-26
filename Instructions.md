## Instructions to Run the Smart Hospital Project

#### Set up Tomcat
Download and install Apache Tomcat from their official website.


#### Set up MySQL Database
Download and install MySQL from their official website. Then, set up a new connection and remember the username and password you set up. From this point forward in the document, we will refer to the username as "root" and the password as "rootuser" as those are our own credentials. You should change all occurrences of these values depending on your own chosen credentials.

#### Update Maven Dependencies
Go to the `pom.xml` file and download the necessary dependencies.

#### Update Database Configuration
In `src/main/java/com/hms/db/DbConnection.java`, modify the following line:
`conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","rootuser");`
- Link can most likely kept the same.
- "root" and "rootuser" are your own MySQL connection's username and password. Set them up accordingly.

Similarly, in all other DAO and Admin Servlet test files (`src/main/java/com/hms/dao/*DAOTest.java`, `src/main/java/com/hms/admin/servlet/*.java`), update the connection string to match your MySQL credentials.
- These include AppointmentDAOTest, DoctorDAOTest, PatientDAOTest, UserDAOTest; all files in `admin/servlet/*`.

#### Run the Project
1. Non-frontend files can be run in IntelliJ as normal. The web tests all require you to start a Tomcat server first (a localhost link should pop up and you should be able to interact with the application), then you can run the tests.



## Debugging / Troubleshooting