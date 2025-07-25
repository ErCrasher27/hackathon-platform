package it.unina.hackathon.dao;

import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProgressoDAO {

    /**
     * Salva un nuovo progresso nel database.
     *
     * @param progresso il progresso da salvare
     * @return risposta contenente il progresso salvato e lo stato dell'operazione
     */
    ProgressoResponse saveProgresso(Progresso progresso);

    /**
     * Recupera tutti i progressi registrati da un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente la lista dei progressi del team
     */
    ProgressoListResponse getProgressiByTeam(int teamId);

    /**
     * Rimuove un progresso dal database.
     *
     * @param progressoId l'ID del progresso da rimuovere
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    ResponseResult rimuoviProgresso(int progressoId);

}