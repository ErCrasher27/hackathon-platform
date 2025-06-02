package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    // region Proprietà

    private int teamId;
    private String nome;
    private LocalDateTime dataCreazione;
    private boolean definitivo;

    // endregion

    // region Costruttori

    public Team() {
        this.dataCreazione = LocalDateTime.now();
        this.definitivo = false;
    }

    public Team(String nome) {
        this();
        this.nome = nome;
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

    // region Business

    public boolean creaTeam(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        this.nome = nome.trim();
        this.dataCreazione = LocalDateTime.now();
        return true;
    }

    public boolean aggiungiMembro(Utente utente) {
        // TODO: Implementazione delegata al Controller/DAO
        return utente != null;
    }

    public boolean rimuoviMembro(Utente utente) {
        // TODO: Implementazione delegata al Controller/DAO
        return utente != null;
    }

    public void rendiDefinitivo() {
        this.definitivo = true;
    }

    public int getNumeroMembri() {
        // TODO: Implementazione delegata al DAO
        return 0;
    }

    public boolean verificaDimensioneMassima() {
        // TODO: Implementazione delegata al Controller
        return true;
    }

    public List<String> getNomiMembri() {
        // TODO: Implementazione delegata al DAO
        return new ArrayList<>();
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
        return String.format("Team{id=%d, nome='%s', definitivo=%b}", teamId, nome, definitivo);
    }

    // endregion
}
