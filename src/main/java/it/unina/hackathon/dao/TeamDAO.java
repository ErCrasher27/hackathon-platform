package it.unina.hackathon.dao;

import it.unina.hackathon.model.Team;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

public interface TeamDAO {

    /**
     * Salva un nuovo team nel database.
     *
     * @param team il team da salvare
     * @return risposta contenente il team salvato e lo stato dell'operazione
     */
    TeamResponse saveTeam(Team team);

    /**
     * Recupera il team di cui fa parte un partecipante in un hackathon specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param hackathonId          l'ID dell'hackathon
     * @return risposta contenente il team trovato
     */
    TeamResponse getTeamByPartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    /**
     * Recupera il team associato a una registrazione specifica.
     *
     * @param registrazioneId l'ID della registrazione
     * @return risposta contenente il team trovato
     */
    TeamResponse getTeamByRegistrazione(int registrazioneId);

    /**
     * Recupera tutti i team che partecipano a un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei team
     */
    TeamListResponse getTeamsByHackathon(int hackathonId);

    /**
     * Conta il numero di registrazioni (membri) di un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente il numero di membri del team
     */
    ResponseIntResult contaRegistrazioniByTeam(int teamId);

    /**
     * Conta il numero totale di team in un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di team
     */
    ResponseIntResult contaTeamByHackathon(int hackathonId);

}