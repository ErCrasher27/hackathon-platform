package it.unina.hackathon.implementazioni_postgres_dao;

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

import java.sql.*;
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
                SELECT r.registrazione_id, r.team_fk_teams, r.partecipante_fk_utenti, r.data_ingresso_team, 
                       r.ruolo_fk_ruoli_team, r.data_registrazione, r.hackathon_fk_hackathons,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.partecipante_fk_utenti = u.utente_id
                JOIN teams t ON r.team_fk_teams = t.team_id
                WHERE r.team_fk_teams = ?
                ORDER BY r.ruolo_fk_ruoli_team ASC, r.data_ingresso_team ASC
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
        String query = """
                SELECT r.registrazione_id, r.team_fk_teams, r.partecipante_fk_utenti, r.data_ingresso_team, 
                       r.ruolo_fk_ruoli_team, r.data_registrazione, r.hackathon_fk_hackathons,
                       u.nome, u.cognome, u.username, u.email, u.utente_id
                FROM registrazioni r
                JOIN utenti u ON r.partecipante_fk_utenti = u.utente_id
                WHERE r.hackathon_fk_hackathons = ? AND r.team_fk_teams IS NULL
                ORDER BY r.data_registrazione DESC
                """;

        List<Registrazione> registrazioni = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                registrazioni.add(mapResultSetToRegistrazione(rs));
            }
            return new RegistrazioneListResponse(registrazioni, "Registrati senza team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneListResponse(null, "Errore durante il caricamento!");
        }
    }

    @Override
    public RegistrazioneResponse getRegistrazioneByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId) {
        String query = """
                SELECT r.registrazione_id, r.team_fk_teams, r.partecipante_fk_utenti, r.data_ingresso_team, 
                       r.ruolo_fk_ruoli_team, r.data_registrazione, r.hackathon_fk_hackathons,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.partecipante_fk_utenti = u.utente_id
                LEFT JOIN teams t ON r.team_fk_teams = t.team_id
                WHERE r.partecipante_fk_utenti = ? AND r.hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utentePartecipanteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new RegistrazioneResponse(mapResultSetToRegistrazione(rs), "Registrazione trovata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneResponse(null, "Errore durante la ricerca della registrazione!");
        }
    }

    @Override
    public RegistrazioneResponse saveRegistrazione(Registrazione registrazione) {
        String query = """
                INSERT INTO registrazioni (partecipante_fk_utenti, hackathon_fk_hackathons, team_fk_teams, 
                                         ruolo_fk_ruoli_team, data_ingresso_team) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, registrazione.getUtentePartecipanteId());
            ps.setInt(2, registrazione.getHackathonId());
            ps.setNull(3, Types.INTEGER);
            ps.setNull(4, Types.INTEGER);
            ps.setNull(5, Types.TIMESTAMP);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    registrazione.setRegistrazioneId(generatedKeys.getInt(1));
                }
                return new RegistrazioneResponse(registrazione, "Registrazione salvata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Errore durante il salvataggio della registrazione!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("uq_utente_per_hackathon")) {
                return new RegistrazioneResponse(null, "Utente già registrato a questo hackathon!");
            }
            return new RegistrazioneResponse(null, "Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @Override
    public ResponseIntResult contaRegistrazioniByHackathon(int hackathonId) {
        String query = """
                SELECT COUNT(*) 
                FROM registrazioni 
                WHERE hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ResponseIntResult(rs.getInt(1), "Conteggio registrazioni completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio delle registrazioni!");
        }
    }

    @Override
    public ResponseResult aggiornaTeamConRuolo(int registrazioneId, Integer teamId, RuoloTeam ruoloTeam) {
        String query = """
                UPDATE registrazioni 
                SET team_fk_teams = ?, ruolo_fk_ruoli_team = ?, data_ingresso_team = ?
                WHERE registrazione_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, ruoloTeam.getId());
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setInt(4, registrazioneId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Team e ruolo aggiornati con successo!");
            } else {
                return new ResponseResult(false, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'aggiornamento!");
        }
    }

    @Override
    public ResponseResult aggiornaTeamNullConRuoloNull(int registrazioneId) {
        String query = """
                UPDATE registrazioni 
                SET team_fk_teams = NULL, ruolo_fk_ruoli_team = NULL, data_ingresso_team = NULL
                WHERE registrazione_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Utente rimosso dal team con successo!");
            } else {
                return new ResponseResult(false, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la rimozione dal team!");
        }
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
                WHERE partecipante_fk_utenti = ? AND team_fk_teams = ? AND ruolo_fk_ruoli_team = 1
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
        registrazione.setUtentePartecipanteId(rs.getInt("partecipante_fk_utenti"));
        registrazione.setHackathonId(rs.getInt("hackathon_fk_hackathons"));

        if (rs.getObject("team_fk_teams") != null) {
            registrazione.setTeamId(rs.getInt("team_fk_teams"));
            registrazione.setRuolo(RuoloTeam.fromId(rs.getInt("ruolo_fk_ruoli_team")));
            registrazione.setDataIngressoTeam(rs.getTimestamp("data_ingresso_team").toLocalDateTime());
        }

        Utente utentePartecipante = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE);
        utentePartecipante.setUtenteId(rs.getInt("utente_id"));
        registrazione.setUtentePartecipante(utentePartecipante);

        try {
            if (rs.findColumn("team_nome") > 0 && rs.getString("team_nome") != null) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_fk_teams"));
                team.setNome(rs.getString("team_nome"));
                registrazione.setTeam(team);
            }
        } catch (SQLException _) {
        }

        return registrazione;
    }
}