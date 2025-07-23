package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.UtenteDAO;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.UtenteResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public UtenteListResponse getUtentiGiudiciNonInvitati(int hackathonId) {
        String query = """
                SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, u.ruolo_fk_ruoli_utente
                FROM utenti u
                WHERE ru.ruolo_id = 2
                AND u.utente_id NOT IN (
                    SELECT DISTINCT ig.invitato_id 
                    FROM inviti_giudice ig 
                    WHERE ig.hackathon_fk_hackathons = ?
                )
                ORDER BY u.cognome, u.nome
                """;

        List<Utente> utentiGiudici = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                utentiGiudici.add(mapResultSetToUtente(rs));
            }
            return new UtenteListResponse(utentiGiudici, "Giudici non invitati caricati!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento!");
        }
    }

    @Override
    public UtenteListResponse getUtentiGiudiciInvitati(int hackathonId) {
        String query = """
                 SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, u.ruolo_fk_ruoli_utente
                FROM utenti u
                JOIN inviti_giudice ig ON u.utente_id = ig.invitato_fk_utenti
                JOIN stati_invito si ON ig.stato_fk_stati_invito = si.stato_id
                WHERE ig.hackathon_id = ? AND u.ruolo_fk_ruoli_utente = 2
                ORDER BY ig.data_invito DESC
                """;

        List<Utente> giudici = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = mapResultSetToUtente(rs);
                giudici.add(utente);
            }
            return new UtenteListResponse(giudici, "Giudici invitati caricati!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento!");
        }
    }

    @Override
    public UtenteResponse saveUtente(Utente utente) {
        String query = """
                INSERT INTO utenti (username, email, password, nome, cognome, ruolo_fk_ruoli_utente) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
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
            } else {
                return new UtenteResponse(null, "Errore durante la registrazione!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteResponse(null, "Errore durante la registrazione: " + e.getMessage());
        }
    }

    @Override
    public UtenteResponse findByUsername(String username) {
        String query = """
                SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                              u.data_registrazione, u.ruolo_fk_ruoli_utente
                       FROM utenti u
                       WHERE u.username = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new UtenteResponse(mapResultSetToUtente(rs), "Utente trovato con successo!");
            } else {
                return new UtenteResponse(null, "Username non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteResponse(null, "Errore durante la ricerca dell'utente!");
        }
    }

    @Override
    public ResponseResult usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM utenti WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    return new ResponseResult(true, "Username già esistente!");
                } else {
                    return new ResponseResult(false, "Username disponibile!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica dell'username!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica dell'username!");
        }
    }

    @Override
    public ResponseResult emailExists(String email) {
        String query = "SELECT COUNT(*) FROM utenti WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    return new ResponseResult(true, "Email già esistente!");
                } else {
                    return new ResponseResult(false, "Email disponibile!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica dell'email!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica dell'email!");
        }
    }

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("nome"), rs.getString("cognome"), TipoUtente.valueOf(rs.getString("ruolo_fk_ruoli_utente")));
        utente.setUtenteId(rs.getInt("utente_id"));
        utente.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        return utente;
    }
}