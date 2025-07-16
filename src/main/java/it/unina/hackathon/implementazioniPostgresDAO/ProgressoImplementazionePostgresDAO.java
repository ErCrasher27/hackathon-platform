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