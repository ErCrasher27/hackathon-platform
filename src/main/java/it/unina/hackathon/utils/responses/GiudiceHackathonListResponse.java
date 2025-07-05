package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.GiudiceHackathon;

import java.util.List;

public record GiudiceHackathonListResponse(List<GiudiceHackathon> giudiciHackathon, String message) {
}