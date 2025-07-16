package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.MembroTeamDAO;
import it.unina.hackathon.model.MembroTeam;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.MembroTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MembroTeamImplementazionePostgresDAO implements MembroTeamDAO {
    private Connection connection;

    public MembroTeamImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MembroTeamListResponse getMembriByTeam(int teamId) {
        String query = """
                SELECT mt.membro_team_id, mt.team_id, mt.utente_id, mt.data_ingresso, mt.ruolo_team,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM membri_team mt
                JOIN utenti u ON mt.utente_id = u.utente_id
                JOIN team t ON mt.team_id = t.team_id
                WHERE mt.team_id = ?
                ORDER BY mt.ruolo_team DESC, mt.data_ingresso ASC
                """;

        List<MembroTeam> membri = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                membri.add(mapResultSetToMembroTeam(rs));
            }
            return new MembroTeamListResponse(membri, "Membri del team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamListResponse(null, "Errore durante il caricamento dei membri del team!");
        }
    }

    @Override
    public ResponseResult deleteMembro(int membroId) {
        String query = "DELETE FROM membri_team WHERE membro_team_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, membroId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Membro rimosso dal team con successo!");
            } else {
                return new ResponseResult(false, "Membro non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la rimozione del membro!");
        }
    }

    @Override
    public ResponseResult isLeader(int utenteId, int teamId) {
        String query = """
                SELECT COUNT(*) 
                FROM membri_team 
                WHERE utente_id = ? AND team_id = ? AND ruolo_team = 'LEADER'
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean isLeader = rs.getInt(1) > 0;
                if (isLeader) {
                    return new ResponseResult(true, "L'utente è il leader del team!");
                } else {
                    return new ResponseResult(false, "L'utente non è il leader del team!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica del ruolo: " + e.getMessage());
        }
    }

    private MembroTeam mapResultSetToMembroTeam(ResultSet rs) throws SQLException {
        MembroTeam membro = new MembroTeam();
        membro.setMembroTeamId(rs.getInt("membro_team_id"));
        membro.setTeamId(rs.getInt("team_id"));
        membro.setUtenteId(rs.getInt("utente_id"));
        membro.setDataIngresso(rs.getTimestamp("data_ingresso").toLocalDateTime());
        membro.setRuolo(RuoloTeam.valueOf(rs.getString("ruolo_team")));

        // Mappa l'utente
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE);
        utente.setUtenteId(rs.getInt("utente_id"));
        membro.setUtente(utente);

        // Mappa il team
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setNome(rs.getString("team_nome"));
        membro.setTeam(team);

        return membro;
    }
}