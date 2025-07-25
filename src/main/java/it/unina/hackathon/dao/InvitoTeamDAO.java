package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoTeamDAO {

    /**
     * Recupera tutti gli inviti ricevuti da un utente per un hackathon specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param hackathonId          l'ID dell'hackathon
     * @return risposta contenente la lista degli inviti ricevuti
     */
    InvitoTeamListResponse getInvitiTeamByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    /**
     * Crea un nuovo invito per unirsi a un team.
     *
     * @param registrazioneInvitanteId     l'ID della registrazione di chi invia l'invito
     * @param utentePartecipanteInvitatoId l'ID dell'utente da invitare
     * @param messaggio                    messaggio personalizzato dell'invito
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult saveInvitoUtente(int registrazioneInvitanteId, int utentePartecipanteInvitatoId, String messaggio);

    /**
     * Aggiorna lo stato di un invito (accettato, rifiutato, ecc.).
     *
     * @param invitoTeamId l'ID dell'invito da aggiornare
     * @param risposta     il nuovo stato dell'invito
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult aggiornaStatoInvito(int invitoTeamId, StatoInvito risposta);

}