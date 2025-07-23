package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class InvitoTeam {

    // region Proprietà

    private int invitoId;
    private int registrazioneInvitanteId;
    private int utentePartecipanteInvitatoId;
    private String messaggioMotivazionale;
    private StatoInvito statoInvito;
    private LocalDateTime dataInvito;
    private Registrazione registrazioneInvitante;
    private Utente utentePartecipanteInvitato;

    // endregion

    // region Costruttori

    public InvitoTeam() {
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

    public void setRegistrazioneInvitanteId(int registrazioneInvitanteId) {
        this.registrazioneInvitanteId = registrazioneInvitanteId;
    }

    public void setUtentePartecipanteInvitatoId(int utentePartecipanteInvitatoId) {
        this.utentePartecipanteInvitatoId = utentePartecipanteInvitatoId;
    }

    public String getMessaggioMotivazionale() {
        return messaggioMotivazionale;
    }

    public void setMessaggioMotivazionale(String messaggioMotivazionale) {
        this.messaggioMotivazionale = messaggioMotivazionale;
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

    public Registrazione getRegistrazioneInvitante() {
        return registrazioneInvitante;
    }

    public void setRegistrazioneInvitante(Registrazione registrazioneInvitante) {
        this.registrazioneInvitante = registrazioneInvitante;
    }

    public Utente getUtentePartecipanteInvitato() {
        return utentePartecipanteInvitato;
    }

    public void setUtentePartecipanteInvitato(Utente utentePartecipanteInvitato) {
        this.utentePartecipanteInvitato = utentePartecipanteInvitato;
    }

    // endregion

    // region Override Methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitoTeam that = (InvitoTeam) o;
        return invitoId == that.invitoId && registrazioneInvitanteId == that.registrazioneInvitanteId && utentePartecipanteInvitatoId == that.utentePartecipanteInvitatoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invitoId, registrazioneInvitanteId, utentePartecipanteInvitatoId);
    }

    @Override
    public String toString() {
        return "InvitoTeam{" + "invitoId=" + invitoId + ", invitanteId=" + registrazioneInvitanteId + ", invitatoId=" + utentePartecipanteInvitatoId + ", statoInvito=" + statoInvito + ", dataInvito=" + dataInvito + '}';
    }

    // endregion
}