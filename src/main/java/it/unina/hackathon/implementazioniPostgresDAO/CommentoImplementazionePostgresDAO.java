package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.CommentoDAO;
import it.unina.hackathon.model.Commento;
import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.CommentoListResponse;
import it.unina.hackathon.utils.responses.CommentoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

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
                INSERT INTO commenti (progresso_id, giudice_id, testo, data_commento) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commento.getProgressoId());
            ps.setInt(2, commento.getGiudiceId());
            ps.setString(3, commento.getTesto());
            ps.setTimestamp(4, Timestamp.valueOf(commento.getDataCommento()));

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
    public CommentoResponse getCommentoById(int commentoId) {
        String query = """
                SELECT c.commento_id, c.progresso_id, c.giudice_id, c.testo, c.data_commento,
                       u.nome, u.cognome, u.username, u.email,
                       p.titolo as progresso_titolo
                FROM commenti c
                JOIN utenti u ON c.giudice_id = u.utente_id
                JOIN progressi p ON c.progresso_id = p.progresso_id
                WHERE c.commento_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commentoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Commento commento = mapResultSetToCommento(rs);
                return new CommentoResponse(commento, "Commento trovato con successo!");
            } else {
                return new CommentoResponse(null, "Commento non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoResponse(null, "Errore durante la ricerca del commento!");
        }
    }

    @Override
    public CommentoListResponse getCommentiByProgresso(int progressoId) {
        String query = """
                SELECT c.commento_id, c.progresso_id, c.giudice_id, c.testo, c.data_commento,
                       u.nome, u.cognome, u.username, u.email
                FROM commenti c
                JOIN utenti u ON c.giudice_id = u.utente_id
                JOIN progressi p ON c.progresso_id = p.progresso_id
                WHERE c.progresso_id = ?
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

    @Override
    public CommentoListResponse getCommentiByGiudice(int giudiceId) {
        String query = """
                SELECT c.commento_id, c.progresso_id, c.giudice_id, c.testo, c.data_commento,
                       u.nome, u.cognome, u.username, u.email,
                       p.titolo as progresso_titolo,
                       t.nome as team_nome
                FROM commenti c
                JOIN utenti u ON c.giudice_id = u.utente_id
                JOIN progressi p ON c.progresso_id = p.progresso_id
                JOIN team t ON p.team_id = t.team_id
                WHERE c.giudice_id = ?
                ORDER BY c.data_commento DESC
                """;

        List<Commento> commenti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commenti.add(mapResultSetToCommento(rs));
            }
            return new CommentoListResponse(commenti, "Commenti del giudice caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoListResponse(null, "Errore durante il caricamento dei commenti del giudice!");
        }
    }

    @Override
    public CommentoResponse updateCommento(Commento commento) {
        String query = """
                UPDATE commenti 
                SET testo = ?
                WHERE commento_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, commento.getTesto());
            ps.setInt(2, commento.getCommentoId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new CommentoResponse(commento, "Commento aggiornato con successo!");
            } else {
                return new CommentoResponse(null, "Commento non trovato per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoResponse(null, "Errore durante l'aggiornamento del commento!");
        }
    }

    @Override
    public ResponseResult deleteCommento(int commentoId) {
        String query = "DELETE FROM commenti WHERE commento_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, commentoId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Commento eliminato con successo!");
            } else {
                return new ResponseResult(false, "Commento non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'eliminazione del commento!");
        }
    }

    @Override
    public CommentoListResponse getCommentiRecenti(int hackathonId, int giorni) {
        String query = """
                SELECT c.commento_id, c.progresso_id, c.giudice_id, c.testo, c.data_commento,
                       u.nome, u.cognome, u.username, u.email,
                       p.titolo as progresso_titolo,
                       t.nome as team_nome
                FROM commenti c
                JOIN utenti u ON c.giudice_id = u.utente_id
                JOIN progressi p ON c.progresso_id = p.progresso_id
                JOIN team t ON p.team_id = t.team_id
                WHERE t.hackathon_id = ? 
                AND c.data_commento >= NOW() - INTERVAL '%d days'
                ORDER BY c.data_commento DESC
                """.formatted(giorni);

        List<Commento> commenti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commenti.add(mapResultSetToCommento(rs));
            }
            return new CommentoListResponse(commenti, "Commenti recenti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommentoListResponse(null, "Errore durante il caricamento dei commenti recenti!");
        }
    }

    private Commento mapResultSetToCommento(ResultSet rs) throws SQLException {
        Commento commento = new Commento();
        commento.setCommentoId(rs.getInt("commento_id"));
        commento.setProgressoId(rs.getInt("progresso_id"));
        commento.setGiudiceId(rs.getInt("giudice_id"));
        commento.setTesto(rs.getString("testo"));
        commento.setDataCommento(rs.getTimestamp("data_commento").toLocalDateTime());

        // Mappa il giudice
        Utente giudice = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        giudice.setUtenteId(rs.getInt("giudice_id"));
        commento.setGiudice(giudice);

        // Mappa il progresso
        Progresso progresso = new Progresso();
        progresso.setProgressoId(rs.getInt("progresso_id"));
        commento.setProgresso(progresso);

        return commento;
    }
}