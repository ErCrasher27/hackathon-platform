package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.InvitoGiudiceResponse;
import it.unina.hackathon.utils.responses.InvitoGiudiceListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoGiudiceDAO {

    InvitoGiudiceListResponse getInvitiRicevuti(int utenteId);

    InvitoGiudiceResponse getInvitoByInvitatoHackathon(int utenteId, int hackathonId);

    ResponseResult inviaInvito(int utenteInvitanteId, int utenteInvitatoId, int hackathonId);

    ResponseResult rimuoviInvito(int invitoGiudiceId);

    ResponseResult rispondiInvito(int invitoGiudiceId, StatoInvito risposta);

}
