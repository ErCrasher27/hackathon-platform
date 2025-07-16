package it.unina.hackathon.dao;

import it.unina.hackathon.model.Problema;
import it.unina.hackathon.utils.responses.ProblemaListResponse;
import it.unina.hackathon.utils.responses.ProblemaResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface ProblemaDAO {

    ProblemaResponse saveProblema(Problema problema);

    ProblemaListResponse getProblemiByHackathon(int hackathonId);

    ResponseResult deleteProblema(int problemaId);

}