package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    // region Proprietà

    private int teamId;
    private String nome;
    private int hackathonId;
    private LocalDateTime dataCreazione;
    private boolean definitivo;
    private int numeroMembri;
    private String nomiMembri;
    private List<Utente> membri;
    private int maxDimensione;

    // endregion

    // region Costruttori

    public Team() {
        this.dataCreazione = LocalDateTime.now();
        this.definitivo = false;
        this.membri = new ArrayList<>();
    }

    public Team(String nome) {
        this();
        this.nome = nome;
    }

    public Team(String nome, int hackathonId) {
        this(nome);
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

    public String getNomiMembri() {
        if (nomiMembri != null && !nomiMembri.isEmpty()) {
            return nomiMembri;
        }

        if (membri != null && !membri.isEmpty()) {
            return membri.stream().map(Utente::getNomeCompleto).reduce((a, b) -> a + ", " + b).orElse("Nessun membro");
        }

        return "Nessun membro";
    }

    public void setNomiMembri(String nomiMembri) {
        this.nomiMembri = nomiMembri;
    }

    public List<Utente> getMembri() {
        return membri != null ? membri : new ArrayList<>();
    }

    public void setMembri(List<Utente> membri) {
        this.membri = membri;
        if (membri != null) {
            this.numeroMembri = membri.size();
        }
    }

    public int getMaxDimensione() {
        return maxDimensione;
    }

    public void setMaxDimensione(int maxDimensione) {
        this.maxDimensione = maxDimensione;
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
        if (utente == null) return false;

        if (membri == null) {
            membri = new ArrayList<>();
        }

        // Verifica se l'utente è già nel team
        if (membri.stream().anyMatch(m -> m.getUtenteId() == utente.getUtenteId())) {
            return false;
        }

        // Verifica dimensione massima
        if (maxDimensione > 0 && membri.size() >= maxDimensione) {
            return false;
        }

        membri.add(utente);
        this.numeroMembri = membri.size();
        return true;
    }

    public boolean rimuoviMembro(Utente utente) {
        if (utente == null || membri == null) return false;

        boolean removed = membri.removeIf(m -> m.getUtenteId() == utente.getUtenteId());
        if (removed) {
            this.numeroMembri = membri.size();
        }
        return removed;
    }

    public void rendiDefinitivo() {
        this.definitivo = true;
    }

    public boolean verificaDimensioneMassima() {
        return maxDimensione <= 0 || getNumeroMembri() <= maxDimensione;
    }

    public boolean haSpazioDisponibile() {
        return maxDimensione <= 0 || getNumeroMembri() < maxDimensione;
    }

    public boolean isMembro(Utente utente) {
        if (utente == null || membri == null) return false;
        return membri.stream().anyMatch(m -> m.getUtenteId() == utente.getUtenteId());
    }

    public Utente getLeader() {
        // Assumiamo che il primo membro sia il leader
        return membri != null && !membri.isEmpty() ? membri.get(0) : null;
    }

    public String getStatoTeam() {
        if (definitivo) {
            return "Definitivo";
        } else if (getNumeroMembri() == 0) {
            return "Vuoto";
        } else if (maxDimensione > 0 && getNumeroMembri() >= maxDimensione) {
            return "Completo";
        } else {
            return "In formazione";
        }
    }

    public String getInfoCompleta() {
        return String.format("Team '%s' - %d membri - %s - Creato: %s", nome, getNumeroMembri(), getStatoTeam(), dataCreazione.toLocalDate().toString());
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