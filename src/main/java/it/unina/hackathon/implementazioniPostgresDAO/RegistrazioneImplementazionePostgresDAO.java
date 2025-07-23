package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.RegistrazioneDAO;
import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.RegistrazioneResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrazioneImplementazionePostgresDAO implements RegistrazioneDAO {
    private Connection connection;

    public RegistrazioneImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RegistrazioneListResponse getRegistrazioniByTeam(int teamId) {
        String query = """
                SELECT r.registrazione_id, r.team_fk_teams, r.partecipante_fk_utenti, r.data_ingresso_team, r.ruolo_fk_ruoli_team,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.partecipante_fk_utenti = u.utente_id
                JOIN team t ON r.team_fk_teams = t.team_id
                WHERE r.team_fk_teams = ?
                ORDER BY r.ruolo_fk_ruoli_team DESC, r.data_ingresso_team ASC
                """;

        List<Registrazione> registrazioni = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                registrazioni.add(mapResultSetToRegistrazione(rs));
            }
            return new RegistrazioneListResponse(registrazioni, "Registrazioni caricate con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneListResponse(null, "Errore durante il caricamento delle registrazioni!");
        }
    }

    @Override
    public RegistrazioneListResponse getRegistratiConTeamNullByHackathon(int hackathonId) {
        return null;
    }

    @Override
    public RegistrazioneResponse getRegistrazioneByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId) {
        return null;
    }

    @Override
    public RegistrazioneResponse saveRegistrazione(Registrazione registrazione) {
        return null;
    }

    @Override
    public ResponseIntResult contaRegistrazioniByHackathon(int hackathonId) {
        return null;
    }

    @Override
    public ResponseResult aggiornaTeamConRuolo(int registrazioneId, Integer teamId, RuoloTeam ruoloTeam) {
        return null;
    }

    @Override
    public ResponseResult aggiornaTeamNullConRuoloNull(int registrazioneId) {
        return null;
    }

    @Override
    public ResponseResult rimuoviRegistrazione(int registrazioneId) {
        String query = "DELETE FROM registrazioni WHERE registrazione_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Registrazione rimossa con successo!");
            } else {
                return new ResponseResult(false, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la rimozione della Registrazione!");
        }
    }

    @Override
    public ResponseResult isLeaderByUtentePartecipanteTeam(int utentePartecipanteId, int teamId) {
        String query = """
                SELECT COUNT(*) 
                FROM registrazioni 
                WHERE partecipante_fk_utenti = ? AND team_fk_teams = ? AND ruolo_team = 'LEADER'
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utentePartecipanteId);
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

    private Registrazione mapResultSetToRegistrazione(ResultSet rs) throws SQLException {
        Registrazione registrazione = new Registrazione();
        registrazione.setRegistrazioneId(rs.getInt("registrazione_id"));
        registrazione.setTeamId(rs.getInt("team_fk_teams"));
        registrazione.setUtentePartecipanteId(rs.getInt("partecipante_fk_utenti"));
        registrazione.setDataIngressoTeam(rs.getTimestamp("data_ingresso_team").toLocalDateTime());
        registrazione.setRuolo(RuoloTeam.valueOf(rs.getString("ruolo_fk_ruoli_team")));

        // Mappa l'utente
        Utente utentePartecipante = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE);
        utentePartecipante.setUtenteId(rs.getInt("utente_id"));
        registrazione.setUtentePartecipante(utentePartecipante);

        // Mappa il team
        Team team = new Team();
        team.setTeamId(rs.getInt("team_fk_teams"));
        team.setNome(rs.getString("team_nome"));
        registrazione.setTeam(team);

        return registrazione;
    }
}