package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.MembroTeamDAO;
import it.unina.hackathon.model.MembroTeam;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.MembroTeamListResponse;
import it.unina.hackathon.utils.responses.MembroTeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
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
    public MembroTeamResponse saveMembro(MembroTeam membro) {
        String query = """
                INSERT INTO membri_team (team_id, utente_id, data_ingresso, ruolo_team) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, membro.getTeamId());
            ps.setInt(2, membro.getUtenteId());
            ps.setTimestamp(3, Timestamp.valueOf(membro.getDataIngresso()));
            ps.setString(4, membro.getRuolo().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    membro.setMembroTeamId(generatedKeys.getInt(1));
                }
                return new MembroTeamResponse(membro, "Membro aggiunto al team con successo!");
            } else {
                return new MembroTeamResponse(null, "Errore durante l'aggiunta del membro al team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamResponse(null, "Errore durante l'aggiunta del membro: " + e.getMessage());
        }
    }

    @Override
    public MembroTeamResponse getMembroById(int membroId) {
        String query = """
                SELECT mt.membro_team_id, mt.team_id, mt.utente_id, mt.data_ingresso, mt.ruolo_team,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM membri_team mt
                JOIN utenti u ON mt.utente_id = u.utente_id
                JOIN team t ON mt.team_id = t.team_id
                WHERE mt.membro_team_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, membroId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MembroTeam membro = mapResultSetToMembroTeam(rs);
                return new MembroTeamResponse(membro, "Membro trovato con successo!");
            } else {
                return new MembroTeamResponse(null, "Membro non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamResponse(null, "Errore durante la ricerca del membro!");
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
    public MembroTeamListResponse getTeamByUtente(int utenteId) {
        String query = """
                SELECT mt.membro_team_id, mt.team_id, mt.utente_id, mt.data_ingresso, mt.ruolo_team,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome,
                       h.titolo as hackathon_titolo
                FROM membri_team mt
                JOIN utenti u ON mt.utente_id = u.utente_id
                JOIN team t ON mt.team_id = t.team_id
                JOIN hackathon h ON t.hackathon_id = h.hackathon_id
                WHERE mt.utente_id = ?
                ORDER BY mt.data_ingresso DESC
                """;

        List<MembroTeam> membri = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                membri.add(mapResultSetToMembroTeam(rs));
            }
            return new MembroTeamListResponse(membri, "Team dell'utente caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamListResponse(null, "Errore durante il caricamento dei team dell'utente!");
        }
    }

    @Override
    public MembroTeamResponse updateMembro(MembroTeam membro) {
        String query = """
                UPDATE membri_team 
                SET ruolo_team = ?
                WHERE membro_team_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, membro.getRuolo().name());
            ps.setInt(2, membro.getMembroTeamId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new MembroTeamResponse(membro, "Membro aggiornato con successo!");
            } else {
                return new MembroTeamResponse(null, "Membro non trovato per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamResponse(null, "Errore durante l'aggiornamento del membro!");
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
    public ResponseResult cambiaRuolo(int membroId, RuoloTeam nuovoRuolo) {
        String query = "UPDATE membri_team SET ruolo_team = ? WHERE membro_team_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nuovoRuolo.name());
            ps.setInt(2, membroId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Ruolo del membro cambiato con successo!");
            } else {
                return new ResponseResult(false, "Membro non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante il cambio di ruolo del membro!");
        }
    }

    @Override
    public MembroTeamResponse getMembroByUtenteTeam(int utenteId, int teamId) {
        String query = """
                SELECT mt.membro_team_id, mt.team_id, mt.utente_id, mt.data_ingresso, mt.ruolo_team,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM membri_team mt
                JOIN utenti u ON mt.utente_id = u.utente_id
                JOIN team t ON mt.team_id = t.team_id
                WHERE mt.utente_id = ? AND mt.team_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MembroTeam membro = mapResultSetToMembroTeam(rs);
                return new MembroTeamResponse(membro, "Membro trovato con successo!");
            } else {
                return new MembroTeamResponse(null, "L'utente non fa parte di questo team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new MembroTeamResponse(null, "Errore durante la ricerca del membro!");
        }
    }

    @Override
    public ResponseResult verificaMembroEsistente(int utenteId, int teamId) {
        String query = "SELECT COUNT(*) FROM membri_team WHERE utente_id = ? AND team_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    return new ResponseResult(true, "L'utente fa già parte di questo team!");
                } else {
                    return new ResponseResult(false, "L'utente non fa parte di questo team!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica del membro!");
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