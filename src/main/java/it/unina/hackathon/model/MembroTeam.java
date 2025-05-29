package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta l'appartenenza di un utente a un team.
 * Gestisce il ruolo dell'utente nel team e la data di ingresso.
 */
public class MembroTeam {

    // region Proprietà

    private int membroTeamId;
    private LocalDateTime dataIngresso;
    private RuoloTeam ruoloTeam;

    // endregion

    // region Costruttori

    public MembroTeam() {
        this.dataIngresso = LocalDateTime.now();
        this.ruoloTeam = RuoloTeam.MEMBRO;
    }

    public MembroTeam(RuoloTeam ruolo) {
        this();
        this.ruoloTeam = ruolo;
    }

    // endregion

    // region Getter e Setter

    public int getMembroTeamId() {
        return membroTeamId;
    }

    public void setMembroTeamId(int membroTeamId) {
        this.membroTeamId = membroTeamId;
    }

    public LocalDateTime getDataIngresso() {
        return dataIngresso;
    }

    public void setDataIngresso(LocalDateTime dataIngresso) {
        this.dataIngresso = dataIngresso;
    }

    public RuoloTeam getRuoloTeam() {
        return ruoloTeam;
    }

    public void setRuoloTeam(RuoloTeam ruoloTeam) {
        this.ruoloTeam = ruoloTeam;
    }

    // endregion

    // region Business

    public boolean joinTeam() {
        this.dataIngresso = LocalDateTime.now();
        return true;
    }

    public boolean leaveTeam() {
        // TODO: Implementazione delegata al Controller
        return true;
    }

    public void promoteToLeader() {
        this.ruoloTeam = RuoloTeam.LEADER;
    }

    // endregion

    // region Overrides

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MembroTeam that = (MembroTeam) obj;
        return membroTeamId == that.membroTeamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(membroTeamId);
    }

    @Override
    public String toString() {
        return String.format("MembroTeam{id=%d, ruolo=%s}", membroTeamId, ruoloTeam);
    }

    // endregion
}
