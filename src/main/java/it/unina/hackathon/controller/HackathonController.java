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

/**
 * Controller principale per la gestione completa della piattaforma hackathon.
 * Implementa il pattern Singleton e gestisce tutte le operazioni per i diversi tipi di utente.
 */
public class HackathonController {

    // region Singleton e Proprietà Principali

    private static HackathonController instance;
    private final UtenteDAO utenteDAO;

    // endregion

    // region DAO - Accesso ai Dati
    private final HackathonDAO hackathonDAO;
    private final TeamDAO teamDAO;
    private final PartecipanteDAO partecipanteDAO;
    private final GiudiceHackathonDAO giudiceHackathonDAO;
    private final ProblemaDAO problemaDAO;
    private final CommentoDAO commentoDAO;
    private final VotoDAO votoDAO;
    private final ProgressoDAO progressoDAO;
    private final MembroTeamDAO membroTeamDAO;
    private final InvitoTeamDAO invitoTeamDAO;
    private Utente utenteCorrente;

    // endregion

    // region Costruttore e Singleton

    /**
     * Costruttore privato per il pattern Singleton.
     * Inizializza tutti i DAO necessari per l'accesso ai dati.
     */
    private HackathonController() {
        this.utenteDAO = new UtenteImplementazionePostgresDAO();
        this.hackathonDAO = new HackathonImplementazionePostgresDAO();
        this.teamDAO = new TeamImplementazionePostgresDAO();
        this.partecipanteDAO = new PartecipanteImplementazionePostgresDAO();
        this.giudiceHackathonDAO = new GiudiceHackathonImplementazionePostgresDAO();
        this.problemaDAO = new ProblemaImplementazionePostgresDAO();
        this.commentoDAO = new CommentoImplementazionePostgresDAO();
        this.votoDAO = new VotoImplementazionePostgresDAO();
        this.progressoDAO = new ProgressoImplementazionePostgresDAO();
        this.membroTeamDAO = new MembroTeamImplementazionePostgresDAO();
        this.invitoTeamDAO = new InvitoTeamImplementazionePostgresDAO();
    }

    /**
     * Restituisce l'istanza singleton del controller.
     *
     * @return l'istanza unica del controller
     */
    public static HackathonController getInstance() {
        if (instance == null) {
            instance = new HackathonController();
        }
        return instance;
    }

    // endregion

    // region Gestione Autenticazione

    /**
     * Effettua il login di un utente nel sistema.
     *
     * @param username nome utente
     * @param password password dell'utente
     * @return risposta contenente i dati dell'utente se il login è riuscito
     */
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

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param nome             nome dell'utente
     * @param cognome          cognome dell'utente
     * @param email            email dell'utente
     * @param username         nome utente scelto
     * @param password         password scelta
     * @param confermaPassword conferma della password
     * @param tipoUtente       tipo di utente (ORGANIZZATORE, GIUDICE, PARTECIPANTE)
     * @return risposta contenente i dati dell'utente registrato
     */
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

    /**
     * Effettua il logout dell'utente corrente.
     */
    public void effettuaLogout() {
        this.utenteCorrente = null;
    }

    /**
     * Avvia una nuova sessione per l'utente specificato.
     *
     * @param utente l'utente per cui avviare la sessione
     */
    private void avviaSessioneUtente(Utente utente) {
        this.utenteCorrente = utente;
    }

    // endregion

    // region Gestione Utente Corrente

    /**
     * Restituisce l'utente attualmente autenticato.
     *
     * @return l'utente corrente o null se non autenticato
     */
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Restituisce l'ID dell'utente corrente.
     *
     * @return ID dell'utente corrente
     */
    public int getIdUtenteCorrente() {
        return utenteCorrente.getUtenteId();
    }

    /**
     * Restituisce il tipo dell'utente corrente.
     *
     * @return tipo di utente corrente
     */
    public TipoUtente getTipoUtenteCorrente() {
        return utenteCorrente.getTipoUtente();
    }

    // endregion

    // region Funzionalità Organizzatore - Gestione Hackathon

