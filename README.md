# Online Personal Finance Tracker

A student-built Java web application developed as a college project to
manage personal finances through **expenses, budgets, reports, advisor
guidance, and feedback**.

The project demonstrates Java, JDBC, MySQL, JSP, Servlets, session
management, role-based access, exception handling, transactions, and
basic multithreading.

------------------------------------------------------------------------

## 1. Project Overview

The application provides different features according to the user's
role.

  -----------------------------------------------------------------------
  Role                                Main Responsibilities
  ----------------------------------- -----------------------------------
  **User**                            Register/login, manage expenses,
                                      manage budgets, view reports, view
                                      advice, submit feedback

  **Advisor**                         View available users, view selected
                                      users' expenses, provide financial
                                      advice

  **Admin**                           Manage user accounts, review
                                      feedback, update feedback status,
                                      view basic statistics
  -----------------------------------------------------------------------

### Main Features

  -----------------------------------------------------------------------
  Feature                             Description
  ----------------------------------- -----------------------------------
  Authentication                      Registration, login, logout and
                                      session-based authentication

  Expense Management                  Add, view, edit and delete expenses

  Budget Management                   Create, view, edit and delete
                                      budgets

  Reports                             Spending summaries, category-wise
                                      spending and budget comparison

  Advisor Module                      Advisors can view user expense
                                      information and provide advice

  Feedback                            Users can submit feedback and
                                      admins can review it
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 2. Technologies Used

  Area                   Technology
  ---------------------- -----------------------------------
  Programming Language   Java
  Frontend               JSP, HTML, CSS, JavaScript, JSTL
  Web Layer              Jakarta Servlets, Servlet Filters
  Backend                Java Service and DAO layers
  Database               MySQL 8.x
  Database Access        JDBC
  Build Tool             Maven
  Web Server             Apache Tomcat 11
  Java Version           JDK 21+
  Packaging              WAR

> The Maven project is compiled for Java 21, so **JDK 21 or newer** is
> recommended.

------------------------------------------------------------------------

## 3. How the Application Works

The project follows a layered architecture:

``` text
Browser
   ↓
JSP / HTML / CSS / JavaScript
   ↓
Servlet
   ↓
Service
   ↓
DAO
   ↓
JDBC
   ↓
MySQL
```

  Layer                Purpose
  -------------------- --------------------------------------------------
  **JSP / Frontend**   Displays pages and collects user input
  **Servlets**         Receive HTTP requests and send responses
  **Services**         Handle validation and application/business logic
  **DAOs**             Perform database operations using JDBC
  **Models**           Represent application data
  **Filters**          Handle authentication and role-based access

------------------------------------------------------------------------

## 4. Database

The project uses MySQL.

### Database Name

``` text
suspicious4
```

### Main Tables

  Table        Purpose
  ------------ ---------------------------------
  `user`       Stores user account information
  `expenses`   Stores user expenses
  `budgets`    Stores budgets
  `advice`     Stores advisor advice
  `feedback`   Stores user feedback

The database structure is available at:

``` text
database/schema.sql
```

The schema contains the database and table structure but **does not
contain fake/demo records**.

------------------------------------------------------------------------

# 5. Running the Project Locally

Follow these steps in order. They are written for someone setting up the
project on a fresh Windows machine.

## Requirements

Install the following before starting:

  Software          Recommended
  ----------------- -----------------------
  Java              JDK 21 or newer
  Maven             Latest stable version
  MySQL             MySQL 8.x
  Database Tool     MySQL Workbench
  Web Server        Apache Tomcat 11
  Version Control   Git

### Check Java

Open Command Prompt or Git Bash:

``` bash
java -version
```

You should see your installed Java version.

### Check Maven

``` bash
mvn -version
```

If both commands work, continue.

------------------------------------------------------------------------

## Step 1 --- Get the Project

Clone the GitHub repository:

``` bash
git clone https://github.com/shreybaba/Online-Personal-Finance-Tracker.git
```

Enter the project folder:

``` bash
cd Online-Personal-Finance-Tracker
```

You can also download the repository as a ZIP and extract it.

------------------------------------------------------------------------

## Step 2 --- Start MySQL

Make sure your local MySQL server is running.

Open **MySQL Workbench** and connect to your local MySQL server.

------------------------------------------------------------------------

## Step 3 --- Create the Database

Inside the project, locate:

``` text
database/schema.sql
```

Open this file in MySQL Workbench.

Run the complete SQL script.

The script creates the database:

``` text
suspicious4
```

and its required tables.

### Verify the database

Run:

