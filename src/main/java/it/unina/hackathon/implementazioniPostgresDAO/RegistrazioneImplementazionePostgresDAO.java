package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.RegistrazioneDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.RegistrazioneResponse;
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
    public RegistrazioneResponse saveRegistrazione(Registrazione registrazione) {
        String query = """
                INSERT INTO registrazioni (utente_id, hackathon_id, data_registrazione, team_id) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, registrazione.getUtenteId());
            ps.setInt(2, registrazione.getHackathonId());
            ps.setTimestamp(3, Timestamp.valueOf(registrazione.getDataRegistrazione()));

            if (registrazione.getTeamId() != null) {
                ps.setInt(4, registrazione.getTeamId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    registrazione.setRegistrazioneId(generatedKeys.getInt(1));
                }
                return new RegistrazioneResponse(registrazione, "Registrazione completata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Errore durante la registrazione!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneResponse(null, "Errore durante la registrazione: " + e.getMessage());
        }
    }

    @Override
    public RegistrazioneResponse getRegistrazioneById(int registrazioneId) {
        String query = """
                SELECT r.registrazione_id, r.utente_id, r.hackathon_id, r.data_registrazione, r.team_id,
                       u.username, u.email, u.nome, u.cognome,
                       h.titolo as hackathon_titolo,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN hackathon h ON r.hackathon_id = h.hackathon_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.registrazione_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Registrazione registrazione = mapResultSetToRegistrazione(rs);
                return new RegistrazioneResponse(registrazione, "Registrazione trovata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneResponse(null, "Errore durante la ricerca della registrazione!");
        }
    }

    @Override
    public RegistrazioneListResponse getRegistrazioniByHackathon(int hackathonId) {
        String query = """
                SELECT r.registrazione_id, r.utente_id, r.hackathon_id, r.data_registrazione, r.team_id,
                       u.username, u.email, u.nome, u.cognome,
                       h.titolo as hackathon_titolo,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN hackathon h ON r.hackathon_id = h.hackathon_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.hackathon_id = ?
                ORDER BY r.data_registrazione DESC
                """;

        List<Registrazione> registrazioni = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                registrazioni.add(mapResultSetToRegistrazione(rs));
            }
            return new RegistrazioneListResponse(registrazioni, "Registrazioni dell'hackathon caricate con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneListResponse(null, "Errore durante il caricamento delle registrazioni!");
        }
    }

    @Override
    public RegistrazioneListResponse getRegistrazioniByUtente(int utenteId) {
        String query = """
                SELECT r.registrazione_id, r.utente_id, r.hackathon_id, r.data_registrazione, r.team_id,
                       u.username, u.email, u.nome, u.cognome,
                       h.titolo as hackathon_titolo,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN hackathon h ON r.hackathon_id = h.hackathon_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.utente_id = ?
                ORDER BY r.data_registrazione DESC
                """;

        List<Registrazione> registrazioni = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                registrazioni.add(mapResultSetToRegistrazione(rs));
            }
            return new RegistrazioneListResponse(registrazioni, "Registrazioni dell'utente caricate con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneListResponse(null, "Errore durante il caricamento delle registrazioni dell'utente!");
        }
    }

    @Override
    public RegistrazioneResponse updateRegistrazione(Registrazione registrazione) {
        String query = """
                UPDATE registrazioni 
                SET team_id = ?
                WHERE registrazione_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (registrazione.getTeamId() != null) {
                ps.setInt(1, registrazione.getTeamId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, registrazione.getRegistrazioneId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new RegistrazioneResponse(registrazione, "Registrazione aggiornata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Registrazione non trovata per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneResponse(null, "Errore durante l'aggiornamento della registrazione!");
        }
    }

    @Override
    public ResponseResult deleteRegistrazione(int registrazioneId) {
        String query = "DELETE FROM registrazioni WHERE registrazione_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Registrazione eliminata con successo!");
            } else {
                return new ResponseResult(false, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'eliminazione della registrazione!");
        }
    }

    @Override
    public RegistrazioneResponse getRegistrazioneByUtenteHackathon(int utenteId, int hackathonId) {
        String query = """
                SELECT r.registrazione_id, r.utente_id, r.hackathon_id, r.data_registrazione, r.team_id,
                       u.username, u.email, u.nome, u.cognome,
                       h.titolo as hackathon_titolo,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN hackathon h ON r.hackathon_id = h.hackathon_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.utente_id = ? AND r.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Registrazione registrazione = mapResultSetToRegistrazione(rs);
                return new RegistrazioneResponse(registrazione, "Registrazione trovata con successo!");
            } else {
                return new RegistrazioneResponse(null, "Utente non registrato a questo hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegistrazioneResponse(null, "Errore durante la ricerca della registrazione!");
        }
    }

    @Override
    public ResponseResult verificaRegistrazioneEsistente(int utenteId, int hackathonId) {
        String query = "SELECT COUNT(*) FROM registrazioni WHERE utente_id = ? AND hackathon_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    return new ResponseResult(true, "Utente già registrato a questo hackathon!");
                } else {
                    return new ResponseResult(false, "Utente non ancora registrato!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica della registrazione!");
        }
    }

    @Override
    public ResponseResult assegnaTeam(int registrazioneId, int teamId) {
        String query = "UPDATE registrazioni SET team_id = ? WHERE registrazione_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, registrazioneId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Team assegnato alla registrazione con successo!");
            } else {
                return new ResponseResult(false, "Registrazione non trovata!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'assegnazione del team!");
        }
    }

    private Registrazione mapResultSetToRegistrazione(ResultSet rs) throws SQLException {
        Registrazione registrazione = new Registrazione();
        registrazione.setRegistrazioneId(rs.getInt("registrazione_id"));
        registrazione.setUtenteId(rs.getInt("utente_id"));
        registrazione.setHackathonId(rs.getInt("hackathon_id"));
        registrazione.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());

        // Gestione team_id nullable
        int teamId = rs.getInt("team_id");
        if (!rs.wasNull()) {
            registrazione.setTeamId(teamId);
        }

        // Mappa l'utente
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.PARTECIPANTE // Default per registrazioni
        );
        utente.setUtenteId(rs.getInt("utente_id"));
        registrazione.setUtente(utente);

        // Mappa l'hackathon
        Hackathon hackathon = new Hackathon();
        hackathon.setHackathonId(rs.getInt("hackathon_id"));
        hackathon.setTitolo(rs.getString("hackathon_titolo"));
        registrazione.setHackathon(hackathon);

        // Mappa il team se presente
        String teamNome = rs.getString("team_nome");
        if (teamNome != null) {
            Team team = new Team();
            team.setTeamId(teamId);
            team.setNome(teamNome);
            registrazione.setTeam(team);
        }

        return registrazione;
    }
}