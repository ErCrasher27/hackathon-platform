package it.unina.hackathon.dao;

import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface HackathonDAO {

    /**
     * Salva un nuovo hackathon nel database.
     *
     * @param hackathon l'hackathon da salvare
     * @return risposta contenente l'hackathon salvato e lo stato dell'operazione
     */
    HackathonResponse saveHackathon(Hackathon hackathon);

    /**
     * Recupera un hackathon tramite il suo ID univoco.
     *
     * @param hackathonId l'ID dell'hackathon da recuperare
     * @return risposta contenente l'hackathon trovato
     */
    HackathonResponse getHackathonById(int hackathonId);

    /**
     * Recupera tutti gli hackathon che hanno uno stato specifico.
     *
     * @param hs lo stato degli hackathon da cercare
     * @return risposta contenente la lista degli hackathon con lo stato specificato
     */
    HackathonListResponse getHackathonsByHackathonStatus(HackathonStatus hs);

    /**
     * Recupera tutti gli hackathon organizzati da un utente specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente organizzatore
     * @return risposta contenente la lista degli hackathon organizzati
     */
    HackathonListResponse getHackathonsByOrganizzatore(int utentePartecipanteId);

    /**
     * Recupera tutti gli hackathon a cui partecipa un utente specifico.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @return risposta contenente la lista degli hackathon a cui partecipa
     */
    HackathonListResponse getHackathonsByPartecipante(int utentePartecipanteId);

    /**
     * Recupera tutti gli hackathon in cui un utente funge da giudice.
     *
     * @param utenteGiudiceId l'ID dell'utente giudice
     * @return risposta contenente la lista degli hackathon da giudicare
     */
    HackathonListResponse getHackathonByGiudice(int utenteGiudiceId);

    /**
     * Modifica lo stato di un hackathon esistente.
     *
     * @param hackathonId l'ID dell'hackathon da modificare
     * @param nuovoStato  il nuovo stato da assegnare all'hackathon
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato);

}