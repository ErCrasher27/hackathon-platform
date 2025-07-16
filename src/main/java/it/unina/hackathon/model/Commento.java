package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Commento {

    // region Proprietà

    private int commentoId;
    private int progressoId;
    private int giudiceId;
    private String testo;
    private LocalDateTime dataCommento;
    private Utente giudice;
    private Progresso progresso;

    // endregion

    // region Costruttori

    public Commento() {
        this.dataCommento = LocalDateTime.now();
    }

    public Commento(int progressoId, int giudiceId, String testo) {
        this();
        this.progressoId = progressoId;
        this.giudiceId = giudiceId;
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

    public int getGiudiceId() {
        return giudiceId;
    }

    public void setGiudiceId(int giudiceId) {
        this.giudiceId = giudiceId;
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

    public Utente getGiudice() {
        return giudice;
    }

    public void setGiudice(Utente giudice) {
        this.giudice = giudice;
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
        return String.format("Commento{id=%d, progresso=%d, giudice=%d, data=%s}", commentoId, progressoId, giudiceId, dataCommento != null ? dataCommento.toLocalDate() : "N/A");
    }

    // endregion
}