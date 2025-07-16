package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class GiudiceHackathon {

    // region Proprietà

    private int giudiceHackathonId;
    private int hackathonId;
    private int giudiceId;
    private int invitatoDaId;
    private LocalDateTime dataInvito;
    private StatoInvito statoInvito;
    private Utente giudice;
    private Utente invitatoDa;

    // endregion

    // region Costruttori

    public GiudiceHackathon() {
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
    }

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

    public int getGiudiceId() {
        return giudiceId;
    }

    public void setGiudiceId(int giudiceId) {
        this.giudiceId = giudiceId;
    }

    public void setInvitatoDaId(int invitatoDaId) {
        this.invitatoDaId = invitatoDaId;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }

    public void setDataInvito(LocalDateTime dataInvito) {
        this.dataInvito = dataInvito;
    }

    public StatoInvito getStatoInvito() {
        return statoInvito;
    }

    public void setStatoInvito(StatoInvito statoInvito) {
        this.statoInvito = statoInvito;
    }

    public Utente getGiudice() {
        return giudice;
    }

    public void setGiudice(Utente giudice) {
        this.giudice = giudice;
    }

    public Utente getInvitatoDa() {
        return invitatoDa;
    }

    public void setInvitatoDa(Utente invitatoDa) {
        this.invitatoDa = invitatoDa;
    }

    // endregion

    // region Business

    public boolean isAccepted() {
        return statoInvito == StatoInvito.ACCEPTED;
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
        return String.format("GiudiceHackathon{id=%d, hackathonId=%d, giudiceId=%d, stato=%s}", giudiceHackathonId, hackathonId, giudiceId, statoInvito);
    }

    // endregion
}