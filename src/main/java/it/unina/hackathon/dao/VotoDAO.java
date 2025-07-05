package it.unina.hackathon.dao;

import it.unina.hackathon.model.Voto;
import it.unina.hackathon.utils.responses.VotoListResponse;
import it.unina.hackathon.utils.responses.VotoResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface VotoDAO {

    VotoResponse saveVoto(Voto voto);

    VotoResponse getVotoById(int votoId);

    VotoListResponse getVotiByTeam(int teamId, int hackathonId);

    VotoListResponse getVotiByGiudice(int giudiceId, int hackathonId);

    VotoListResponse getVotiByHackathon(int hackathonId);

    VotoResponse updateVoto(Voto voto);

    ResponseResult deleteVoto(int votoId);

    VotoResponse getVotoByGiudiceTeam(int giudiceId, int teamId, int hackathonId);

    ResponseResult verificaVotoEsistente(int giudiceId, int teamId, int hackathonId);
}