package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface PartecipanteDAO {

    UtenteListResponse getPartecipantiHackathon(int hackathonId);

    ResponseIntResult contaPartecipantiRegistrati(int hackathonId);

    ResponseIntResult contaTeamFormati(int hackathonId);

    ResponseResult registratiAdHackathon(int hackathonId, int partecipanteId);

    ResponseResult annullaRegistrazione(int hackathonId, int partecipanteId);

    UtenteListResponse getPartecipantiSenzaTeam(int hackathonId);
}