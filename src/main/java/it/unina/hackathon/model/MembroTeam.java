package it.unina.hackathon.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class MembroTeam {

    // region Proprietà

    private int membroTeamId;
    private LocalDateTime dataIngresso;

    // endregion

    // region Costruttori

    public MembroTeam() {
        this.dataIngresso = LocalDateTime.now();
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
        return String.format("MembroTeam{id=%d}", membroTeamId);
    }

    // endregion
}
