package it.unina.hackathon.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDatabase {
    private static ConnessioneDatabase instance;
    private final String nome = "postgres";
    private final String password = "Soloio91.";
    private final String url = "jdbc:postgresql://localhost:5432/hackathon_platform";
    private final String driver = "org.postgresql.Driver";
    public Connection connection = null;

    private ConnessioneDatabase() throws SQLException {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, nome, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }
}