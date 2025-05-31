package it.unina.hackathon.utils;

import it.unina.hackathon.model.Utente;

public record UtenteResponse(Utente utente, String message) {
}