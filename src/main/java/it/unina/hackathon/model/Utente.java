package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta un utente della piattaforma hackathon.
 * Un utente può assumere diversi ruoli: partecipante, organizzatore, giudice.
 * La classe gestisce le funzionalità comuni a tutti i ruoli.
 *
 * <p>Vincoli di business:
 * <ul>
 *   <li>Username univoco</li>
 *   <li>Email univoca e formato valido</li>
 *   <li>Nome e cognome obbligatori</li>
 * </ul>
 */
public class Utente {

    // region Proprietà

    /**
     * Identificativo univoco dell'utente
     */
    private int utenteId;

    /**
     * Nome utente univoco per login
     */
    private String username;

    /**
     * Indirizzo email univoco
     */
    private String email;

    /**
     * Hash della password per sicurezza
     */
    private String passwordHash;

    /**
     * Nome anagrafico dell'utente
     */
    private String nome;

    /**
     * Cognome anagrafico dell'utente
     */
    private String cognome;

    /**
     * Data di registrazione nella piattaforma
     */
    private LocalDateTime dataRegistrazione;

    /**
     * Flag che indica se l'account è attivo
     */
    private boolean attivo;

    // endregion

    // region Costruttori

    /**
     * Costruttore di default.
     */
    public Utente() {
        this.dataRegistrazione = LocalDateTime.now();
        this.attivo = true;
    }

    /**
     * Costruttore con parametri principali.
     *
     * @param username     nome utente univoco
     * @param email        indirizzo email
     * @param passwordHash hash della password
     * @param nome         nome anagrafico
     * @param cognome      cognome anagrafico
     */
    public Utente(String username, String email, String passwordHash, String nome, String cognome) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nome = nome;
        this.cognome = cognome;
    }

    // endregion

    // region Getter e Setter

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDateTime getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDateTime dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    // endregion

    // region Business

    /**
     * Registra un nuovo utente nel sistema.
     * Imposta la data di registrazione e attiva l'account.
     *
     * @return true se la registrazione è avvenuta con successo
     */
    public boolean registrati() {
        // Validazione input
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        if (cognome == null || cognome.trim().isEmpty()) {
            return false;
        }

        // Imposta valori di registrazione di default: utente attivo e data di registrazione
        this.dataRegistrazione = LocalDateTime.now();
        this.attivo = true;

        return true;
    }

    /**
     * Autentica l'utente verificando la password.
     *
     * @param password password da verificare
     * @return true se l'autenticazione è riuscita
     */
    public boolean autenticati(String password) {
        if (password == null || !attivo) {
            return false;
        }

        // TODO: Implementare verifica hash sicura (es. BCrypt)
        // TODO: Per ora confronto diretto (da migliorare in produzione)
        return password.equals(this.passwordHash);
    }

    /**
     * Modifica il profilo utente.
     * Permette di aggiornare nome, cognome ed email.
     */
    public void modificaProfilo() {
        // TODO: Metodo che verrà esteso dalla GUI per permettere
        // TODO: la modifica interattiva del profilo utente
        // TODO: Per ora implementazione vuota, sarà chiamato dal Controller
    }

    /**
     * Verifica se l'utente può assumere il ruolo di organizzatore.
     *
     * @return true se può essere organizzatore
     */
    public boolean isOrganizzatore() {
        // Un utente attivo può organizzare hackathon
        // TODO: Valutare di passare l'Hackathon id per conoscere il ruolo dell'utente
        return attivo;
    }

    /**
     * Verifica se l'utente può assumere il ruolo di giudice.
     *
     * @return true se può essere giudice
     */
    public boolean isGiudice() {
        // Un utente attivo può essere invitato come giudice
        // TODO: Valutare di passare l'Hackathon id per conoscere il ruolo dell'utente
        return attivo;
    }

    /**
     * Verifica se l'utente può assumere il ruolo di partecipante.
     *
     * @return true se può essere partecipante
     */
    public boolean isPartecipante() {
        // Un utente attivo può partecipare agli hackathon
        // TODO: Valutare di passare l'Hackathon id per conoscere il ruolo dell'utente
        return attivo;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Utente utente = (Utente) obj;
        return utenteId == utente.utenteId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(utenteId);
    }

    @Override
    public String toString() {
        return String.format("Utente{id=%d, username='%s', nome='%s %s', attivo=%b}", utenteId, username, nome, cognome, attivo);
    }

    // endregion
}