package it.unina.hackathon.controller;

import it.unina.hackathon.model.Utente;

public class Controller {
    private static Controller instance;

    private final AuthenticationController authController;
    private final NavigationController navigationController;
    private Utente utenteCorrente;

    private Controller() {
        this.authController = new AuthenticationController(this);
        this.navigationController = new NavigationController(this);
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

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void setUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public void logout() {
        this.utenteCorrente = null;
    }

}