package it.unina.hackathon.dao;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.UtenteResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface UtenteDAO {

    UtenteListResponse getUtentiGiudiciNonInvitati(int hackathonId);

    UtenteListResponse getUtentiGiudiciInvitati(int hackathonId);

    UtenteResponse saveUtente(Utente utente);

    UtenteResponse findByUsername(String username);

    ResponseResult usernameExists(String username);

    ResponseResult emailExists(String email);

}