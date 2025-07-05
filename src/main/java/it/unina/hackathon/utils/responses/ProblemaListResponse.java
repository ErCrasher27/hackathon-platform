package it.unina.hackathon.utils.responses;

import it.unina.hackathon.model.Problema;

import java.util.List;

public record ProblemaListResponse(List<Problema> problemi, String message) {
}
