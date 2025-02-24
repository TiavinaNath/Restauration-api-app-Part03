package org.restau.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private final static int defaultPort = 5432;
    private final String host = System.getenv("DB_HOST");
    private final String database = System.getenv("DB_NAME");
    private final String user = System.getenv("DB_USER");
    private final String password = System.getenv("DB_PASSWORD");
    private final String url;


    public DbConnection() {
        url = "jdbc:postgresql://" + host + ":" + defaultPort + "/" + database;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}