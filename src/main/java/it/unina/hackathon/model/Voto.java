package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Voto {

    // region Proprietà

    private int votoId;
    private int valore;
    private String criteriValutazione;
    private LocalDateTime dataVoto;

    // endregion

    // region Costruttori

    public Voto() {
        this.dataVoto = LocalDateTime.now();
    }

    public Voto(int valore, String criteriValutazione) {
        this();
        this.valore = valore;
        this.criteriValutazione = criteriValutazione;
    }

    // endregion

    // region Getter e Setter

    public int getVotoId() {
        return votoId;
    }

    public void setVotoId(int votoId) {
        this.votoId = votoId;
    }

    public int getValore() {
        return valore;
    }

    public void setValore(int valore) {
        this.valore = valore;
    }

    public String getCriteriValutazione() {
        return criteriValutazione;
    }

    public void setCriteriValutazione(String criteriValutazione) {
        this.criteriValutazione = criteriValutazione;
    }

    public LocalDateTime getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(LocalDateTime dataVoto) {
        this.dataVoto = dataVoto;
    }

    // endregion

    // region Business

    public boolean assegnaVoto(int valore) {
        if (!validaVoto()) {
            return false;
        }
        this.valore = valore;
        this.dataVoto = LocalDateTime.now();
        return true;
    }

    public boolean modificaVoto(int nuovoValore) {
        if (nuovoValore < 0 || nuovoValore > 10) {
            return false;
        }
        this.valore = nuovoValore;
        this.dataVoto = LocalDateTime.now();
        return true;
    }

    public boolean validaVoto() {
        return valore >= 0 && valore <= 10;
    }

    public String getDettagliVoto() {
        return String.format("Voto: %d/10\nCriteri: %s\nData: %s", valore, criteriValutazione, dataVoto);
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
        return String.format("Voto{id=%d, valore=%d/10}", votoId, valore);
    }

    // endregion
}
