package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.InvitoTeamDAO;
import it.unina.hackathon.model.InvitoTeam;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvitoTeamImplementazionePostgresDAO implements InvitoTeamDAO {
    private Connection connection;

    public InvitoTeamImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseResult inviaRichiesta(int teamId, int partecipanteId) {
        // Il partecipante richiede di entrare nel team
        // In questo caso invitante_id = partecipante che richiede, invitato_id = stesso partecipante
        // Ma per distinguere dalle richieste dai team, useremo un approccio diverso:
        // Troveremo il leader del team e lo useremo come invitato_id

        String queryLeader = """
                SELECT mt.utente_id
                FROM membri_team mt
                WHERE mt.team_id = ? AND mt.ruolo_team = 'LEADER'
                LIMIT 1
                """;

        String queryInsert = """
                INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id, data_invito) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try {
            // Prima troviamo il leader del team
            int leaderId = 0;
            try (PreparedStatement psLeader = connection.prepareStatement(queryLeader)) {
                psLeader.setInt(1, teamId);
                ResultSet rs = psLeader.executeQuery();
                if (rs.next()) {
                    leaderId = rs.getInt("utente_id");
                } else {
                    return new ResponseResult(false, "Team non trovato o senza leader!");
                }
            }

            // Ora inseriamo la richiesta
            try (PreparedStatement ps = connection.prepareStatement(queryInsert)) {
                ps.setInt(1, teamId);
                ps.setInt(2, partecipanteId); // Chi invia la richiesta
                ps.setInt(3, leaderId); // A chi viene inviata (leader del team)
                ps.setString(4, "Richiesta di partecipazione al team");
                ps.setInt(5, StatoInvito.PENDING.getId());
                ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    return new ResponseResult(true, "Richiesta inviata con successo!");
                } else {
                    return new ResponseResult(false, "Errore durante l'invio della richiesta!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key")) {
                return new ResponseResult(false, "Hai già inviato una richiesta per questo team!");
            }
            return new ResponseResult(false, "Errore durante l'invio della richiesta: " + e.getMessage());
        }
    }

    @Override
    public ResponseResult inviaInvito(int teamId, int utenteIdDaInvitare, int invitanteId, String messaggio) {
        String query = """
                INSERT INTO inviti_team (team_id, invitante_id, invitato_id, messaggio_motivazionale, stato_invito_id, data_invito) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ps.setInt(2, invitanteId); // Membro del team che invia l'invito
            ps.setInt(3, utenteIdDaInvitare); // Utente da invitare
            ps.setString(4, messaggio);
            ps.setInt(5, StatoInvito.PENDING.getId());
            ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito inviato con successo!");
            } else {
                return new ResponseResult(false, "Errore durante l'invio dell'invito!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key")) {
                return new ResponseResult(false, "Questo utente ha già un invito per questo team!");
            }
            return new ResponseResult(false, "Errore durante l'invio dell'invito: " + e.getMessage());
        }
    }

    @Override
    public InvitoTeamListResponse getInvitiRicevuti(int partecipanteId) {
        String query = """
                SELECT i.invito_id, i.team_id, i.invitante_id, i.invitato_id, 
                       i.messaggio_motivazionale, i.stato_invito_id, i.data_invito, i.data_risposta,
                       t.nome as team_nome,
                       u_invitante.nome as invitante_nome, u_invitante.cognome as invitante_cognome, 
                       u_invitante.username as invitante_username, u_invitante.email as invitante_email,
                       u_invitato.nome as invitato_nome, u_invitato.cognome as invitato_cognome,
                       u_invitato.username as invitato_username, u_invitato.email as invitato_email
                FROM inviti_team i
                JOIN team t ON i.team_id = t.team_id
                JOIN utenti u_invitante ON i.invitante_id = u_invitante.utente_id
                JOIN utenti u_invitato ON i.invitato_id = u_invitato.utente_id
                WHERE i.invitato_id = ?
                ORDER BY i.data_invito DESC
                """;

        List<InvitoTeam> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, partecipanteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                inviti.add(mapResultSetToInvitoTeam(rs));
            }
            return new InvitoTeamListResponse(inviti, "Inviti ricevuti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitoTeamListResponse(null, "Errore durante il caricamento degli inviti ricevuti!");
        }
    }

    @Override
    public ResponseResult rispondiInvito(int invitoId, StatoInvito risposta) {
        String query = """
                UPDATE inviti_team 
                SET stato_invito_id = ?, data_risposta = ?
                WHERE invito_id = ? AND stato_invito_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, risposta.getId());
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(3, invitoId);
            ps.setInt(4, StatoInvito.PENDING.getId()); // Solo inviti in attesa possono essere modificati

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                String message = risposta == StatoInvito.ACCEPTED ? "Invito accettato con successo!" : "Invito rifiutato!";
                return new ResponseResult(true, message);
            } else {
                return new ResponseResult(false, "Invito non trovato o già processato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la risposta all'invito!");
        }
    }

    @Override
    public InvitoTeamListResponse getInvitiInviati(int partecipanteId) {
        String query = """
                SELECT i.invito_id, i.team_id, i.invitante_id, i.invitato_id, 
                       i.messaggio_motivazionale, i.stato_invito_id, i.data_invito, i.data_risposta,
                       t.nome as team_nome,
                       u_invitante.nome as invitante_nome, u_invitante.cognome as invitante_cognome, 
                       u_invitante.username as invitante_username, u_invitante.email as invitante_email,
                       u_invitato.nome as invitato_nome, u_invitato.cognome as invitato_cognome,
                       u_invitato.username as invitato_username, u_invitato.email as invitato_email
                FROM inviti_team i
                JOIN team t ON i.team_id = t.team_id
                JOIN utenti u_invitante ON i.invitante_id = u_invitante.utente_id
                JOIN utenti u_invitato ON i.invitato_id = u_invitato.utente_id
                WHERE i.invitante_id = ?
                ORDER BY i.data_invito DESC
                """;

        List<InvitoTeam> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, partecipanteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                inviti.add(mapResultSetToInvitoTeam(rs));
            }
            return new InvitoTeamListResponse(inviti, "Inviti inviati caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitoTeamListResponse(null, "Errore durante il caricamento degli inviti inviati!");
        }
    }

    @Override
    public ResponseResult annullaInvito(int invitoId) {
        String query = """
                UPDATE inviti_team 
                SET stato_invito_id = ?, data_risposta = ?
                WHERE invito_id = ? AND stato_invito_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, StatoInvito.DECLINED.getId()); // Imposta come rifiutato
            ps.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(3, invitoId);
            ps.setInt(4, StatoInvito.PENDING.getId()); // Solo inviti in attesa possono essere annullati

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito annullato con successo!");
            } else {
                return new ResponseResult(false, "Invito non trovato o già processato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'annullamento dell'invito!");
        }
    }

    private InvitoTeam mapResultSetToInvitoTeam(ResultSet rs) throws SQLException {
        InvitoTeam invito = new InvitoTeam();
        invito.setInvitoId(rs.getInt("invito_id"));
        invito.setTeamId(rs.getInt("team_id"));
        invito.setInvitanteId(rs.getInt("invitante_id"));
        invito.setInvitatoId(rs.getInt("invitato_id"));
        invito.setMessaggioMotivazionale(rs.getString("messaggio_motivazionale"));
        invito.setStatoInvito(StatoInvito.fromId(rs.getInt("stato_invito_id")));
        invito.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());

        Timestamp dataRisposta = rs.getTimestamp("data_risposta");
        if (dataRisposta != null) {
            invito.setDataRisposta(dataRisposta.toLocalDateTime());
        }

        // Mappa il team
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setNome(rs.getString("team_nome"));
        invito.setTeam(team);

        // Mappa l'invitante
        Utente invitante = new Utente(rs.getString("invitante_username"), rs.getString("invitante_email"), "", // Password non esposta
                rs.getString("invitante_nome"), rs.getString("invitante_cognome"), TipoUtente.PARTECIPANTE);
        invitante.setUtenteId(rs.getInt("invitante_id"));
        invito.setInvitante(invitante);

        // Mappa l'invitato
        Utente invitato = new Utente(rs.getString("invitato_username"), rs.getString("invitato_email"), "", // Password non esposta
                rs.getString("invitato_nome"), rs.getString("invitato_cognome"), TipoUtente.PARTECIPANTE);
        invitato.setUtenteId(rs.getInt("invitato_id"));
        invito.setInvitato(invitato);

        return invito;
    }
}