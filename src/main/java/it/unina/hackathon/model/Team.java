package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Team {

    // region Proprietà

    private int teamId;
    private String nome;
    private int hackathonId;
    private LocalDateTime dataCreazione;
    private boolean definitivo;

    // endregion

    // region Costruttori

    public Team() {
        this.dataCreazione = LocalDateTime.now();
        this.definitivo = false;
    }

    public Team(String nome, int hackathonId) {
        this();
        this.nome = nome;
        this.hackathonId = hackathonId;
    }

    // endregion

    // region Getter e Setter

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public boolean isDefinitivo() {
        return definitivo;
    }

    public void setDefinitivo(boolean definitivo) {
        this.definitivo = definitivo;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Team team = (Team) obj;
        return teamId == team.teamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId);
    }

    @Override
    public String toString() {
        return String.format("Team{id=%d, nome='%s', hackathonId=%d, definitivo=%b}", teamId, nome, hackathonId, definitivo);
    }

    // endregion
}