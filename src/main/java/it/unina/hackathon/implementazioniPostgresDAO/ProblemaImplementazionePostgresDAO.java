package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.ProblemaDAO;
import it.unina.hackathon.model.Problema;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.ProblemaListResponse;
import it.unina.hackathon.utils.responses.ProblemaResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemaImplementazionePostgresDAO implements ProblemaDAO {
    private Connection connection;

    public ProblemaImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProblemaResponse saveProblema(Problema problema) {
        String query = """
                INSERT INTO problema (hackathon_id, titolo, descrizione, data_pubblicazione, pubblicato_da) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, problema.getHackathonId());
            ps.setString(2, problema.getTitolo());
            ps.setString(3, problema.getDescrizione());
            ps.setTimestamp(4, Timestamp.valueOf(problema.getDataPubblicazione()));
            ps.setInt(5, problema.getPubblicatoDaId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    problema.setProblemaId(generatedKeys.getInt(1));
                }
                return new ProblemaResponse(problema, "Problema pubblicato con successo!");
            } else {
                return new ProblemaResponse(null, "Errore durante la pubblicazione del problema!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaResponse(null, "Errore durante la pubblicazione del problema: " + e.getMessage());
        }
    }

    @Override
    public ProblemaResponse getProblemaById(int problemaId) {
        String query = """
                SELECT p.problema_id, p.hackathon_id, p.titolo, p.descrizione, 
                       p.data_pubblicazione, p.pubblicato_da,
                       u.nome, u.cognome, u.username, u.email
                FROM problema p
                JOIN utenti u ON p.pubblicato_da = u.utente_id
                WHERE p.problema_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, problemaId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Problema problema = mapResultSetToProblema(rs);
                return new ProblemaResponse(problema, "Problema trovato con successo!");
            } else {
                return new ProblemaResponse(null, "Problema non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaResponse(null, "Errore durante la ricerca del problema!");
        }
    }

    @Override
    public ProblemaListResponse getProblemiByHackathon(int hackathonId) {
        String query = """
                SELECT p.problema_id, p.hackathon_id, p.titolo, p.descrizione, 
                       p.data_pubblicazione, p.pubblicato_da,
                       u.nome, u.cognome, u.username, u.email
                FROM problema p
                JOIN utenti u ON p.pubblicato_da = u.utente_id
                WHERE p.hackathon_id = ?
                ORDER BY p.data_pubblicazione DESC
                """;

        List<Problema> problemi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                problemi.add(mapResultSetToProblema(rs));
            }
            return new ProblemaListResponse(problemi, "Problemi dell'hackathon caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaListResponse(null, "Errore durante il caricamento dei problemi!");
        }
    }

    @Override
    public ProblemaListResponse getProblemiByGiudice(int giudiceId, int hackathonId) {
        String query = """
                SELECT p.problema_id, p.hackathon_id, p.titolo, p.descrizione, 
                       p.data_pubblicazione, p.pubblicato_da,
                       u.nome, u.cognome, u.username, u.email,
                       h.titolo as hackathon_titolo
                FROM problema p
                JOIN utenti u ON p.pubblicato_da = u.utente_id
                JOIN hackathon h ON p.hackathon_id = h.hackathon_id
                WHERE p.pubblicato_da = ? AND h.hackathon_id = ?
                ORDER BY p.data_pubblicazione DESC
                """;

        List<Problema> problemi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                problemi.add(mapResultSetToProblema(rs));
            }
            return new ProblemaListResponse(problemi, "Problemi del giudice caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaListResponse(null, "Errore durante il caricamento dei problemi del giudice!");
        }
    }

    @Override
    public ProblemaResponse updateProblema(Problema problema) {
        String query = """
                UPDATE problema 
                SET titolo = ?, descrizione = ?
                WHERE problema_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, problema.getTitolo());
            ps.setString(2, problema.getDescrizione());
            ps.setInt(3, problema.getProblemaId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ProblemaResponse(problema, "Problema aggiornato con successo!");
            } else {
                return new ProblemaResponse(null, "Problema non trovato per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaResponse(null, "Errore durante l'aggiornamento del problema!");
        }
    }

    @Override
    public ResponseResult deleteProblema(int problemaId) {
        String query = "DELETE FROM problema WHERE problema_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, problemaId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Problema eliminato con successo!");
            } else {
                return new ResponseResult(false, "Problema non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'eliminazione del problema!");
        }
    }

    @Override
    public ProblemaResponse getProblemaAttivoByHackathon(int hackathonId) {
        String query = """
                SELECT p.problema_id, p.hackathon_id, p.titolo, p.descrizione, 
                       p.data_pubblicazione, p.pubblicato_da,
                       u.nome, u.cognome, u.username, u.email
                FROM problema p
                JOIN utenti u ON p.pubblicato_da = u.utente_id
                WHERE p.hackathon_id = ?
                ORDER BY p.data_pubblicazione DESC
                LIMIT 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Problema problema = mapResultSetToProblema(rs);
                return new ProblemaResponse(problema, "Problema attivo trovato con successo!");
            } else {
                return new ProblemaResponse(null, "Nessun problema pubblicato per questo hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaResponse(null, "Errore durante la ricerca del problema attivo!");
        }
    }

    private Problema mapResultSetToProblema(ResultSet rs) throws SQLException {
        Problema problema = new Problema();
        problema.setProblemaId(rs.getInt("problema_id"));
        problema.setHackathonId(rs.getInt("hackathon_id"));
        problema.setTitolo(rs.getString("titolo"));
        problema.setDescrizione(rs.getString("descrizione"));
        problema.setDataPubblicazione(rs.getTimestamp("data_pubblicazione").toLocalDateTime());
        problema.setPubblicatoDaId(rs.getInt("pubblicato_da"));

        // Mappa il giudice che ha pubblicato
        Utente pubblicatoDa = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        pubblicatoDa.setUtenteId(rs.getInt("pubblicato_da"));
        problema.setPubblicatoDa(pubblicatoDa);

        return problema;
    }
}