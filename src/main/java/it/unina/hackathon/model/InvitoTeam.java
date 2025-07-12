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

    public InvitoTeam(int teamId, int invitanteId, int invitatoId) {
        this();
        this.teamId = teamId;
        this.invitanteId = invitanteId;
        this.invitatoId = invitatoId;
    }

    public InvitoTeam(int teamId, int invitanteId, int invitatoId, String messaggioMotivazionale) {
        this(teamId, invitanteId, invitatoId);
        this.messaggioMotivazionale = messaggioMotivazionale;
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

    public int getInvitanteId() {
        return invitanteId;
    }

    public void setInvitanteId(int invitanteId) {
        this.invitanteId = invitanteId;
    }

    public int getInvitatoId() {
        return invitatoId;
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

    public LocalDateTime getDataRisposta() {
        return dataRisposta;
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

    // region Business

    public boolean inviaInvito(String messaggio) {
        if (!validaInvito()) {
            return false;
        }
        this.messaggioMotivazionale = messaggio;
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
        return true;
    }

    public boolean accettaInvito() {
        if (statoInvito != StatoInvito.PENDING) {
            return false;
        }
        this.statoInvito = StatoInvito.ACCEPTED;
        this.dataRisposta = LocalDateTime.now();
        return true;
    }

    public boolean rifiutaInvito() {
        if (statoInvito != StatoInvito.PENDING) {
            return false;
        }
        this.statoInvito = StatoInvito.DECLINED;
        this.dataRisposta = LocalDateTime.now();
        return true;
    }

    public boolean validaInvito() {
        return teamId > 0 && invitanteId > 0 && invitatoId > 0 && invitanteId != invitatoId;
    }

    public boolean isPending() {
        return statoInvito == StatoInvito.PENDING;
    }

    public boolean isAccettato() {
        return statoInvito == StatoInvito.ACCEPTED;
    }

    public boolean isRifiutato() {
        return statoInvito == StatoInvito.DECLINED;
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