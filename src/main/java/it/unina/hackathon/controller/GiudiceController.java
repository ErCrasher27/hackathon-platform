package it.unina.hackathon.controller;

import it.unina.hackathon.dao.*;
import it.unina.hackathon.implementazioniPostgresDAO.*;
import it.unina.hackathon.model.Commento;
import it.unina.hackathon.model.Problema;
import it.unina.hackathon.model.Voto;
import it.unina.hackathon.utils.responses.*;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public class GiudiceController {

    // region Proprietà

    private final Controller mainController;
    private final GiudiceHackathonDAO giudiceHackathonDAO;
    private final HackathonDAO hackathonDAO;
    private final ProblemaDAO problemaDAO;
    private final CommentoDAO commentoDAO;
    private final VotoDAO votoDAO;
    private final ProgressoDAO progressoDAO;
    private final TeamDAO teamDAO;

    // endregion

    // region Costruttore

    public GiudiceController(Controller mainController) {
        this.mainController = mainController;
        this.giudiceHackathonDAO = new GiudiceHackathonImplementazionePostgresDAO();
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
        this.problemaDAO = new ProblemaImplementazionePostgresDAO();
        this.commentoDAO = new CommentoImplementazionePostgresDAO();
        this.votoDAO = new VotoImplementazionePostgresDAO();
        this.progressoDAO = new ProgressoImplementazionePostgresDAO();
        this.teamDAO = new TeamImplementazionePostgresDAO();
    }

    // endregion

    // region Gestione Inviti

    public GiudiceHackathonListResponse getInvitiRicevuti() {
        int giudiceId = mainController.getIdUtenteCorrente();
        return giudiceHackathonDAO.getInvitiRicevuti(giudiceId);
    }

    public ResponseResult accettaInvito(int giudiceHackathonId) {
        return giudiceHackathonDAO.accettaInvito(giudiceHackathonId);
    }

    public ResponseResult rifiutaInvito(int giudiceHackathonId) {
        return giudiceHackathonDAO.rifiutaInvito(giudiceHackathonId);
    }

    // endregion

    // region Visualizzazione Progetti Assegnati

    public HackathonListResponse getHackathonAssegnati() {
        int giudiceId = mainController.getIdUtenteCorrente();
        return hackathonDAO.getHackathonAccettati(giudiceId);

    }

    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    // endregion

    // region Pubblicazione Problemi

    public ProblemaResponse pubblicaProblema(int hackathonId, String titolo, String descrizione) {
        Problema problema = new Problema(titolo, descrizione, hackathonId, mainController.getIdUtenteCorrente());
        return problemaDAO.saveProblema(problema);
    }

    public ProblemaListResponse getTuttiProblemiHackathon(int hackathonId) {
        return problemaDAO.getProblemiByHackathon(hackathonId);
    }

    public ResponseResult eliminaProblema(int problemaId) {
        return problemaDAO.deleteProblema(problemaId);
    }

    // endregion

    // region Valutazione Progressi e Commenti

    public ProgressoListResponse getProgressiTeam(int teamId) {
        return progressoDAO.getProgressiByTeam(teamId);
    }

    public CommentoResponse scriviCommento(int progressoId, String testo) {
        Commento commento = new Commento(progressoId, mainController.getIdUtenteCorrente(), testo);
        return commentoDAO.saveCommento(commento);
    }

    public CommentoListResponse getCommentiProgresso(int progressoId) {
        return commentoDAO.getCommentiByProgresso(progressoId);
    }

    // endregion

    // region Votazione Team

    public TeamListResponse getTeamDaVotare(int hackathonId) {
        return teamDAO.getTeamByHackathon(hackathonId);
    }

    public VotoResponse assegnaVoto(int hackathonId, int teamId, int valore) {
        int giudiceId = mainController.getIdUtenteCorrente();
        Voto voto = new Voto(hackathonId, teamId, giudiceId, valore);

        if (!voto.validaVoto()) {
            return new VotoResponse(null, "Voto non valido! Valore deve essere tra 0 e 10.");
        }

        return votoDAO.saveVoto(voto);
    }

    public VotoResponse getVotoTeam(int hackathonId, int teamId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return votoDAO.getVotoByGiudiceTeam(giudiceId, teamId, hackathonId);
    }

    // endregion

    // region Statistiche e Reporting

    public VotoListResponse getClassificaHackathon(int hackathonId) {
        return votoDAO.getClassificaByHackathon(hackathonId);
    }

    // endregion

}