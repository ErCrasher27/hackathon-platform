package it.unina.hackathon.utils;

import it.unina.hackathon.model.Hackathon;

import java.util.List;

public record HackathonListResponse(List<Hackathon> hackathons, String message) {
}