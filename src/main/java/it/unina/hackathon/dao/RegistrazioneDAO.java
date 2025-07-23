package it.unina.hackathon.dao;

import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.RegistrazioneResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface RegistrazioneDAO {

    /**
     * Recupera tutte le registrazioni associate a un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente la lista delle registrazioni del team
     */
    RegistrazioneListResponse getRegistrazioniByTeam(int teamId);

    /**
     * Recupera tutte le registrazioni di un hackathon che non sono ancora
     * assegnate a nessun team.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista delle registrazioni senza team
     */
    RegistrazioneListResponse getRegistratiConTeamNullByHackathon(int hackathonId);

    /**
     * Recupera la registrazione di un utente per un hackathon specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param hackathonId          l'ID dell'hackathon
     * @return risposta contenente la registrazione trovata
     */
    RegistrazioneResponse getRegistrazioneByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    /**
     * Salva una nuova registrazione nel database.
     *
     * @param registrazione la registrazione da salvare
     * @return risposta contenente la registrazione salvata e lo stato dell'operazione
     */
    RegistrazioneResponse saveRegistrazione(Registrazione registrazione);

    /**
     * Conta il numero di registrazioni per un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di registrazioni
     */
    ResponseIntResult contaRegistrazioniByHackathon(int hackathonId);

    /**
     * Aggiorna l'assegnazione di una registrazione a un team con un ruolo specifico.
     *
     * @param registrazioneId l'ID della registrazione
     * @param teamId          l'ID del team da assegnare
     * @param ruoloTeam       il ruolo da assegnare nel team
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult aggiornaTeamConRuolo(int registrazioneId, Integer teamId, RuoloTeam ruoloTeam);

    /**
     * Rimuove l'assegnazione di una registrazione da un team,
     * azzerando team e ruolo.
     *
     * @param registrazioneId l'ID della registrazione
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult aggiornaTeamNullConRuoloNull(int registrazioneId);

    /**
     * Rimuove completamente una registrazione dal database.
     *
     * @param registrazioneId l'ID della registrazione da rimuovere
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult rimuoviRegistrazione(int registrazioneId);

    /**
     * Verifica se un utente è il leader di un team specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param teamId               l'ID del team
     * @return risposta indicante se l'utente è leader del team
     */
    ResponseResult isLeaderByUtentePartecipanteTeam(int utentePartecipanteId, int teamId);

}