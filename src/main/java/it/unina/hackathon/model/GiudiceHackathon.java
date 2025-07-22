package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class GiudiceHackathon {

    // region Proprietà

    private int giudiceHackathonId;
    private int hackathonId;
    private int giudiceId;
    private Utente utenteGiudice;
    private LocalDateTime dataAssegnazione;

    // endregion

    // region Getter e Setter

    public int getGiudiceHackathonId() {
        return giudiceHackathonId;
    }

    public void setGiudiceHackathonId(int giudiceHackathonId) {
        this.giudiceHackathonId = giudiceHackathonId;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public void setGiudiceId(int giudiceId) {
        this.giudiceId = giudiceId;
    }

    public Utente getUtenteGiudice() {
        return utenteGiudice;
    }

    public void setUtenteGiudice(Utente utenteGiudice) {
        this.utenteGiudice = utenteGiudice;
    }

    public LocalDateTime getDataAssegnazione() {
        return dataAssegnazione;
    }

    public void setDataAssegnazione(LocalDateTime dataAssegnazione) {
        this.dataAssegnazione = dataAssegnazione;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GiudiceHackathon that = (GiudiceHackathon) obj;
        return giudiceHackathonId == that.giudiceHackathonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(giudiceHackathonId);
    }

    @Override
    public String toString() {
        return String.format("GiudiceHackathon{id=%d, hackathonId=%d, giudiceId=%d}", giudiceHackathonId, hackathonId, giudiceId);
    }

    // endregion
}