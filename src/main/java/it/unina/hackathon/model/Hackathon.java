package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta un evento hackathon.
 * Contiene tutte le informazioni relative all'evento, ai vincoli temporali
 * e ai limiti di partecipazione.
 *
 * <p>Vincoli di business:
 * <ul>
 *   <li>data_inizio < data_fine</li>
 *   <li>data_chiusura_registrazioni < data_inizio (2 giorni prima)</li>
 *   <li>max_iscritti > 0</li>
 *   <li>max_dimensione_team > 0</li>
 * </ul>
 */
public class Hackathon {

    // region Proprietà

    private int hackathonId;
    private String titolo;
    private String descrizione;
    private String sede;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private LocalDateTime dataChiusuraRegistrazioni;
    private int maxIscritti;
    private int maxDimensioneTeam;
    private HackathonStatus status;
    private LocalDateTime dataCreazione;

    // endregion

    // region Costruttori

    public Hackathon() {
        this.dataCreazione = LocalDateTime.now();
        this.status = HackathonStatus.REGISTRAZIONI_APERTE;
    }

    public Hackathon(String titolo, String descrizione, String sede, LocalDateTime dataInizio, LocalDateTime dataFine, LocalDateTime dataChiusuraRegistrazioni, int maxIscritti, int maxDimensioneTeam) {
        this();
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.sede = sede;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dataChiusuraRegistrazioni = dataChiusuraRegistrazioni;
        this.maxIscritti = maxIscritti;
        this.maxDimensioneTeam = maxDimensioneTeam;
    }

    // endregion

    // region Getter e Setter

    public int getHackathonId() {
        return hackathonId;
    }

    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
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

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public LocalDateTime getDataChiusuraRegistrazioni() {
        return dataChiusuraRegistrazioni;
    }

    public void setDataChiusuraRegistrazioni(LocalDateTime dataChiusuraRegistrazioni) {
        this.dataChiusuraRegistrazioni = dataChiusuraRegistrazioni;
    }

    public int getMaxIscritti() {
        return maxIscritti;
    }

    public void setMaxIscritti(int maxIscritti) {
        this.maxIscritti = maxIscritti;
    }

    public int getMaxDimensioneTeam() {
        return maxDimensioneTeam;
    }

    public void setMaxDimensioneTeam(int maxDimensioneTeam) {
        this.maxDimensioneTeam = maxDimensioneTeam;
    }

    public HackathonStatus getStatus() {
        return status;
    }

    public void setStatus(HackathonStatus status) {
        this.status = status;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    // endregion

    // region Business

    public boolean creaHackathon() {
        return verificaVincoli();
    }

    public void apriRegistrazioni() {
        this.status = HackathonStatus.REGISTRAZIONI_APERTE;
    }

    public void chiudiRegistrazioni() {
        this.status = HackathonStatus.REGISTRAZIONI_CHIUSE;
    }

    public void iniziaHackathon() {
        this.status = HackathonStatus.IN_CORSO;
    }

    public void terminaHackathon() {
        this.status = HackathonStatus.TERMINATO;
    }

    public boolean verificaVincoli() {
        if (dataInizio == null || dataFine == null || dataChiusuraRegistrazioni == null) {
            return false;
        }
        if (!dataInizio.isBefore(dataFine)) {
            return false;
        }
        if (!dataChiusuraRegistrazioni.isBefore(dataInizio)) {
            return false;
        }
        return maxIscritti > 0 && maxDimensioneTeam > 0;
    }

    public int getNumeroIscritti() {
        // TODO: Implementazione delegata al DAO
        return 0;
    }

    public int getNumeroTeam() {
        // TODO: Implementazione delegata al DAO
        return 0;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hackathon hackathon = (Hackathon) obj;
        return hackathonId == hackathon.hackathonId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hackathonId);
    }

    @Override
    public String toString() {
        return String.format("Hackathon{id=%d, titolo='%s', sede='%s', status=%s}", hackathonId, titolo, sede, status);
    }

    // endregion
}
