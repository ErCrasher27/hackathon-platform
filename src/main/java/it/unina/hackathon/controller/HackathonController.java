package it.unina.hackathon.controller;

import it.unina.hackathon.dao.*;
import it.unina.hackathon.gui.comuni.LoginGUI;
import it.unina.hackathon.gui.comuni.RegistrazioneGUI;
import it.unina.hackathon.gui.giudice.GestisciProblemiGUI;
import it.unina.hackathon.gui.giudice.HomeGiudiceGUI;
import it.unina.hackathon.gui.giudice.ValutazioneProgettoGUI;
import it.unina.hackathon.gui.organizzatore.CreaHackathonGUI;
import it.unina.hackathon.gui.organizzatore.GestisciHackathonGUI;
import it.unina.hackathon.gui.organizzatore.HomeOrganizzatoreGUI;
import it.unina.hackathon.gui.partecipante.GestisciProgettoGUI;
import it.unina.hackathon.gui.partecipante.HomePartecipanteGUI;
import it.unina.hackathon.implementazioniPostgresDAO.*;
import it.unina.hackathon.model.*;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.responses.*;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import javax.swing.*;
import java.time.LocalDateTime;

import static it.unina.hackathon.utils.UtilsUi.applyStyleFrame;

public class HackathonController {

    private static HackathonController instance;
    private final UtenteDAO utenteDAO;
    private final HackathonDAO hackathonDAO;
    private final TeamDAO teamDAO;
    private final GiudiceHackathonDAO giudiceHackathonDAO;
    private final InvitoGiudiceDAO invitoGiudiceDAO;
    private final ProblemaDAO problemaDAO;
    private final CommentoDAO commentoDAO;
    private final VotoDAO votoDAO;
    private final ProgressoDAO progressoDAO;
    private final RegistrazioneDAO registrazioneDAO;
    private final InvitoTeamDAO invitoTeamDAO;
    private Utente utenteCorrente;

    private HackathonController() {
        this.utenteDAO = new UtenteImplementazionePostgresDAO();
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
        this.teamDAO = new TeamImplementazionePostgresDAO();
        this.giudiceHackathonDAO = new GiudiceHackathonImplementazionePostgresDAO();
        this.invitoGiudiceDAO = new InvitoGiudiceImplementazionePostgresDAO();
        this.problemaDAO = new ProblemaImplementazionePostgresDAO();
        this.commentoDAO = new CommentoImplementazionePostgresDAO();
        this.votoDAO = new VotoImplementazionePostgresDAO();
        this.progressoDAO = new ProgressoImplementazionePostgresDAO();
        this.registrazioneDAO = new RegistrazioneImplementazionePostgresDAO();
        this.invitoTeamDAO = new InvitoTeamImplementazionePostgresDAO();
    }

    public static HackathonController getInstance() {
        if (instance == null) {
            instance = new HackathonController();
        }
        return instance;
    }


    public UtenteResponse effettuaLogin(String username, String password) {
        Utente utenteLogin = new Utente(username, password);

        // Valida i dati di input
        UtenteResponse esitoValidazione = utenteLogin.validaLogin();
        if (esitoValidazione.utente() == null) {
            return esitoValidazione;
        }

        // Cerca l'utente nel database
        UtenteResponse esitoRicerca = utenteDAO.findByUsername(utenteLogin.getUsername());
        if (esitoRicerca.utente() == null) {
            return esitoRicerca;
        }

        // Autentica l'utente
        UtenteResponse esitoAutenticazione = utenteLogin.autenticati(esitoRicerca.utente());
        if (esitoAutenticazione.utente() != null) {
            avviaSessioneUtente(esitoRicerca.utente());
        }

        return esitoAutenticazione;
    }

