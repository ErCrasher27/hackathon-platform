package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.responses.GiudiceHackathonResponse;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface GiudiceHackathonDAO {

    UtenteListResponse getGiudiciNonInvitati(int hackathonId);

    GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId);

    ResponseResult invitaGiudice(int hackathonId, int giudiceId, int invitatoDa);

    ResponseResult rimuoviInvito(int hackathonId, int giudiceId);

    ResponseIntResult contaGiudiciAccettati(int hackathonId);

    GiudiceHackathonListResponse getInvitiRicevuti(int giudiceId);

    GiudiceHackathonListResponse getInvitiPending(int giudiceId);

    ResponseResult rispondiInvito(int giudiceHackathonId, StatoInvito risposta);

    ResponseResult accettaInvito(int giudiceHackathonId);

    ResponseResult rifiutaInvito(int giudiceHackathonId);

    GiudiceHackathonResponse getStatoInvito(int hackathonId, int giudiceId);

    ResponseResult verificaPermessiValutazione(int hackathonId, int giudiceId);

}