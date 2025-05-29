package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta un problema pubblicato per un hackathon.
 * I giudici pubblicano i problemi che i team devono risolvere.
 */
public class Problema {

    // region Proprietà

    private int problemaId;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataPubblicazione;

    // endregion

    // region Costruttori

    public Problema() {
        this.dataPubblicazione = LocalDateTime.now();
    }

    public Problema(String titolo, String descrizione) {
        this();
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    // endregion

    // region Getter e Setter

    public int getProblemaId() {
        return problemaId;
    }

    public void setProblemaId(int problemaId) {
        this.problemaId = problemaId;
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

    public LocalDateTime getDataPubblicazione() {
        return dataPubblicazione;
    }

    public void setDataPubblicazione(LocalDateTime dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    // endregion

    // region Business

    public boolean pubblicaProblema() {
        if (titolo == null || titolo.trim().isEmpty()) {
            return false;
        }
        if (descrizione == null || descrizione.trim().isEmpty()) {
            return false;
        }
        this.dataPubblicazione = LocalDateTime.now();
        return true;
    }

    public void modificaDescrizione(String nuova) {
        if (nuova != null && !nuova.trim().isEmpty()) {
            this.descrizione = nuova.trim();
        }
    }

    public String getDettagli() {
        return String.format("Titolo: %s\nDescrizione: %s\nPubblicato: %s", titolo, descrizione, dataPubblicazione);
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Problema problema = (Problema) obj;
        return problemaId == problema.problemaId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemaId);
    }

    @Override
    public String toString() {
        return String.format("Problema{id=%d, titolo='%s'}", problemaId, titolo);
    }

    // endregion
}
