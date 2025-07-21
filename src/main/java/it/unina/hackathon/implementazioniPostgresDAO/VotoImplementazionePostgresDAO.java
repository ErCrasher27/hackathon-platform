package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.VotoDAO;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.Voto;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.VotoListResponse;
import it.unina.hackathon.utils.responses.VotoResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VotoImplementazionePostgresDAO implements VotoDAO {
    private Connection connection;

    public VotoImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public VotoResponse saveVoto(Voto voto) {
        String query = """
                INSERT INTO voti (team_id, giudice_hackathon_id, valore, data_voto) 
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, voto.getTeamId());
            ps.setInt(2, voto.getGiudiceId());
            ps.setInt(3, voto.getValore());
            ps.setTimestamp(4, Timestamp.valueOf(voto.getDataVoto()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    voto.setVotoId(generatedKeys.getInt(1));
                }
                return new VotoResponse(voto, "Voto salvato con successo!");
            } else {
                return new VotoResponse(null, "Errore durante il salvataggio del voto!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoResponse(null, "Errore durante il salvataggio del voto: " + e.getMessage());
        }
    }

    @Override
    public VotoResponse getVotoByGiudiceTeamHackathon(int giudiceHackathonId, int teamId) {
        String query = """
                SELECT v.voto_id, v.team_id, v.giudice_hackathon_id, v.valore, v.data_voto,
                       u.nome, u.cognome, u.username, u.email, u.utente_id,
                       t.nome as team_nome,
                       gh.giudice_hackathon_id
                FROM voti v
                JOIN giudici_hackathon gh ON v.giudice_hackathon_id = gh.giudice_hackathon_id
                JOIN utenti u ON gh.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.giudice_hackathon_id = ? AND v.team_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceHackathonId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Voto voto = mapResultSetToVoto(rs);
                return new VotoResponse(voto, "Voto trovato con successo!");
            } else {
                return new VotoResponse(null, "Voto non ancora assegnato per questo team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoResponse(null, "Errore durante la ricerca del voto!");
        }
    }

    public VotoListResponse getClassificaByHackathon(int hackathonId) {
        String query = """
                SELECT 
                    v.team_id,
                    t.nome as team_nome,
                    AVG(v.valore) as media_voti,
                    COUNT(v.voto_id) as numero_voti
                FROM voti v
                JOIN team t ON v.team_id = t.team_id
                WHERE t.hackathon_id = ?
                GROUP BY v.team_id, t.nome
                ORDER BY media_voti DESC, numero_voti DESC, t.nome ASC
                """;

        List<Voto> classifica = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            int posizione = 1;
            while (rs.next()) {
                Voto votoClassifica = new Voto();

                // Dati base
                votoClassifica.setTeamId(rs.getInt("team_id"));

                // Dati specifici della classifica
                votoClassifica.setPosizione(posizione++);
                votoClassifica.setMediaVoti(rs.getDouble("media_voti"));
                votoClassifica.setNumeroVoti(rs.getInt("numero_voti"));

                // Team
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setNome(rs.getString("team_nome"));
                votoClassifica.setTeam(team);

                classifica.add(votoClassifica);
            }

            if (classifica.isEmpty()) {
                return new VotoListResponse(null, "Nessun voto trovato per questo hackathon!");
            }

            return new VotoListResponse(classifica, "Classifica caricata con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoListResponse(null, "Errore durante il caricamento della classifica: " + e.getMessage());
        }
    }

    private Voto mapResultSetToVoto(ResultSet rs) throws SQLException {
        Voto voto = new Voto();
        voto.setVotoId(rs.getInt("voto_id"));
        voto.setTeamId(rs.getInt("team_id"));
        voto.setGiudiceId(rs.getInt("giudice_hackathon_id"));
        voto.setValore(rs.getInt("valore"));
        voto.setDataVoto(rs.getTimestamp("data_voto").toLocalDateTime());

        // Mappa il giudice
        Utente giudiceUtente = new Utente(rs.getString("username"), rs.getString("email"), "", rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        giudiceUtente.setUtenteId(rs.getInt("utente_id"));

        GiudiceHackathon giudiceHackathon = new GiudiceHackathon();
        giudiceHackathon.setGiudiceHackathonId(rs.getInt("giudice_hackathon_id"));
        giudiceHackathon.setGiudice(giudiceUtente);
        voto.setGiudice(giudiceHackathon);

        return voto;
    }

}