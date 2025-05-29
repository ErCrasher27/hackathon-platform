package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta un invito per unirsi a un team.
 * Gestisce gli inviti tra partecipanti per la formazione dei team.
 */
public class InvitoTeam {

    // region Proprietà

    private int invitoId;
    private String messaggioMotivazionale;
    private StatoInvito statoInvito;
    private LocalDateTime dataInvito;
    private LocalDateTime dataRisposta;

    // endregion

    // region Costruttori

    public InvitoTeam() {
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
    }

    public InvitoTeam(String messaggio) {
        this();
        this.messaggioMotivazionale = messaggio;
    }

    // endregion

    // region Getter e Setter

    public int getInvitoId() {
        return invitoId;
    }

    public void setInvitoId(int invitoId) {
        this.invitoId = invitoId;
    }

    public String getMessaggioMotivazionale() {
        return messaggioMotivazionale;
    }

    public void setMessaggioMotivazionale(String messaggioMotivazionale) {
        this.messaggioMotivazionale = messaggioMotivazionale;
    }

    public StatoInvito getStatoInvito() {
        return statoInvito;
    }

    public void setStatoInvito(StatoInvito statoInvito) {
        this.statoInvito = statoInvito;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }

    public void setDataInvito(LocalDateTime dataInvito) {
        this.dataInvito = dataInvito;
    }

    public LocalDateTime getDataRisposta() {
        return dataRisposta;
    }

    public void setDataRisposta(LocalDateTime dataRisposta) {
        this.dataRisposta = dataRisposta;
    }

    // endregion

    // region Business

    public boolean inviaInvito(String messaggio) {
        if (messaggio == null || messaggio.trim().isEmpty()) {
            return false;
        }
        this.messaggioMotivazionale = messaggio.trim();
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
        return true;
    }

    public boolean accettaInvito() {
        if (statoInvito == StatoInvito.PENDING) {
            this.statoInvito = StatoInvito.ACCEPTED;
            this.dataRisposta = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean rifiutaInvito() {
        if (statoInvito == StatoInvito.PENDING) {
            this.statoInvito = StatoInvito.DECLINED;
            this.dataRisposta = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InvitoTeam that = (InvitoTeam) obj;
        return invitoId == that.invitoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invitoId);
    }

    @Override
    public String toString() {
        return String.format("InvitoTeam{id=%d, stato=%s}", invitoId, statoInvito);
    }

    // endregion
}