``` sql
SHOW DATABASES;

USE suspicious4;

SHOW TABLES;
```

You should see:

  Expected Table
  ----------------
  `advice`
  `budgets`
  `expenses`
  `feedback`
  `user`

**Do not manually insert random demo data.** The project is designed to
work with actual data created through the application.

------------------------------------------------------------------------

## Step 4 --- Configure Database Credentials

The application needs your local MySQL credentials.

Create this file:

``` text
src/main/resources/db.properties
```

Use:

``` text
db.properties.example
```

as the template.

Add:

``` properties
db.url=jdbc:mysql://localhost:3306/suspicious4
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace:

``` text
YOUR_MYSQL_PASSWORD
```

with the password of your local MySQL `root` account.

### Important

  File                      What to do
  ------------------------- ------------------------
  `db.properties.example`   Safe to keep in GitHub
  `db.properties`           Keep local only
  MySQL password            Never commit it

`db.properties` is excluded through `.gitignore`.

------------------------------------------------------------------------

## Step 5 --- Build the Project

Make sure your terminal is inside the project root --- the folder
containing `pom.xml`.

Run:

``` bash
mvn package
```

If the build is successful, Maven creates:

``` text
target/finance.war
```

The important file is:

  File                   Purpose
  ---------------------- ----------------------------
  `target/finance.war`   Deployable web application

------------------------------------------------------------------------

## Step 6 --- Deploy to Tomcat

Find your Tomcat installation.

For example:

``` text
C:\apache-tomcat-11.x.x
```

Open:

``` text
C:\apache-tomcat-11.x.x\webapps\
```

Copy **only**:

``` text
target/finance.war
```

into the Tomcat `webapps` folder.

### Example

``` text
C:\apache-tomcat-11.x.x\webapps\finance.war
```

**Do not manually copy the `target/finance` folder.**

When Tomcat starts, it automatically extracts the WAR and creates:

``` text
webapps/
├── finance.war
└── finance/
```

------------------------------------------------------------------------

## Step 7 --- Start Tomcat

Go to the Tomcat `bin` folder:

``` text
C:\apache-tomcat-11.x.x\bin
```

### Windows

Run:

``` text
startup.bat
```

### Git Bash

``` bash
cd "C:/apache-tomcat-11.x.x/bin"
./startup.bat
```

Wait a few seconds for Tomcat to start and deploy the application.

------------------------------------------------------------------------

## Step 8 --- Open the Application

Open your browser and visit:

``` text
http://localhost:8080/finance/
```

The `/finance` part comes from the WAR file name:

``` text
finance.war
```

You should now see the Finance application.

------------------------------------------------------------------------

## Step 9 --- Test the Application

A basic testing flow is:

``` text
Register
   ↓
Login
   ↓
User Dashboard
   ↓
Add Expense
   ↓
View / Edit / Delete Expense
   ↓
Create Budget
   ↓
View Reports
```

Advisor and Admin features can then be tested using accounts with the
appropriate roles.

The application does not depend on pre-filled fake financial data.

------------------------------------------------------------------------

## Step 10 --- Stop Tomcat

When finished, open:

``` text
C:\apache-tomcat-11.x.x\bin
```

Run:

``` text
shutdown.bat
```

Or from Git Bash:

``` bash
./shutdown.bat
```

------------------------------------------------------------------------

# 6. Common Setup Problems

## Maven is not recognized

If this appears:

``` text
'mvn' is not recognized...
```

Maven is either not installed correctly or its `bin` directory is not
available in PATH.

Check:

``` bash
mvn -version
```

------------------------------------------------------------------------

## MySQL Connection Error

Check the following:

  Check          Expected
  -------------- ------------------------------------
  MySQL server   Running
  Database       `suspicious4`
  Username       `root`
  Password       Your local MySQL password
  Config file    `src/main/resources/db.properties`
  Tables         Created from `database/schema.sql`

Verify with:

``` sql
USE suspicious4;
SHOW TABLES;
```

------------------------------------------------------------------------

## Login Shows `500 Server Error`

A `500` error means something failed on the server side.

First check the **Tomcat console/log immediately after clicking Login**.

Common local setup causes are:

-   MySQL is not running
-   `db.properties` is missing
-   MySQL username/password is incorrect
-   `suspicious4` does not exist
-   Required tables were not created

**Do not change Java code immediately. Check the Tomcat error first.**

------------------------------------------------------------------------

## Application Does Not Open

Check:

  Check      Expected
  ---------- ----------------------------------
  Tomcat     Running
  WAR file   Inside `Tomcat/webapps`
  WAR name   `finance.war`
  URL        `http://localhost:8080/finance/`

