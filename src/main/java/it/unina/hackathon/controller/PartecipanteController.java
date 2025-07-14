package it.unina.hackathon.controller;

import it.unina.hackathon.dao.*;
import it.unina.hackathon.implementazioniPostgresDAO.*;
import it.unina.hackathon.model.Progresso;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.*;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public class PartecipanteController {

    // region Proprietà

    private final Controller mainController;
    private final HackathonDAO hackathonDAO;
    private final TeamDAO teamDAO;
    private final PartecipanteDAO partecipanteDAO;
    private final MembroTeamImplementazionePostgresDAO membroTeamImplementazionePostgresDAO;
    private final ProgressoDAO progressoDAO;
    private final InvitoTeamDAO invitoTeamDAO;

    // endregion

    // region Costruttore

    public PartecipanteController(Controller mainController) {
        this.mainController = mainController;
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
        this.teamDAO = new TeamImplementazionePostgresDAO();
        this.partecipanteDAO = new PartecipanteImplementazionePostgresDAO();
        this.membroTeamImplementazionePostgresDAO = new MembroTeamImplementazionePostgresDAO();
        this.progressoDAO = new ProgressoImplementazionePostgresDAO();
        this.invitoTeamDAO = new InvitoTeamImplementazionePostgresDAO();
    }

    // endregion

    // region Gestione Registrazioni

    public HackathonListResponse getHackathonDisponibili() {
        return hackathonDAO.getHackathonByHackathonStatus(HackathonStatus.REGISTRAZIONI_APERTE);
    }

    public HackathonListResponse getHackathonRegistrati(int partecipanteId) {
        return hackathonDAO.getHackathonByPartecipante(partecipanteId);
    }

    public ResponseResult registratiAdHackathon(int hackathonId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return partecipanteDAO.registratiAdHackathon(hackathonId, partecipanteId);
    }

    public ResponseResult annullaRegistrazione(int hackathonId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return partecipanteDAO.annullaRegistrazione(hackathonId, partecipanteId);
    }

    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    // endregion

    // region Gestione Team

    public TeamResponse creaTeam(int hackathonId, String nomeTeam) {
        Team nuovoTeam = new Team(nomeTeam, hackathonId);
        int creatorId = mainController.getIdUtenteCorrente();

        TeamResponse teamResponse = teamDAO.saveTeam(nuovoTeam);
        if (teamResponse.team() != null) {
            teamDAO.aggiungiMembro(teamResponse.team().getTeamId(), creatorId, RuoloTeam.LEADER);
        }
        return teamResponse;
    }

    public TeamResponse getTeamCorrente(int hackathonId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return teamDAO.getTeamByUtente(partecipanteId, hackathonId);
    }

    public TeamListResponse getTeamDisponibili(int hackathonId) {
        HackathonResponse hackathonResponse = hackathonDAO.getHackathonById(hackathonId);
        if (hackathonResponse.hackathon() != null) {
            int maxDimensione = hackathonResponse.hackathon().getMaxDimensioneTeam();
            return teamDAO.getTeamDisponibili(hackathonId, maxDimensione);
        }
        return new TeamListResponse(null, "Errore durante il caricamento dell'hackathon!");
    }

    public ResponseResult richiediPartecipazione(int teamId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return invitoTeamDAO.inviaRichiesta(teamId, partecipanteId);
    }

    public ResponseResult accettaInTeam(int teamId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return teamDAO.aggiungiMembro(teamId, partecipanteId, RuoloTeam.MEMBRO);
    }

    public ResponseResult abbandonaTeam(int teamId) {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return teamDAO.rimuoviMembro(teamId, partecipanteId);
    }

    public ResponseResult invitaInTeam(int teamId, int utenteIdDaInvitare, String messaggio) {
        int invitanteId = mainController.getIdUtenteCorrente();
        return invitoTeamDAO.inviaInvito(teamId, utenteIdDaInvitare, invitanteId, messaggio);
    }

    public MembroTeamListResponse getMembriTeam(int teamId) {
        return membroTeamImplementazionePostgresDAO.getMembriByTeam(teamId);
    }

    public ResponseResult cambiaRuoloMembro(int teamId, RuoloTeam nuovoRuolo) {
        return membroTeamImplementazionePostgresDAO.cambiaRuolo(teamId, nuovoRuolo);
    }

    public ResponseResult rimuoviMembroTeam(int membroId) {
        return membroTeamImplementazionePostgresDAO.deleteMembro(membroId);
    }

    public ResponseResult isLeader(int utenteId, int teamId) {
        return membroTeamImplementazionePostgresDAO.isLeader(utenteId, teamId);
    }

    // endregion

    // region Gestione Inviti Team

    public InvitoTeamListResponse getInvitiRicevuti() {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return invitoTeamDAO.getInvitiRicevuti(partecipanteId);
    }

    public InvitoTeamListResponse getInvitiInviati() {
        int partecipanteId = mainController.getIdUtenteCorrente();
        return invitoTeamDAO.getInvitiInviati(partecipanteId);
    }

    public ResponseResult rispondiInvito(int invitoId, StatoInvito risposta) {
        return invitoTeamDAO.rispondiInvito(invitoId, risposta);
    }

    public ResponseResult annullaInvito(int invitoId) {
        return invitoTeamDAO.annullaInvito(invitoId);
    }

    // endregion

    // region Gestione Progressi

    public ProgressoListResponse getProgressiTeam(int teamId) {
        return progressoDAO.getProgressiByTeam(teamId);
    }

    public ProgressoResponse getProgresso(int progressoId) {
        return progressoDAO.getProgressoById(progressoId);
    }

    public ProgressoResponse caricaProgresso(int teamId, String titolo, String descrizione, String url) {
        Progresso progresso = new Progresso(teamId, titolo, descrizione, url);
        progresso.setCaricatoDaId(mainController.getIdUtenteCorrente());
        return progressoDAO.saveProgresso(progresso);
    }

    public ProgressoResponse modificaProgresso(int progressoId, String titolo, String descrizione, String url) {
        ProgressoResponse progressoResponse = progressoDAO.getProgressoById(progressoId);
        if (progressoResponse.progresso() != null) {
            Progresso progresso = progressoResponse.progresso();
            progresso.setTitolo(titolo);
            progresso.setDescrizione(descrizione);
            progresso.setDocumentoPath(url);
            return progressoDAO.updateProgresso(progresso);
        }
        return progressoResponse;
    }

    public ResponseResult eliminaProgresso(int progressoId) {
        return progressoDAO.deleteProgresso(progressoId);
    }

    // endregion

    // region Utilities e Info

    public UtenteListResponse getPartecipantiDisponibili(int hackathonId) {
        return partecipanteDAO.getPartecipantiSenzaTeam(hackathonId);
    }

    public ResponseIntResult contaMembriTeam(int teamId) {
        return teamDAO.contaMembriTeam(teamId);
    }

    public ResponseResult verificaSpazioDisponibile(int teamId) {
        return teamDAO.verificaSpazioDisponibile(teamId);
    }

    public ResponseResult rendiTeamDefinitivo(int teamId) {
        return teamDAO.rendiDefinitivo(teamId);
    }

    public ResponseIntResult contaPartecipanti(int hackathonId) {
        return partecipanteDAO.contaPartecipantiRegistrati(hackathonId);
    }

    // endregion
}