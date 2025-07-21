package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Commento {

    // region Proprietà

    private int commentoId;
    private int progressoId;
    private int giudiceHackathonId;
    private String testo;
    private LocalDateTime dataCommento;
    private GiudiceHackathon giudiceHackathon;
    private Progresso progresso;

    // endregion

    // region Costruttori

    public Commento() {
        this.dataCommento = LocalDateTime.now();
    }

    public Commento(int progressoId, int giudiceId, String testo) {
        this();
        this.progressoId = progressoId;
        this.giudiceHackathonId = giudiceId;
        this.testo = testo;
    }

    // endregion

    // region Getter e Setter

    public void setCommentoId(int commentoId) {
        this.commentoId = commentoId;
    }

    public int getProgressoId() {
        return progressoId;
    }

    public int getGiudiceHackathonId() {
        return giudiceHackathonId;
    }

    public String getTesto() {
        return testo;
    }

    public LocalDateTime getDataCommento() {
        return dataCommento;
    }

    public void setDataCommento(LocalDateTime dataCommento) {
        this.dataCommento = dataCommento;
    }

    public GiudiceHackathon getGiudiceHackathon() {
        return giudiceHackathon;
    }

    public void setGiudiceHackathon(GiudiceHackathon giudiceHackathon) {
        this.giudiceHackathon = giudiceHackathon;
    }

    public Progresso getProgresso() {
        return progresso;
    }

    public void setProgresso(Progresso progresso) {
        this.progresso = progresso;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Commento commento = (Commento) obj;
        return commentoId == commento.commentoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentoId);
    }

    @Override
    public String toString() {
        return String.format("Commento{id=%d, progresso=%d, giudice=%d, data=%s}", commentoId, progressoId, giudiceHackathonId, dataCommento != null ? dataCommento.toLocalDate() : "N/A");
    }

    // endregion
}