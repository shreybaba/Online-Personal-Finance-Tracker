# Online Personal Finance Tracker

A web-based personal finance management system developed as a college project.  
The application helps users manage their expenses and budgets, view financial reports, receive financial advice, and submit feedback.

## Tech Stack

- Java 26
- JSP
- Jakarta Servlets
- JDBC
- MySQL
- Maven
- Apache Tomcat 11
- HTML, CSS and JavaScript

## Features

### User
- Register and log in
- Manage personal expenses
- Add, edit and delete expenses
- Manage budgets
- View financial reports
- View profile information
- View financial advice
- Submit feedback

### Advisor
- Advisor login
- View registered users
- View user-related expense information
- Provide financial advice

### Admin
- Admin login
- View and manage users
- View feedback
- Manage feedback status
- View system-level information

## Project Architecture

The application follows a layered architecture:

```text
JSP / Frontend
      ↓
   Servlets
      ↓
   Services
      ↓
     DAO
      ↓
     JDBC
      ↓
    MySQL
```

## Backend Structure

```text
src/main/java/com/finance/
├── DBConnection.java   JDBC connections (settings from db.properties / env vars)
├── model/              User, Expense, Budget, Advice, Feedback + report row types
├── dao/                SQL for each table (prepared statements only)
├── service/            Validation and business rules
├── servlet/            Controllers mapped to the URLs the JSPs use
├── filter/             Login/role checks, UTF-8 + flash messages
└── util/               ID generation, money/percent helpers, input validation
```

Access rules: `/user/*` and `/feedback/*` are for `USER`, `/advisor/*` for `ADVISOR`, `/admin/*` for `ADMIN`.
Visitors who are not logged in are sent to `/login`; a logged-in account with the wrong role gets a 403 page.

## Running Locally

1. Create the database and tables:
   ```bash
   mysql -u root -p < db/schema.sql
   ```
2. Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and set your MySQL password.
   You can also set the `DB_URL`, `DB_USER` and `DB_PASSWORD` environment variables instead.
3. Build the WAR:
   ```bash
   mvn clean package
   ```
4. Copy `target/finance.war` into Tomcat 11's `webapps/` folder, start Tomcat, and open `http://localhost:8080/finance/`.
