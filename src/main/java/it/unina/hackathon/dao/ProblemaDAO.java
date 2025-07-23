package it.unina.hackathon.dao;

import it.unina.hackathon.model.Problema;
import it.unina.hackathon.utils.responses.ProblemaListResponse;
import it.unina.hackathon.utils.responses.ProblemaResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProblemaDAO {

    /**
     * Salva un nuovo problema nel database.
     *
     * @param problema il problema da salvare
     * @return risposta contenente il problema salvato e lo stato dell'operazione
     */
    ProblemaResponse saveProblema(Problema problema);

    /**
     * Recupera tutti i problemi associati a un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei problemi dell'hackathon
     */
    ProblemaListResponse getProblemiByHackathon(int hackathonId);

    /**
     * Rimuove un problema dal database.
     *
     * @param problemaId l'ID del problema da rimuovere
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult rimuoviProblema(int problemaId);

}