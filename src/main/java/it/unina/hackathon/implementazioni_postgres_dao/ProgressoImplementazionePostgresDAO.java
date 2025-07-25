package it.unina.hackathon.implementazioni_postgres_dao;

import it.unina.hackathon.dao.ProgressoDAO;
import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
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
                INSERT INTO progressi (registrazione_fk_registrazioni, documento_path, documento_nome) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progresso.getCaricatoDaRegistrazioneId());
            ps.setString(2, progresso.getDocumentoPath());
            ps.setString(3, progresso.getDocumentoNome());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    progresso.setProgressoId(generatedKeys.getInt(1));
                    progresso.setDataCaricamento(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
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
                SELECT p.progresso_id, p.registrazione_fk_registrazioni, p.documento_path, 
                       p.documento_nome, p.data_caricamento,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       r.registrazione_id, r.team_fk_teams, r.ruolo_fk_ruoli_team
                FROM progressi p
                JOIN registrazioni r ON p.registrazione_fk_registrazioni = r.registrazione_id
                JOIN utenti u ON r.partecipante_fk_utenti = u.utente_id
                WHERE r.team_fk_teams = ?
                ORDER BY p.data_caricamento DESC
                """;

        List<Progresso> progressi = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progressi.add(mapResultSetToProgresso(rs));
            }
            return new ProgressoListResponse(progressi, "Progressi del team caricati!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ProgressoListResponse(null, "Errore durante il caricamento!");
        }
    }

    @Override
    public ResponseResult rimuoviProgresso(int progressoId) {
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
        progresso.setDocumentoPath(rs.getString("documento_path"));
        progresso.setDocumentoNome(rs.getString("documento_nome"));
        progresso.setDataCaricamento(rs.getTimestamp("data_caricamento").toLocalDateTime());
        progresso.setCaricatoDaRegistrazioneId(rs.getInt("registrazione_fk_registrazioni"));

        Utente caricatoDaUtentePartecipante = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE);
        caricatoDaUtentePartecipante.setUtenteId(rs.getInt("utente_id"));

        Registrazione caricatoDaRegistrazione = new Registrazione();
        caricatoDaRegistrazione.setRegistrazioneId(rs.getInt("registrazione_id"));
        caricatoDaRegistrazione.setTeamId(rs.getInt("team_fk_teams"));
        caricatoDaRegistrazione.setUtentePartecipante(caricatoDaUtentePartecipante);
        caricatoDaRegistrazione.setRuolo(RuoloTeam.fromId(rs.getInt("ruolo_fk_ruoli_team")));
        progresso.setCaricatoDaRegistrazione(caricatoDaRegistrazione);

        return progresso;
    }
}