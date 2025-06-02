package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.HackathonDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.HackathonResponse;

import java.sql.*;

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HackathonResponse(null, "Errore durante la creazione dell'Hackathon: " + e.getMessage());
        }
        return new HackathonResponse(null, "Errore durante la creazione dell'Hackathon!");
    }

    private Hackathon mapResultSetToHackathon(ResultSet rs) throws SQLException {
        Hackathon hackathon = new Hackathon(rs.getString("titolo"), rs.getString("descrizione"), rs.getString("sede"), rs.getTimestamp("data_inizio").toLocalDateTime(), rs.getTimestamp("data_fine").toLocalDateTime(), rs.getInt("max_iscritti"), rs.getInt("max_dimensione_team"));
        hackathon.setHackathonId(rs.getInt("hackathon_id"));
        hackathon.setStatus(HackathonStatus.valueOf(rs.getString("status_name")));
        hackathon.setDataCreazione(rs.getTimestamp("data_creazione").toLocalDateTime());
        return hackathon;
    }
}