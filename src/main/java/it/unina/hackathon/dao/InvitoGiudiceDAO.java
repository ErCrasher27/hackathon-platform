package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoGiudiceListResponse;
import it.unina.hackathon.utils.responses.InvitoGiudiceResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoGiudiceDAO {

    /**
     * Recupera tutti gli inviti ricevuti da un giudice specifico.
     *
     * @param utenteGiudiceId l'ID dell'utente giudice
     * @return risposta contenente la lista degli inviti ricevuti dal giudice
     */
    InvitoGiudiceListResponse getInvitiGiudiceByUtenteGiudice(int utenteGiudiceId);

    /**
     * Recupera l'invito specifico di un giudice per un hackathon.
     *
     * @param utenteGiudiceId l'ID dell'utente giudice
     * @param hackathonId     l'ID dell'hackathon
     * @return risposta contenente l'invito trovato
     */
    InvitoGiudiceResponse getInvitoGiudiceByUtenteGiudiceHackathon(int utenteGiudiceId, int hackathonId);

    /**
     * Crea un nuovo invito per un giudice a partecipare a un hackathon.
     *
     * @param utenteOrganizzatoreInvitanteId l'ID dell'organizzatore che invia l'invito
     * @param utenteGiudiceInvitatoId        l'ID del giudice da invitare
     * @param hackathonId                    l'ID dell'hackathon per cui si invia l'invito
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult saveInvitoGiudice(int utenteOrganizzatoreInvitanteId, int utenteGiudiceInvitatoId, int hackathonId);

    /**
     * Aggiorna lo stato di un invito giudice (accettato, rifiutato, ecc.).
     *
     * @param invitoGiudiceId l'ID dell'invito da aggiornare
     * @param risposta        il nuovo stato dell'invito
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult aggiornaStatoInvito(int invitoGiudiceId, StatoInvito risposta);

}