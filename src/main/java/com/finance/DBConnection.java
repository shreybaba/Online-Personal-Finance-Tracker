package com.finance;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens JDBC connections to the MySQL database.
 *
 * Settings are read from db.properties on the classpath (src/main/resources),
 * and can be overridden with the DB_URL, DB_USER and DB_PASSWORD environment variables.
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not read db.properties: " + e.getMessage());
        }

        URL = setting("DB_URL", props.getProperty("db.url", "jdbc:mysql://localhost:3306/suspicious4"));
        USER = setting("DB_USER", props.getProperty("db.user", "root"));
        PASSWORD = setting("DB_PASSWORD", props.getProperty("db.password", ""));

        try {
            // Tomcat does not always auto-register drivers found in WEB-INF/lib
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found on the classpath");
        }
    }

    private static String setting(String envName, String fallback) {
        String value = System.getenv(envName);
        return value != null && !value.isBlank() ? value : fallback;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
