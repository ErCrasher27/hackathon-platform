package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Progresso {

    // region Proprietà

    private int progressoId;
    private String titolo;
    private String descrizione;
    private String documentoPath;
    private String documentoNome;
    private LocalDateTime dataCaricamento;

    // endregion

    // region Costruttori

    public Progresso() {
        this.dataCaricamento = LocalDateTime.now();
    }

    public Progresso(String titolo, String descrizione, String documentoPath, String documentoNome) {
        this();
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.documentoPath = documentoPath;
        this.documentoNome = documentoNome;
    }

    // endregion

    // region Getter e Setter

    public int getProgressoId() {
        return progressoId;
    }

    public void setProgressoId(int progressoId) {
        this.progressoId = progressoId;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

    // endregion

    // region Business

    public boolean caricaProgresso() {
        if (titolo == null || titolo.trim().isEmpty()) {
            return false;
        }
        if (documentoPath == null || documentoPath.trim().isEmpty()) {
            return false;
        }
        this.dataCaricamento = LocalDateTime.now();
        return true;
    }

    public void aggiornaDocumento(String path) {
        if (path != null && !path.trim().isEmpty()) {
            this.documentoPath = path.trim();
            this.dataCaricamento = LocalDateTime.now();
        }
    }

    public String getDettagliCaricamento() {
        return String.format("Documento: %s\nCaricato: %s\nPath: %s", documentoNome, dataCaricamento, documentoPath);
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
        return String.format("Progresso{id=%d, titolo='%s', documento='%s'}", progressoId, titolo, documentoNome);
    }

    // endregion
}
