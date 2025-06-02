package it.unina.hackathon.dao;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.ExistsResponse;
import it.unina.hackathon.utils.UtenteResponse;

public interface UtenteDAO {
    UtenteResponse findByUsername(String username);

    UtenteResponse saveUtente(Utente utente);

    ExistsResponse usernameExists(String username);

    ExistsResponse emailExists(String email);
}