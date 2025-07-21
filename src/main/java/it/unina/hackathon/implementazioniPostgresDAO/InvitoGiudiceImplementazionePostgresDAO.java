package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.InvitoGiudiceDAO;
import it.unina.hackathon.model.InvitoGiudice;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.InvitoGiudiceResponse;
import it.unina.hackathon.utils.responses.InvitoGiudiceListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvitoGiudiceImplementazionePostgresDAO implements InvitoGiudiceDAO {
    private Connection connection;

    public InvitoGiudiceImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InvitoGiudiceListResponse getInvitiRicevuti(int utenteId) {
        String query = """
                SELECT i.invito_id, i.invitante_id, i.invitato_id, i.hackathon_id,
                       i.stato_invito_id, i.data_invito,
                       u_invitante.utente_id as invitante_utente_id,
                       u_invitante.nome as invitante_nome, u_invitante.cognome as invitante_cognome,
                       u_invitante.username as invitante_username, u_invitante.email as invitante_email,
                       u_invitato.utente_id as invitato_utente_id,
                       u_invitato.nome as invitato_nome, u_invitato.cognome as invitato_cognome,
                       u_invitato.username as invitato_username, u_invitato.email as invitato_email
                FROM inviti_giudice i
                JOIN utenti u_invitante ON i.invitante_id = u_invitante.utente_id
                JOIN utenti u_invitato ON i.invitato_id = u_invitato.utente_id
                WHERE i.invitato_id = ?
                ORDER BY i.data_invito DESC
                """;

        List<InvitoGiudice> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                inviti.add(mapResultSetToInvitoGiudice(rs));
            }
            return new InvitoGiudiceListResponse(inviti, "Inviti giudice ricevuti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitoGiudiceListResponse(null, "Errore durante il caricamento degli inviti ricevuti!");
        }
    }

    @Override
    public InvitoGiudiceResponse getInvitoByInvitatoHackathon(int utenteId, int hackathonId) {
        String query = """
                SELECT invito_id, invitante_id, invitato_id, hackathon_id, stato_invito_id, data_invito
                FROM inviti_giudice
                WHERE invitato_id = ? AND hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Assumo che tu abbia una classe InvitoGiudice con questi campi
                InvitoGiudice invito = new InvitoGiudice();
                invito.setInvitoId(rs.getInt("invito_id"));
                invito.setInvitanteId(rs.getInt("invitante_id"));
                invito.setInvitatoId(rs.getInt("invitato_id"));
                invito.setHackathonId(rs.getInt("hackathon_id"));
                invito.setStatoInvito(StatoInvito.fromId(rs.getInt("stato_invito_id")));
                invito.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());

                return new InvitoGiudiceResponse(invito, "Invito trovato con successo.");
            } else {
                return new InvitoGiudiceResponse(null, "Nessun invito trovato per l'utente nell'hackathon specificato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new InvitoGiudiceResponse(null, "Errore durante la ricerca dell'invito: " + e.getMessage());
        }
    }

    @Override
    public ResponseResult inviaInvito(int utenteInvitanteId, int utenteInvitatoId, int hackathonId) {
        String query = """
                INSERT INTO inviti_giudice (invitante_id, invitato_id, hackathon_id, 
                                          stato_invito_id, data_invito) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteInvitanteId);
            ps.setInt(2, utenteInvitatoId);
            ps.setInt(3, hackathonId);
            ps.setInt(4, StatoInvito.PENDING.getId());
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito giudice inviato con successo!");
            } else {
                return new ResponseResult(false, "Errore durante l'invio dell'invito!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key")) {
                return new ResponseResult(false, "Questo giudice ha già un invito per questo hackathon!");
            }
            return new ResponseResult(false, "Errore durante l'invio dell'invito: " + e.getMessage());
        }
    }

    @Override
    public ResponseResult rimuoviInvito(int invitoGiudiceId) {
        String query = "DELETE FROM inviti_giudice WHERE invito_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, invitoGiudiceId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito rimosso con successo!");
            } else {
                return new ResponseResult(false, "Invito non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la rimozione dell'invito!");
        }
    }

    @Override
    public ResponseResult rispondiInvito(int invitoGiudiceId, StatoInvito risposta) {
        String query = """
                UPDATE inviti_giudice 
                SET stato_invito_id = ?
                WHERE invito_id = ? AND stato_invito_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, risposta.getId());
            ps.setInt(2, invitoGiudiceId);
            ps.setInt(3, StatoInvito.PENDING.getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Se accettato, aggiungi alla tabella giudici_hackathon
                if (risposta == StatoInvito.ACCEPTED) {
                    aggiungiGiudiceHackathon(invitoGiudiceId);
                }

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

    private void aggiungiGiudiceHackathon(int invitoGiudiceId) throws SQLException {
        String selectQuery = """
                SELECT invitato_id, hackathon_id 
                FROM inviti_giudice 
                WHERE invito_id = ?
                """;

        String insertQuery = """
                INSERT INTO giudici_hackathon (hackathon_id, giudice_id) 
                VALUES (?, ?)
                """;

        try (PreparedStatement selectPs = connection.prepareStatement(selectQuery); PreparedStatement insertPs = connection.prepareStatement(insertQuery)) {

            selectPs.setInt(1, invitoGiudiceId);
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                insertPs.setInt(1, rs.getInt("hackathon_id"));
                insertPs.setInt(2, rs.getInt("invitato_id"));
                insertPs.executeUpdate();
            }
        }
    }

    private InvitoGiudice mapResultSetToInvitoGiudice(ResultSet rs) throws SQLException {
        InvitoGiudice invito = new InvitoGiudice();
        invito.setInvitoId(rs.getInt("invito_id"));
        invito.setInvitanteId(rs.getInt("invitante_id"));
        invito.setInvitatoId(rs.getInt("invitato_id"));
        invito.setHackathonId(rs.getInt("hackathon_id")); // Solo l'ID, non l'oggetto completo
        invito.setStatoInvito(StatoInvito.fromId(rs.getInt("stato_invito_id")));
        invito.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());

        // Mappa l'invitante
        Utente invitante = new Utente(rs.getString("invitante_username"), rs.getString("invitante_email"), "", rs.getString("invitante_nome"), rs.getString("invitante_cognome"), TipoUtente.ORGANIZZATORE);
        invitante.setUtenteId(rs.getInt("invitante_utente_id"));
        invito.setInvitante(invitante);

        // Mappa l'invitato
        Utente invitato = new Utente(rs.getString("invitato_username"), rs.getString("invitato_email"), "", rs.getString("invitato_nome"), rs.getString("invitato_cognome"), TipoUtente.GIUDICE);
        invitato.setUtenteId(rs.getInt("invitato_utente_id"));
        invito.setInvitato(invitato);

        return invito;
    }
}