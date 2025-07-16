package it.unina.hackathon.dao;

import it.unina.hackathon.model.Commento;
import it.unina.hackathon.utils.responses.CommentoListResponse;
import it.unina.hackathon.utils.responses.CommentoResponse;

public interface CommentoDAO {

    CommentoResponse saveCommento(Commento commento);

    CommentoListResponse getCommentiByProgresso(int progressoId);

}