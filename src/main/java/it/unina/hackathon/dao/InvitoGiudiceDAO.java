package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoGiudiceListResponse;
import it.unina.hackathon.utils.responses.InvitoGiudiceResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoGiudiceDAO {

    InvitoGiudiceListResponse getInvitiGiudiceByUtenteGiudice(int utenteGiudiceId);

    InvitoGiudiceResponse getInvitoGiudiceByUtenteGiudiceHackathon(int utenteGiudiceId, int hackathonId);

    ResponseResult saveInvitoGiudice(int utenteOrganizzatoreInvitanteId, int utenteGiudiceInvitatoId, int hackathonId);

    ResponseResult aggiornaStatoInvito(int invitoGiudiceId, StatoInvito risposta);

}
