package it.unina.hackathon.model;

/**
 * Enum per gli stati degli inviti.
 */
public enum StatoInvito {
    PENDING("In Attesa"), ACCEPTED("Accettato"), DECLINED("Rifiutato");

    private final String descrizione;

    StatoInvito(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
