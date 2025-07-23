package it.unina.hackathon.dao;

import it.unina.hackathon.model.Commento;
import it.unina.hackathon.utils.responses.CommentoListResponse;
import it.unina.hackathon.utils.responses.CommentoResponse;

public interface CommentoDAO {

    /**
     * Salva un nuovo commento nel database.
     *
     * @param commento il commento da salvare
     * @return risposta contenente il commento salvato e lo stato dell'operazione
     */
    CommentoResponse saveCommento(Commento commento);

    /**
     * Recupera tutti i commenti associati a un determinato progresso.
     *
     * @param progressoId l'ID del progresso per cui recuperare i commenti
     * @return risposta contenente la lista dei commenti trovati
     */
    CommentoListResponse getCommentiByProgresso(int progressoId);

}