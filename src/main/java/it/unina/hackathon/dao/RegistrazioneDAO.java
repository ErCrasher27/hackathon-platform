package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface RegistrazioneDAO {

    RegistrazioneListResponse getRegistrazioniByTeam(int teamId);

    ResponseResult deleteRegistrazione(int registrazioneId);

    ResponseResult isLeader(int utentePartecipanteId, int teamId);

}