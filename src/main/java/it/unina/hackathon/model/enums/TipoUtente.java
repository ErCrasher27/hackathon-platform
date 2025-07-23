package it.unina.hackathon.model.enums;

public enum TipoUtente implements EnumHandler {
    ORGANIZZATORE(1, "Organizzatore"), GIUDICE(2, "Giudice"), PARTECIPANTE(3, "Partecipante");

    private final int id;
    private final String displayName;

    TipoUtente(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static TipoUtente fromId(int id) {
        for (TipoUtente tipo : values()) {
            if (tipo.id == id) return tipo;
        }
        throw new IllegalArgumentException("TipoUtente non trovato per id: " + id);
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
