package it.unina.hackathon.dao;

import it.unina.hackathon.utils.ResponseIntResult;
import it.unina.hackathon.utils.TeamListResponse;
import it.unina.hackathon.utils.UtenteListResponse;

public interface PartecipanteDAO {

    UtenteListResponse getPartecipantiHackathon(int hackathonId);

    TeamListResponse getTeamHackathon(int hackathonId);

    ResponseIntResult contaPartecipantiRegistrati(int hackathonId);

    ResponseIntResult contaTeamFormati(int hackathonId);

    UtenteListResponse getMembriTeam(int teamId);
}