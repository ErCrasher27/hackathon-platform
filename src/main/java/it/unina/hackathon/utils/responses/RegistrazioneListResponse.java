package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Registrazione;

import java.util.List;

public record RegistrazioneListResponse(List<Registrazione> registrazioni, String message) {
}
