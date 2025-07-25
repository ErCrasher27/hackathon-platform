package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Progresso;

import java.util.List;

public record ProgressoListResponse(List<Progresso> progressi, String message) {
}
