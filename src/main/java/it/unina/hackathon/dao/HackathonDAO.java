package it.unina.hackathon.dao;

import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.utils.HackathonListResponse;
import it.unina.hackathon.utils.HackathonResponse;

public interface HackathonDAO {
    HackathonResponse saveHackathon(Hackathon hackathon);

    HackathonListResponse getAllHackathonByOrganizzatore(int organizzatoreId);
}