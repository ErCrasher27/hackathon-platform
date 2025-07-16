package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.GiudiceHackathonDAO;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

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
    public GiudiceHackathonListResponse getGiudiciNonInvitati(int hackathonId) {
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

        List<GiudiceHackathon> giudici = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GiudiceHackathon giudiceHackathon = new GiudiceHackathon();
                giudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
                giudiceHackathon.setHackathonId(rs.getInt("hackathon_id"));
                giudiceHackathon.setGiudiceId(rs.getInt("giudice_id"));
                giudiceHackathon.setInvitatoDaId(rs.getInt("invitato_da"));
                giudiceHackathon.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());
                giudiceHackathon.setStatoInvito(StatoInvito.valueOf(rs.getString("stato_invito")));

                Utente giudice = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
                giudice.setUtenteId(rs.getInt("giudice_id"));
                giudiceHackathon.setGiudice(giudice);

                Utente invitatoDa = new Utente(rs.getString("invitato_da_username"), "", "", "", "", TipoUtente.ORGANIZZATORE);
                invitatoDa.setUtenteId(rs.getInt("invitato_da"));
                giudiceHackathon.setInvitatoDa(invitatoDa);

                giudici.add(giudiceHackathon);
            }
            return new GiudiceHackathonListResponse(giudici, "Giudici non invitati caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new GiudiceHackathonListResponse(null, "Errore durante il caricamento dei giudici non invitati!");
        }
    }

    @Override
    public GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId) {
        String query = """
                SELECT gh.giudice_hackathon_id, gh.hackathon_id, gh.giudice_id, gh.data_invito, 
                       gh.invitato_da, invs.status_name as stato_invito,
                       u.username, u.nome, u.cognome, u.email,
                       organizzatore.username as invitato_da_username
                FROM giudici_hackathon gh
                JOIN utenti u ON gh.giudice_id = u.utente_id
                JOIN utenti organizzatore ON gh.invitato_da = organizzatore.utente_id
                JOIN invito_status invs ON gh.stato_invito_id = invs.status_id
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
                giudiceHackathon.setHackathonId(rs.getInt("hackathon_id"));
                giudiceHackathon.setGiudiceId(rs.getInt("giudice_id"));
                giudiceHackathon.setInvitatoDaId(rs.getInt("invitato_da"));
                giudiceHackathon.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());
                giudiceHackathon.setStatoInvito(StatoInvito.valueOf(rs.getString("stato_invito")));

                Utente giudice = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
                giudice.setUtenteId(rs.getInt("giudice_id"));
                giudiceHackathon.setGiudice(giudice);

                Utente invitatoDa = new Utente(rs.getString("invitato_da_username"), "", "", "", "", TipoUtente.ORGANIZZATORE);
                invitatoDa.setUtenteId(rs.getInt("invitato_da"));
                giudiceHackathon.setInvitatoDa(invitatoDa);

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
                JOIN invito_status invs ON gh.stato_invito_id = invs.status_id
                WHERE gh.hackathon_id = ? AND invs.status_name = 'ACCEPTED'
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

    @Override
    public GiudiceHackathonListResponse getInvitiRicevuti(int giudiceId) {
        String query = """
                SELECT gh.giudice_hackathon_id, gh.hackathon_id, gh.giudice_id, gh.data_invito, 
                       gh.invitato_da, invs.status_name as stato_invito,
                       h.titolo as hackathon_titolo, h.descrizione as hackathon_descrizione,
                       h.data_inizio, h.data_fine, h.sede,
                       organizzatore.username as invitato_da_username, organizzatore.nome as organizzatore_nome,
                       organizzatore.cognome as organizzatore_cognome
                FROM giudici_hackathon gh
                JOIN hackathon h ON gh.hackathon_id = h.hackathon_id
                JOIN utenti organizzatore ON gh.invitato_da = organizzatore.utente_id
                JOIN invito_status invs ON gh.stato_invito_id = invs.status_id
                WHERE gh.giudice_id = ?
                ORDER BY gh.data_invito DESC
                """;

        List<GiudiceHackathon> inviti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GiudiceHackathon giudiceHackathon = new GiudiceHackathon();
                giudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
                giudiceHackathon.setHackathonId(rs.getInt("hackathon_id"));
                giudiceHackathon.setGiudiceId(rs.getInt("giudice_id"));
                giudiceHackathon.setInvitatoDaId(rs.getInt("invitato_da"));
                giudiceHackathon.setDataInvito(rs.getTimestamp("data_invito").toLocalDateTime());
                giudiceHackathon.setStatoInvito(StatoInvito.valueOf(rs.getString("stato_invito")));

                Utente invitatoDa = new Utente(rs.getString("invitato_da_username"), "", "", rs.getString("organizzatore_nome"), rs.getString("organizzatore_cognome"), TipoUtente.ORGANIZZATORE);
                invitatoDa.setUtenteId(rs.getInt("invitato_da"));
                giudiceHackathon.setInvitatoDa(invitatoDa);

                inviti.add(giudiceHackathon);
            }
            return new GiudiceHackathonListResponse(inviti, "Inviti ricevuti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new GiudiceHackathonListResponse(null, "Errore durante il caricamento degli inviti ricevuti!");
        }
    }

    @Override
    public ResponseResult rispondiInvito(int giudiceHackathonId, StatoInvito risposta) {
        String query = """
                UPDATE giudici_hackathon 
                SET stato_invito_id = (SELECT status_id FROM invito_status WHERE status_name = ?)
                WHERE giudice_hackathon_id = ? 
                AND stato_invito_id = (SELECT status_id FROM invito_status WHERE status_name = 'PENDING')
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, risposta.toString());
            ps.setInt(2, giudiceHackathonId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                String message = risposta == StatoInvito.ACCEPTED ? "Invito accettato con successo!" : "Invito rifiutato con successo!";
                return new ResponseResult(true, message);
            } else {
                return new ResponseResult(false, "Invito non trovato o già processato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la risposta all'invito!");
        }
    }


}