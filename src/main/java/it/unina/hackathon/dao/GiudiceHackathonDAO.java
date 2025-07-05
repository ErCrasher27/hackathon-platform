package it.unina.hackathon.dao;

import it.unina.hackathon.utils.responses.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface GiudiceHackathonDAO {

    UtenteListResponse getGiudiciNonInvitati(int hackathonId);

    GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId);

    ResponseResult invitaGiudice(int hackathonId, int giudiceId, int invitatoDa);

    ResponseResult rimuoviInvito(int hackathonId, int giudiceId);

    ResponseIntResult contaGiudiciAccettati(int hackathonId);
}