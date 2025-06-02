package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Registrazione {

    // region Proprietà

    private int registrazioneId;
    private LocalDateTime dataRegistrazione;

    // endregion

    // region Costruttori

    public Registrazione() {
        this.dataRegistrazione = LocalDateTime.now();
    }

    // endregion

    // region Getter e Setter

    public int getRegistrazioneId() {
        return registrazioneId;
    }

    public void setRegistrazioneId(int registrazioneId) {
        this.registrazioneId = registrazioneId;
    }

    public LocalDateTime getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDateTime dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    // endregion

    // region Business

    public boolean registraUtente() {
        this.dataRegistrazione = LocalDateTime.now();
        return true;
    }

    public boolean annullaRegistrazione() {
        // TODO: Implementazione delegata al Controller
        return true;
    }

    public void assegnaTeam(Team team) {
        // TODO: Implementazione delegata al Controller/DAO
        // TODO: Il collegamento team viene gestito a livello di database
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Registrazione that = (Registrazione) obj;
        return registrazioneId == that.registrazioneId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrazioneId);
    }

    @Override
    public String toString() {
        return String.format("Registrazione{id=%d, data=%s}", registrazioneId, dataRegistrazione);
    }

    // endregion
}
