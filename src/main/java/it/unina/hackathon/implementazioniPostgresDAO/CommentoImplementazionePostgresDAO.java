package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.CommentoDAO;
import it.unina.hackathon.model.Commento;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.CommentoListResponse;
import it.unina.hackathon.utils.responses.CommentoResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentoImplementazionePostgresDAO implements CommentoDAO {
    private Connection connection;

    public CommentoImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommentoResponse saveCommento(Commento commento) {
        String query = """
                INSERT INTO commenti (progresso_fk_progressi, giudice_hack_fk_giudici_hackathon, testo) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commento.getProgressoId());
            ps.setInt(2, commento.getGiudiceHackathonId());
            ps.setString(3, commento.getTesto());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commento.setCommentoId(generatedKeys.getInt(1));
                }
                return new CommentoResponse(commento, "Commento salvato con successo!");
            } else {
                return new CommentoResponse(null, "Errore durante il salvataggio del commento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoResponse(null, "Errore durante il salvataggio del commento: " + e.getMessage());
        }
    }

    @Override
    public CommentoListResponse getCommentiByProgresso(int progressoId) {
        String query = """
                SELECT c.commento_id, c.progresso_fk_progressi, c.giudice_hack_fk_giudici_hackathon, c.testo, c.data_commento,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       gh.giudice_hackathon_id
                FROM commenti c
                JOIN giudici_hackathon gh ON c.giudice_hack_fk_giudici_hackathon = gh.giudice_hackathon_id
                JOIN utenti u ON gh.giudice_hackathon_id = u.utente_id
                WHERE c.progresso_fk_progressi = ?
                ORDER BY c.data_commento DESC
                """;

        List<Commento> commenti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, progressoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commenti.add(mapResultSetToCommento(rs));
            }
            return new CommentoListResponse(commenti, "Commenti del progresso caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoListResponse(null, "Errore durante il caricamento dei commenti del progresso!");
        }
    }

    private Commento mapResultSetToCommento(ResultSet rs) throws SQLException {
        Commento commento = new Commento(rs.getInt("progresso_id"), rs.getInt("giudice_hackathon_id"), rs.getString("testo"));
        commento.setCommentoId(rs.getInt("commento_id"));
        commento.setDataCommento(rs.getTimestamp("data_commento").toLocalDateTime());

        // Mappa il giudice
        Utente utenteGiudice = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        utenteGiudice.setUtenteId(rs.getInt("utente_id"));

        GiudiceHackathon giudiceHackathon = new GiudiceHackathon();
        giudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
        giudiceHackathon.setUtenteGiudice(utenteGiudice);

        commento.setGiudiceHackathon(giudiceHackathon);

        return commento;
    }
}