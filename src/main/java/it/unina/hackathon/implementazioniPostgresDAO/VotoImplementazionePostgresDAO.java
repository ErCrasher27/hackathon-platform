package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.VotoDAO;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.Voto;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.VotoListResponse;
import it.unina.hackathon.utils.responses.VotoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

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
                INSERT INTO voti (hackathon_id, team_id, giudice_id, valore, criteri_valutazione, data_voto) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, voto.getHackathonId());
            ps.setInt(2, voto.getTeamId());
            ps.setInt(3, voto.getGiudiceId());
            ps.setInt(4, voto.getValore());
            ps.setString(5, voto.getCriteriValutazione());
            ps.setTimestamp(6, Timestamp.valueOf(voto.getDataVoto()));

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
    public VotoResponse getVotoById(int votoId) {
        String query = """
                SELECT v.voto_id, v.hackathon_id, v.team_id, v.giudice_id, v.valore, 
                       v.criteri_valutazione, v.data_voto,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM voti v
                JOIN utenti u ON v.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.voto_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, votoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Voto voto = mapResultSetToVoto(rs);
                return new VotoResponse(voto, "Voto trovato con successo!");
            } else {
                return new VotoResponse(null, "Voto non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoResponse(null, "Errore durante la ricerca del voto!");
        }
    }

    @Override
    public VotoListResponse getVotiByTeam(int teamId) {
        String query = """
                SELECT v.voto_id, v.hackathon_id, v.team_id, v.giudice_id, v.valore, 
                       v.criteri_valutazione, v.data_voto,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM voti v
                JOIN utenti u ON v.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.team_id = ?
                ORDER BY v.data_voto DESC
                """;

        List<Voto> voti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                voti.add(mapResultSetToVoto(rs));
            }
            return new VotoListResponse(voti, "Voti del team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoListResponse(null, "Errore durante il caricamento dei voti del team!");
        }
    }

    @Override
    public VotoListResponse getVotiByGiudice(int giudiceId, int hackathonId) {
        String query = """
                SELECT v.voto_id, v.hackathon_id, v.team_id, v.giudice_id, v.valore, 
                       v.criteri_valutazione, v.data_voto,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM voti v
                JOIN utenti u ON v.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.giudice_id = ? AND v.hackathon_id = ?
                ORDER BY v.data_voto DESC
                """;

        List<Voto> voti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                voti.add(mapResultSetToVoto(rs));
            }
            return new VotoListResponse(voti, "Voti del giudice caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoListResponse(null, "Errore durante il caricamento dei voti del giudice!");
        }
    }

    @Override
    public VotoListResponse getVotiByHackathon(int hackathonId) {
        String query = """
                SELECT v.voto_id, v.hackathon_id, v.team_id, v.giudice_id, v.valore, 
                       v.criteri_valutazione, v.data_voto,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome,
                       AVG(v.valore) OVER (PARTITION BY v.team_id) as media_team
                FROM voti v
                JOIN utenti u ON v.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.hackathon_id = ?
                ORDER BY media_team DESC, v.team_id, v.data_voto DESC
                """;

        List<Voto> voti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                voti.add(mapResultSetToVoto(rs));
            }
            return new VotoListResponse(voti, "Voti dell'hackathon caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoListResponse(null, "Errore durante il caricamento dei voti dell'hackathon!");
        }
    }

    @Override
    public VotoResponse updateVoto(Voto voto) {
        String query = """
                UPDATE voti 
                SET valore = ?, criteri_valutazione = ?, data_voto = ?
                WHERE voto_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, voto.getValore());
            ps.setString(2, voto.getCriteriValutazione());
            ps.setTimestamp(3, Timestamp.valueOf(voto.getDataVoto()));
            ps.setInt(4, voto.getVotoId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new VotoResponse(voto, "Voto aggiornato con successo!");
            } else {
                return new VotoResponse(null, "Voto non trovato per l'aggiornamento!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new VotoResponse(null, "Errore durante l'aggiornamento del voto!");
        }
    }

    @Override
    public ResponseResult deleteVoto(int votoId) {
        String query = "DELETE FROM voti WHERE voto_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, votoId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Voto eliminato con successo!");
            } else {
                return new ResponseResult(false, "Voto non trovato!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'eliminazione del voto!");
        }
    }

    @Override
    public VotoResponse getVotoByGiudiceTeam(int giudiceId, int teamId, int hackathonId) {
        String query = """
                SELECT v.voto_id, v.hackathon_id, v.team_id, v.giudice_id, v.valore, 
                       v.criteri_valutazione, v.data_voto,
                       u.nome, u.cognome, u.username, u.email,
                       t.nome as team_nome
                FROM voti v
                JOIN utenti u ON v.giudice_id = u.utente_id
                JOIN team t ON v.team_id = t.team_id
                WHERE v.giudice_id = ? AND v.team_id = ? AND v.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ps.setInt(2, teamId);
            ps.setInt(3, hackathonId);
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

    @Override
    public ResponseResult verificaVotoEsistente(int giudiceId, int teamId, int hackathonId) {
        String query = "SELECT COUNT(*) FROM voti WHERE giudice_id = ? AND team_id = ? AND hackathon_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, giudiceId);
            ps.setInt(2, teamId);
            ps.setInt(3, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                if (exists) {
                    return new ResponseResult(true, "Voto già assegnato a questo team!");
                } else {
                    return new ResponseResult(false, "Voto non ancora assegnato!");
                }
            } else {
                return new ResponseResult(false, "Errore durante la verifica!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica del voto!");
        }
    }

    @Override
    public VotoListResponse getClassificaByHackathon(int hackathonId) {
        String query = """
                SELECT 
                    v.team_id,
                    t.nome as team_nome,
                    AVG(v.valore) as media_voti,
                    COUNT(v.voto_id) as numero_voti,
                    MIN(v.hackathon_id) as hackathon_id,
                    MIN(v.data_voto) as prima_valutazione,
                    MAX(v.data_voto) as ultima_valutazione
                FROM voti v
                JOIN team t ON v.team_id = t.team_id
                WHERE v.hackathon_id = ?
                GROUP BY v.team_id, t.nome
                ORDER BY media_voti DESC, numero_voti DESC, t.nome ASC
                """;

        List<Voto> classifica = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            int posizione = 1;
            while (rs.next()) {
                // Creo un oggetto Voto "virtuale" che rappresenta la classifica del team
                Voto votoClassifica = new Voto();

                // Imposto i dati del team
                votoClassifica.setTeamId(rs.getInt("team_id"));
                votoClassifica.setHackathonId(rs.getInt("hackathon_id"));

                // Uso il campo valore per memorizzare la media (arrotondata)
                votoClassifica.setValore((int) Math.round(rs.getDouble("media_voti")));

                // Uso il campo criteri_valutazione per memorizzare info della classifica
                String infoClassifica = String.format("Posizione: %d | Media: %.2f | Voti ricevuti: %d", posizione++, rs.getDouble("media_voti"), rs.getInt("numero_voti"));
                votoClassifica.setCriteriValutazione(infoClassifica);

                votoClassifica.setDataVoto(rs.getTimestamp("ultima_valutazione").toLocalDateTime());

                // Creo e imposto il team
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setNome(rs.getString("team_nome"));
                votoClassifica.setTeam(team);

                // Non imposto il giudice per la classifica (è un dato aggregato)

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
        voto.setHackathonId(rs.getInt("hackathon_id"));
        voto.setTeamId(rs.getInt("team_id"));
        voto.setGiudiceId(rs.getInt("giudice_id"));
        voto.setValore(rs.getInt("valore"));
        voto.setCriteriValutazione(rs.getString("criteri_valutazione"));
        voto.setDataVoto(rs.getTimestamp("data_voto").toLocalDateTime());

        // Mappa il giudice
        Utente giudice = new Utente(rs.getString("username"), rs.getString("email"), "", // Password non esposta
                rs.getString("nome"), rs.getString("cognome"), TipoUtente.GIUDICE);
        giudice.setUtenteId(rs.getInt("giudice_id"));
        voto.setGiudice(giudice);

        // Mappa il team
        Team team = new Team();
        team.setTeamId(rs.getInt("team_id"));
        team.setNome(rs.getString("team_nome"));
        voto.setTeam(team);

        return voto;
    }
}