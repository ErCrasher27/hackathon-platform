package it.unina.hackathon.controller;

import it.unina.hackathon.dao.UtenteDAO;
import it.unina.hackathon.implementazioniPostgresDAO.UtenteImplementazionePostgresDAO;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.ResponseResult;
import it.unina.hackathon.utils.UtenteResponse;

public class AuthenticationController {
    private final Controller mainController;
    private final UtenteDAO utenteDAO;

    public AuthenticationController(Controller mainController) {
        this.mainController = mainController;
        this.utenteDAO = new UtenteImplementazionePostgresDAO();
    }

    public UtenteResponse login(String username, String password) {
        Utente utenteLoggingIn = new Utente(username, password);

        // Valida
        UtenteResponse esitoValidazione = utenteLoggingIn.validaLogin();
        if (esitoValidazione.utente() == null) {
            return esitoValidazione; // Validazione fallita
        }

        // Ricerca
        UtenteResponse esitoRicerca = utenteDAO.findByUsername(utenteLoggingIn.getUsername());
        if (esitoRicerca.utente() == null) {
            return esitoRicerca; // Ricerca fallita
        }

        // Autentica
        UtenteResponse esitoAutenticazione = utenteLoggingIn.autenticati(esitoRicerca.utente());
        if (esitoAutenticazione.utente() != null) {
            iniziaSessione(esitoRicerca.utente());
        }
        return esitoAutenticazione; // Autenticazione riuscita/fallita
    }

    public UtenteResponse register(String nome, String cognome, String email, String username, String password, String confermaPassword, TipoUtente tipoUtente) {
        Utente utenteRegisteringIn = new Utente(username, email, password, nome, cognome, tipoUtente);

        // Valida
        UtenteResponse esitoValidazione = utenteRegisteringIn.validaRegistrazione(confermaPassword);
        if (esitoValidazione.utente() == null) {
            return esitoValidazione; // Validazione fallita
        }

        // Ricerca Username ed Email, (se trovati la registrazione fallisce)
        ResponseResult esitoRicercaUsername = utenteDAO.usernameExists(utenteRegisteringIn.getUsername());
        if (esitoRicercaUsername.result()) {
            return new UtenteResponse(null, esitoRicercaUsername.message());
        }
        ResponseResult esitoRicercaEmail = utenteDAO.emailExists(utenteRegisteringIn.getEmail());
        if (esitoRicercaEmail.result()) {
            return new UtenteResponse(null, esitoRicercaEmail.message());
        }

        // Registra
        return utenteDAO.saveUtente(utenteRegisteringIn); // Registrazione riuscita/fallita
    }

    private void iniziaSessione(Utente utente) {
        mainController.setUtenteCorrente(utente);
    }

    public void logout() {
        mainController.logout();
    }
}