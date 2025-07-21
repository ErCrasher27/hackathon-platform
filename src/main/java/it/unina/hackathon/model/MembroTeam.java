package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.RuoloTeam;

import java.time.LocalDateTime;
import java.util.Objects;

public class MembroTeam {

    // region Proprietà

    private int membroTeamId;
    private int teamId;
    private int utenteId;
    private LocalDateTime dataIngresso;
    private RuoloTeam ruolo;
    private Utente utente;
    private Team team;

    // endregion

    // region Costruttori

    public MembroTeam() {
        this.dataIngresso = LocalDateTime.now();
        this.ruolo = RuoloTeam.MEMBRO; // Default
    }

    // endregion

    // region Getter e Setter

    public int getMembroTeamId() {
        return membroTeamId;
    }

    public void setMembroTeamId(int membroTeamId) {
        this.membroTeamId = membroTeamId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public LocalDateTime getDataIngresso() {
        return dataIngresso;
    }

    public void setDataIngresso(LocalDateTime dataIngresso) {
        this.dataIngresso = dataIngresso;
    }

    public RuoloTeam getRuolo() {
        return ruolo;
    }

    public void setRuolo(RuoloTeam ruolo) {
        this.ruolo = ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MembroTeam that = (MembroTeam) obj;
        return membroTeamId == that.membroTeamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(membroTeamId);
    }

    @Override
    public String toString() {
        return String.format("MembroTeam{id=%d, team=%d, utente=%d, ruolo=%s}", membroTeamId, teamId, utenteId, ruolo != null ? ruolo.name() : "null");
    }

    // endregion
}