package it.unina.hackathon.dao;

import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface HackathonDAO {

    HackathonResponse saveHackathon(Hackathon hackathon);

    HackathonResponse getHackathonById(int hackathonId);

    HackathonListResponse getHackathonByHackathonStatus(HackathonStatus hs);

    HackathonListResponse getHackathonsByOrganizzatore(int organizzatoreId);

    HackathonListResponse getHackathonsByPartecipante(int partecipanteId);

    HackathonListResponse getHackathonAccettati(int giudiceId);

    ResponseResult cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato);

}