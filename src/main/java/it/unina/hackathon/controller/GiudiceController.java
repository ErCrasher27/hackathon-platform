package it.unina.hackathon.controller;

import it.unina.hackathon.dao.*;
import it.unina.hackathon.implementazioniPostgresDAO.*;
import it.unina.hackathon.model.Commento;
import it.unina.hackathon.model.Problema;
import it.unina.hackathon.model.Voto;
import it.unina.hackathon.model.enums.StatoInvito;
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

    public GiudiceHackathonListResponse getInvitiPending() {
        int giudiceId = mainController.getIdUtenteCorrente();
        return giudiceHackathonDAO.getInvitiPending(giudiceId);
    }

    public ResponseResult rispondiInvito(int giudiceHackathonId, StatoInvito risposta) {
        return giudiceHackathonDAO.rispondiInvito(giudiceHackathonId, risposta);
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

    public GiudiceHackathonResponse getStatoInvito(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return giudiceHackathonDAO.getStatoInvito(hackathonId, giudiceId);
    }

    // endregion

    // region Pubblicazione Problemi

    public ProblemaResponse pubblicaProblema(int hackathonId, String titolo, String descrizione) {
        Problema problema = new Problema(titolo, descrizione, hackathonId, mainController.getIdUtenteCorrente());
        return problemaDAO.saveProblema(problema);
    }

    public ProblemaListResponse getProblemiPubblicati(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return problemaDAO.getProblemiByGiudice(hackathonId, giudiceId);
    }

    public ProblemaListResponse getTuttiProblemiHackathon(int hackathonId) {
        return problemaDAO.getProblemiByHackathon(hackathonId);
    }

    public ProblemaResponse modificaProblema(int problemaId, String titolo, String descrizione) {
        ProblemaResponse problemaResponse = problemaDAO.getProblemaById(problemaId);
        if (problemaResponse.problema() != null) {
            Problema problema = problemaResponse.problema();
            problema.setTitolo(titolo);
            problema.setDescrizione(descrizione);
            return problemaDAO.updateProblema(problema);
        }
        return problemaResponse;
    }

    public ResponseResult eliminaProblema(int problemaId) {
        return problemaDAO.deleteProblema(problemaId);
    }

    // endregion

    // region Valutazione Progressi e Commenti

    public ProgressoListResponse getProgressiDaValutare(int hackathonId) {
        return progressoDAO.getProgressiByHackathon(hackathonId);
    }

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

    public CommentoListResponse getMieiCommenti(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return commentoDAO.getCommentiByGiudice(giudiceId);
    }

    public CommentoResponse modificaCommento(int commentoId, String nuovoTesto) {
        CommentoResponse commentoResponse = commentoDAO.getCommentoById(commentoId);
        if (commentoResponse.commento() != null) {
            Commento commento = commentoResponse.commento();
            commento.setTesto(nuovoTesto);
            return commentoDAO.updateCommento(commento);
        }
        return commentoResponse;
    }

    public ResponseResult eliminaCommento(int commentoId) {
        return commentoDAO.deleteCommento(commentoId);
    }

    // endregion

    // region Votazione Team

    public TeamListResponse getTeamDaVotare(int hackathonId) {
        return teamDAO.getTeamByHackathon(hackathonId);
    }

    public VotoResponse assegnaVoto(int hackathonId, int teamId, int valore, String criteri) {
        int giudiceId = mainController.getIdUtenteCorrente();
        Voto voto = new Voto(hackathonId, teamId, giudiceId, valore, criteri);

        if (!voto.validaVoto()) {
            return new VotoResponse(null, "Voto non valido! Valore deve essere tra 0 e 10.");
        }

        return votoDAO.saveVoto(voto);
    }

    public VotoResponse modificaVoto(int votoId, int nuovoValore, String nuoviCriteri) {
        VotoResponse votoResponse = votoDAO.getVotoById(votoId);
        if (votoResponse.voto() != null) {
            Voto voto = votoResponse.voto();
            if (voto.modificaVoto(nuovoValore, nuoviCriteri)) {
                return votoDAO.updateVoto(voto);
            }
            return new VotoResponse(null, "Valore del voto non valido!");
        }
        return votoResponse;
    }

    public VotoListResponse getMieiVoti(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return votoDAO.getVotiByGiudice(hackathonId, giudiceId);
    }

    public VotoResponse getVotoTeam(int hackathonId, int teamId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return votoDAO.getVotoByGiudiceTeam(hackathonId, teamId, giudiceId);
    }

    public VotoListResponse getVotiTeam(int teamId) {
        return votoDAO.getVotiByTeam(teamId);
    }

    public ResponseResult eliminaVoto(int votoId) {
        return votoDAO.deleteVoto(votoId);
    }

    // endregion

    // region Statistiche e Reporting

    public VotoListResponse getClassificaHackathon(int hackathonId) {
        return votoDAO.getClassificaByHackathon(hackathonId);
    }

    public CommentoListResponse getCommentiRecenti(int hackathonId, int giorni) {
        return commentoDAO.getCommentiRecenti(hackathonId, giorni);
    }

    public ResponseResult verificaPermessiValutazione(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        return giudiceHackathonDAO.verificaPermessiValutazione(hackathonId, giudiceId);
    }

    // endregion

    // region Utilities

    public boolean haPermessiValutazione(int hackathonId) {
        int giudiceId = mainController.getIdUtenteCorrente();
        GiudiceHackathonResponse statoResponse = giudiceHackathonDAO.getStatoInvito(hackathonId, giudiceId);
        return statoResponse.giudiceHackathon() != null && statoResponse.giudiceHackathon().getStatoInvito() == StatoInvito.ACCEPTED;
    }

    public boolean haGiaVotato(int hackathonId, int teamId) {
        VotoResponse votoResponse = getVotoTeam(hackathonId, teamId);
        return votoResponse.voto() != null;
    }

    // endregion
}