If port `8080` is already being used by another application, Tomcat may
need a different port.

------------------------------------------------------------------------

# 7. Project Structure

``` text
Online-Personal-Finance-Tracker/
│
├── database/
│   └── schema.sql
│
├── docs/
│
├── src/
│   └── main/
│       ├── java/com/finance/
│       │   ├── dao/
│       │   ├── exception/
│       │   ├── filter/
│       │   ├── model/
│       │   ├── service/
│       │   ├── servlet/
│       │   └── util/
│       │
│       ├── resources/
│       │   └── db.properties
│       │
│       └── webapp/
│           ├── WEB-INF/
│           ├── assets/
│           └── index.jsp
│
├── db.properties.example
├── pom.xml
├── README.md
└── .gitignore
```

------------------------------------------------------------------------

# 8. Java Concepts Demonstrated

This project was developed as a student project to apply concepts from
Java, database, and web programming.

  -----------------------------------------------------------------------
  Concept                             Where It Is Used
  ----------------------------------- -----------------------------------
  **OOP**                             Models, Services, DAOs,
                                      encapsulation and inheritance

  **Collections**                     `List`, `ArrayList`, `Map`,
                                      `HashMap`

  **Exception Handling**              Custom application exception
                                      hierarchy

  **JDBC**                            Database connection and SQL
                                      operations

  **Transactions**                    Commit/rollback during user
                                      deletion

  **Multithreading**                  `ExecutorService`, `Callable`,
                                      `Future` in reports

  **HTTP**                            Servlets, GET/POST, parameters,
                                      forwards and redirects

  **Sessions**                        `HttpSession`, authentication and
                                      role filters
  -----------------------------------------------------------------------

### Exception Hierarchy

``` text
FinanceTrackerException
├── AuthenticationException
├── AuthorizationException
├── DatabaseException
└── ValidationException
```

### Multithreading

The report service uses:

``` text
ExecutorService
Callable
Future
```

for independent report calculations.

### Transactions

The project demonstrates:

``` text
setAutoCommit(false)
commit()
rollback()
```

for transactional database operations.

------------------------------------------------------------------------

# 9. Review 1 Coverage

The implementation covers the major Review 1 requirements.

  -----------------------------------------------------------------------
  Review 1 Requirement                Implementation in Project
  ----------------------------------- -----------------------------------
  Problem Understanding & Solution    Personal finance management system
  Design                              with User, Advisor and Admin roles

  OOP                                 Models, Services, DAOs, inheritance
                                      and encapsulation

  Collections                         `List`, `ArrayList`, `Map`,
                                      `HashMap`

  Exception Handling                  Custom exception hierarchy

  Threads                             `ExecutorService`, `Callable`,
                                      `Future`

  Database Schema                     MySQL database with five main
                                      tables

  JDBC                                `Connection`, `PreparedStatement`,
                                      `ResultSet`

  CRUD                                Users, expenses, budgets, advice
                                      and feedback

  Transactions                        Commit and rollback in `UserDAO`

  HTTP                                Servlets, GET/POST, parameters,
                                      forwards and redirects

  Sessions                            `HttpSession`, `AuthFilter`,
                                      `RoleFilter`
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 10. Important Files for Demonstration

  --------------------------------------------------------------------------------------------
  Topic                               Important File / Folder
  ----------------------------------- --------------------------------------------------------
  Database                            `database/schema.sql`

  Database Connection                 `src/main/java/com/finance/DBConnection.java`

  JDBC / DAO                          `src/main/java/com/finance/dao/`

  Services                            `src/main/java/com/finance/service/`

  Models                              `src/main/java/com/finance/model/`

  Authentication                      `src/main/java/com/finance/servlet/auth/`

  Sessions & Roles                    `src/main/java/com/finance/filter/`

  Multithreading                      `src/main/java/com/finance/service/ReportService.java`

  Transactions                        `src/main/java/com/finance/dao/UserDAO.java`
  --------------------------------------------------------------------------------------------

------------------------------------------------------------------------

# 11. Repository Safety

### Safe to commit

``` text
db.properties.example
```

It contains placeholder values only.

### Do not commit

``` text
src/main/resources/db.properties
```

It contains the local MySQL password.

Generated files such as `target/`, IDE files, logs, and other local
development files are also excluded through `.gitignore`.

------------------------------------------------------------------------

# 12. Project Repository

**GitHub:**\
https://github.com/shreybaba/Online-Personal-Finance-Tracker

The repository contains the source code, database schema, configuration
template, and documentation required to understand and run the student
project locally.
