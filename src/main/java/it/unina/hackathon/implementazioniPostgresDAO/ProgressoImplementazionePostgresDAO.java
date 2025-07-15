package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.ProgressoDAO;
import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressoImplementazionePostgresDAO implements ProgressoDAO {
    private Connection connection;

    public ProgressoImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProgressoResponse saveProgresso(Progresso progresso) {
        String query = """
                INSERT INTO progressi (team_id, titolo, descrizione, documento_path, documento_nome, 
                                     data_caricamento, caricato_da) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progresso.getTeamId());
            ps.setString(4, progresso.getDocumentoPath());
            ps.setString(5, progresso.getDocumentoNome());
            ps.setTimestamp(6, Timestamp.valueOf(progresso.getDataCaricamento()));
            ps.setInt(7, progresso.getCaricatoDaId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    progresso.setProgressoId(generatedKeys.getInt(1));
                }
                return new ProgressoResponse(progresso, "Progresso salvato con successo!");
            } else {
                return new ProgressoResponse(null, "Errore durante il salvataggio del progresso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoResponse(null, "Errore durante il salvataggio del progresso: " + e.getMessage());
        }
    }

    @Override
    public ProgressoResponse getProgressoById(int progressoId) {
        String query = """
                SELECT p.progresso_id, p.team_id, p.titolo, p.descrizione, p.documento_path, 
                       p.documento_nome, p.data_caricamento, p.caricato_da,
                       u.nome, u.cognome, u.username, u.email
                FROM progressi p
                JOIN utenti u ON p.caricato_da = u.utente_id
                WHERE p.progresso_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, progressoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Progresso progresso = mapResultSetToProgresso(rs);
                return new ProgressoResponse(progresso, "Progresso trovato con successo!");
            } else {
                return new ProgressoResponse(null, "Progresso non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoResponse(null, "Errore durante la ricerca del progresso!");
        }
    }

    @Override
    public ProgressoListResponse getProgressiByTeam(int teamId) {
        String query = """
                SELECT p.progresso_id, p.team_id, p.documento_path, 
                       p.documento_nome, p.data_caricamento, p.caricato_da,
                       u.nome, u.cognome, u.username, u.email
                FROM progressi p
                JOIN utenti u ON p.caricato_da = u.utente_id
                WHERE p.team_id = ?
                ORDER BY p.data_caricamento DESC
                """;

        List<Progresso> progressi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progressi.add(mapResultSetToProgresso(rs));
            }
            return new ProgressoListResponse(progressi, "Progressi del team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoListResponse(null, "Errore durante il caricamento dei progressi del team!");
        }
    }

    @Override
    public ProgressoListResponse getProgressiByHackathon(int hackathonId) {
        String query = """
                SELECT p.progresso_id, p.team_id, p.titolo, p.descrizione, p.documento_path, 
                       p.documento_nome, p.data_caricamento, p.caricato_da,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM progressi p
                JOIN utenti u ON p.caricato_da = u.utente_id
                JOIN team t ON p.team_id = t.team_id
                WHERE t.hackathon_id = ?
                ORDER BY p.data_caricamento DESC
                """;

        List<Progresso> progressi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progressi.add(mapResultSetToProgresso(rs));
            }
            return new ProgressoListResponse(progressi, "Progressi dell'hackathon caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoListResponse(null, "Errore durante il caricamento dei progressi dell'hackathon!");
        }
    }

    @Override
    public ProgressoResponse updateProgresso(Progresso progresso) {
        String query = """
                UPDATE progressi 
                SET titolo = ?, descrizione = ?, documento_path = ?, documento_nome = ?
                WHERE progresso_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(3, progresso.getDocumentoPath());
            ps.setString(4, progresso.getDocumentoNome());
            ps.setInt(5, progresso.getProgressoId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ProgressoResponse(progresso, "Progresso aggiornato con successo!");
            } else {
                return new ProgressoResponse(null, "Progresso non trovato per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoResponse(null, "Errore durante l'aggiornamento del progresso!");
        }
    }

    @Override
    public ResponseResult deleteProgresso(int progressoId) {
        String query = "DELETE FROM progressi WHERE progresso_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, progressoId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Progresso eliminato con successo!");
            } else {
                return new ResponseResult(false, "Progresso non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'eliminazione del progresso!");
        }
    }

    @Override
    public ProgressoListResponse getProgressiDaValutare(int giudiceId, int hackathonId) {
        String query = """
                SELECT DISTINCT p.progresso_id, p.team_id, p.titolo, p.descrizione, p.documento_path, 
                       p.documento_nome, p.data_caricamento, p.caricato_da,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM progressi p
                JOIN utenti u ON p.caricato_da = u.utente_id
                JOIN team t ON p.team_id = t.team_id
                JOIN giudici_hackathon gh ON t.hackathon_id = gh.hackathon_id
                WHERE gh.giudice_id = ? AND t.hackathon_id = ? 
                AND gh.stato_invito_id = (SELECT status_id FROM invito_status WHERE status_name = 'ACCEPTED')
                ORDER BY p.data_caricamento DESC
                """;

        List<Progresso> progressi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progressi.add(mapResultSetToProgresso(rs));
            }
            return new ProgressoListResponse(progressi, "Progressi da valutare caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoListResponse(null, "Errore durante il caricamento dei progressi da valutare!");
        }
    }

    @Override
    public ProgressoListResponse getProgressiRecenti(int hackathonId, int giorni) {
        String query = """
                SELECT p.progresso_id, p.team_id, p.titolo, p.descrizione, p.documento_path, 
                       p.documento_nome, p.data_caricamento, p.caricato_da,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM progressi p
                JOIN utenti u ON p.caricato_da = u.utente_id
                JOIN team t ON p.team_id = t.team_id
                WHERE t.hackathon_id = ? 
                AND p.data_caricamento >= NOW() - INTERVAL '%d days'
                ORDER BY p.data_caricamento DESC
                """.formatted(giorni);

        List<Progresso> progressi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progressi.add(mapResultSetToProgresso(rs));
            }
            return new ProgressoListResponse(progressi, "Progressi recenti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoListResponse(null, "Errore durante il caricamento dei progressi recenti!");
        }
    }

    private Progresso mapResultSetToProgresso(ResultSet rs) throws SQLException {
        Progresso progresso = new Progresso();
        progresso.setProgressoId(rs.getInt("progresso_id"));
        progresso.setTeamId(rs.getInt("team_id"));
        progresso.setDocumentoPath(rs.getString("documento_path"));
        progresso.setDocumentoNome(rs.getString("documento_nome"));
        progresso.setDataCaricamento(rs.getTimestamp("data_caricamento").toLocalDateTime());
        progresso.setCaricatoDaId(rs.getInt("caricato_da"));

        // Mappa l'utente che ha caricato
        Utente caricatoDa = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE // Default per chi carica progressi
        );
        caricatoDa.setUtenteId(rs.getInt("caricato_da"));
        progresso.setCaricatoDa(caricatoDa);

        return progresso;
    }
}