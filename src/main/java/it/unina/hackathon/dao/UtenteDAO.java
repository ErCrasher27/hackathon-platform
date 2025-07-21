package it.unina.hackathon.dao;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.UtenteResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface UtenteDAO {

    UtenteListResponse getPartecipantiByHackathon(int hackathonId);

    UtenteListResponse getPartecipantiSenzaTeam(int hackathonId);

    UtenteListResponse getGiudiciNonInvitati(int hackathonId);

    UtenteListResponse getGiudiciInvitati(int hackathonId);

    UtenteResponse saveUtente(Utente utente);

    UtenteResponse findByUsername(String username);

    ResponseIntResult contaPartecipantiRegistrati(int hackathonId);

    ResponseResult usernameExists(String username);

    ResponseResult emailExists(String email);

    ResponseResult registratiAdHackathon(int userId, int hackathonId);

    ResponseResult annullaRegistrazione(int userId, int hackathonId);

}