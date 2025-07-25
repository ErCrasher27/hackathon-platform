package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Team;

import java.util.List;

public record TeamListResponse(List<Team> teams, String message) {
}