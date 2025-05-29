package it.unina.hackathon.model;

/**
 * Enum per i ruoli possibili all'interno di un team.
 */
public enum RuoloTeam {
    LEADER("Leader"), MEMBRO("Membro");

    private final String descrizione;

    RuoloTeam(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
