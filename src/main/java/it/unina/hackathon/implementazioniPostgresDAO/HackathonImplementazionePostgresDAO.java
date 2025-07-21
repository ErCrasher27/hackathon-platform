package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.HackathonDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HackathonImplementazionePostgresDAO implements HackathonDAO {
    private Connection connection;

    public HackathonImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HackathonResponse saveHackathon(Hackathon hackathon) {
        String query = """
                INSERT INTO hackathon (titolo, descrizione, sede, data_inizio, data_fine, 
                                     data_chiusura_registrazioni, max_iscritti, max_dimensione_team, 
                                     organizzatore_id, status_id) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 
                        (SELECT status_id FROM hackathon_status WHERE status_name = ?))
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, hackathon.getTitolo());
            ps.setString(2, hackathon.getDescrizione());
            ps.setString(3, hackathon.getSede());
            ps.setTimestamp(4, Timestamp.valueOf(hackathon.getDataInizio()));
            ps.setTimestamp(5, Timestamp.valueOf(hackathon.getDataFine()));
            ps.setTimestamp(6, Timestamp.valueOf(hackathon.getDataChiusuraRegistrazioni()));
            ps.setInt(7, hackathon.getMaxIscritti());
            ps.setInt(8, hackathon.getMaxDimensioneTeam());
            ps.setInt(9, hackathon.getOrganizzatoreId());
            ps.setString(10, hackathon.getStatus().name());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    hackathon.setHackathonId(generatedKeys.getInt(1));
                }
                return new HackathonResponse(hackathon, "Hackathon creato con successo!");
            } else {
                return new HackathonResponse(null, "Errore durante la creazione dell'hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonResponse(null, "Errore durante la creazione dell'hackathon: " + e.getMessage());
        }
    }

    public HackathonResponse getHackathonById(int hackathonId) {
        String query = """
                SELECT h.hackathon_id, h.titolo, h.descrizione, h.sede, h.data_inizio, h.data_fine, 
                       h.data_chiusura_registrazioni, h.max_iscritti, h.max_dimensione_team, 
                       h.organizzatore_id, h.data_creazione, hs.status_name 
                FROM hackathon h 
                JOIN hackathon_status hs ON h.status_id = hs.status_id 
                WHERE h.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Hackathon hackathon = mapResultSetToHackathon(rs);
                return new HackathonResponse(hackathon, "Hackathon trovato con successo!");
            } else {
                return new HackathonResponse(null, "Hackathon non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonResponse(null, "Errore durante la ricerca dell'hackathon!");
        }
    }

    @Override
    public HackathonListResponse getHackathonByHackathonStatus(HackathonStatus hs) {
        int hsId = hs.getId();
        String query = """
                SELECT h.hackathon_id, h.titolo, h.descrizione, h.sede, h.data_inizio, h.data_fine, 
                       h.data_chiusura_registrazioni, h.max_iscritti, h.max_dimensione_team, 
                       h.organizzatore_id, h.data_creazione, hs.status_name 
                FROM hackathon h 
                JOIN hackathon_status hs ON h.status_id = hs.status_id 
                WHERE hs.status_id  = ?
                ORDER BY h.data_creazione DESC
                """;

        List<Hackathon> hackathonList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hsId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hackathonList.add(mapResultSetToHackathon(rs));
            }

            return new HackathonListResponse(hackathonList, "Hackathon recuperati con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonListResponse(null, "Errore durante il recupero degli hackathon!");
        }
    }

    @Override
    public HackathonListResponse getHackathonsByOrganizzatore(int utenteId) {
        String query = """
                SELECT h.hackathon_id, h.titolo, h.descrizione, h.sede, h.data_inizio, h.data_fine, 
                       h.data_chiusura_registrazioni, h.max_iscritti, h.max_dimensione_team, 
                       h.organizzatore_id, h.data_creazione, hs.status_name 
                FROM hackathon h 
                JOIN hackathon_status hs ON h.status_id = hs.status_id 
                WHERE h.organizzatore_id = ?
                ORDER BY h.data_creazione DESC
                """;

        List<Hackathon> hackathonList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hackathonList.add(mapResultSetToHackathon(rs));
            }

            return new HackathonListResponse(hackathonList, "Hackathon recuperati con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonListResponse(null, "Errore durante il recupero degli hackathon!");
        }
    }

    @Override
    public HackathonListResponse getHackathonsByPartecipante(int utenteId) {
        String query = """
                SELECT h.hackathon_id, h.titolo, h.descrizione, h.sede, h.data_inizio, h.data_fine, 
                       h.data_chiusura_registrazioni, h.max_iscritti, h.max_dimensione_team, 
                       h.organizzatore_id, h.data_creazione, hs.status_name,
                       r.data_registrazione
                FROM hackathon h 
                JOIN hackathon_status hs ON h.status_id = hs.status_id 
                JOIN registrazioni r ON h.hackathon_id = r.hackathon_id
                WHERE r.utente_id = ?
                ORDER BY r.data_registrazione DESC
                """;

        List<Hackathon> hackathonList = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hackathonList.add(mapResultSetToHackathon(rs));
            }

            return new HackathonListResponse(hackathonList, "Hackathon recuperati con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonListResponse(null, "Errore durante il recupero degli hackathon!");
        }
    }

    @Override
    public HackathonListResponse getHackathonAccettati(int utenteId) {
        String query = """
                SELECT h.hackathon_id, h.titolo, h.descrizione, h.data_inizio, h.data_fine,
                       h.sede, h.max_iscritti, h.max_dimensione_team, h.data_creazione, 
                       h.organizzatore_id, h.data_chiusura_registrazioni,
                       hs.status_name
                FROM hackathon h
                JOIN giudici_hackathon gh ON h.hackathon_id = gh.hackathon_id
                JOIN hackathon_status hs ON h.status_id = hs.status_id
                WHERE gh.giudice_id = ?
                ORDER BY h.data_inizio DESC
                """;

        List<Hackathon> hackathons = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                hackathons.add(mapResultSetToHackathon(rs));
            }

            return new HackathonListResponse(hackathons, "Hackathon accettati caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonListResponse(null, "Errore durante il caricamento degli hackathon accettati!");
        }
    }

    public ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        String query = """
                UPDATE hackathon 
                SET status_id = (SELECT status_id FROM hackathon_status WHERE status_name = ?)
                WHERE hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nuovoStato.name());
            ps.setInt(2, hackathonId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Stato hackathon cambiato con successo!");
            } else {
                return new ResponseResult(false, "Hackathon non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante il cambio di stato dell'hackathon!");
        }
    }

    private Hackathon mapResultSetToHackathon(ResultSet rs) throws SQLException {
        Hackathon hackathon = new Hackathon(rs.getString("titolo"), rs.getString("descrizione"), rs.getString("sede"), rs.getTimestamp("data_inizio").toLocalDateTime(), rs.getTimestamp("data_fine").toLocalDateTime(), rs.getInt("max_iscritti"), rs.getInt("max_dimensione_team"));
        hackathon.setHackathonId(rs.getInt("hackathon_id"));
        hackathon.setStatus(HackathonStatus.valueOf(rs.getString("status_name")));
        hackathon.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        return hackathon;
    }
}