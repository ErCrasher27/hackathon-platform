package it.unina.hackathon.dao;

import it.unina.hackathon.model.Team;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

public interface TeamDAO {

    TeamResponse saveTeam(Team team);

    TeamResponse getTeamByPartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    TeamResponse getTeamByRegistrazione(int registrazioneId);

    TeamListResponse getTeamsByHackathon(int hackathonId);

    ResponseIntResult contaRegistrazioniByTeam(int teamId);

    ResponseIntResult contaTeamByHackathon(int hackathonId);

}