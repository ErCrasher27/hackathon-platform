package it.unina.hackathon.dao;

import it.unina.hackathon.model.MembroTeam;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.MembroTeamListResponse;
import it.unina.hackathon.utils.responses.MembroTeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface MembroTeamDAO {

    MembroTeamResponse saveMembro(MembroTeam membro);

    MembroTeamResponse getMembroById(int membroId);

    MembroTeamListResponse getMembriByTeam(int teamId);

    MembroTeamListResponse getTeamByUtente(int utenteId);

    MembroTeamResponse updateMembro(MembroTeam membro);

    ResponseResult deleteMembro(int membroId);

    ResponseResult cambiaRuolo(int membroId, RuoloTeam nuovoRuolo);

    MembroTeamResponse getMembroByUtenteTeam(int utenteId, int teamId);

    ResponseResult verificaMembroEsistente(int utenteId, int teamId);

    ResponseResult isLeader(int utenteId, int teamId);

    MembroTeamResponse getLeaderByTeam(int teamId);
}