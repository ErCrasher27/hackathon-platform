package it.unina.hackathon.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnessioneDatabase {
    /**
     * Istanza singleton della classe
     */
    private static ConnessioneDatabase instance;

    /**
     * Nome utente per la connessione al database
     */
    private final String nome = "postgres";

    /**
     * Password per la connessione al database
     */
    private final String password = "Soloio91.";

    /**
     * URL di connessione al database PostgreSQL
     */
    private final String url = "jdbc:postgresql://localhost:5432/hackathon_platform";

    /**
     * Driver JDBC per PostgreSQL
     */
    private final String driver = "org.postgresql.Driver";

    /**
     * Connessione attiva al database
     */
    public Connection connection = null;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza la connessione al database PostgreSQL.
     *
     * @throws SQLException se si verifica un errore durante la connessione
     */
    private ConnessioneDatabase() throws SQLException {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, nome, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Restituisce l'istanza singleton della connessione al database.
     * Se l'istanza non esiste o la connessione è chiusa, ne crea una nuova.
     *
     * @return l'istanza singleton di ConnessioneDatabase
     * @throws SQLException se si verifica un errore durante la connessione
     */
    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }
}