package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.MembroTeam;

import java.util.List;

public record MembroTeamListResponse(List<MembroTeam> membri, String message) {
}