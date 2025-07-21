package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.MembroTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface MembroTeamDAO {

    MembroTeamListResponse getMembriByTeam(int teamId);

    ResponseResult deleteMembro(int membroTeamId);

    ResponseResult isLeader(int utenteId, int teamId);

}