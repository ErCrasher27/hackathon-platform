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

    public Commento(String testo) {
        this();
        this.testo = testo;
    }

    public Commento(int progressoId, int giudiceId, String testo) {
        this(testo);
        this.progressoId = progressoId;
        this.giudiceId = giudiceId;
    }

    // endregion

    // region Getter e Setter

    public int getCommentoId() {
        return commentoId;
    }

    public void setCommentoId(int commentoId) {
        this.commentoId = commentoId;
    }

    public int getProgressoId() {
        return progressoId;
    }

    public void setProgressoId(int progressoId) {
        this.progressoId = progressoId;
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

    public void setTesto(String testo) {
        this.testo = testo;
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

    // region Business

    public boolean scriviCommento(String testo) {
        if (!validaTesto(testo)) {
            return false;
        }
        this.testo = testo.trim();
        this.dataCommento = LocalDateTime.now();
        return true;
    }

    public boolean modificaCommento(String nuovoTesto) {
        if (!validaTesto(nuovoTesto)) {
            return false;
        }
        this.testo = nuovoTesto.trim();
        // Manteniamo la data originale per i commenti modificati
        return true;
    }

    public boolean validaTesto(String testo) {
        return testo != null && !testo.trim().isEmpty() && testo.trim().length() <= 1000;
    }

    public boolean validaCommento() {
        if (!validaTesto(testo)) {
            return false;
        }
        return progressoId > 0 && giudiceId > 0;
    }

    public String getAnteprimaTesto() {
        if (testo == null || testo.isEmpty()) {
            return "Nessun commento";
        }
        return testo.length() > 150 ? testo.substring(0, 150) + "..." : testo;
    }

    public String getDettagliCompleti() {
        return String.format("Commento di %s\nData: %s\n\n%s", giudice != null ? giudice.getNomeCompleto() : "Giudice sconosciuto", dataCommento, testo);
    }

    public int getLunghezzaTesto() {
        return testo != null ? testo.length() : 0;
    }

    public boolean isVuoto() {
        return testo == null || testo.trim().isEmpty();
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