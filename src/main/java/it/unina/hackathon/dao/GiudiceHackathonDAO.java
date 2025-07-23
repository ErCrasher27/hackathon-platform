package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.GiudiceHackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

public interface GiudiceHackathonDAO {

    /**
     * Recupera l'associazione giudice-hackathon per un giudice e hackathon specifici.
     *
     * @param utenteGiudiceId l'ID dell'utente giudice
     * @param hackathonId     l'ID dell'hackathon
     * @return risposta contenente l'associazione giudice-hackathon trovata
     */
    GiudiceHackathonResponse getGiudiceHackathonByUtenteGiudiceHackathon(int utenteGiudiceId, int hackathonId);

    /**
     * Conta il numero totale di giudici assegnati a un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di giudici dell'hackathon
     */
    ResponseIntResult contaGiudiceHackathonByHackathon(int hackathonId);

}