package it.unina.hackathon.dao;

import it.unina.hackathon.model.Voto;
import it.unina.hackathon.utils.responses.VotoListResponse;
import it.unina.hackathon.utils.responses.VotoResponse;

public interface VotoDAO {

    VotoResponse saveVoto(Voto voto);

    VotoResponse getVotoByGiudiceTeamHackathon(int giudiceHackathonId, int teamId);

    VotoListResponse getClassificaByHackathon(int hackathonId);

}