    /**
     * Crea un nuovo hackathon.
     *
     * @param titolo        titolo dell'hackathon
     * @param descrizione   descrizione dell'hackathon
     * @param sede          sede dell'hackathon
     * @param dataInizio    data di inizio
     * @param dataFine      data di fine
     * @param maxIscrizioni massimo numero di iscrizioni
     * @param maxMembriTeam massimo numero di membri per team
     * @return risposta contenente l'hackathon creato
     */
    public HackathonResponse creaHackathon(String titolo, String descrizione, String sede, LocalDateTime dataInizio, LocalDateTime dataFine, int maxIscrizioni, int maxMembriTeam) {
        Hackathon nuovoHackathon = new Hackathon(titolo, descrizione, sede, dataInizio, dataFine, maxIscrizioni, maxMembriTeam);
        nuovoHackathon.setOrganizzatoreId(getIdUtenteCorrente());
        nuovoHackathon.apriRegistrazioni();

        // Valida i dati dell'hackathon
        HackathonResponse esitoValidazione = nuovoHackathon.validaCreating();
        if (esitoValidazione.hackathon() == null) {
            return esitoValidazione;
        }

        return hackathonDAO.saveHackathon(nuovoHackathon);
    }

    /**
     * Restituisce tutti gli hackathon creati dall'organizzatore specificato.
     *
     * @param organizzatoreId ID dell'organizzatore
     * @return lista degli hackathon dell'organizzatore
     */
    public HackathonListResponse getHackathonOrganizzatore(int organizzatoreId) {
        return hackathonDAO.getHackathonsByOrganizzatore(organizzatoreId);
    }

