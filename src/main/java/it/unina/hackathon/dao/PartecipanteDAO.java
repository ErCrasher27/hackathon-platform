package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

public interface PartecipanteDAO {

    UtenteListResponse getPartecipantiHackathon(int hackathonId);

    TeamListResponse getTeamHackathon(int hackathonId);

    ResponseIntResult contaPartecipantiRegistrati(int hackathonId);

    ResponseIntResult contaTeamFormati(int hackathonId);

    UtenteListResponse getMembriTeam(int teamId);
}