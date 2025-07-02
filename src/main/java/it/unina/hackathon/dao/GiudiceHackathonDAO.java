package it.unina.hackathon.dao;

import it.unina.hackathon.utils.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.ResponseIntResult;
import it.unina.hackathon.utils.ResponseResult;
import it.unina.hackathon.utils.UtenteListResponse;

public interface GiudiceHackathonDAO {

    UtenteListResponse getGiudiciNonInvitati(int hackathonId);

    GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId);

    ResponseResult invitaGiudice(int hackathonId, int giudiceId, int invitatoDa);

    ResponseResult rimuoviInvito(int hackathonId, int giudiceId);

    ResponseIntResult contaGiudiciAccettati(int hackathonId);
}