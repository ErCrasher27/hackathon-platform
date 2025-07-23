package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Progresso {

    // region Proprietà

    private int progressoId;
    private int caricatoDaRegistrazioneId;
    private String documentoPath;
    private String documentoNome;
    private LocalDateTime dataCaricamento;
    private Registrazione caricatoDaRegistrazione;

    // endregion

    // region Costruttori

    public Progresso() {
        this.dataCaricamento = LocalDateTime.now();
    }

    public Progresso(String documentoPath) {
        this();
        this.documentoPath = documentoPath;
    }

    // endregion

    // region Getter e Setter

    public int getProgressoId() {
        return progressoId;
    }

    public void setProgressoId(int progressoId) {
        this.progressoId = progressoId;
    }

    public int getCaricatoDaRegistrazioneId() {
        return caricatoDaRegistrazioneId;
    }

    public void setCaricatoDaRegistrazioneId(int caricatoDaRegistrazioneId) {
        this.caricatoDaRegistrazioneId = caricatoDaRegistrazioneId;
    }

    public String getDocumentoPath() {
        return documentoPath;
    }

    public void setDocumentoPath(String documentoPath) {
        this.documentoPath = documentoPath;
    }

    public String getDocumentoNome() {
        return documentoNome;
    }

    public void setDocumentoNome(String documentoNome) {
        this.documentoNome = documentoNome;
    }

    public LocalDateTime getDataCaricamento() {
        return dataCaricamento;
    }

    public void setDataCaricamento(LocalDateTime dataCaricamento) {
        this.dataCaricamento = dataCaricamento;
    }

    public Registrazione getCaricatoDaRegistrazione() {
        return caricatoDaRegistrazione;
    }

    public void setCaricatoDaRegistrazione(Registrazione caricatoDaRegistrazione) {
        this.caricatoDaRegistrazione = caricatoDaRegistrazione;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Progresso progresso = (Progresso) obj;
        return progressoId == progresso.progressoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(progressoId);
    }

    @Override
    public String toString() {
        return String.format("Progresso{id=%d, caricato=%s}", progressoId, dataCaricamento != null ? dataCaricamento.toLocalDate() : "N/A");
    }

    // endregion
}