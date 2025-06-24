package it.unina.hackathon.model.tmp;

import it.unina.hackathon.model.enums.StatoInvito;

import java.time.LocalDateTime;
import java.util.Objects;

public class GiudiceHackathon {

    // region Proprietà

    private int giudiceHackathonId;
    private LocalDateTime dataInvito;
    private StatoInvito statoInvito;

    // endregion

    // region Costruttori

    public GiudiceHackathon() {
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
    }

    // endregion

    // region Getter e Setter

    public int getGiudiceHackathonId() {
        return giudiceHackathonId;
    }

    public void setGiudiceHackathonId(int giudiceHackathonId) {
        this.giudiceHackathonId = giudiceHackathonId;
    }

    public LocalDateTime getDataInvito() {
        return dataInvito;
    }

    public void setDataInvito(LocalDateTime dataInvito) {
        this.dataInvito = dataInvito;
    }

    public StatoInvito getStatoInvito() {
        return statoInvito;
    }

    public void setStatoInvito(StatoInvito statoInvito) {
        this.statoInvito = statoInvito;
    }

    // endregion

    // region Business

    public boolean invitaGiudice() {
        this.dataInvito = LocalDateTime.now();
        this.statoInvito = StatoInvito.PENDING;
        return true;
    }

    public boolean accettaInvito() {
        if (statoInvito == StatoInvito.PENDING) {
            this.statoInvito = StatoInvito.ACCEPTED;
            return true;
        }
        return false;
    }

    public boolean rifiutaInvito() {
        if (statoInvito == StatoInvito.PENDING) {
            this.statoInvito = StatoInvito.DECLINED;
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
        GiudiceHackathon that = (GiudiceHackathon) obj;
        return giudiceHackathonId == that.giudiceHackathonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(giudiceHackathonId);
    }

    @Override
    public String toString() {
        return String.format("GiudiceHackathon{id=%d, stato=%s}", giudiceHackathonId, statoInvito);
    }

    // endregion
}
