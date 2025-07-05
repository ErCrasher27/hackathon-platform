package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Problema {

    // region Proprietà

    private int problemaId;
    private int hackathonId;
    private int pubblicatoDaId;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataPubblicazione;
    private Utente pubblicatoDa;

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

    public Problema(String titolo, String descrizione, int hackathonId, int pubblicatoDaId) {
        this(titolo, descrizione);
        this.hackathonId = hackathonId;
        this.pubblicatoDaId = pubblicatoDaId;
    }

    // endregion

    // region Getter e Setter

    public int getProblemaId() {
        return problemaId;
    }

    public void setProblemaId(int problemaId) {
        this.problemaId = problemaId;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public int getPubblicatoDaId() {
        return pubblicatoDaId;
    }

    public void setPubblicatoDaId(int pubblicatoDaId) {
        this.pubblicatoDaId = pubblicatoDaId;
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

    public Utente getPubblicatoDa() {
        return pubblicatoDa;
    }

    public void setPubblicatoDa(Utente pubblicatoDa) {
        this.pubblicatoDa = pubblicatoDa;
    }

    // endregion

    // region Business

    public boolean pubblicaProblema() {
        if (!validaProblema()) {
            return false;
        }
        this.dataPubblicazione = LocalDateTime.now();
        return true;
    }

    public boolean validaProblema() {
        if (titolo == null || titolo.trim().isEmpty()) {
            return false;
        }
        if (descrizione == null || descrizione.trim().isEmpty()) {
            return false;
        }
        if (hackathonId <= 0) {
            return false;
        }
        return pubblicatoDaId > 0;
    }

    public void modificaDescrizione(String nuovaDescrizione) {
        if (nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
            this.descrizione = nuovaDescrizione.trim();
        }
    }

    public void modificaTitolo(String nuovoTitolo) {
        if (nuovoTitolo != null && !nuovoTitolo.trim().isEmpty()) {
            this.titolo = nuovoTitolo.trim();
        }
    }

    public String getDettagliCompleti() {
        return String.format("Titolo: %s\n\nDescrizione:\n%s\n\nPubblicato: %s\nDa: %s", titolo, descrizione, dataPubblicazione, pubblicatoDa != null ? pubblicatoDa.getNomeCompleto() : "N/A");
    }

    public String getAnteprimaDescrizione() {
        if (descrizione == null || descrizione.isEmpty()) {
            return "Nessuna descrizione";
        }
        return descrizione.length() > 100 ? descrizione.substring(0, 100) + "..." : descrizione;
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
        return String.format("Problema{id=%d, titolo='%s', hackathon=%d, pubblicato=%s}", problemaId, titolo, hackathonId, dataPubblicazione != null ? dataPubblicazione.toLocalDate() : "N/A");
    }

    // endregion
}