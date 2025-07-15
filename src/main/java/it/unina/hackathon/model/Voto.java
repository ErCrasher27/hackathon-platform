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

    public Voto(int valore) {
        this();
        this.valore = valore;
    }

    public Voto(int hackathonId, int teamId, int giudiceId, int valore) {
        this(valore);
        this.hackathonId = hackathonId;
        this.teamId = teamId;
        this.giudiceId = giudiceId;
    }

    // endregion

    // region Getter e Setter

    public int getVotoId() {
        return votoId;
    }

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

    public boolean assegnaVoto(int valore) {
        if (!validaValore(valore)) {
            return false;
        }
        this.valore = valore;
        this.dataVoto = LocalDateTime.now();
        return true;
    }

    public boolean assegnaVoto(int valore, String criteri) {
        return assegnaVoto(valore);
    }

    public boolean modificaVoto(int nuovoValore) {
        if (!validaValore(nuovoValore)) {
            return false;
        }
        this.valore = nuovoValore;
        this.dataVoto = LocalDateTime.now();
        return true;
    }

    public boolean modificaVoto(int nuovoValore, String nuoviCriteri) {
        return modificaVoto(nuovoValore);
    }

    public boolean validaValore(int valore) {
        return valore >= 0 && valore <= 10;
    }

    public boolean validaVoto() {
        if (!validaValore(valore)) {
            return false;
        }
        return hackathonId > 0 && teamId > 0 && giudiceId > 0;
    }

    public String getDettagliVoto() {
        return String.format("Voto: %d/10\nCriteri: %s\nData: %s\nGiudice: %s\nTeam: %s", valore, dataVoto, giudice != null ? giudice.getNomeCompleto() : "N/A", team != null ? team.getNome() : "N/A");
    }

    public String getValutazioneTestuale() {
        if (valore >= 9) return "Eccellente";
        if (valore >= 7) return "Buono";
        if (valore >= 6) return "Sufficiente";
        if (valore >= 4) return "Insufficiente";
        return "Gravemente insufficiente";
    }

    public boolean isPositivo() {
        return valore >= 6;
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