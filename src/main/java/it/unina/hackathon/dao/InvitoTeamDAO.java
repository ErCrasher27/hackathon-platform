package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoTeamDAO {

    InvitoTeamListResponse getInvitiRicevuti(int utenteId, int hackathonId);

    ResponseResult inviaInvito(int membroTeamInvitanteId, int utenteInvitatoId, String messaggio);

    ResponseResult rispondiInvito(int invitoTeamId, StatoInvito risposta);

}