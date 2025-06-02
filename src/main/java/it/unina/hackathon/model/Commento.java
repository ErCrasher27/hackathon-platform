package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Commento {

    // region Proprietà

    private int commentoId;
    private String testo;
    private LocalDateTime dataCommento;

    // endregion

    // region Costruttori

    public Commento() {
        this.dataCommento = LocalDateTime.now();
    }

    public Commento(String testo) {
        this();
        this.testo = testo;
    }

    // endregion

    // region Getter e Setter

    public int getCommentoId() {
        return commentoId;
    }

    public void setCommentoId(int commentoId) {
        this.commentoId = commentoId;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public LocalDateTime getDataCommento() {
        return dataCommento;
    }

    public void setDataCommento(LocalDateTime dataCommento) {
        this.dataCommento = dataCommento;
    }

    // endregion

    // region Business

    public boolean scriviCommento(String testo) {
        if (testo == null || testo.trim().isEmpty()) {
            return false;
        }
        this.testo = testo.trim();
        this.dataCommento = LocalDateTime.now();
        return true;
    }

    public boolean modificaCommento(String nuovoTesto) {
        if (nuovoTesto == null || nuovoTesto.trim().isEmpty()) {
            return false;
        }
        this.testo = nuovoTesto.trim();
        this.dataCommento = LocalDateTime.now();
        return true;
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
        return String.format("Commento{id=%d, data=%s}", commentoId, dataCommento);
    }

    // endregion
}
