package it.unina.hackathon.dao;

import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.RegistrazioneResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface RegistrazioneDAO {

    RegistrazioneResponse saveRegistrazione(Registrazione registrazione);

    RegistrazioneResponse getRegistrazioneById(int registrazioneId);

    RegistrazioneListResponse getRegistrazioniByHackathon(int hackathonId);

    RegistrazioneListResponse getRegistrazioniByUtente(int utenteId);

    RegistrazioneResponse updateRegistrazione(Registrazione registrazione);

    ResponseResult deleteRegistrazione(int registrazioneId);

    RegistrazioneResponse getRegistrazioneByUtenteHackathon(int utenteId, int hackathonId);

    ResponseResult verificaRegistrazioneEsistente(int utenteId, int hackathonId);

    ResponseResult assegnaTeam(int registrazioneId, int teamId);
}