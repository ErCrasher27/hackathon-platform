package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.InvitoGiudice;

import java.util.List;

public record InvitoGiudiceListResponse(List<InvitoGiudice> invitiGiudice, String message) {
}
