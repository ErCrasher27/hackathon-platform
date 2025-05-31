package it.unina.hackathon.model;

/**
 * Enum per i tipi utente.
 */
public enum TipoUtente {
    ORGANIZZATORE("Organizzatore"), GIUDICE("Giudice"), PARTECIPANTE("Partecipante");

    private final String descrizione;

    TipoUtente(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
