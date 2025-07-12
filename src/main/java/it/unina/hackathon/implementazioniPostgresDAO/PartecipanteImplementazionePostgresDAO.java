package it.unina.hackathon.implementazioniPostgresDAO;

import it.unina.hackathon.dao.PartecipanteDAO;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ConnessioneDatabase;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartecipanteImplementazionePostgresDAO implements PartecipanteDAO {
    private Connection connection;

    public PartecipanteImplementazionePostgresDAO() {
        try {
            connection = ConnessioneDatabase.getInstance().connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        String query = """
                SELECT DISTINCT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name, r.data_registrazione as data_reg_hackathon,
                       t.nome as team_nome
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
                LEFT JOIN team t ON r.team_id = t.team_id
                WHERE r.hackathon_id = ?
                ORDER BY r.data_registrazione DESC
                """;

        List<Utente> partecipanti = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = mapResultSetToUtente(rs);
                partecipanti.add(utente);
            }
            return new UtenteListResponse(partecipanti, "Partecipanti caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento dei partecipanti!");
        }
    }

    @Override
    public ResponseIntResult contaPartecipantiRegistrati(int hackathonId) {
        String query = """
                SELECT COUNT(DISTINCT r.utente_id) 
                FROM registrazioni r
                WHERE r.hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio partecipanti completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei partecipanti!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei partecipanti!");
        }
    }

    @Override
    public ResponseIntResult contaTeamFormati(int hackathonId) {
        String query = """
                SELECT COUNT(*) 
                FROM team 
                WHERE hackathon_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return new ResponseIntResult(count, "Conteggio team completato con successo!");
            } else {
                return new ResponseIntResult(-1, "Errore durante il conteggio dei team!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseIntResult(-1, "Errore durante il conteggio dei team!");
        }
    }

    @Override
    public ResponseResult registratiAdHackathon(int hackathonId, int partecipanteId) {
        // Prima verifica se è già registrato
        String checkQuery = """
                SELECT COUNT(*) 
                FROM registrazioni 
                WHERE hackathon_id = ? AND utente_id = ?
                """;

        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            checkPs.setInt(1, hackathonId);
            checkPs.setInt(2, partecipanteId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return new ResponseResult(false, "Utente già registrato a questo hackathon!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la verifica della registrazione!");
        }

        // Procedi con la registrazione
        String insertQuery = """
                INSERT INTO registrazioni (hackathon_id, utente_id, data_registrazione) 
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """;

        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, partecipanteId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Registrazione completata con successo!");
            } else {
                return new ResponseResult(false, "Errore durante la registrazione!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante la registrazione all'hackathon!");
        }
    }

    @Override
    public ResponseResult annullaRegistrazione(int hackathonId, int partecipanteId) {
        String deleteQuery = """
                DELETE FROM registrazioni 
                WHERE hackathon_id = ? AND utente_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, partecipanteId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return new ResponseResult(true, "Registrazione annullata con successo!");
            } else {
                return new ResponseResult(false, "Nessuna registrazione trovata da annullare!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseResult(false, "Errore durante l'annullamento della registrazione!");
        }
    }

    @Override
    public UtenteListResponse getPartecipantiSenzaTeam(int hackathonId) {
        String query = """
                SELECT DISTINCT u.utente_id, u.username, u.email, u.password, u.nome, u.cognome, 
                       u.data_registrazione, ur.role_name, r.data_registrazione as data_reg_hackathon
                FROM registrazioni r
                JOIN utenti u ON r.utente_id = u.utente_id
                JOIN user_roles ur ON u.tipo_utente_id = ur.role_id
                LEFT JOIN membri_team mt ON u.utente_id = mt.utente_id
                LEFT JOIN team t ON mt.team_id = t.team_id AND t.hackathon_id = ?
                WHERE r.hackathon_id = ? AND t.team_id IS NULL
                ORDER BY r.data_registrazione DESC
                """;

        List<Utente> partecipantiSenzaTeam = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, hackathonId);
            ps.setInt(2, hackathonId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Utente utente = mapResultSetToUtente(rs);
                partecipantiSenzaTeam.add(utente);
            }
            return new UtenteListResponse(partecipantiSenzaTeam, "Partecipanti senza team caricati con successo!");
        } catch (SQLException e) {
            e.printStackTrace();
            return new UtenteListResponse(null, "Errore durante il caricamento dei partecipanti senza team!");
        }
    }

    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("nome"), rs.getString("cognome"), TipoUtente.valueOf(rs.getString("role_name")));
        utente.setUtenteId(rs.getInt("utente_id"));
        utente.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        return utente;
    }
}