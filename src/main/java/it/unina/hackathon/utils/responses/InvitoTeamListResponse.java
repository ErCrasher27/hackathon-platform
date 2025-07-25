package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.InvitoTeam;

import java.util.List;

public record InvitoTeamListResponse(List<InvitoTeam> invitiTeam, String message) {
}
