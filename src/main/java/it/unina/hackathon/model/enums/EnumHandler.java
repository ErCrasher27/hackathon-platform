package it.unina.hackathon.model.enums;

public interface EnumHandler {

    /**
     * Restituisce l'identificatore numerico univoco dell'enum.
     *
     * @return l'ID numerico dell'enum
     */
    int getId();

    /**
     * Restituisce il nome visualizzabile dell'enum per l'interfaccia utente.
     *
     * @return il nome da mostrare all'utente
     */
    String getDisplayName();

}