package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface GiudiceHackathonDAO {

    GiudiceHackathonListResponse getGiudiciNonInvitati(int hackathonId);

    GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId);

    GiudiceHackathonListResponse getInvitiRicevuti(int giudiceId);

    ResponseIntResult contaGiudiciAccettati(int hackathonId);

    ResponseResult invitaGiudice(int hackathonId, int giudiceId, int invitatoDa);

    ResponseResult rimuoviInvito(int hackathonId, int giudiceId);

    ResponseResult rispondiInvito(int giudiceHackathonId, StatoInvito risposta);

}