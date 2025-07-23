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
                INSERT INTO problemi (giudice_hack_fk_giudici_hackathon, titolo, descrizione) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, problema.getPubblicatoDaGiudiceHackathonId());
            ps.setString(2, problema.getTitolo());
            ps.setString(3, problema.getDescrizione());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    problema.setProblemaId(generatedKeys.getInt(1));
                    problema.setDataPubblicazione(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
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
                SELECT p.problema_id, p.giudice_hack_fk_giudici_hackathon, p.titolo, p.descrizione, 
                       p.data_pubblicazione,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       gh.giudice_hackathon_id, gh.hackathon_fk_hackathons
                FROM problemi p
                JOIN giudici_hackathon gh ON p.giudice_hack_fk_giudici_hackathon = gh.giudice_hackathon_id
                JOIN utenti u ON gh.giudice_fk_utenti = u.utente_id
                WHERE gh.hackathon_fk_hackathons = ?
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
    public ResponseResult rimuoviProblema(int problemaId) {
        String query = "DELETE FROM problemi WHERE problema_id = ?";

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
        problema.setPubblicatoDaGiudiceHackathonId(rs.getInt("giudice_hack_fk_giudici_hackathon"));

        Utente utenteGiudice = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        utenteGiudice.setUtenteId(rs.getInt("utente_id"));

        GiudiceHackathon pubblicatoDaGiudiceHackathon = new GiudiceHackathon();
        pubblicatoDaGiudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
        pubblicatoDaGiudiceHackathon.setUtenteGiudice(utenteGiudice);
        pubblicatoDaGiudiceHackathon.setHackathonId(rs.getInt("hackathon_fk_hackathons"));
        problema.setPubblicatoDaGiudiceHackathon(pubblicatoDaGiudiceHackathon);

        return problema;
    }
}