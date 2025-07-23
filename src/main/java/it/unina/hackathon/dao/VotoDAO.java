package it.unina.hackathon.dao;

import it.unina.hackathon.model.Voto;
import it.unina.hackathon.utils.responses.VotoListResponse;
import it.unina.hackathon.utils.responses.VotoResponse;

public interface VotoDAO {

    /**
     * Salva un nuovo voto nel database.
     *
     * @param voto il voto da salvare
     * @return risposta contenente il voto salvato e lo stato dell'operazione
     */
    VotoResponse saveVoto(Voto voto);

    /**
     * Recupera il voto assegnato da un giudice specifico a un team specifico.
     *
     * @param giudiceHackathonId l'ID del giudice dell'hackathon
     * @param teamId             l'ID del team valutato
     * @return risposta contenente il voto trovato
     */
    VotoResponse getVotoByGiudiceTeamHackathon(int giudiceHackathonId, int teamId);

    /**
     * Recupera la classifica completa di un hackathon basata sui voti ricevuti.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei voti ordinata per classifica
     */
    VotoListResponse getClassificaByHackathon(int hackathonId);

}