    public UtenteResponse effettuaRegistrazione(String nome, String cognome, String email, String username, String password, String confermaPassword, TipoUtente tipoUtente) {
        Utente nuovoUtente = new Utente(username, email, password, nome, cognome, tipoUtente);

        // Valida i dati di registrazione
        UtenteResponse esitoValidazione = nuovoUtente.validaRegistrazione(confermaPassword);
        if (esitoValidazione.utente() == null) {
            return esitoValidazione;
        }

        // Verifica unicità username
        ResponseResult esitoUsername = utenteDAO.usernameExists(nuovoUtente.getUsername());
        if (esitoUsername.result()) {
            return new UtenteResponse(null, esitoUsername.message());
        }

        // Verifica unicità email
        ResponseResult esitoEmail = utenteDAO.emailExists(nuovoUtente.getEmail());
        if (esitoEmail.result()) {
            return new UtenteResponse(null, esitoEmail.message());
        }

        // Salva il nuovo utente
        return utenteDAO.saveUtente(nuovoUtente);
    }

    public void effettuaLogout() {
        this.utenteCorrente = null;
    }

    private void avviaSessioneUtente(Utente utente) {
        this.utenteCorrente = utente;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    private int getIdUtenteCorrente() {
        return utenteCorrente.getUtenteId();
    }

    private TipoUtente getTipoUtenteCorrente() {
        return utenteCorrente.getTipoUtente();
    }

    public HackathonResponse creaHackathon(String titolo, String descrizione, String sede, LocalDateTime dataInizio, LocalDateTime dataFine, int maxIscrizioni, int maxMembriTeam) {
        Hackathon nuovoHackathon = new Hackathon(titolo, descrizione, sede, dataInizio, dataFine, maxIscrizioni, maxMembriTeam);
        nuovoHackathon.setUtenteOrganizzatoreId(getIdUtenteCorrente());
        nuovoHackathon.apriRegistrazioni();

        // Valida i dati dell'hackathon
        HackathonResponse esitoValidazione = nuovoHackathon.validaCreating();
        if (esitoValidazione.hackathon() == null) {
            return esitoValidazione;
        }

        return hackathonDAO.saveHackathon(nuovoHackathon);
    }

    public HackathonListResponse getHackathonOrganizzatore() {
        return hackathonDAO.getHackathonsByOrganizzatore(getIdUtenteCorrente());
    }

    public ResponseResult modificaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        return hackathonDAO.cambiaStatoHackathon(hackathonId, nuovoStato);
    }

    public UtenteListResponse getGiudiciNonInvitati(int hackathonId) {
        return utenteDAO.getUtentiGiudiciNonInvitatiByHackathon(hackathonId);
    }

    public UtenteListResponse getGiudiciInvitati(int hackathonId) {
        return utenteDAO.getUtentiGiudiciInvitatiByHackathon(hackathonId);
    }

    public ResponseResult invitaGiudice(int utenteGiudiceId, int hackathonId) {
        return invitoGiudiceDAO.saveInvitoGiudice(getIdUtenteCorrente(), utenteGiudiceId, hackathonId);
    }

