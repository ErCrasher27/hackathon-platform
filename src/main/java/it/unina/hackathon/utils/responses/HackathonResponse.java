package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Hackathon;

public record HackathonResponse(Hackathon hackathon, String message) {
}