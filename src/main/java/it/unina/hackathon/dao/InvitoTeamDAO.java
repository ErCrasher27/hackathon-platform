package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoTeamDAO {

    InvitoTeamListResponse getInvitiRicevuti(int partecipanteId);

    ResponseResult inviaInvito(int teamId, int utenteIdDaInvitare, int invitanteId, String messaggio);

    ResponseResult rispondiInvito(int invitoId, StatoInvito risposta);

}