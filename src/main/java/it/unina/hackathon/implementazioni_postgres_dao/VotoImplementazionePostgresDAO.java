package it.unina.hackathon.implementazioni_postgres_dao;

import it.unina.hackathon.dao.VotoDAO;
import it.unina.hackathon.model.ClassificaTeam;
import it.unina.hackathon.model.Voto;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.ClassificaListResponse;
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
                INSERT INTO voti (team_fk_teams, giudice_hack_fk_giudici_hackathon, valore) 
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, voto.getTeamId());
            ps.setInt(2, voto.getGiudiceHackathonId());
            ps.setInt(3, voto.getValore());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    voto.setVotoId(generatedKeys.getInt(1));
                    voto.setDataVoto(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
                }
                return new VotoResponse(voto, "Voto salvato con successo!");
            } else {
                return new VotoResponse(null, "Errore durante il salvataggio del voto!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("uq_voto_per_giudice_team")) {
                return new VotoResponse(null, "Hai già votato questo team!");
            }
            return new VotoResponse(null, "Errore durante il salvataggio del voto: " + e.getMessage());
        }
    }

    @Override
    public VotoResponse getVotoByGiudiceTeamHackathon(int giudiceHackathonId, int teamId) {
        String query = """
                SELECT v.voto_id, v.team_fk_teams, v.giudice_hack_fk_giudici_hackathon, v.valore, v.data_voto
                FROM voti v
                WHERE v.giudice_hack_fk_giudici_hackathon = ? AND v.team_fk_teams = ?
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

    public ClassificaListResponse getClassificaByHackathon(int hackathonId) {
        String query = """
                SELECT 
                    team_id,
                    team,
                    hackathon_id,
                    media_voti,
                    num_voti,
                    num_membri
                FROM v_classifica_hackathons 
                WHERE hackathon_id = ?
                ORDER BY media_voti DESC NULLS LAST, num_voti DESC, team ASC
                """;

        List<ClassificaTeam> classifica = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            int posizione = 1;
            while (rs.next()) {
                ClassificaTeam team = new ClassificaTeam();

                team.setTeamId(rs.getInt("team_id"));
                team.setNomeTeam(rs.getString("team"));
                team.setHackathonId(rs.getInt("hackathon_id"));
                team.setPosizione(posizione++);

                Double mediaVoti = rs.getDouble("media_voti");
                team.setMediaVoti(mediaVoti);

                team.setNumeroVoti(rs.getInt("num_voti"));
                team.setNumeroMembri(rs.getInt("num_membri"));

                classifica.add(team);
            }

            if (classifica.isEmpty()) {
                return new ClassificaListResponse(null, "Nessun team trovato per questo hackathon!");
            }

            return new ClassificaListResponse(classifica, "Classifica caricata con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
            return new ClassificaListResponse(null, "Errore durante il caricamento della classifica: " + e.getMessage());
        }
    }

    private Voto mapResultSetToVoto(ResultSet rs) throws SQLException {
        Voto voto = new Voto();
        voto.setVotoId(rs.getInt("voto_id"));
        voto.setTeamId(rs.getInt("team_fk_teams"));
        voto.setGiudiceHackathonId(rs.getInt("giudice_hack_fk_giudici_hackathon"));
        voto.setValore(rs.getInt("valore"));
        voto.setDataVoto(rs.getTimestamp("data_voto").toLocalDateTime());
        return voto;
    }
}