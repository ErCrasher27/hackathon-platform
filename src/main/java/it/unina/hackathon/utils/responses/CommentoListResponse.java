package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Commento;

import java.util.List;

public record CommentoListResponse(List<Commento> commenti, String message) {
}
