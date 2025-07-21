package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.ProblemaDAO;
import it.unina.hackathon.model.GiudiceHackathon;
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
                INSERT INTO problema (pubblicato_da, titolo, descrizione) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, problema.getPubblicatoDaId());
            ps.setString(2, problema.getTitolo());
            ps.setString(3, problema.getDescrizione());

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
    public ProblemaListResponse getProblemiByHackathon(int hackathonId) {
        String query = """
                SELECT p.problema_id, p.pubblicato_da, p.titolo, p.descrizione, 
                       p.data_pubblicazione,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       gh.giudice_hackathon_id, gh.hackathon_id
                FROM problema p
                JOIN giudici_hackathon gh ON p.pubblicato_da = gh.giudice_hackathon_id
                JOIN utenti u ON gh.giudice_id = u.utente_id
                WHERE gh.hackathon_id = ?
                ORDER BY p.data_pubblicazione DESC
                """;

        List<Problema> problemi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                problemi.add(mapResultSetToProblema(rs));
            }
            return new ProblemaListResponse(problemi, "Problemi dell'hackathon caricati!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProblemaListResponse(null, "Errore durante il caricamento!");
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

    private Problema mapResultSetToProblema(ResultSet rs) throws SQLException {
        Problema problema = new Problema();
        problema.setProblemaId(rs.getInt("problema_id"));
        problema.setTitolo(rs.getString("titolo"));
        problema.setDescrizione(rs.getString("descrizione"));
        problema.setDataPubblicazione(rs.getTimestamp("data_pubblicazione").toLocalDateTime());
        problema.setPubblicatoDaId(rs.getInt("pubblicato_da"));

        // Mappa il giudice che ha pubblicato
        Utente pubblicatoDaUtente = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        pubblicatoDaUtente.setUtenteId(rs.getInt("utente_id"));

        GiudiceHackathon pubblicatoDaGiudiceHackathon = new GiudiceHackathon();
        pubblicatoDaGiudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
        pubblicatoDaGiudiceHackathon.setGiudice(pubblicatoDaUtente);
        pubblicatoDaGiudiceHackathon.setHackathonId(rs.getInt("hackathon_id"));
        problema.setPubblicatoDa(pubblicatoDaGiudiceHackathon);

        return problema;
    }
}