package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.GiudiceHackathonDAO;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiudiceHackathonImplementazionePostgresDAO implements GiudiceHackathonDAO {
    private Connection connection;

    public GiudiceHackathonImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UtenteListResponse getGiudiciNonInvitati(int hackathonId) {
        String query = """
                SELECT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name 
                FROM utenti u 
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id 
                WHERE ur.role_name = 'GIUDICE' 
                AND u.utente_id NOT IN (
                    SELECT gh.giudice_id 
                    FROM giudici_hackathon gh 
                    WHERE gh.hackathon_id = ?
                )
                ORDER BY u.nome, u.cognome
                """;

        List<Utente> giudici = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                giudici.add(mapResultSetToUtente(rs));
            }
            return new UtenteListResponse(giudici, "Giudici non invitati caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento dei giudici non invitati!");
        }
    }

    @Override
    public GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId) {
        String query = """
                SELECT gh.giudice_hackathon_id, gh.hackathon_id, gh.giudice_id, gh.data_invito, 
                       gh.invitato_da, is.status_name as stato_invito,
                       u.username, u.nome, u.cognome, u.email,
                       organizzatore.username as invitato_da_username
                FROM giudici_hackathon gh
                JOIN utenti u ON gh.giudice_id = u.utente_id
                JOIN utenti organizzatore ON gh.invitato_da = organizzatore.utente_id
                JOIN invito_status is ON gh.stato_invito_id = is.status_id
                WHERE gh.hackathon_id = ?
                ORDER BY gh.data_invito DESC
                """;

        List<GiudiceHackathon> giudiciInvitati = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GiudiceHackathon giudiceHackathon = new GiudiceHackathon();
                giudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
                giudiceHackathon.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());
                giudiceHackathon.setStatoInvito(StatoInvito.valueOf(rs.getString("stato_invito")));
                giudiciInvitati.add(giudiceHackathon);
            }
            return new GiudiceHackathonListResponse(giudiciInvitati, "Giudici invitati caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new GiudiceHackathonListResponse(null, "Errore durante il caricamento dei giudici invitati!");
        }
    }

    @Override
    public ResponseResult invitaGiudice(int hackathonId, int giudiceId, int invitatoDa) {
        String query = """
                INSERT INTO giudici_hackathon (hackathon_id, giudice_id, invitato_da, stato_invito_id)
                VALUES (?, ?, ?, (SELECT status_id FROM invito_status WHERE status_name = 'PENDING'))
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, giudiceId);
            ps.setInt(3, invitatoDa);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Invito inviato con successo!");
            } else {
                return new ResponseResult(false, "Errore nell'invio dell'invito!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore nell'invio dell'invito!");
        }
    }

    @Override
    public ResponseResult rimuoviInvito(int hackathonId, int giudiceId) {
        String query = """
                DELETE FROM giudici_hackathon 
                WHERE hackathon_id = ? AND giudice_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, giudiceId);

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
    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        String query = """
                SELECT COUNT(*) 
                FROM giudici_hackathon gh
                JOIN invito_status is ON gh.stato_invito_id = is.status_id
                WHERE gh.hackathon_id = ? AND is.status_name = 'ACCEPTED'
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio giudici accettati completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei giudici accettati!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei giudici accettati!");
        }
    }

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("nome"), rs.getString("cognome"), TipoUtente.valueOf(rs.getString("role_name")));
        utente.setUtenteId(rs.getInt("utente_id"));
        utente.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        return utente;
    }
}