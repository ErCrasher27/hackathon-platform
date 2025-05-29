package it.unina.hackathon.model;

/**
 * Enum per gli stati possibili di un hackathon.
 */
public enum HackathonStatus {
    REGISTRAZIONI_APERTE("Registrazioni Aperte"), REGISTRAZIONI_CHIUSE("Registrazioni Chiuse"), IN_CORSO("In Corso"), TERMINATO("Terminato");

    private final String descrizione;

    HackathonStatus(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
