package it.unina.hackathon.dao;

import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProgressoDAO {

    ProgressoResponse saveProgresso(Progresso progresso);

    ProgressoListResponse getProgressiByTeam(int teamId);

    ResponseResult deleteProgresso(int progressoId);

}