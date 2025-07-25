package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.ClassificaTeam;

import java.util.List;

public record ClassificaListResponse(List<ClassificaTeam> classifica, String message) {
}