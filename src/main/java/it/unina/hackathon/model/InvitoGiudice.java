package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class InvitoGiudice {

    // region Proprietà

    private int invitoId;
    private int utenteOrganizzatoreInvitanteId;
    private int utenteGiudiceInvitatoId;
    private int hackathonId;
    private StatoInvito statoInvito;
    private LocalDateTime dataInvito;
    private Utente utenteOrganizzatoreInvitante;

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

    public void setUtenteOrganizzatoreInvitanteId(int utenteOrganizzatoreInvitanteId) {
        this.utenteOrganizzatoreInvitanteId = utenteOrganizzatoreInvitanteId;
    }

    public void setUtenteGiudiceInvitatoId(int utenteGiudiceInvitatoId) {
        this.utenteGiudiceInvitatoId = utenteGiudiceInvitatoId;
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

    public Utente getUtenteOrganizzatoreInvitante() {
        return utenteOrganizzatoreInvitante;
    }

    public void setUtenteOrganizzatoreInvitante(Utente utenteOrganizzatoreInvitante) {
        this.utenteOrganizzatoreInvitante = utenteOrganizzatoreInvitante;
    }

    // endregion

    // region Override Methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitoGiudice that = (InvitoGiudice) o;
        return invitoId == that.invitoId && utenteOrganizzatoreInvitanteId == that.utenteOrganizzatoreInvitanteId && utenteGiudiceInvitatoId == that.utenteGiudiceInvitatoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invitoId, utenteOrganizzatoreInvitanteId, utenteGiudiceInvitatoId);
    }

    @Override
    public String toString() {
        return "InvitoGiudice{" + "invitoId=" + invitoId + ", invitanteId=" + utenteOrganizzatoreInvitanteId + ", invitatoId=" + utenteGiudiceInvitatoId + ", statoInvito=" + statoInvito + ", dataInvito=" + dataInvito + '}';
    }

    // endregion
}