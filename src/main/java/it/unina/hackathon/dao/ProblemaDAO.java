package it.unina.hackathon.dao;

import it.unina.hackathon.model.Problema;
import it.unina.hackathon.utils.responses.ProblemaListResponse;
import it.unina.hackathon.utils.responses.ProblemaResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProblemaDAO {

    ProblemaResponse saveProblema(Problema problema);

    ProblemaResponse getProblemaById(int problemaId);

    ProblemaListResponse getProblemiByHackathon(int hackathonId);

    ProblemaListResponse getProblemiByGiudice(int giudiceId);

    ProblemaResponse updateProblema(Problema problema);

    ResponseResult deleteProblema(int problemaId);

    ProblemaResponse getProblemaAttivoByHackathon(int hackathonId);
}