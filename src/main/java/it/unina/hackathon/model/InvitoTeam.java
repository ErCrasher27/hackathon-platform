package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class InvitoTeam {

    // region Proprietà

    private int invitoId;
    private int teamId;
    private int invitanteId;
    private int invitatoId;
    private String messaggioMotivazionale;
    private StatoInvito statoInvito;
    private LocalDateTime dataInvito;
    private LocalDateTime dataRisposta;
    private Team team;
    private Utente invitante;
    private Utente invitato;

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

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public void setInvitanteId(int invitanteId) {
        this.invitanteId = invitanteId;
    }

    public void setInvitatoId(int invitatoId) {
        this.invitatoId = invitatoId;
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

    public void setDataRisposta(LocalDateTime dataRisposta) {
        this.dataRisposta = dataRisposta;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Utente getInvitante() {
        return invitante;
    }

    public void setInvitante(Utente invitante) {
        this.invitante = invitante;
    }

    public Utente getInvitato() {
        return invitato;
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
        InvitoTeam that = (InvitoTeam) o;
        return invitoId == that.invitoId && teamId == that.teamId && invitanteId == that.invitanteId && invitatoId == that.invitatoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invitoId, teamId, invitanteId, invitatoId);
    }

    @Override
    public String toString() {
        return "InvitoTeam{" + "invitoId=" + invitoId + ", teamId=" + teamId + ", invitanteId=" + invitanteId + ", invitatoId=" + invitatoId + ", statoInvito=" + statoInvito + ", dataInvito=" + dataInvito + '}';
    }

    // endregion
}