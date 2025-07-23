package it.unina.hackathon.dao;

import it.unina.hackathon.model.Registrazione;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.utils.responses.RegistrazioneListResponse;
import it.unina.hackathon.utils.responses.RegistrazioneResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface RegistrazioneDAO {

    RegistrazioneListResponse getRegistrazioniByTeam(int teamId);

    RegistrazioneListResponse getRegistratiConTeamNullByHackathon(int hackathonId);

    RegistrazioneResponse getRegistrazioneByUtentePartecipanteHackathon(int utentePartecipanteId, int hackathonId);

    RegistrazioneResponse saveRegistrazione(Registrazione registrazione);

    ResponseIntResult contaRegistrazioniByHackathon(int hackathonId);

    ResponseResult aggiornaTeamConRuolo(int registrazioneId, Integer teamId, RuoloTeam ruoloTeam);

    ResponseResult aggiornaTeamNullConRuoloNull(int registrazioneId);

    ResponseResult rimuoviRegistrazione(int registrazioneId);

    ResponseResult isLeaderByUtentePartecipanteTeam(int utentePartecipanteId, int teamId);

}