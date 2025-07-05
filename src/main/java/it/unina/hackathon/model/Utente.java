package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.responses.UtenteResponse;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

public class Utente {

    // Regex per validazione email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    // Regex per validazione username
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]+$");

    // region Proprietà

    private final String username;
    private final String password;
    private int utenteId;
    private String email;
    private String nome;
    private String cognome;
    private TipoUtente tipoUtente;
    private LocalDateTime dataRegistrazione;

    // endregion

    // region Costruttori

    public Utente(String username, String password) {
        this.username = username != null ? username.trim() : null;
        this.password = password;
    }

    public Utente(String username, String email, String password, String nome, String cognome, TipoUtente tipoUtente) {
        this.username = username != null ? username.trim() : null;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
        this.nome = nome != null ? nome.trim() : null;
        this.cognome = cognome != null ? cognome.trim() : null;
        this.tipoUtente = tipoUtente;
    }

    // endregion

    // region Getters e Setters

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public TipoUtente getTipoUtente() {
        return tipoUtente;
    }

    public LocalDateTime getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDateTime dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    // endregion

    // region Business

    public UtenteResponse validaLogin() {
        if (this.username == null || this.username.isEmpty()) {
            return new UtenteResponse(null, "Username è obbligatorio!");
        }
        if (this.password == null || this.password.isEmpty()) {
            return new UtenteResponse(null, "Password è obbligatoria!");
        }
        return new UtenteResponse(new Utente(this.username, this.password), "Credenziali potenzialmente valide");
    }

    public UtenteResponse validaRegistrazione(String confermaPassword) {
        // Validazione nome
        if (nome == null || nome.isEmpty()) {
            return new UtenteResponse(null, "Nome è obbligatorio!");
        }

        // Validazione cognome
        if (cognome == null || cognome.isEmpty()) {
            return new UtenteResponse(null, "Cognome è obbligatorio!");
        }

        // Validazione username
        if (username == null || username.isEmpty()) {
            return new UtenteResponse(null, "Username è obbligatorio!");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return new UtenteResponse(null, "Username può contenere solo lettere, numeri, punti e underscore!");
        }

        // Validazione email
        if (email == null || email.isEmpty()) {
            return new UtenteResponse(null, "Email è obbligatoria!");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new UtenteResponse(null, "Formato email non valido!");
        }

        // Validazione password
        if (password == null || password.isEmpty()) {
            return new UtenteResponse(null, "Password è obbligatoria!");
        }

        // Validazione conferma password
        if (!password.equals(confermaPassword)) {
            return new UtenteResponse(null, "Password e conferma password devono coincidere!");
        }

        // Validazione tipo utente
        if (tipoUtente == null) {
            return new UtenteResponse(null, "Tipo utente è obbligatorio!");
        }

        return new UtenteResponse(this, "Validazione completata con successo!");
    }

    public UtenteResponse autenticati(Utente utenteDaAutenticare) {
        boolean autenticazione = this.username.equals(utenteDaAutenticare.getUsername()) && this.password.equals(utenteDaAutenticare.getPassword());
        if (autenticazione) {
            return new UtenteResponse(utenteDaAutenticare, "Login avvenuto con successo!");
        } else {
            return new UtenteResponse(null, "Password errata!");
        }
    }

    public boolean isOrganizzatore() {
        return tipoUtente == TipoUtente.ORGANIZZATORE;
    }

    public boolean isGiudice() {
        return tipoUtente == TipoUtente.GIUDICE;
    }

    public boolean isPartecipante() {
        return tipoUtente == TipoUtente.PARTECIPANTE;
    }

    public String getNomeCompleto() {
        return nome + " " + cognome;
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
        return String.format("Utente{id=%d, username='%s', nome='%s', tipo=%s}", utenteId, username, getNomeCompleto(), tipoUtente);
    }

    // endregion
}