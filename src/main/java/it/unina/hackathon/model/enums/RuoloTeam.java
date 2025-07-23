package it.unina.hackathon.model.enums;

public enum RuoloTeam implements EnumHandler {
    LEADER(1, "Leader"), MEMBRO(2, "Membro");

    private final int id;
    private final String displayName;

    RuoloTeam(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static RuoloTeam fromId(int id) {
        for (RuoloTeam ruolo : values()) {
            if (ruolo.id == id) return ruolo;
        }
        throw new IllegalArgumentException("RuoloTeam non trovato per id: " + id);
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