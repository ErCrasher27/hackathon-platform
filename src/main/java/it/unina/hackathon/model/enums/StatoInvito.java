package it.unina.hackathon.model.enums;

public enum StatoInvito implements EnumHandler {
    PENDING(1, "In Attesa"), ACCEPTED(2, "Accettato"), DECLINED(3, "Rifiutato");
    private final int id;
    private final String displayName;

    StatoInvito(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static StatoInvito fromId(int id) {
        for (StatoInvito status : values()) {
            if (status.id == id) return status;
        }
        throw new IllegalArgumentException("InvitoStatus non trovato per id: " + id);
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