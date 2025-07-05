package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.PartecipanteDAO;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartecipanteImplementazionePostgresDAO implements PartecipanteDAO {
    private Connection connection;

    public PartecipanteImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        String query = """
                SELECT DISTINCT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name, r.data_registrazione as data_reg_hackathon,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.hackathon_id = ?
                ORDER BY r.data_registrazione DESC
                """;

        List<Utente> partecipanti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = mapResultSetToUtente(rs);
                partecipanti.add(utente);
            }
            return new UtenteListResponse(partecipanti, "Partecipanti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento dei partecipanti!");
        }
    }

    @Override
    public TeamListResponse getTeamHackathon(int hackathonId) {
        String query = """
                SELECT t.team_id, t.nome, t.data_creazione, t.definitivo,
                       COUNT(mt.utente_id) as numero_membri,
                       h.max_dimensione_team,
                       STRING_AGG(u.nome || ' ' || u.cognome, ', ') as membri
                FROM team t
                JOIN hackathon h ON t.hackathon_id = h.hackathon_id
                LEFT JOIN membri_team mt ON t.team_id = mt.team_id
                LEFT JOIN utenti u ON mt.utente_id = u.utente_id
                WHERE t.hackathon_id = ?
                GROUP BY t.team_id, t.nome, t.data_creazione, t.definitivo, h.max_dimensione_team
                ORDER BY t.data_creazione DESC
                """;

        List<Team> teams = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setNome(rs.getString("nome"));
                team.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
                team.setDefinitivo(rs.getBoolean("definitivo"));

                teams.add(team);
            }
            return new TeamListResponse(teams, "Team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new TeamListResponse(null, "Errore durante il caricamento dei team!");
        }
    }

    @Override
    public ResponseIntResult contaPartecipantiRegistrati(int hackathonId) {
        String query = """
                SELECT COUNT(DISTINCT r.utente_id) 
                FROM registrazioni r
                WHERE r.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio partecipanti completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei partecipanti!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei partecipanti!");
        }
    }

    @Override
    public ResponseIntResult contaTeamFormati(int hackathonId) {
        String query = """
                SELECT COUNT(*) 
                FROM team 
                WHERE hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio team completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei team!");
        }
    }

    @Override
    public UtenteListResponse getMembriTeam(int teamId) {
        String query = """
                SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name, mt.ruolo_team, mt.data_ingresso
                FROM membri_team mt
                JOIN utenti u ON mt.utente_id = u.utente_id
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
                WHERE mt.team_id = ?
                ORDER BY mt.data_ingresso
                """;

        List<Utente> membri = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = mapResultSetToUtente(rs);
                membri.add(utente);
            }
            return new UtenteListResponse(membri, "Membri del team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento dei membri del team!");
        }
    }

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("nome"), rs.getString("cognome"), TipoUtente.valueOf(rs.getString("role_name")));
        utente.setUtenteId(rs.getInt("utente_id"));
        utente.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        return utente;
    }
}