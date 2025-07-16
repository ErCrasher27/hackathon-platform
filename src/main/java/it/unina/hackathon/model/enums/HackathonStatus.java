package it.unina.hackathon.model.enums;

public enum HackathonStatus implements EnumHandler {
    REGISTRAZIONI_APERTE(1, "Registrazioni Aperte"), REGISTRAZIONI_CHIUSE(2, "Registrazioni Chiuse"), IN_CORSO(3, "In Corso"), TERMINATO(4, "Terminato");
    private final int id;
    private final String displayName;

    HackathonStatus(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}