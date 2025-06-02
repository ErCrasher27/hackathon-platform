package it.unina.hackathon.controller;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.model.enums.TipoUtente;

public class Controller {
    private static Controller instance;

    private final AuthenticationController authController;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;
    private Utente utenteCorrente;

    private Controller() {
        this.authController = new AuthenticationController(this);
        this.navigationController = new NavigationController(this);
        this.organizzatoreController = new OrganizzatoreController(this);
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public AuthenticationController getAuthController() {
        return authController;
    }

    public NavigationController getNavigationController() {
        return navigationController;
    }

    public OrganizzatoreController getOrganizzatoreController() {
        return organizzatoreController;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public int getIdUtenteCorrente() {
        return utenteCorrente.getUtenteId();
    }

    public TipoUtente getTipoUtenteUtenteCorrente() {
        return utenteCorrente.getTipoUtente();
    }

    public void logout() {
        this.utenteCorrente = null;
    }

}