package it.unina.hackathon.model;

import it.unina.hackathon.model.enums.RuoloTeam;

import java.time.LocalDateTime;
import java.util.Objects;

public class Registrazione {

    // region Proprietà

    private int registrazioneId;
    private int teamId;
    private int utentePartecipanteId;
    private int hackathonId;
    private LocalDateTime dataIngressoTeam;
    private RuoloTeam ruolo;
    private Utente utentePartecipante;
    private Team team;

    // endregion

    // region Costruttori

    public Registrazione() {
        this.dataIngressoTeam = LocalDateTime.now();
    }

    public Registrazione(int utentePartecipanteId, int hackathonId) {
        this();
        this.utentePartecipanteId = utentePartecipanteId;
        this.hackathonId = hackathonId;
    }

    // endregion

    // region Getter e Setter

    public int getRegistrazioneId() {
        return registrazioneId;
    }

    public void setRegistrazioneId(int membroTeamId) {
        this.registrazioneId = membroTeamId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUtentePartecipanteId() {
        return utentePartecipanteId;
    }

    public void setUtentePartecipanteId(int utentePartecipanteId) {
        this.utentePartecipanteId = utentePartecipanteId;
    }

    public LocalDateTime getDataIngressoTeam() {
        return dataIngressoTeam;
    }

    public void setDataIngressoTeam(LocalDateTime dataIngresso) {
        this.dataIngressoTeam = dataIngresso;
    }

    public RuoloTeam getRuolo() {
        return ruolo;
    }

    public void setRuolo(RuoloTeam ruolo) {
        this.ruolo = ruolo;
    }

    public Utente getUtentePartecipante() {
        return utentePartecipante;
    }

    public void setUtentePartecipante(Utente utentePartecipante) {
        this.utentePartecipante = utentePartecipante;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Registrazione that = (Registrazione) obj;
        return registrazioneId == that.registrazioneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrazioneId);
    }

    @Override
    public String toString() {
        return String.format("Registrazione{id=%d, team=%d, utente=%d, ruolo=%s}", registrazioneId, teamId, utentePartecipanteId, ruolo != null ? ruolo.getDisplayName() : "null");
    }

    // endregion
}