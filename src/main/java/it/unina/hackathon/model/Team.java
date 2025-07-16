package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    // region Proprietà

    private final List<Utente> membri;
    private int teamId;
    private String nome;
    private int hackathonId;
    private LocalDateTime dataCreazione;
    private boolean definitivo;
    private int numeroMembri;
    private String nomiMembri;
    private int maxDimensione;

    // endregion

    // region Costruttori

    public Team() {
        this.dataCreazione = LocalDateTime.now();
        this.definitivo = false;
        this.membri = new ArrayList<>();
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

    public int getNumeroMembri() {
        return numeroMembri > 0 ? numeroMembri : (membri != null ? membri.size() : 0);
    }

    public void setNumeroMembri(int numeroMembri) {
        this.numeroMembri = numeroMembri;
    }

    public void setNomiMembri(String nomiMembri) {
        this.nomiMembri = nomiMembri;
    }

    public int getMaxDimensione() {
        return maxDimensione;
    }

    public void setMaxDimensione(int maxDimensione) {
        this.maxDimensione = maxDimensione;
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
        return String.format("Team{id=%d, nome='%s', hackathonId=%d, membri=%d, definitivo=%b}", teamId, nome, hackathonId, getNumeroMembri(), definitivo);
    }

    // endregion
}