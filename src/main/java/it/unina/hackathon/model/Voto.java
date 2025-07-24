package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Voto {

    // region Proprietà

    private int votoId;
    private int teamId;
    private int giudiceHackathonId;
    private int valore;
    private LocalDateTime dataVoto;
    private GiudiceHackathon giudiceHackathon;
    private Team team;

    // endregion

    // region Costruttori

    public Voto() {
        this.dataVoto = LocalDateTime.now();
    }

    public Voto(int teamId, int giudiceId, int valore) {
        this();
        this.teamId = teamId;
        this.giudiceHackathonId = giudiceId;
        this.valore = valore;
    }

    // endregion

    // region Getter e Setter

    public void setVotoId(int votoId) {
        this.votoId = votoId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getGiudiceHackathonId() {
        return giudiceHackathonId;
    }

    public void setGiudiceHackathonId(int giudiceHackathonId) {
        this.giudiceHackathonId = giudiceHackathonId;
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

    public GiudiceHackathon getGiudiceHackathon() {
        return giudiceHackathon;
    }

    public void setGiudiceHackathon(GiudiceHackathon giudiceHackathon) {
        this.giudiceHackathon = giudiceHackathon;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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
        return String.format("Voto{id=%d, valore=%d/10, team=%d, giudice=%d}", votoId, valore, teamId, giudiceHackathonId);
    }

    // endregion
}