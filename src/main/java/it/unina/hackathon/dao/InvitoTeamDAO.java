package it.unina.hackathon.dao;

import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.InvitoTeamListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface InvitoTeamDAO {

    InvitoTeamListResponse getInvitiTeamByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    ResponseResult saveInvitoUtente(int registrazioneInvitanteId, int utentePartecipanteInvitatoId, String messaggio);

    ResponseResult aggiornaStatoInvito(int invitoTeamId, StatoInvito risposta);

}