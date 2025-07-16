package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.TeamDAO;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamImplementazionePostgresDAO implements TeamDAO {
    private Connection connection;

    public TeamImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TeamResponse saveTeam(Team team) {
        String query = """
                INSERT INTO team (nome, hackathon_id, data_creazione, definitivo) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, team.getNome());
            ps.setInt(2, team.getHackathonId());
            ps.setTimestamp(3, Timestamp.valueOf(team.getDataCreazione()));
            ps.setBoolean(4, team.isDefinitivo());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    team.setTeamId(generatedKeys.getInt(1));
                }
                return new TeamResponse(team, "Team creato con successo!");
            } else {
                return new TeamResponse(null, "Errore durante la creazione del team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamResponse(null, "Errore durante la creazione del team: " + e.getMessage());
        }
    }

    @Override
    public TeamListResponse getTeamByHackathon(int hackathonId) {
        String query = """
                SELECT t.team_id, t.nome, t.hackathon_id, t.data_creazione, t.definitivo,
                       COUNT(mt.utente_id) as numero_membri,
                       h.max_dimensione_team,
                       STRING_AGG(u.nome || ' ' || u.cognome, ', ') as nomi_membri
                FROM team t
                JOIN hackathon h ON t.hackathon_id = h.hackathon_id
                LEFT JOIN membri_team mt ON t.team_id = mt.team_id
                LEFT JOIN utenti u ON mt.utente_id = u.utente_id
                WHERE t.hackathon_id = ?
                GROUP BY t.team_id, t.nome, t.hackathon_id, t.data_creazione, t.definitivo, h.max_dimensione_team
                ORDER BY t.data_creazione DESC
                """;

        List<Team> teams = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
            return new TeamListResponse(teams, "Team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamListResponse(null, "Errore durante il caricamento dei team!");
        }
    }

    @Override
    public ResponseResult aggiungiMembro(int teamId, int utenteId, RuoloTeam ruoloTeam) {
        String query = """
                INSERT INTO membri_team (team_id, utente_id, data_ingresso, ruolo_team) 
                VALUES (?, ?, CURRENT_TIMESTAMP, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, utenteId);
            ps.setString(3, ruoloTeam.getDisplayName().toUpperCase());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Membro aggiunto al team con successo!");
            } else {
                return new ResponseResult(false, "Errore durante l'aggiunta del membro!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'aggiunta del membro: " + e.getMessage());
        }
    }

    @Override
    public ResponseResult rimuoviMembro(int teamId, int utenteId) {
        String query = "DELETE FROM membri_team WHERE team_id = ? AND utente_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, utenteId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Membro rimosso dal team con successo!");
            } else {
                return new ResponseResult(false, "Membro non trovato nel team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la rimozione del membro!");
        }
    }

    @Override
    public TeamResponse getTeamByPartecipanteHackathon(int partecipanteId, int hackathonId) {
        String query = """
                SELECT t.team_id, t.nome, t.hackathon_id, t.data_creazione, t.definitivo,
                       mt.ruolo_team,
                       (SELECT COUNT(*) FROM membri_team mt3 WHERE mt3.team_id = t.team_id) as numero_membri
                FROM membri_team mt
                JOIN team t ON mt.team_id = t.team_id
                WHERE mt.utente_id = ? AND t.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, partecipanteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setNome(rs.getString("nome"));
                team.setHackathonId(rs.getInt("hackathon_id"));
                team.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
                team.setDefinitivo(rs.getBoolean("definitivo"));
                team.setNumeroMembri(rs.getInt("numero_membri"));

                return new TeamResponse(team, "Team del partecipante trovato con successo!");
            } else {
                return new TeamResponse(null, "Il partecipante non fa parte di alcun team per questo hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamResponse(null, "Errore durante la ricerca del team del partecipante: " + e.getMessage());
        }
    }

    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setNome(rs.getString("nome"));
        team.setHackathonId(rs.getInt("hackathon_id"));
        team.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        team.setDefinitivo(rs.getBoolean("definitivo"));
        team.setNumeroMembri(rs.getInt("numero_membri"));
        team.setMaxDimensione(rs.getInt("max_dimensione_team"));

        String nomiMembri = rs.getString("nomi_membri");
        team.setNomiMembri(nomiMembri != null ? nomiMembri : "Nessun membro");

        return team;
    }
}