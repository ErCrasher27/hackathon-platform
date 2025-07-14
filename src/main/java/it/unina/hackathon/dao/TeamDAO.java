package it.unina.hackathon.dao;

import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface TeamDAO {

    TeamResponse saveTeam(Team team);

    TeamResponse getTeamById(int teamId);

    TeamListResponse getTeamByHackathon(int hackathonId);

    TeamListResponse getTeamDisponibili(int hackathonId, int maxDimensione);

    TeamResponse updateTeam(Team team);

    ResponseResult deleteTeam(int teamId);

    ResponseResult aggiungiMembro(int teamId, int utenteId, RuoloTeam ruoloTeam);

    ResponseResult rimuoviMembro(int teamId, int utenteId);

    ResponseResult rendiDefinitivo(int teamId);

    TeamResponse getTeamByUtente(int utenteId, int hackathonId);

    ResponseResult verificaSpazioDisponibile(int teamId);

    ResponseIntResult contaMembriTeam(int teamId);

    TeamListResponse getTeamHackathon(int hackathonId);
}