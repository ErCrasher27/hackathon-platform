package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.InvitoTeamDAO;
import it.unina.hackathon.model.InvitoTeam;
import it.unina.hackathon.model.MembroTeam;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
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
    public InvitoTeamListResponse getInvitiRicevuti(int utenteId, int hackathonId) {
        String query = """
                SELECT i.invito_id, i.invitante_id, i.invitato_id, 
                       i.messaggio_motivazionale, i.stato_invito_id, i.data_invito,
                       mt.membro_team_id, mt.team_id, mt.ruolo_team, mt.data_ingresso,
                       u_invitante.utente_id as invitante_utente_id,
                       u_invitante.nome as invitante_nome, u_invitante.cognome as invitante_cognome, 
                       u_invitante.username as invitante_username, u_invitante.email as invitante_email,
                       u_invitato.nome as invitato_nome, u_invitato.cognome as invitato_cognome,
                       u_invitato.username as invitato_username, u_invitato.email as invitato_email
                FROM inviti_team i
                JOIN membri_team mt ON i.invitante_id = mt.membro_team_id
                JOIN team t ON mt.team_id = t.team_id
                JOIN utenti u_invitante ON mt.utente_id = u_invitante.utente_id
                JOIN utenti u_invitato ON i.invitato_id = u_invitato.utente_id
                WHERE i.invitato_id = ? AND t.hackathon_id = ?
                ORDER BY i.data_invito DESC
                """;

        List<InvitoTeam> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, hackathonId);
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
    public ResponseResult inviaInvito(int membroTeamInvitanteId, int utenteInvitatoId, String messaggio) {
        String query = """
                INSERT INTO inviti_team (invitante_id, invitato_id, messaggio_motivazionale, 
                                       stato_invito_id, data_invito) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, membroTeamInvitanteId);
            ps.setInt(2, utenteInvitatoId);
            ps.setString(3, messaggio);
            ps.setInt(4, StatoInvito.PENDING.getId());
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));

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
    public ResponseResult rispondiInvito(int invitoTeamId, StatoInvito risposta) {
        String query = """
                UPDATE inviti_team 
                SET stato_invito_id = ?
                WHERE invito_id = ? AND stato_invito_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, risposta.getId());
            ps.setInt(2, invitoTeamId);
            ps.setInt(3, StatoInvito.PENDING.getId()); // Solo inviti in attesa possono essere modificati

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

    private InvitoTeam mapResultSetToInvitoTeam(ResultSet rs) throws SQLException {
        InvitoTeam invito = new InvitoTeam();
        invito.setInvitoId(rs.getInt("invito_id"));
        invito.setInvitanteId(rs.getInt("invitante_id")); // membro_team_id
        invito.setInvitatoId(rs.getInt("invitato_id"));
        invito.setMessaggioMotivazionale(rs.getString("messaggio_motivazionale"));
        invito.setStatoInvito(StatoInvito.fromId(rs.getInt("stato_invito_id")));
        invito.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());

        // Mappa l'invitante (membro del team)
        Utente invitanteUtente = new Utente(rs.getString("invitante_username"), rs.getString("invitante_email"), "", rs.getString("invitante_nome"), rs.getString("invitante_cognome"), TipoUtente.PARTECIPANTE);
        invitanteUtente.setUtenteId(rs.getInt("invitante_utente_id"));

        MembroTeam invitanteMembroTeam = new MembroTeam();
        invitanteMembroTeam.setMembroTeamId(rs.getInt("membro_team_id"));
        invitanteMembroTeam.setTeamId(rs.getInt("team_id"));
        invitanteMembroTeam.setUtente(invitanteUtente);
        invitanteMembroTeam.setRuolo(RuoloTeam.valueOf(rs.getString("ruolo_team")));
        invitanteMembroTeam.setDataIngresso(rs.getTimestamp("data_ingresso").toLocalDateTime());
        invito.setInvitante(invitanteMembroTeam);

        // Mappa l'invitato
        Utente invitato = new Utente(rs.getString("invitato_username"), rs.getString("invitato_email"), "", rs.getString("invitato_nome"), rs.getString("invitato_cognome"), TipoUtente.PARTECIPANTE);
        invitato.setUtenteId(rs.getInt("invitato_id"));
        invito.setInvitato(invitato);

        return invito;
    }
}