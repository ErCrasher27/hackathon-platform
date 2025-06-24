package it.unina.hackathon.dao;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.ResponseResult;
import it.unina.hackathon.utils.UtenteResponse;

public interface UtenteDAO {
    UtenteResponse findByUsername(String username);

    UtenteResponse saveUtente(Utente utente);

    ResponseResult usernameExists(String username);

    ResponseResult emailExists(String email);
}