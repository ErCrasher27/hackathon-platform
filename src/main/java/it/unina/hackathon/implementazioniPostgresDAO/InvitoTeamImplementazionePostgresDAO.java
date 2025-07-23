package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.InvitoTeamDAO;
import it.unina.hackathon.model.InvitoTeam;
import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public InvitoTeamListResponse getInvitiTeamByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId) {
        String query = """
                SELECT i.invito_id, i.invitante_reg_fk_registrazioni, i.invitato_fk_utenti, 
                       i.messaggio, i.stato_fk_stati_invito, i.data_invito,
                       r.registrazione_id, r.team_fk_teams, r.ruolo_fk_ruoli_team, r.data_ingresso_team,
                       u_invitante.utente_id as invitante_utente_id,
                       u_invitante.nome as invitante_nome, u_invitante.cognome as invitante_cognome, 
                       u_invitante.username as invitante_username, u_invitante.email as invitante_email,
                       u_invitato.nome as invitato_nome, u_invitato.cognome as invitato_cognome,
                       u_invitato.username as invitato_username, u_invitato.email as invitato_email
                FROM inviti_team i
                JOIN registrazioni r ON i.invitante_reg_fk_registrazioni = r.registrazione_id
                JOIN utenti u_invitante ON r.partecipante_fk_utenti = u_invitante.utente_id
                JOIN utenti u_invitato ON i.invitato_fk_utenti = u_invitato.utente_id
                WHERE i.invitato_fk_utenti = ? AND r.hackathon_fk_hackathons = ?
                ORDER BY i.data_invito DESC
                """;

        List<InvitoTeam> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utentePartecipanteId);
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
    public ResponseResult saveInvitoUtente(int registrazioneInvitanteId, int utentePartecipanteInvitatoId, String messaggio) {
        String query = """
                INSERT INTO inviti_team (invitante_reg_fk_registrazioni, invitato_fk_utenti, messaggio, 
                                       stato_fk_stati_invito) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, registrazioneInvitanteId);
            ps.setInt(2, utentePartecipanteInvitatoId);
            ps.setString(3, messaggio);
            ps.setInt(4, StatoInvito.PENDING.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito inviato con successo!");
            } else {
                return new ResponseResult(false, "Errore durante l'invio dell'invito!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("uq_invito_team")) {
                return new ResponseResult(false, "Questo utente ha già un invito per questo team!");
            }
            return new ResponseResult(false, "Errore durante l'invio dell'invito: " + e.getMessage());
        }
    }

    @Override
    public ResponseResult aggiornaStatoInvito(int invitoTeamId, StatoInvito risposta) {
        String query = """
                UPDATE inviti_team 
                SET stato_fk_stati_invito = ?
                WHERE invito_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, risposta.getId());
            ps.setInt(2, invitoTeamId);

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
        invito.setRegistrazioneInvitanteId(rs.getInt("invitante_reg_fk_registrazioni"));
        invito.setUtentePartecipanteInvitatoId(rs.getInt("invitato_fk_utenti"));
        invito.setMessaggioMotivazionale(rs.getString("messaggio"));
        invito.setStatoInvito(StatoInvito.fromId(rs.getInt("stato_fk_stati_invito")));
        invito.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());

        Utente invitanteUtentePartecipante = new Utente(rs.getString("invitante_username"), rs.getString("invitante_email"), "", rs.getString("invitante_nome"), rs.getString("invitante_cognome"), TipoUtente.PARTECIPANTE);
        invitanteUtentePartecipante.setUtenteId(rs.getInt("invitante_utente_id"));

        Registrazione invitanteRegistrazione = new Registrazione();
        invitanteRegistrazione.setRegistrazioneId(rs.getInt("registrazione_id"));
        invitanteRegistrazione.setTeamId(rs.getInt("team_fk_teams"));
        invitanteRegistrazione.setUtentePartecipante(invitanteUtentePartecipante);
        invitanteRegistrazione.setRuolo(RuoloTeam.fromId(rs.getInt("ruolo_fk_ruoli_team")));
        if (rs.getTimestamp("data_ingresso_team") != null) {
            invitanteRegistrazione.setDataIngressoTeam(rs.getTimestamp("data_ingresso_team").toLocalDateTime());
        }
        invito.setRegistrazioneInvitante(invitanteRegistrazione);

        Utente invitatoUtentePartecipante = new Utente(rs.getString("invitato_username"), rs.getString("invitato_email"), "", rs.getString("invitato_nome"), rs.getString("invitato_cognome"), TipoUtente.PARTECIPANTE);
        invitatoUtentePartecipante.setUtenteId(rs.getInt("invitato_fk_utenti"));
        invito.setUtentePartecipanteInvitato(invitatoUtentePartecipante);

        return invito;
    }
}