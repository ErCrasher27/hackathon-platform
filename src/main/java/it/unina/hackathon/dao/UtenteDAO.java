package it.unina.hackathon.dao;

import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.UtenteResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

public interface UtenteDAO {

    /**
     * Recupera tutti gli utenti partecipanti a un hackathon specifico.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista degli utenti partecipanti
     */
    UtenteListResponse getUtentiPartecipantiByHackathon(int hackathonId);

    /**
     * Recupera tutti i giudici che non sono ancora stati invitati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei giudici non invitati
     */
    UtenteListResponse getUtentiGiudiciNonInvitatiByHackathon(int hackathonId);

    /**
     * Recupera tutti i giudici che sono stati invitati a un hackathon.
     *
     * @param hackathonId l'ID dell'hackathon
     * @return risposta contenente la lista dei giudici invitati
     */
    UtenteListResponse getUtentiGiudiciInvitatiByHackathon(int hackathonId);

    /**
     * Salva un nuovo utente nel database.
     *
     * @param utente l'utente da salvare
     * @return risposta contenente l'utente salvato e lo stato dell'operazione
     */
    UtenteResponse saveUtente(Utente utente);

    /**
     * Trova un utente tramite il suo username.
     *
     * @param username l'username dell'utente da cercare
     * @return risposta contenente l'utente trovato
     */
    UtenteResponse findByUsername(String username);

    /**
     * Verifica se un username è già in uso nel sistema.
     *
     * @param username l'username da verificare
     * @return risposta indicante se l'username esiste già
     */
    ResponseResult usernameExists(String username);

    /**
     * Verifica se un indirizzo email è già registrato nel sistema.
     *
     * @param email l'indirizzo email da verificare
     * @return risposta indicante se l'email esiste già
     */
    ResponseResult emailExists(String email);

}