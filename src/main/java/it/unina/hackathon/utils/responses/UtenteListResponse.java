package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Utente;

import java.util.List;

public record UtenteListResponse(List<Utente> utenti, String message) {
}