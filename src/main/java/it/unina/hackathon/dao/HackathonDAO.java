package it.unina.hackathon.dao;

import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface HackathonDAO {
    HackathonResponse saveHackathon(Hackathon hackathon);

    HackathonListResponse getAllHackathonByOrganizzatore(int organizzatoreId);

    HackathonResponse getHackathonById(int hackathonId);

    HackathonResponse updateHackathon(Hackathon hackathon);

    ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato);

    HackathonListResponse getHackathonByHackathonStatus(HackathonStatus hs);

    HackathonListResponse getHackathonByPartecipante(int partecipanteId);

    HackathonListResponse getHackathonAccettati(int giudiceId);

}