    /**
     * Modifica lo stato di un hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @param nuovoStato  nuovo stato dell'hackathon
     * @return esito dell'operazione
     */
    public ResponseResult modificaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        return hackathonDAO.cambiaStatoHackathon(hackathonId, nuovoStato);
    }

    // endregion

    // region Funzionalità Organizzatore - Gestione Giudici

    /**
     * Restituisce tutti i giudici non ancora invitati all'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei giudici non invitati
     */
    public GiudiceHackathonListResponse getGiudiciNonInvitati(int hackathonId) {
        return giudiceHackathonDAO.getGiudiciNonInvitati(hackathonId);
    }

    /**
     * Restituisce tutti i giudici invitati all'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei giudici invitati
     */
    public GiudiceHackathonListResponse getGiudiciInvitati(int hackathonId) {
        return giudiceHackathonDAO.getGiudiciInvitati(hackathonId);
    }

    /**
     * Invia un invito a un giudice per partecipare come valutatore.
     *
     * @param hackathonId ID dell'hackathon
     * @param giudiceId   ID del giudice da invitare
     * @return esito dell'operazione
     */
    public ResponseResult invitaGiudice(int hackathonId, int giudiceId) {
        return giudiceHackathonDAO.invitaGiudice(hackathonId, giudiceId, getIdUtenteCorrente());
    }

    /**
     * Rimuove l'invito a un giudice.
     *
     * @param hackathonId ID dell'hackathon
     * @param giudiceId   ID del giudice
     * @return esito dell'operazione
     */
    public ResponseResult rimuoviInvitoGiudice(int hackathonId, int giudiceId) {
        return giudiceHackathonDAO.rimuoviInvito(hackathonId, giudiceId);
    }

    /**
     * Conta i giudici che hanno accettato l'invito.
     *
     * @param hackathonId ID dell'hackathon
     * @return numero di giudici che hanno accettato
     */
    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        return giudiceHackathonDAO.contaGiudiciAccettati(hackathonId);
    }

    // endregion

    // region Funzionalità Organizzatore - Statistiche

    /**
     * Restituisce la lista dei partecipanti all'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei partecipanti
     */
    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        return partecipanteDAO.getPartecipantiByHackathon(hackathonId);
    }

    /**
     * Restituisce la lista dei team dell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei team
     */
    public TeamListResponse getTeamHackathon(int hackathonId) {
        return teamDAO.getTeamByHackathon(hackathonId);
    }

    /**
     * Conta i partecipanti registrati all'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return numero di partecipanti registrati
     */
    public ResponseIntResult contaPartecipantiRegistrati(int hackathonId) {
        return partecipanteDAO.contaPartecipantiRegistrati(hackathonId);
    }

    /**
     * Conta i team formati nell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return numero di team formati
     */
    public ResponseIntResult contaTeamFormati(int hackathonId) {
        return partecipanteDAO.contaTeamFormati(hackathonId);
    }

    /**
     * Restituisce il team di un partecipante specifico.
     *
     * @param partecipanteId ID del partecipante
     * @param hackathonId    ID dell'hackathon
     * @return team del partecipante
     */
    public TeamResponse getTeamPartecipante(int partecipanteId, int hackathonId) {
        return teamDAO.getTeamByPartecipanteHackathon(partecipanteId, hackathonId);
    }

    // endregion

    // region Funzionalità Giudice - Gestione Inviti

    /**
     * Restituisce gli inviti ricevuti dal giudice corrente.
     *
     * @return lista degli inviti ricevuti
     */
    public GiudiceHackathonListResponse getInvitiGiudiceRicevuti() {
        return giudiceHackathonDAO.getInvitiRicevuti(getIdUtenteCorrente());
    }

    /**
     * Accetta un invito da giudice.
     *
     * @param giudiceHackathonId ID dell'invito
     * @return esito dell'operazione
     */
    public ResponseResult accettaInvitoGiudice(int giudiceHackathonId) {
        return giudiceHackathonDAO.rispondiInvito(giudiceHackathonId, StatoInvito.ACCEPTED);
    }

    /**
     * Rifiuta un invito da giudice.
     *
     * @param giudiceHackathonId ID dell'invito
     * @return esito dell'operazione
     */
    public ResponseResult rifiutaInvitoGiudice(int giudiceHackathonId) {
        return giudiceHackathonDAO.rispondiInvito(giudiceHackathonId, StatoInvito.DECLINED);
    }

    // endregion

    // region Funzionalità Giudice - Gestione Hackathon

    /**
     * Restituisce gli hackathon assegnati al giudice corrente.
     *
     * @return lista degli hackathon assegnati
     */
    public HackathonListResponse getHackathonAssegnati() {
        return hackathonDAO.getHackathonAccettati(getIdUtenteCorrente());
    }

    /**
     * Restituisce i dettagli di un hackathon specifico.
     *
     * @param hackathonId ID dell'hackathon
     * @return dettagli dell'hackathon
     */
    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    // endregion

    // region Funzionalità Giudice - Gestione Problemi

    /**
     * Pubblica un nuovo problema nell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @param titolo      titolo del problema
     * @param descrizione descrizione del problema
     * @return risposta contenente il problema pubblicato
     */
    public ProblemaResponse pubblicaProblema(int hackathonId, String titolo, String descrizione) {
        Problema nuovoProblema = new Problema(titolo, descrizione, hackathonId, getIdUtenteCorrente());
        return problemaDAO.saveProblema(nuovoProblema);
    }

    /**
     * Restituisce tutti i problemi pubblicati nell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei problemi
     */
    public ProblemaListResponse getProblemiHackathon(int hackathonId) {
        return problemaDAO.getProblemiByHackathon(hackathonId);
    }

    /**
     * Elimina un problema pubblicato.
     *
     * @param problemaId ID del problema
     * @return esito dell'operazione
     */
    public ResponseResult eliminaProblema(int problemaId) {
        return problemaDAO.deleteProblema(problemaId);
    }

    // endregion

    // region Funzionalità Giudice - Valutazione e Commenti

    /**
     * Restituisce i progressi di un team specifico.
     *
     * @param teamId ID del team
     * @return lista dei progressi del team
     */
    public ProgressoListResponse getProgressiTeam(int teamId) {
        return progressoDAO.getProgressiByTeam(teamId);
    }

    /**
     * Scrive un commento su un progresso.
     *
     * @param progressoId   ID del progresso
     * @param testoCommento testo del commento
     * @return risposta contenente il commento creato
     */
    public CommentoResponse scriviCommento(int progressoId, String testoCommento) {
        Commento nuovoCommento = new Commento(progressoId, getIdUtenteCorrente(), testoCommento);
        return commentoDAO.saveCommento(nuovoCommento);
    }

    /**
     * Restituisce i commenti di un progresso.
     *
     * @param progressoId ID del progresso
     * @return lista dei commenti
     */
    public CommentoListResponse getCommentiProgresso(int progressoId) {
        return commentoDAO.getCommentiByProgresso(progressoId);
    }

    // endregion

    // region Funzionalità Giudice - Votazione

    /**
     * Assegna un voto a un team.
     *
     * @param hackathonId ID dell'hackathon
     * @param teamId      ID del team
     * @param valoreVoto  valore del voto (0-10)
     * @return risposta contenente il voto assegnato
     */
    public VotoResponse assegnaVoto(int hackathonId, int teamId, int valoreVoto) {
        Voto nuovoVoto = new Voto(hackathonId, teamId, getIdUtenteCorrente(), valoreVoto);

        if (!nuovoVoto.validaVoto()) {
            return new VotoResponse(null, "Voto non valido! Il valore deve essere compreso tra 0 e 10.");
        }

        return votoDAO.saveVoto(nuovoVoto);
    }

    /**
     * Restituisce il voto assegnato a un team dal giudice corrente.
     *
     * @param hackathonId ID dell'hackathon
     * @param teamId      ID del team
     * @return voto assegnato
     */
    public VotoResponse getVotoTeam(int hackathonId, int teamId) {
        return votoDAO.getVotoByGiudiceTeamHackathon(getIdUtenteCorrente(), teamId, hackathonId);
    }

    /**
     * Restituisce la classifica dell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return classifica basata sui voti
     */
    public VotoListResponse getClassificaHackathon(int hackathonId) {
        return votoDAO.getClassificaByHackathon(hackathonId);
    }

    // endregion

    // region Funzionalità Partecipante - Gestione Hackathon

    /**
     * Restituisce gli hackathon disponibili per la registrazione.
     *
     * @return lista degli hackathon aperti
     */
    public HackathonListResponse getHackathonDisponibili() {
        return hackathonDAO.getHackathonByHackathonStatus(HackathonStatus.REGISTRAZIONI_APERTE);
    }

    /**
     * Restituisce gli hackathon ai quali è registrato un partecipante.
     *
     * @param partecipanteId ID del partecipante
     * @return lista degli hackathon del partecipante
     */
    public HackathonListResponse getHackathonPartecipante(int partecipanteId) {
        return hackathonDAO.getHackathonsByPartecipante(partecipanteId);
    }

    /**
     * Registra il partecipante corrente a un hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return esito dell'operazione
     */
    public ResponseResult registratiHackathon(int hackathonId) {
        return partecipanteDAO.registratiAdHackathon(hackathonId, getIdUtenteCorrente());
    }

    /**
     * Annulla la registrazione a un hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return esito dell'operazione
     */
    public ResponseResult annullaRegistrazione(int hackathonId) {
        return partecipanteDAO.annullaRegistrazione(hackathonId, getIdUtenteCorrente());
    }

    // endregion

    // region Funzionalità Partecipante - Gestione Team

    /**
     * Crea un nuovo team nell'hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @param nomeTeam    nome del team
     * @return risposta contenente il team creato
     */
    public TeamResponse creaTeam(int hackathonId, String nomeTeam) {
        Team nuovoTeam = new Team(nomeTeam, hackathonId);
        int creatoreId = getIdUtenteCorrente();

        TeamResponse teamResponse = teamDAO.saveTeam(nuovoTeam);
        if (teamResponse.team() != null) {
            teamDAO.aggiungiMembro(teamResponse.team().getTeamId(), creatoreId, RuoloTeam.LEADER);
        }

        return teamResponse;
    }

    /**
     * Abbandona il team corrente.
     *
     * @param teamId ID del team
     * @return esito dell'operazione
     */
    public ResponseResult abbandonaTeam(int teamId) {
        return teamDAO.rimuoviMembro(teamId, getIdUtenteCorrente());
    }

    /**
     * Invia un invito a un utente per unirsi al team.
     *
     * @param teamId    ID del team
     * @param utenteId  ID dell'utente da invitare
     * @param messaggio messaggio dell'invito
     * @return esito dell'operazione
     */
    public ResponseResult invitaUtenteInTeam(int teamId, int utenteId, String messaggio) {
        return invitoTeamDAO.inviaInvito(teamId, utenteId, getIdUtenteCorrente(), messaggio);
    }

    // endregion

    // region Funzionalità Partecipante - Gestione Membri Team

    /**
     * Restituisce i membri di un team.
     *
     * @param teamId ID del team
     * @return lista dei membri del team
     */
    public MembroTeamListResponse getMembriTeam(int teamId) {
        return membroTeamDAO.getMembriByTeam(teamId);
    }

    /**
     * Rimuove un membro dal team.
     *
     * @param membroId ID del membro
     * @return esito dell'operazione
     */
    public ResponseResult rimuoviMembroTeam(int membroId) {
        return membroTeamDAO.deleteMembro(membroId);
    }

    /**
     * Verifica se un utente è leader del team.
     *
     * @param utenteId ID dell'utente
     * @param teamId   ID del team
     * @return true se l'utente è leader
     */
    public ResponseResult verificaLeaderTeam(int utenteId, int teamId) {
        return membroTeamDAO.isLeader(utenteId, teamId);
    }

    // endregion

    // region Funzionalità Partecipante - Gestione Inviti Team

    /**
     * Restituisce gli inviti ricevuti dal partecipante.
     *
     * @return lista degli inviti ricevuti
     */
    public InvitoTeamListResponse getInvitiTeamRicevuti() {
        return invitoTeamDAO.getInvitiRicevuti(getIdUtenteCorrente());
    }

    /**
     * Risponde a un invito per un team.
     *
     * @param invitoId ID dell'invito
     * @param risposta risposta all'invito (ACCETTATO/RIFIUTATO)
     * @return esito dell'operazione
     */
    public ResponseResult rispondiInvitoTeam(int invitoId, StatoInvito risposta) {
        return invitoTeamDAO.rispondiInvito(invitoId, risposta);
    }

    // endregion

    // region Funzionalità Partecipante - Gestione Progressi

    /**
     * Carica un nuovo progresso per il team.
     *
     * @param urlDocumento URL del documento
     * @return risposta contenente il progresso caricato
     */
    public ProgressoResponse caricaProgresso(int teamId, String urlDocumento) {
        Progresso nuovoProgresso = new Progresso(teamId, urlDocumento);
        nuovoProgresso.setCaricatoDaId(getIdUtenteCorrente());
        return progressoDAO.saveProgresso(nuovoProgresso);
    }

    /**
     * Elimina un progresso.
     *
     * @param progressoId ID del progresso
     * @return esito dell'operazione
     */
    public ResponseResult eliminaProgresso(int progressoId) {
        return progressoDAO.deleteProgresso(progressoId);
    }

    // endregion

    // region Funzionalità Partecipante - Utilità

    /**
     * Restituisce i partecipanti disponibili (senza team) per un hackathon.
     *
     * @param hackathonId ID dell'hackathon
     * @return lista dei partecipanti disponibili
     */
    public UtenteListResponse getPartecipantiDisponibili(int hackathonId) {
        return partecipanteDAO.getPartecipantiSenzaTeam(hackathonId);
    }

    // endregion

    // region Navigazione tra GUI

    /**
     * Naviga alla schermata home appropriata in base al tipo di utente.
     *
     * @param frameCorrente frame corrente da chiudere
     * @param tipoUtente    tipo di utente per determinare la home
     */
    public void vaiAllaHome(JFrame frameCorrente, TipoUtente tipoUtente) {
        JFrame nuovoFrame = creaFrameHome(tipoUtente);
        if (nuovoFrame != null) {
            cambiaFrame(frameCorrente, nuovoFrame);
        }
    }

    /**
     * Naviga alla schermata di login.
     *
     * @param frameCorrente frame corrente da chiudere
     */
    public void vaiAlLogin(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new LoginGUI().getFrame());
    }

    /**
     * Naviga alla schermata di registrazione.
     *
     * @param frameCorrente frame corrente da chiudere
     */
    public void vaiAllaRegistrazione(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new RegistrazioneGUI().getFrame());
    }

    /**
     * Naviga alla schermata di creazione hackathon.
     *
     * @param frameCorrente frame corrente da chiudere
     */
    public void vaiACreareHackathon(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new CreaHackathonGUI().getFrame());
    }

    /**
     * Naviga alla schermata di gestione hackathon.
     *
     * @param frameCorrente frame corrente da chiudere
     * @param hackathonId   ID dell'hackathon da gestire
     */
    public void vaiAGestireHackathon(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciHackathonGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla schermata di valutazione progetto.
     *
     * @param frameCorrente frame corrente da chiudere
     * @param hackathonId   ID dell'hackathon
     */
    public void vaiAValutareProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new ValutazioneProgettoGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla schermata di gestione problemi.
     *
     * @param frameCorrente frame corrente da chiudere
     * @param hackathonId   ID dell'hackathon
     */
    public void vaiAGestireProblemi(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProblemiGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla schermata di gestione progetto.
     *
     * @param frameCorrente frame corrente da chiudere
     * @param hackathonId   ID dell'hackathon
     */
    public void vaiAGestireProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProgettoGUI(hackathonId).getFrame());
    }

    /**
     * Crea il frame home appropriato in base al tipo di utente.
     *
     * @param tipoUtente tipo di utente
     * @return frame home corrispondente
     */
    private JFrame creaFrameHome(TipoUtente tipoUtente) {
        return switch (tipoUtente) {
            case ORGANIZZATORE -> new HomeOrganizzatoreGUI().getFrame();
            case GIUDICE -> new HomeGiudiceGUI().getFrame();
            case PARTECIPANTE -> new HomePartecipanteGUI().getFrame();
        };
    }

    /**
     * Effettua il cambio di frame chiudendo quello corrente e aprendo il nuovo.
     *
     * @param frameCorrente frame da chiudere
     * @param nuovoFrame    frame da aprire
     */
    private void cambiaFrame(JFrame frameCorrente, JFrame nuovoFrame) {
        mostraFrame(nuovoFrame);
        chiudiFrame(frameCorrente);
    }

    /**
     * Mostra un frame applicando lo stile predefinito.
     *
     * @param frame frame da mostrare
     */
    private void mostraFrame(JFrame frame) {
        applyStyleFrame(frame);
        frame.setVisible(true);
    }

    /**
     * Chiude un frame e libera le risorse.
     *
     * @param frame frame da chiudere
     */
    private void chiudiFrame(JFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }

    // endregion

}