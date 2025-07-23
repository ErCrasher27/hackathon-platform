package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.TeamDAO;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

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
                INSERT INTO teams (nome, hackathon_fk_hackathons, definitivo) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, team.getNome());
            ps.setInt(2, team.getHackathonId());
            ps.setBoolean(3, team.isDefinitivo());

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
    public TeamResponse getTeamByPartecipanteHackathon(int utentePartecipanteId, int hackathonId) {
        String query = """
                SELECT t.team_id, t.nome, t.hackathon_fk_hackathons, t.data_creazione, t.definitivo
                FROM teams t
                JOIN registrazioni r ON r.team_fk_teams = t.team_id
                WHERE r.partecipante_fk_utenti = ? AND t.hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utentePartecipanteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TeamResponse(mapResultSetToTeam(rs), "Team del partecipante trovato con successo!");
            } else {
                return new TeamResponse(null, "Il partecipante non fa parte di alcun team per questo hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamResponse(null, "Errore durante la ricerca del team del partecipante: " + e.getMessage());
        }
    }

    @Override
    public TeamResponse getTeamByRegistrazione(int registrazioneId) {
        String query = """
                          SELECT t.team_id, t.nome, t.hackathon_fk_hackathons, t.data_creazione, t.definitivo
                FROM teams t
                          JOIN registrazioni r ON r.team_fk_teams = t.team_id
                          WHERE r.registrazione_id
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TeamResponse(mapResultSetToTeam(rs), "Team trovato con successo tramite membro_team_id!");
            } else {
                return new TeamResponse(null, "Nessun team trovato per il membro specificato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamResponse(null, "Errore durante la ricerca del team tramite membro_team_id: " + e.getMessage());
        }
    }

    @Override
    public TeamListResponse getTeamByHackathon(int hackathonId) {
        String query = """
                SELECT t.team_id, t.nome, t.hackathon_fk_hackathons, t.data_creazione, t.definitivo
                FROM teams t
                WHERE t.hackathon_fk_hackathons = ?
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
    public ResponseIntResult contaNumeroMembri(int teamId) {
        String query = """
                SELECT COUNT(*) AS numero_membri
                FROM teams t
                JOIN registrazioni r ON r.team_fk_teams = t.team_id
                WHERE team_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ResponseIntResult(rs.getInt("numero_membri"), "Numero di membri calcolato con successo!");
            } else {
                return new ResponseIntResult(-1, "Team non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei membri: " + e.getMessage());
        }
    }

    @Override
    public ResponseIntResult contaTeamFormati(int hackathonId) {
        String query = """
                SELECT COUNT(DISTINCT t.team_id) AS numero_team
                FROM team t
                JOIN registrazioni r ON r.team_fk_teams = t.team_id
                WHERE t.hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ResponseIntResult(rs.getInt("numero_team"), "Numero di team formati calcolato con successo!");
            } else {
                return new ResponseIntResult(-1, "Hackathon non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei team: " + e.getMessage());
        }
    }

    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setNome(rs.getString("nome"));
        team.setHackathonId(rs.getInt("hackathon_fk_hackathons"));
        team.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        team.setDefinitivo(rs.getBoolean("definitivo"));
        return team;
    }
}