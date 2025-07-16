package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Progresso {

    // region Proprietà

    private int progressoId;
    private int teamId;
    private int caricatoDaId;
    private String documentoPath;
    private String documentoNome;
    private LocalDateTime dataCaricamento;
    private Utente caricatoDa;

    // endregion

    // region Costruttori

    public Progresso() {
        this.dataCaricamento = LocalDateTime.now();
    }

    public Progresso(int teamId, String documentoPath) {
        this();
        this.teamId = teamId;
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

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getCaricatoDaId() {
        return caricatoDaId;
    }

    public void setCaricatoDaId(int caricatoDaId) {
        this.caricatoDaId = caricatoDaId;
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

    public Utente getCaricatoDa() {
        return caricatoDa;
    }

    public void setCaricatoDa(Utente caricatoDa) {
        this.caricatoDa = caricatoDa;
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
        return String.format("Progresso{id=%d, team=%d, caricato=%s}", progressoId, teamId, dataCaricamento != null ? dataCaricamento.toLocalDate() : "N/A");
    }

    // endregion
}