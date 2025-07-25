package it.unina.hackathon.model;

import java.util.Objects;

public class ClassificaTeam {

    // region Proprietà

    private int teamId;
    private String nomeTeam;
    private int hackathonId;
    private int posizione;
    private Double mediaVoti;
    private Integer numeroVoti;
    private Integer numeroMembri;

    // endregion

    // region Getter e Setter

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getNomeTeam() {
        return nomeTeam;
    }

    public void setNomeTeam(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }

    public int getPosizione() {
        return posizione;
    }

    public void setPosizione(int posizione) {
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

    public Integer getNumeroMembri() {
        return numeroMembri;
    }

    public void setNumeroMembri(Integer numeroMembri) {
        this.numeroMembri = numeroMembri;
    }

    // endregion

    // region Business

    public boolean haRicevutoVoti() {
        return numeroVoti != null && numeroVoti > 0;
    }

    public String getMediaVotiFormattata() {
        if (mediaVoti == null) return "N/A";
        return String.format("%.2f", mediaVoti);
    }

    public String getDescrizioneClassifica() {
        StringBuilder sb = new StringBuilder();
        sb.append(posizione).append("° posto - ");
        sb.append(nomeTeam);
        if (haRicevutoVoti()) {
            sb.append(" (Media: ").append(getMediaVotiFormattata());
            sb.append(", Voti: ").append(numeroVoti).append(")");
        } else {
            sb.append(" (Nessun voto ricevuto)");
        }
        return sb.toString();
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClassificaTeam that = (ClassificaTeam) obj;
        return teamId == that.teamId && hackathonId == that.hackathonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, hackathonId);
    }

    @Override
    public String toString() {
        return String.format("ClassificaTeam{teamId=%d, nome='%s', posizione=%d, media=%.2f, voti=%d}", teamId, nomeTeam, posizione, mediaVoti, numeroVoti);
    }

    // endregion
}