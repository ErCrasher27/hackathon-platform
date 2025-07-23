package it.unina.hackathon.gui;

import javax.swing.*;

public interface GUIHandler {

    /**
     * Inizializza tutti i componenti dell'interfaccia grafica.
     * Questo metodo deve creare e configurare tutti gli elementi UI necessari.
     */
    void initializeComponents();

    /**
     * Configura le proprietà principali del frame dell'applicazione.
     * Include impostazioni come dimensioni, posizione e comportamento di chiusura.
     */
    void setupFrame();

    /**
     * Configura tutti i listener per gli eventi dell'interfaccia utente.
     * Associa le azioni ai componenti dell'interfaccia.
     */
    void setupEventListeners();

    /**
     * Carica i dati iniziali necessari per la visualizzazione.
     * Popola l'interfaccia con i dati recuperati dal database o altre fonti.
     */
    void loadData();

    /**
     * Restituisce il frame principale dell'interfaccia grafica.
     *
     * @return il JFrame principale della finestra
     */
    JFrame getFrame();

    /**
     * Rende visibile la finestra dell'applicazione.
     * Implementazione di default che mostra il frame.
     */
    default void showFrame() {
        getFrame().setVisible(true);
    }

}