    public GiudiceHackathonResponse getGiudiceHackathon(int hackathonId) {
        return giudiceHackathonDAO.getGiudiceHackathonByUtenteGiudiceHackathon(getIdUtenteCorrente(), hackathonId);
    }

    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        return giudiceHackathonDAO.contaGiudiceHackathonByHackathon(hackathonId);
    }

    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        return utenteDAO.getUtentiPartecipantiByHackathon(hackathonId);
    }

    public TeamListResponse getTeamsHackathon(int hackathonId) {
        return teamDAO.getTeamsByHackathon(hackathonId);
    }

    public ResponseIntResult contaPartecipantiRegistrati(int hackathonId) {
        return registrazioneDAO.contaRegistrazioniByHackathon(hackathonId);
    }

    public ResponseIntResult contaNumeroMembri(int teamId) {
        return teamDAO.contaRegistrazioniByTeam(teamId);
    }

    public ResponseIntResult contaTeamFormati(int hackathonId) {
        return teamDAO.contaTeamByHackathon(hackathonId);
    }

    public TeamResponse getTeamPartecipante(int hackathonId) {
        return teamDAO.getTeamByPartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
    }

    public TeamResponse getTeam(int registrazioneId) {
        return teamDAO.getTeamByRegistrazione(registrazioneId);
    }

    public InvitoGiudiceListResponse getInvitiGiudiceRicevuti() {
        return invitoGiudiceDAO.getInvitiGiudiceByUtenteGiudice(getIdUtenteCorrente());
    }

    public InvitoGiudiceResponse getInvitoByInvitatoHackathon(int utenteGiudiceId, int hackathonId) {
        return invitoGiudiceDAO.getInvitoGiudiceByUtenteGiudiceHackathon(utenteGiudiceId, hackathonId);
    }

    public ResponseResult accettaInvitoGiudice(int invitoGiudiceId) {
        return invitoGiudiceDAO.aggiornaStatoInvito(invitoGiudiceId, StatoInvito.ACCEPTED);
    }

    public ResponseResult rifiutaInvitoGiudice(int invitoGiudiceId) {
        return invitoGiudiceDAO.aggiornaStatoInvito(invitoGiudiceId, StatoInvito.DECLINED);
    }

    public HackathonListResponse getHackathonAssegnati(int hackathonId) {
        GiudiceHackathonResponse giudiceHackathonCorrente = giudiceHackathonDAO.getGiudiceHackathonByUtenteGiudiceHackathon(getIdUtenteCorrente(), hackathonId);
        if (giudiceHackathonCorrente.giudiceHackathon() != null) {
            return hackathonDAO.getHackathonByGiudiceHackathon(giudiceHackathonCorrente.giudiceHackathon().getGiudiceHackathonId());
        } else {
            return new HackathonListResponse(null, "Impossibile caricare gli hackathon!");
        }
    }

    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    public ProblemaResponse pubblicaProblema(int giudiceHackathonId, String titolo, String descrizione) {
        Problema nuovoProblema = new Problema(titolo, descrizione, giudiceHackathonId);
        return problemaDAO.saveProblema(nuovoProblema);
    }

    public ProblemaListResponse getProblemiHackathon(int hackathonId) {
        return problemaDAO.getProblemiByHackathon(hackathonId);
    }

    public ResponseResult eliminaProblema(int problemaId) {
        return problemaDAO.rimuoviProblema(problemaId);
    }

    public ProgressoListResponse getProgressiTeam(int teamId) {
        return progressoDAO.getProgressiByTeam(teamId);
    }

    public CommentoResponse scriviCommento(int progressoId, int giudiceHackathonId, String testoCommento) {
        Commento nuovoCommento = new Commento(progressoId, giudiceHackathonId, testoCommento);
        return commentoDAO.saveCommento(nuovoCommento);
    }

    public CommentoListResponse getCommentiProgresso(int progressoId) {
        return commentoDAO.getCommentiByProgresso(progressoId);
    }

    public VotoResponse assegnaVoto(int giudiceHackathonId, int valoreVoto, int teamId) {
        Voto nuovoVoto = new Voto(teamId, giudiceHackathonId, valoreVoto);

        if (!nuovoVoto.validaVoto()) {
            return new VotoResponse(null, "Voto non valido! Il valore deve essere compreso tra 0 e 10.");
        }

        return votoDAO.saveVoto(nuovoVoto);
    }

    public VotoResponse getVotoTeam(int teamId) {
        return votoDAO.getVotoByGiudiceTeamHackathon(getIdUtenteCorrente(), teamId);
    }

    public VotoListResponse getClassificaHackathon(int hackathonId) {
        return votoDAO.getClassificaByHackathon(hackathonId);
    }

    public HackathonListResponse getHackathonDisponibili() {
        return hackathonDAO.getHackathonsByHackathonStatus(HackathonStatus.REGISTRAZIONI_APERTE);
    }

    public HackathonListResponse getHackathonRegistrati() {
        return hackathonDAO.getHackathonsByPartecipante(getIdUtenteCorrente());
    }

    public RegistrazioneResponse registratiAdHackathon(int hackathonId) {
        Registrazione nuovaRegistrazione = new Registrazione(getIdUtenteCorrente(), hackathonId);
        return registrazioneDAO.saveRegistrazione(nuovaRegistrazione);
    }

    public ResponseResult annullaRegistrazione(int registrazioneId) {
        return registrazioneDAO.rimuoviRegistrazione(registrazioneId);
    }

    public TeamResponse creaTeam(int hackathonId, String nomeTeam) {
        Team nuovoTeam = new Team(nomeTeam, hackathonId);
        TeamResponse teamCreato = teamDAO.saveTeam(nuovoTeam);
        if (teamCreato.team() != null) {
            RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
            if (registrazioneCorrente.registrazione() != null) {
                registrazioneDAO.aggiornaTeamConRuolo(registrazioneCorrente.registrazione().getRegistrazioneId(), teamCreato.team().getTeamId(), RuoloTeam.LEADER);
            }
        }
        return teamCreato;
    }

    public ResponseResult abbandonaTeam(int hackathonId) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return registrazioneDAO.aggiornaTeamNullConRuoloNull(registrazioneCorrente.registrazione().getRegistrazioneId());
        } else {
            return new ResponseResult(false, "Impossibile abbandonare il team!");
        }
    }

    public ResponseResult invitaUtenteInTeam(int utentePartecipanteId, String messaggio) {
        return invitoTeamDAO.saveInvitoUtente(getIdUtenteCorrente(), utentePartecipanteId, messaggio);
    }

    public RegistrazioneListResponse getMembriTeam(int teamId) {
        return registrazioneDAO.getRegistrazioniByTeam(teamId);
    }

    public ResponseResult annullaRegistrazioneHackathon(int hackathonId) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return registrazioneDAO.rimuoviRegistrazione(registrazioneCorrente.registrazione().getRegistrazioneId());
        } else {
            return new ResponseResult(false, "Impossibile annullare la registrazione!");
        }
    }

    public ResponseResult verificaLeaderTeam(int teamId) {
        return registrazioneDAO.isLeaderByUtentePartecipanteTeam(getIdUtenteCorrente(), teamId);
    }

    public InvitoTeamListResponse getInvitiTeamRicevuti(int hackathonId) {
        return invitoTeamDAO.getInvitiTeamByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
    }

    public ResponseResult rispondiInvitoTeam(int invitoTeamId, StatoInvito risposta) {
        return invitoTeamDAO.aggiornaStatoInvito(invitoTeamId, risposta);
    }

    public ProgressoResponse caricaProgresso(String urlDocumento) {
        Progresso nuovoProgresso = new Progresso(urlDocumento);
        nuovoProgresso.setCaricatoDaRegistrazioneId(getIdUtenteCorrente());
        return progressoDAO.saveProgresso(nuovoProgresso);
    }

    public ResponseResult eliminaProgresso(int progressoId) {
        return progressoDAO.rimuoviProgresso(progressoId);
    }

    public RegistrazioneListResponse getPartecipantiDisponibili(int hackathonId) {
        return registrazioneDAO.getRegistratiConTeamNullByHackathon(hackathonId);
    }

    public void vaiAllaHome(JFrame frameCorrente) {
        JFrame nuovoFrame = creaFrameHome(getTipoUtenteCorrente());
        if (nuovoFrame != null) {
            cambiaFrame(frameCorrente, nuovoFrame);
        }
    }

    public void vaiAlLogin(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new LoginGUI().getFrame());
    }

    public void vaiAllaRegistrazione(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new RegistrazioneGUI().getFrame());
    }

    public void vaiACreareHackathon(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new CreaHackathonGUI().getFrame());
    }

    public void vaiAGestireHackathon(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciHackathonGUI(hackathonId).getFrame());
    }

    public void vaiAValutareProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new ValutazioneProgettoGUI(hackathonId).getFrame());
    }

    public void vaiAGestireProblemi(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProblemiGUI(hackathonId).getFrame());
    }

    public void vaiAGestireProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProgettoGUI(hackathonId).getFrame());
    }

    private JFrame creaFrameHome(TipoUtente tipoUtente) {
        return switch (tipoUtente) {
            case ORGANIZZATORE -> new HomeOrganizzatoreGUI().getFrame();
            case GIUDICE -> new HomeGiudiceGUI().getFrame();
            case PARTECIPANTE -> new HomePartecipanteGUI().getFrame();
        };
    }

    private void cambiaFrame(JFrame frameCorrente, JFrame nuovoFrame) {
        mostraFrame(nuovoFrame);
        chiudiFrame(frameCorrente);
    }

    private void mostraFrame(JFrame frame) {
        applyStyleFrame(frame);
        frame.setVisible(true);
    }

    private void chiudiFrame(JFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }


}