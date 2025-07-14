package it.unina.hackathon.controller;

import it.unina.hackathon.dao.GiudiceHackathonDAO;
import it.unina.hackathon.dao.HackathonDAO;
import it.unina.hackathon.dao.PartecipanteDAO;
import it.unina.hackathon.dao.TeamDAO;
import it.unina.hackathon.implementazioniPostgresDAO.GiudiceHackathonImplementazionePostgresDAO;
import it.unina.hackathon.implementazioniPostgresDAO.HackathonImplementazionePostgresDAO;
import it.unina.hackathon.implementazioniPostgresDAO.PartecipanteImplementazionePostgresDAO;
import it.unina.hackathon.implementazioniPostgresDAO.TeamImplementazionePostgresDAO;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.*;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import java.time.LocalDateTime;

public class OrganizzatoreController {
    private final Controller mainController;
    private final HackathonDAO hackathonDAO;
    private final GiudiceHackathonDAO giudiceHackathonDAO;
    private final PartecipanteDAO partecipanteDAO;
    private final TeamDAO teamDao;

    public OrganizzatoreController(Controller mainController) {
        this.mainController = mainController;
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
        this.giudiceHackathonDAO = new GiudiceHackathonImplementazionePostgresDAO();
        this.partecipanteDAO = new PartecipanteImplementazionePostgresDAO();
        this.teamDao = new TeamImplementazionePostgresDAO();
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

    public HackathonListResponse getAllHackathonByOrganizzatore(int organizzatoreId) {
        return hackathonDAO.getAllHackathonByOrganizzatore(organizzatoreId);
    }

    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    public ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        return hackathonDAO.cambiaStatoHackathon(hackathonId, nuovoStato);
    }

    public UtenteListResponse getAllGiudiciNonInvitatiInHackathon(int hackathonId) {
        return giudiceHackathonDAO.getGiudiciNonInvitati(hackathonId);
    }

    public GiudiceHackathonListResponse getAllGiudiciInvitatiInHackathon(int hackathonId) {
        return giudiceHackathonDAO.getGiudiciInvitati(hackathonId);
    }

    public ResponseResult invitaGiudice(int hackathonId, int giudiceId) {
        return giudiceHackathonDAO.invitaGiudice(hackathonId, giudiceId, mainController.getIdUtenteCorrente());
    }

    public ResponseResult rimuoviInvitoGiudice(int hackathonId, int giudiceId) {
        return giudiceHackathonDAO.rimuoviInvito(hackathonId, giudiceId);
    }

    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        return partecipanteDAO.getPartecipantiHackathon(hackathonId);
    }

    public TeamListResponse getTeamHackathon(int hackathonId) {
        return teamDao.getTeamHackathon(hackathonId);
    }

    public ResponseIntResult contaPartecipanti(int hackathonId) {
        return partecipanteDAO.contaPartecipantiRegistrati(hackathonId);
    }

    public ResponseIntResult contaTeam(int hackathonId) {
        return partecipanteDAO.contaTeamFormati(hackathonId);
    }

    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        return giudiceHackathonDAO.contaGiudiciAccettati(hackathonId);
    }
}