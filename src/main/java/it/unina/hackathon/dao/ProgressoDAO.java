package it.unina.hackathon.dao;

import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProgressoDAO {

    ProgressoResponse saveProgresso(Progresso progresso);

    ProgressoResponse getProgressoById(int progressoId);

    ProgressoListResponse getProgressiByTeam(int teamId);

    ProgressoListResponse getProgressiByHackathon(int hackathonId);

    ProgressoResponse updateProgresso(Progresso progresso);

    ResponseResult deleteProgresso(int progressoId);

    ProgressoListResponse getProgressiDaValutare(int giudiceId, int hackathonId);

    ProgressoListResponse getProgressiRecenti(int hackathonId, int giorni);
}