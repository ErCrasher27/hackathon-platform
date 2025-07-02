package it.unina.hackathon.utils;

import it.unina.hackathon.model.Utente;

import java.util.List;

public record UtenteListResponse(List<Utente> utenti, String message) {
}