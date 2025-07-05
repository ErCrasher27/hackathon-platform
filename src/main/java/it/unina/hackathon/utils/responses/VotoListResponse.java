package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Voto;

import java.util.List;

public record VotoListResponse(List<Voto> voti, String message) {
}
