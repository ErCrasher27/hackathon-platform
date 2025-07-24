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

    /**
     * Istanza singleton del controller
     */
    private static HackathonController instance;

    /**
     * DAO per la gestione degli utenti
     */
    private final UtenteDAO utenteDAO;

    /**
     * DAO per la gestione degli hackathon
     */
    private final HackathonDAO hackathonDAO;

    /**
     * DAO per la gestione dei team
     */
    private final TeamDAO teamDAO;

    /**
     * DAO per la gestione delle associazioni giudice-hackathon
     */
    private final GiudiceHackathonDAO giudiceHackathonDAO;

    /**
     * DAO per la gestione degli inviti ai giudici
     */
    private final InvitoGiudiceDAO invitoGiudiceDAO;

    /**
     * DAO per la gestione dei problemi
     */
    private final ProblemaDAO problemaDAO;

    /**
     * DAO per la gestione dei commenti
     */
    private final CommentoDAO commentoDAO;

    /**
     * DAO per la gestione dei voti
     */
    private final VotoDAO votoDAO;

    /**
     * DAO per la gestione dei progressi
     */
    private final ProgressoDAO progressoDAO;

    /**
     * DAO per la gestione delle registrazioni
     */
    private final RegistrazioneDAO registrazioneDAO;

    /**
     * DAO per la gestione degli inviti ai team
     */
    private final InvitoTeamDAO invitoTeamDAO;

    /**
     * Utente attualmente autenticato nel sistema
     */
    private Utente utenteCorrente;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Inizializza tutti i DAO necessari per il funzionamento del controller.
     */
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

    /**
     * Restituisce l'istanza singleton del controller.
     *
     * @return l'istanza unica di HackathonController
     */
    public static HackathonController getInstance() {
        if (instance == null) {
            instance = new HackathonController();
        }
        return instance;
    }

    /**
     * Esegue il login di un utente nel sistema.
     * Valida le credenziali, autentica l'utente e avvia la sessione.
     *
     * @param username il nome utente
     * @param password la password dell'utente
     * @return risposta contenente l'utente autenticato o l'errore
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
     * Valida i dati, verifica l'unicità di username ed email, e salva l'utente.
     *
     * @param nome             il nome dell'utente
     * @param cognome          il cognome dell'utente
     * @param email            l'indirizzo email dell'utente
     * @param username         il nome utente scelto
     * @param password         la password scelta
     * @param confermaPassword la conferma della password
     * @param tipoUtente       il tipo di utente (partecipante, organizzatore, giudice)
     * @return risposta contenente l'utente registrato o l'errore
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
     * Effettua il logout dell'utente corrente, terminando la sessione.
     */
    public void effettuaLogout() {
        this.utenteCorrente = null;
    }

    /**
     * Avvia una nuova sessione utente impostando l'utente corrente.
     *
     * @param utente l'utente che ha effettuato l'accesso
     */
    private void avviaSessioneUtente(Utente utente) {
        this.utenteCorrente = utente;
    }

    /**
     * Restituisce l'utente attualmente autenticato.
     *
     * @return l'utente corrente o null se nessuno è autenticato
     */
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Restituisce l'ID dell'utente attualmente autenticato.
     *
     * @return l'ID dell'utente corrente
     */
    private int getIdUtenteCorrente() {
        return utenteCorrente.getUtenteId();
    }

    /**
     * Restituisce il tipo dell'utente attualmente autenticato.
     *
     * @return il tipo di utente corrente
     */
    private TipoUtente getTipoUtenteCorrente() {
        return utenteCorrente.getTipoUtente();
    }

    /**
     * Crea un nuovo hackathon con i parametri specificati.
     * L'hackathon viene associato all'organizzatore corrente e aperto alle registrazioni.
     *
     * @param titolo        il titolo dell'hackathon
     * @param descrizione   la descrizione dell'hackathon
     * @param sede          la sede dove si svolge l'hackathon
     * @param dataInizio    la data e ora di inizio
     * @param dataFine      la data e ora di fine
     * @param maxIscrizioni il numero massimo di iscrizioni
     * @param maxMembriTeam il numero massimo di membri per team
     * @return risposta contenente l'hackathon creato o l'errore
     */
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

    /**
     * Recupera tutti gli hackathon organizzati dall'utente corrente.
     *
     * @return risposta contenente la lista degli hackathon organizzati
     */
    public HackathonListResponse getHackathonOrganizzatore() {
        return hackathonDAO.getHackathonsByOrganizzatore(getIdUtenteCorrente());
    }

    /**
     * Modifica lo stato di un hackathon esistente.
     *
     * @param hackathonId l'ID dell'hackathon da modificare
     * @param nuovoStato  il nuovo stato da assegnare
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult modificaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        return hackathonDAO.cambiaStatoHackathon(hackathonId, nuovoStato);
    }

    /**
     * Recupera la lista dei giudici non ancora invitati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei giudici disponibili
     */
    public UtenteListResponse getGiudiciNonInvitati(int hackathonId) {
        return utenteDAO.getUtentiGiudiciNonInvitatiByHackathon(hackathonId);
    }

    /**
     * Recupera la lista dei giudici già invitati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei giudici invitati
     */
    public UtenteListResponse getGiudiciInvitati(int hackathonId) {
        return utenteDAO.getUtentiGiudiciInvitatiByHackathon(hackathonId);
    }

    /**
     * Invia un invito a un giudice per partecipare a un hackathon.
     *
     * @param utenteGiudiceId l'ID del giudice da invitare
     * @param hackathonId     l'ID dell'hackathon
     * @return risposta indicante il successo o fallimento dell'invito
     */
    public ResponseResult invitaGiudice(int utenteGiudiceId, int hackathonId) {
        return invitoGiudiceDAO.saveInvitoGiudice(getIdUtenteCorrente(), utenteGiudiceId, hackathonId);
    }

    /**
     * Recupera l'associazione giudice-hackathon per l'utente corrente.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente l'associazione giudice-hackathon
     */
    public GiudiceHackathonResponse getGiudiceHackathon(int hackathonId) {
        return giudiceHackathonDAO.getGiudiceHackathonByUtenteGiudiceHackathon(getIdUtenteCorrente(), hackathonId);
    }

    /**
     * Conta il numero di giudici che hanno accettato l'invito per un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di giudici accettati
     */
    public ResponseIntResult contaGiudiciAccettati(int hackathonId) {
        return giudiceHackathonDAO.contaGiudiceHackathonByHackathon(hackathonId);
    }

    /**
     * Recupera tutti i partecipanti iscritti a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei partecipanti
     */
    public UtenteListResponse getPartecipantiHackathon(int hackathonId) {
        return utenteDAO.getUtentiPartecipantiByHackathon(hackathonId);
    }

    /**
     * Recupera tutti i team che partecipano a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei team
     */
    public TeamListResponse getTeamsHackathon(int hackathonId) {
        return teamDAO.getTeamsByHackathon(hackathonId);
    }

    /**
     * Conta il numero totale di partecipanti registrati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di registrazioni
     */
    public ResponseIntResult contaPartecipantiRegistrati(int hackathonId) {
        return registrazioneDAO.contaRegistrazioniByHackathon(hackathonId);
    }

    /**
     * Conta il numero di membri di un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente il numero di membri del team
     */
    public ResponseIntResult contaNumeroMembri(int teamId) {
        return teamDAO.contaRegistrazioniByTeam(teamId);
    }

    /**
     * Conta il numero totale di team formati in un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il numero di team
     */
    public ResponseIntResult contaTeamFormati(int hackathonId) {
        return teamDAO.contaTeamByHackathon(hackathonId);
    }

    /**
     * Recupera il team di cui fa parte l'utente partecipante in un hackathon.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param hackathonId          l'ID dell'hackathon
     * @return risposta contenente il team del partecipante
     */
    public TeamResponse getTeamPartecipante(int utentePartecipanteId, int hackathonId) {
        return teamDAO.getTeamByPartecipanteHackathon(utentePartecipanteId, hackathonId);
    }

    /**
     * Recupera il team di cui fa parte l'utente corrente in un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente il team del partecipante
     */
    public TeamResponse getTeamPartecipante(int hackathonId) {
        return teamDAO.getTeamByPartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
    }

    /**
     * Recupera il team associato a una registrazione specifica.
     *
     * @param registrazioneId l'ID della registrazione
     * @return risposta contenente il team trovato
     */
    public TeamResponse getTeam(int registrazioneId) {
        return teamDAO.getTeamByRegistrazione(registrazioneId);
    }

    /**
     * Recupera tutti gli inviti ricevuti dall'utente giudice corrente.
     *
     * @return risposta contenente la lista degli inviti ricevuti
     */
    public InvitoGiudiceListResponse getInvitiGiudiceRicevuti() {
        return invitoGiudiceDAO.getInvitiGiudiceByUtenteGiudice(getIdUtenteCorrente());
    }

    /**
     * Recupera un invito specifico per un giudice e un hackathon.
     *
     * @param utenteGiudiceId l'ID del giudice
     * @param hackathonId     l'ID dell'hackathon
     * @return risposta contenente l'invito trovato
     */
    public InvitoGiudiceResponse getInvitoByInvitatoHackathon(int utenteGiudiceId, int hackathonId) {
        return invitoGiudiceDAO.getInvitoGiudiceByUtenteGiudiceHackathon(utenteGiudiceId, hackathonId);
    }

    /**
     * Accetta un invito per diventare giudice di un hackathon.
     *
     * @param invitoGiudiceId l'ID dell'invito da accettare
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult accettaInvitoGiudice(int invitoGiudiceId) {
        return invitoGiudiceDAO.aggiornaStatoInvito(invitoGiudiceId, StatoInvito.ACCEPTED);
    }

    /**
     * Rifiuta un invito per diventare giudice di un hackathon.
     *
     * @param invitoGiudiceId l'ID dell'invito da rifiutare
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult rifiutaInvitoGiudice(int invitoGiudiceId) {
        return invitoGiudiceDAO.aggiornaStatoInvito(invitoGiudiceId, StatoInvito.DECLINED);
    }

    /**
     * Recupera tutti gli hackathon per cui l'utente corrente è stato assegnato come giudice.
     *
     * @return risposta contenente la lista degli hackathon da giudicare
     */
    public HackathonListResponse getHackathonAssegnati() {
        return hackathonDAO.getHackathonByGiudice(getIdUtenteCorrente());
    }

    /**
     * Recupera i dettagli completi di un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente i dettagli dell'hackathon
     */
    public HackathonResponse getDettagliHackathon(int hackathonId) {
        return hackathonDAO.getHackathonById(hackathonId);
    }

    /**
     * Pubblica un nuovo problema per un hackathon.
     *
     * @param giudiceHackathonId l'ID dell'associazione giudice-hackathon
     * @param titolo             il titolo del problema
     * @param descrizione        la descrizione dettagliata del problema
     * @return risposta contenente il problema pubblicato
     */
    public ProblemaResponse pubblicaProblema(int giudiceHackathonId, String titolo, String descrizione) {
        Problema nuovoProblema = new Problema(titolo, descrizione, giudiceHackathonId);
        return problemaDAO.saveProblema(nuovoProblema);
    }

    /**
     * Recupera tutti i problemi associati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei problemi
     */
    public ProblemaListResponse getProblemiHackathon(int hackathonId) {
        return problemaDAO.getProblemiByHackathon(hackathonId);
    }

    /**
     * Elimina un problema esistente.
     *
     * @param problemaId l'ID del problema da eliminare
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult eliminaProblema(int problemaId) {
        return problemaDAO.rimuoviProblema(problemaId);
    }

    /**
     * Recupera tutti i progressi pubblicati da un team.
     *
     * @param teamId l'ID del team
     * @return risposta contenente la lista dei progressi
     */
    public ProgressoListResponse getProgressiTeam(int teamId) {
        return progressoDAO.getProgressiByTeam(teamId);
    }

    /**
     * Scrive un commento su un progresso di un team.
     *
     * @param progressoId        l'ID del progresso da commentare
     * @param giudiceHackathonId l'ID dell'associazione giudice-hackathon
     * @param testoCommento      il testo del commento
     * @return risposta contenente il commento scritto
     */
    public CommentoResponse scriviCommento(int progressoId, int giudiceHackathonId, String testoCommento) {
        Commento nuovoCommento = new Commento(progressoId, giudiceHackathonId, testoCommento);
        return commentoDAO.saveCommento(nuovoCommento);
    }

    /**
     * Recupera tutti i commenti associati a un progresso.
     *
     * @param progressoId l'ID del progresso
     * @return risposta contenente la lista dei commenti
     */
    public CommentoListResponse getCommentiProgresso(int progressoId) {
        return commentoDAO.getCommentiByProgresso(progressoId);
    }

    /**
     * Assegna un voto a un team da parte di un giudice.
     *
     * @param giudiceHackathonId l'ID dell'associazione giudice-hackathon
     * @param valoreVoto         il valore numerico del voto (0-10)
     * @param teamId             l'ID del team da valutare
     * @return risposta contenente il voto assegnato o l'errore
     */
    public VotoResponse assegnaVoto(int giudiceHackathonId, int valoreVoto, int teamId) {
        Voto nuovoVoto = new Voto(teamId, giudiceHackathonId, valoreVoto);

        if (!nuovoVoto.validaVoto()) {
            return new VotoResponse(null, "Voto non valido! Il valore deve essere compreso tra 0 e 10.");
        }

        return votoDAO.saveVoto(nuovoVoto);
    }

    /**
     * Recupera il voto assegnato dall'utente corrente a un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente il voto trovato
     */
    public VotoResponse getVotoTeam(int teamId) {
        return votoDAO.getVotoByGiudiceTeamHackathon(getIdUtenteCorrente(), teamId);
    }

    /**
     * Recupera la classifica completa di un hackathon basata sui voti.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la classifica ordinata
     */
    public VotoListResponse getClassificaHackathon(int hackathonId) {
        return votoDAO.getClassificaByHackathon(hackathonId);
    }

    /**
     * Recupera tutti gli hackathon con registrazioni aperte.
     *
     * @return risposta contenente la lista degli hackathon disponibili
     */
    public HackathonListResponse getHackathonDisponibili() {
        return hackathonDAO.getHackathonsByHackathonStatus(HackathonStatus.REGISTRAZIONI_APERTE);
    }

    /**
     * Recupera tutti gli hackathon a cui è iscritto l'utente corrente.
     *
     * @return risposta contenente la lista degli hackathon dell'utente
     */
    public HackathonListResponse getHackathonRegistrati() {
        return hackathonDAO.getHackathonsByPartecipante(getIdUtenteCorrente());
    }

    /**
     * Registra l'utente corrente a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon a cui registrarsi
     * @return risposta contenente la registrazione effettuata
     */
    public RegistrazioneResponse registratiAdHackathon(int hackathonId) {
        Registrazione nuovaRegistrazione = new Registrazione(getIdUtenteCorrente(), hackathonId);
        return registrazioneDAO.saveRegistrazione(nuovaRegistrazione);
    }

    /**
     * Annulla una registrazione esistente.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult annullaRegistrazione(int hackathonId) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return registrazioneDAO.rimuoviRegistrazione(registrazioneCorrente.registrazione().getRegistrazioneId());
        } else {
            return new ResponseResult(false, "Impossibile annullare la registrazione!");
        }
    }

    /**
     * Crea un nuovo team per un hackathon e assegna l'utente corrente come leader.
     *
     * @param hackathonId l'ID dell'hackathon
     * @param nomeTeam    il nome del team da creare
     * @return risposta contenente il team creato
     */
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

    /**
     * Rimuove l'utente indicato dal suo team in un hackathon.
     *
     * @param utentePartecipanteId l'ID dell'utente partecipante
     * @param hackathonId          l'ID dell'hackathon
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult rimuoviDalTeam(int utentePartecipanteId, int hackathonId) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(utentePartecipanteId, hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return registrazioneDAO.aggiornaTeamNullConRuoloNull(registrazioneCorrente.registrazione().getRegistrazioneId());
        } else {
            return new ResponseResult(false, "Impossibile rimuovere dal team!");
        }
    }

    /**
     * Fa abbandonare l'utente corrente dal suo team in un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult abbandonaTeam(int hackathonId) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return registrazioneDAO.aggiornaTeamNullConRuoloNull(registrazioneCorrente.registrazione().getRegistrazioneId());
        } else {
            return new ResponseResult(false, "Impossibile abbandonare il team!");
        }
    }

    /**
     * Invia un invito a un utente per unirsi al team dell'utente corrente.
     *
     * @param hackathonId          l'ID dell'hackathon
     * @param utentePartecipanteId l'ID dell'utente da invitare
     * @param messaggio            messaggio personalizzato dell'invito
     * @return risposta indicante il successo o fallimento dell'invito
     */
    public ResponseResult invitaUtenteInTeam(int hackathonId, int utentePartecipanteId, String messaggio) {
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            return invitoTeamDAO.saveInvitoUtente(registrazioneCorrente.registrazione().getRegistrazioneId(), utentePartecipanteId, messaggio);
        } else {
            return new ResponseResult(false, "Impossibile invitare l'utente!");
        }
    }

    /**
     * Recupera tutti i membri di un team specifico.
     *
     * @param teamId l'ID del team
     * @return risposta contenente la lista dei membri del team
     */
    public RegistrazioneListResponse getMembriTeam(int teamId) {
        return registrazioneDAO.getRegistrazioniByTeam(teamId);
    }

    /**
     * Verifica se l'utente corrente è il leader di un team specifico.
     *
     * @param teamId l'ID del team da verificare
     * @return risposta indicante se l'utente è leader del team
     */
    public ResponseResult verificaLeaderTeam(int teamId) {
        return registrazioneDAO.isLeaderByUtentePartecipanteTeam(getIdUtenteCorrente(), teamId);
    }

    /**
     * Recupera tutti gli inviti al team ricevuti dall'utente corrente per un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista degli inviti ricevuti
     */
    public InvitoTeamListResponse getInvitiTeamRicevuti(int hackathonId) {
        return invitoTeamDAO.getInvitiTeamByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
    }

    /**
     * Risponde a un invito al team (accetta o rifiuta).
     *
     * @param invitoTeamId l'ID dell'invito
     * @param risposta     lo stato di risposta all'invito
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult rispondiInvitoTeam(int invitoTeamId, StatoInvito risposta) {
        return invitoTeamDAO.aggiornaStatoInvito(invitoTeamId, risposta);
    }

    /**
     * Carica un nuovo progresso per il team dell'utente corrente.
     *
     * @param hackathonId   l'ID dell'hackathon
     * @param urlDocumento  l'URL del documento di progresso
     * @param documentoNome nome del documento di progresso
     * @return risposta contenente il progresso caricato
     */
    public ProgressoResponse caricaProgresso(int hackathonId, String urlDocumento, String documentoNome) {
        Progresso nuovoProgresso = new Progresso(urlDocumento, documentoNome);
        RegistrazioneResponse registrazioneCorrente = registrazioneDAO.getRegistrazioneByUtentePartecipanteHackathon(getIdUtenteCorrente(), hackathonId);
        if (registrazioneCorrente.registrazione() != null) {
            nuovoProgresso.setCaricatoDaRegistrazioneId(registrazioneCorrente.registrazione().getRegistrazioneId());
            return progressoDAO.saveProgresso(nuovoProgresso);
        } else {
            return new ProgressoResponse(null, "Impossibile caricare il progresso!");
        }
    }

    /**
     * Elimina un progresso esistente.
     *
     * @param progressoId l'ID del progresso da eliminare
     * @return risposta indicante il successo o fallimento dell'operazione
     */
    public ResponseResult eliminaProgresso(int progressoId) {
        return progressoDAO.rimuoviProgresso(progressoId);
    }

    /**
     * Recupera tutti i partecipanti di un hackathon che non hanno ancora un team.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei partecipanti disponibili
     */
    public RegistrazioneListResponse getPartecipantiDisponibili(int hackathonId) {
        return registrazioneDAO.getRegistratiConTeamNullByHackathon(hackathonId);
    }

    /**
     * Naviga alla home page appropriata per il tipo di utente corrente.
     *
     * @param frameCorrente il frame attualmente visualizzato
     */
    public void vaiAllaHome(JFrame frameCorrente) {
        JFrame nuovoFrame = creaFrameHome(getTipoUtenteCorrente());
        if (nuovoFrame != null) {
            cambiaFrame(frameCorrente, nuovoFrame);
        }
    }

    /**
     * Naviga alla pagina di login.
     *
     * @param frameCorrente il frame attualmente visualizzato
     */
    public void vaiAlLogin(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new LoginGUI().getFrame());
    }

    /**
     * Naviga alla pagina di registrazione.
     *
     * @param frameCorrente il frame attualmente visualizzato
     */
    public void vaiAllaRegistrazione(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new RegistrazioneGUI().getFrame());
    }

    /**
     * Naviga alla pagina di creazione hackathon.
     *
     * @param frameCorrente il frame attualmente visualizzato
     */
    public void vaiACreareHackathon(JFrame frameCorrente) {
        cambiaFrame(frameCorrente, new CreaHackathonGUI().getFrame());
    }

    /**
     * Naviga alla pagina di gestione di un hackathon specifico.
     *
     * @param frameCorrente il frame attualmente visualizzato
     * @param hackathonId   l'ID dell'hackathon da gestire
     */
    public void vaiAGestireHackathon(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciHackathonGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla pagina di valutazione progetti per un hackathon.
     *
     * @param frameCorrente il frame attualmente visualizzato
     * @param hackathonId   l'ID dell'hackathon da valutare
     */
    public void vaiAValutareProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new ValutazioneProgettoGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla pagina di gestione problemi per un hackathon.
     *
     * @param frameCorrente il frame attualmente visualizzato
     * @param hackathonId   l'ID dell'hackathon
     */
    public void vaiAGestireProblemi(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProblemiGUI(hackathonId).getFrame());
    }

    /**
     * Naviga alla pagina di gestione progetto per un hackathon.
     *
     * @param frameCorrente il frame attualmente visualizzato
     * @param hackathonId   l'ID dell'hackathon
     */
    public void vaiAGestireProgetto(JFrame frameCorrente, int hackathonId) {
        cambiaFrame(frameCorrente, new GestisciProgettoGUI(hackathonId).getFrame());
    }

    /**
     * Crea il frame home appropriato in base al tipo di utente.
     *
     * @param tipoUtente il tipo di utente per cui creare la home
     * @return il frame home corrispondente al tipo di utente
     */
    private JFrame creaFrameHome(TipoUtente tipoUtente) {
        return switch (tipoUtente) {
            case ORGANIZZATORE -> new HomeOrganizzatoreGUI().getFrame();
            case GIUDICE -> new HomeGiudiceGUI().getFrame();
            case PARTECIPANTE -> new HomePartecipanteGUI().getFrame();
        };
    }

    /**
     * Cambia il frame attualmente visualizzato con uno nuovo.
     *
     * @param frameCorrente il frame da chiudere
     * @param nuovoFrame    il frame da mostrare
     */
    private void cambiaFrame(JFrame frameCorrente, JFrame nuovoFrame) {
        mostraFrame(nuovoFrame);
        chiudiFrame(frameCorrente);
    }

    /**
     * Mostra un frame applicando lo stile standard.
     *
     * @param frame il frame da mostrare
     */
    private void mostraFrame(JFrame frame) {
        applyStyleFrame(frame);
        frame.setVisible(true);
    }

    /**
     * Chiude e libera le risorse di un frame.
     *
     * @param frame il frame da chiudere
     */
    private void chiudiFrame(JFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }

}