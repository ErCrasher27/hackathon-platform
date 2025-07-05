package it.unina.hackathon.dao;

import it.unina.hackathon.model.Commento;
import it.unina.hackathon.utils.responses.CommentoListResponse;
import it.unina.hackathon.utils.responses.CommentoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface CommentoDAO {

    CommentoResponse saveCommento(Commento commento);

    CommentoResponse getCommentoById(int commentoId);

    CommentoListResponse getCommentiByProgresso(int progressoId);

    CommentoListResponse getCommentiByGiudice(int giudiceId);

    CommentoResponse updateCommento(Commento commento);

    ResponseResult deleteCommento(int commentoId);

    CommentoListResponse getCommentiRecenti(int hackathonId, int giorni);
}