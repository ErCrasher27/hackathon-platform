package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Problema {

    // region Proprietà

    private int problemaId;
    private int pubblicatoDaGiudiceHackathonId;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataPubblicazione;
    private GiudiceHackathon pubblicatoDaGiudiceHackathon;

    // endregion

    // region Costruttori

    public Problema() {
        this.dataPubblicazione = LocalDateTime.now();
    }

    public Problema(String titolo, String descrizione, int pubblicatoDaId) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.pubblicatoDaGiudiceHackathonId = pubblicatoDaId;
    }

    // endregion

    // region Getter e Setter

    public int getProblemaId() {
        return problemaId;
    }

    public void setProblemaId(int problemaId) {
        this.problemaId = problemaId;
    }

    public int getPubblicatoDaGiudiceHackathonId() {
        return pubblicatoDaGiudiceHackathonId;
    }

    public void setPubblicatoDaGiudiceHackathonId(int pubblicatoDaGiudiceHackathonId) {
        this.pubblicatoDaGiudiceHackathonId = pubblicatoDaGiudiceHackathonId;
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

    public GiudiceHackathon getPubblicatoDaGiudiceHackathon() {
        return pubblicatoDaGiudiceHackathon;
    }

    public void setPubblicatoDaGiudiceHackathon(GiudiceHackathon pubblicatoDaGiudiceHackathon) {
        this.pubblicatoDaGiudiceHackathon = pubblicatoDaGiudiceHackathon;
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
        return String.format("Problema{id=%d, titolo='%s', pubblicato=%s}", problemaId, titolo, dataPubblicazione != null ? dataPubblicazione.toLocalDate() : "N/A");
    }

    // endregion
}