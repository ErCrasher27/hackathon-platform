package it.unina.hackathon.dao;

import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface TeamDAO {

    TeamResponse saveTeam(Team team);

    TeamResponse getTeamByPartecipanteHackathon(int utenteId, int hackathonId);

    TeamResponse getTeamByMembroTeam(int membroTeamId);

    TeamListResponse getTeamByHackathon(int hackathonId);

    ResponseIntResult contaNumeroMembri(int teamId);

    ResponseIntResult contaTeamFormati(int hackathonId);

    ResponseResult aggiungiMembro(int teamId, int utenteId, RuoloTeam ruoloTeam);

    ResponseResult rimuoviMembro(int utenteId, int teamId);

}