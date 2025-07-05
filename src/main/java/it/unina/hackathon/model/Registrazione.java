package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Registrazione {

    // region Proprietà

    private int registrazioneId;
    private int utenteId;
    private int hackathonId;
    private Integer teamId; // Nullable - assegnato quando si unisce a un team
    private LocalDateTime dataRegistrazione;
    private Utente utente;
    private Hackathon hackathon;
    private Team team;

    // endregion

    // region Costruttori

    public Registrazione() {
        this.dataRegistrazione = LocalDateTime.now();
    }

    public Registrazione(int utenteId, int hackathonId) {
        this();
        this.utenteId = utenteId;
        this.hackathonId = hackathonId;
    }

    // endregion

    // region Getter e Setter

    public int getRegistrazioneId() {
        return registrazioneId;
    }

    public void setRegistrazioneId(int registrazioneId) {
        this.registrazioneId = registrazioneId;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public LocalDateTime getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDateTime dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // endregion

    // region Business

    public boolean registraUtente() {
        if (!validaRegistrazione()) {
            return false;
        }
        this.dataRegistrazione = LocalDateTime.now();
        return true;
    }

    public boolean validaRegistrazione() {
        return utenteId > 0 && hackathonId > 0;
    }

    public boolean assegnaTeam(Team team) {
        if (team == null) {
            this.teamId = null;
            this.team = null;
            return true;
        }

        this.teamId = team.getTeamId();
        this.team = team;
        return true;
    }

    public boolean assegnaTeam(int teamId) {
        if (teamId <= 0) {
            this.teamId = null;
            this.team = null;
            return true;
        }

        this.teamId = teamId;
        return true;
    }

    public boolean rimuoviDaTeam() {
        this.teamId = null;
        this.team = null;
        return true;
    }

    public boolean haTeam() {
        return teamId != null && teamId > 0;
    }

    public boolean isRegistrazioneCompleta() {
        return validaRegistrazione() && dataRegistrazione != null;
    }

    public String getStatoRegistrazione() {
        if (!isRegistrazioneCompleta()) {
            return "Registrazione incompleta";
        }

        if (haTeam()) {
            return "Registrato con team";
        } else {
            return "Registrato senza team";
        }
    }

    public String getDettagliRegistrazione() {
        String sb = "Registrazione #" + registrazioneId + "\n" + "Utente: " + (utente != null ? utente.getNomeCompleto() : "N/A") + "\n" + "Hackathon: " + (hackathon != null ? hackathon.getTitolo() : "N/A") + "\n" + "Data registrazione: " + dataRegistrazione + "\n" + "Team: " + (team != null ? team.getNome() : "Nessun team assegnato") + "\n" + "Stato: " + getStatoRegistrazione();
        return sb;
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
        return String.format("Registrazione{id=%d, utente=%d, hackathon=%d, team=%s, data=%s}", registrazioneId, utenteId, hackathonId, teamId != null ? teamId.toString() : "null", dataRegistrazione != null ? dataRegistrazione.toLocalDate() : "N/A");
    }

    // endregion
}