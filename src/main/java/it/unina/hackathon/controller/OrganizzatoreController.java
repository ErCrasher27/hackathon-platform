package it.unina.hackathon.controller;

import it.unina.hackathon.dao.HackathonDAO;
import it.unina.hackathon.implementazioniPostgresDAO.HackathonImplementazionePostgresDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.model.tmp.GiudiceHackathon;
import it.unina.hackathon.utils.HackathonListResponse;
import it.unina.hackathon.utils.HackathonResponse;
import it.unina.hackathon.utils.ResponseResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

    // TODO: I seguenti metodi saranno scritti successivamente

    public HackathonListResponse getAllHackathonByOrganizzatore(int organizzatoreId) {
        return hackathonDAO.getAllHackathonByOrganizzatore(organizzatoreId);
    }

    public List<GiudiceHackathon> getAllGiudiciNonInvitatiInHackathonId(int hackathonId) {
        return Collections.emptyList();
    }

    public List<GiudiceHackathon> getAllGiudiciInvitatiInHackathonId(int hackathonId) {
        return Collections.emptyList();
    }

    public ResponseResult invitaGiudice(int hackathonId, int giudiceId) {
        return new ResponseResult(true, "");
    }

    public ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus stato) {
        return new ResponseResult(true, "");
    }
}