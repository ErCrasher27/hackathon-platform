package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.UtenteDAO;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.ExistsResponse;
import it.unina.hackathon.utils.UtenteResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteImplementazionePostgresDAO implements UtenteDAO {
    private Connection connection;

    public UtenteImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UtenteResponse findByUsername(String username) {
        String query = """
                SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name 
                FROM utenti u 
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id 
                WHERE u.username = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new UtenteResponse(mapResultSetToUtente(rs), "Username trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UtenteResponse(null, "Username trovato!");
    }

    @Override
    public UtenteResponse saveUtente(Utente utente) {
        String query = """
                INSERT INTO utenti (username, email, password, nome, cognome, tipo_utente_id) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, utente.getUsername());
            ps.setString(2, utente.getEmail());
            ps.setString(3, utente.getPassword());
            ps.setString(4, utente.getNome());
            ps.setString(5, utente.getCognome());
            ps.setInt(6, utente.getTipoUtente().getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    utente.setUtenteId(generatedKeys.getInt(1));
                }
                return new UtenteResponse(utente, "Registrazione avvenuta con successo!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteResponse(null, "Errore durante la registrazione: " + e.getMessage());
        }
        return new UtenteResponse(null, "Errore durante la registrazione!");
    }

    @Override
    public ExistsResponse usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM utenti WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean res = rs.getInt(1) > 0;
                String message;
                if (res) {
                    message = "Username già esistente!";
                } else {
                    message = "Username non esistente!";
                }
                return new ExistsResponse(res, message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ExistsResponse(false, "Errore durante la ricerca dell'username!");
    }

    @Override
    public ExistsResponse emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utenti WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean res = rs.getInt(1) > 0;
                String message;
                if (res) {
                    message = "Email già esistente!";
                } else {
                    message = "Email non esistente!";
                }
                return new ExistsResponse(res, message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ExistsResponse(false, "Errore durante la ricerca dell'email!");
    }

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("nome"), rs.getString("cognome"), TipoUtente.valueOf(rs.getString("role_name")));
        utente.setUtenteId(rs.getInt("utente_id"));
        utente.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        return utente;
    }
}