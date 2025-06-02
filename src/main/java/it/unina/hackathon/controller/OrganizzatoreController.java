package it.unina.hackathon.controller;

import it.unina.hackathon.dao.HackathonDAO;
import it.unina.hackathon.implementazioniPostgresDAO.HackathonImplementazionePostgresDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.utils.HackathonResponse;

import java.time.LocalDateTime;

public class OrganizzatoreController {
    private final Controller mainController;
    private final HackathonDAO hackathonDAO;

    public OrganizzatoreController(Controller mainController) {
        this.mainController = mainController;
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
    }

    public HackathonResponse creaHackathon(String title, String description, String site, LocalDateTime startDate, LocalDateTime endDate, int maxSubscription, int maxMemberForTeam) {

        // Crea hackathon con organizzatore corrente
        Hackathon hackathonCreating = new Hackathon(title, description, site, startDate, endDate, maxSubscription, maxMemberForTeam);
        hackathonCreating.setOrganizzatoreId(mainController.getIdUtenteCorrente());
        hackathonCreating.apriRegistrazioni();

        // Valida
        HackathonResponse esitoValidazione = hackathonCreating.validaCreating();
        if (esitoValidazione.hackathon() == null) {
            return esitoValidazione; // Validazione fallita
        }

        // Inserisci
        return hackathonDAO.saveHackathon(hackathonCreating);
    }
}