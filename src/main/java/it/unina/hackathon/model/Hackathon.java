package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.HackathonResponse;

import java.time.LocalDateTime;
import java.util.Objects;

public class Hackathon {

    // region Proprietà

    private int hackathonId;
    private String titolo;
    private String descrizione;
    private String sede;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private LocalDateTime dataChiusuraRegistrazioni;
    private int maxIscritti;
    private int maxDimensioneTeam;
    private HackathonStatus status;
    private int organizzatoreId;
    private LocalDateTime dataCreazione;

    // endregion

    // region Costruttori

    public Hackathon() {
        apriRegistrazioni();
    }

    public Hackathon(String titolo, String descrizione, String sede, LocalDateTime dataInizio, LocalDateTime dataFine, int maxIscritti, int maxDimensioneTeam) {
        this();
        this.titolo = titolo != null ? titolo.trim() : null;
        this.descrizione = descrizione != null ? descrizione.trim() : null;
        this.sede = sede != null ? sede.trim() : null;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dataChiusuraRegistrazioni = dataInizio.minusDays(2);
        this.maxIscritti = maxIscritti;
        this.maxDimensioneTeam = maxDimensioneTeam;
    }

    // endregion

    // region Getter e Setter

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public LocalDateTime getDataChiusuraRegistrazioni() {
        return dataChiusuraRegistrazioni;
    }

    public int getMaxIscritti() {
        return maxIscritti;
    }

    public int getMaxDimensioneTeam() {
        return maxDimensioneTeam;
    }

    public HackathonStatus getStatus() {
        return status;
    }

    public void setStatus(HackathonStatus status) {
        this.status = status;
    }

    public int getOrganizzatoreId() {
        return organizzatoreId;
    }

    public void setOrganizzatoreId(int organizzatoreId) {
        this.organizzatoreId = organizzatoreId;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    // endregion

    // region Business

    public HackathonResponse validaCreating() {
        // Validazione titolo
        if (titolo == null || titolo.isEmpty()) {
            return new HackathonResponse(null, "Titolo è obbligatorio!");
        }

        // Validazione sede
        if (sede == null || sede.isEmpty()) {
            return new HackathonResponse(null, "Sede è obbligatoria!");
        }

        // Validazione date
        if (dataInizio == null) {
            return new HackathonResponse(null, "Data inizio è obbligatoria!");
        }
        if (dataFine == null) {
            return new HackathonResponse(null, "Data fine è obbligatoria!");
        }

        LocalDateTime now = LocalDateTime.now();
        if (dataInizio.isBefore(now.plusDays(3))) {
            return new HackathonResponse(null, "L'hackathon deve essere programmato almeno 3 giorni nel futuro!");
        }
        if (dataFine.isBefore(dataInizio)) {
            return new HackathonResponse(null, "Data fine deve essere successiva alla data inizio!");
        }

        // Validazione numeri
        if (maxIscritti <= 0) {
            return new HackathonResponse(null, "Numero massimo iscritti deve essere positivo!");
        }
        if (maxIscritti > 1000) {
            return new HackathonResponse(null, "Numero massimo iscritti non può superare 1000!");
        }
        if (maxDimensioneTeam <= 0) {
            return new HackathonResponse(null, "Dimensione massima team deve essere positiva!");
        }
        if (maxDimensioneTeam > 10) {
            return new HackathonResponse(null, "Dimensione massima team non può superare 10!");
        }

        return new HackathonResponse(this, "Validazione completata con successo!");
    }

    public void apriRegistrazioni() {
        this.status = HackathonStatus.REGISTRAZIONI_APERTE;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hackathon hackathon = (Hackathon) obj;
        return hackathonId == hackathon.hackathonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hackathonId);
    }

    @Override
    public String toString() {
        return String.format("Hackathon{id=%d, titolo='%s', sede='%s', status=%s}", hackathonId, titolo, sede, status);
    }

    // endregion
}
