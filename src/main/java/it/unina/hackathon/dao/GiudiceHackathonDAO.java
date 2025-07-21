package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.GiudiceHackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

public interface GiudiceHackathonDAO {

    GiudiceHackathonResponse getGiudiceHackathonByUtenteHackathon(int utenteId, int hackathonId);

    ResponseIntResult contaGiudiciAccettati(int hackathonId);

}