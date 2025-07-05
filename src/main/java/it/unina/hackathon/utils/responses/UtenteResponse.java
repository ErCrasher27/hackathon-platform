package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Utente;

public record UtenteResponse(Utente utente, String message) {
}