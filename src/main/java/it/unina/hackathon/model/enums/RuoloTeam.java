package it.unina.hackathon.model.enums;

public enum RuoloTeam implements EnumHandler {
    LEADER(1, "Leader"), MEMBRO(2, "Membro");

    private final int id;
    private final String displayName;

    RuoloTeam(int id, String displayName) {
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