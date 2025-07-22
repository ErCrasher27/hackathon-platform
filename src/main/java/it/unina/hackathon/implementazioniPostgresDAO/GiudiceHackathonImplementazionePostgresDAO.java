package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.GiudiceHackathonDAO;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.GiudiceHackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public GiudiceHackathonResponse getGiudiceHackathonByUtenteHackathon(int utenteId, int hackathonId) {
        String query = """
                SELECT giudice_hackathon_id, hackathon_fk_hackathons, giudice_fk_utenti, data_assegnazione
                FROM giudici_hackathon
                WHERE giudice_fk_utenti = ? AND hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, utenteId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new GiudiceHackathonResponse(mapToGiudiceHackathon(rs), "Giudice Hackathon caricato con successo!");
            } else {
                return new GiudiceHackathonResponse(null, "Errore durante il caricamento del giudice!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new GiudiceHackathonResponse(null, "Errore durante il caricamento del giudice!");
        }
    }

    @Override
    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        String query = """
                SELECT COUNT(*) 
                FROM giudici_hackathon gh
                WHERE gh.hackathon_fk_hackathons = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio giudici assegnati completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei giudici!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei giudici!");
        }
    }

    private GiudiceHackathon mapToGiudiceHackathon(ResultSet rs) throws SQLException {
        GiudiceHackathon giudice = new GiudiceHackathon();
        giudice.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
        giudice.setUtenteGiudiceId(rs.getInt("giudice_id"));
        giudice.setHackathonId(rs.getInt("hackathon_id"));
        giudice.setDataAssegnazione(rs.getTimestamp("data_assegnazione").toLocalDateTime());
        return giudice;
    }


}