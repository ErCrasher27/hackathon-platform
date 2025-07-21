package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class InvitoGiudice {

    // region Proprietà

    private int invitoId;
    private int invitanteId;
    private int invitatoId;
    private int hackathonId;
    private StatoInvito statoInvito;
    private LocalDateTime dataInvito;
    private Utente invitante;
    private Utente invitato;

    // endregion

    // region Costruttori

    public InvitoGiudice() {
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
    }

    // endregion

    // region Getter e Setter

    public int getInvitoId() {
        return invitoId;
    }

    public void setInvitoId(int invitoId) {
        this.invitoId = invitoId;
    }

    public void setInvitanteId(int invitanteId) {
        this.invitanteId = invitanteId;
    }

    public void setInvitatoId(int invitatoId) {
        this.invitatoId = invitatoId;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public StatoInvito getStatoInvito() {
        return statoInvito;
    }

    public void setStatoInvito(StatoInvito statoInvito) {
        this.statoInvito = statoInvito;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }

    public void setDataInvito(LocalDateTime dataInvito) {
        this.dataInvito = dataInvito;
    }

    public Utente getInvitante() {
        return invitante;
    }

    public void setInvitante(Utente invitante) {
        this.invitante = invitante;
    }

    public void setInvitato(Utente invitato) {
        this.invitato = invitato;
    }

    // endregion

    // region Override Methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitoGiudice that = (InvitoGiudice) o;
        return invitoId == that.invitoId && invitanteId == that.invitanteId && invitatoId == that.invitatoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invitoId, invitanteId, invitatoId);
    }

    @Override
    public String toString() {
        return "InvitoGiudice{" + "invitoId=" + invitoId + ", invitanteId=" + invitanteId + ", invitatoId=" + invitatoId + ", statoInvito=" + statoInvito + ", dataInvito=" + dataInvito + '}';
    }

    // endregion
}