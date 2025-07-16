package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoTeamDAO {
    ResponseResult inviaRichiesta(int teamId, int partecipanteId);

    ResponseResult inviaInvito(int teamId, int utenteIdDaInvitare, int invitanteId, String messaggio);

    InvitoTeamListResponse getInvitiRicevuti(int partecipanteId);

    InvitoTeamListResponse getRichiesteIngressoTeam(int teamId);

    ResponseResult rispondiInvito(int invitoId, StatoInvito risposta);

    InvitoTeamListResponse getInvitiInviati(int partecipanteId);

    ResponseResult annullaInvito(int invitoId);
}