package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Voto {

    // region Proprietà

    private int votoId;
    private int hackathonId;
    private int teamId;
    private int giudiceId;
    private int valore;
    private LocalDateTime dataVoto;
    private Utente giudice;
    private Team team;

    // Campi per la classifica
    private Integer posizione;
    private Double mediaVoti;
    private Integer numeroVoti;

    // endregion

    // region Costruttori

    public Voto() {
        this.dataVoto = LocalDateTime.now();
    }

    public Voto(int hackathonId, int teamId, int giudiceId, int valore) {
        this();
        this.hackathonId = hackathonId;
        this.teamId = teamId;
        this.giudiceId = giudiceId;
        this.valore = valore;
    }

    // endregion

    // region Getter e Setter

    public void setVotoId(int votoId) {
        this.votoId = votoId;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getGiudiceId() {
        return giudiceId;
    }

    public void setGiudiceId(int giudiceId) {
        this.giudiceId = giudiceId;
    }

    public int getValore() {
        return valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    public LocalDateTime getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(LocalDateTime dataVoto) {
        this.dataVoto = dataVoto;
    }

    public Utente getGiudice() {
        return giudice;
    }

    public void setGiudice(Utente giudice) {
        this.giudice = giudice;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // Per la classifica

    public Integer getPosizione() {
        return posizione;
    }

    public void setPosizione(Integer posizione) {
        this.posizione = posizione;
    }

    public Double getMediaVoti() {
        return mediaVoti;
    }

    public void setMediaVoti(Double mediaVoti) {
        this.mediaVoti = mediaVoti;
    }

    public Integer getNumeroVoti() {
        return numeroVoti;
    }

    public void setNumeroVoti(Integer numeroVoti) {
        this.numeroVoti = numeroVoti;
    }

    // endregion

    // region Business

    public boolean validaVoto() {
        return valore >= 0 && valore <= 10;
    }

    public String getValutazioneTestuale() {
        if (valore >= 9) return "Eccellente";
        if (valore >= 7) return "Buono";
        if (valore == 6) return "Sufficiente";
        if (valore >= 4) return "Insufficiente";
        return "Gravemente insufficiente";
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Voto voto = (Voto) obj;
        return votoId == voto.votoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(votoId);
    }

    @Override
    public String toString() {
        return String.format("Voto{id=%d, valore=%d/10, hackathon=%d, team=%d, giudice=%d}", votoId, valore, hackathonId, teamId, giudiceId);
    }

    // endregion
}