package it.unina.hackathon.dao;

import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface TeamDAO {

    TeamResponse saveTeam(Team team);

    TeamResponse getTeamByPartecipanteHackathon(int partecipanteId, int hackathonId);

    TeamListResponse getTeamByHackathon(int hackathonId);

    ResponseResult aggiungiMembro(int teamId, int utenteId, RuoloTeam ruoloTeam);

    ResponseResult rimuoviMembro(int teamId, int utenteId);

}