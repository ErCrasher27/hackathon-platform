package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.OrganizzatoreController;
import it.unina.hackathon.gui.GUIHandler;

import javax.swing.*;

import static it.unina.hackathon.utils.UtilsUi.applyStyleFrame;

public class GestisciHackathonGUI implements GUIHandler {
    private final Controller controller;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;
    private JFrame frame;
    private JPanel gestisciHackathonPnl;

    public GestisciHackathonGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.organizzatoreController = controller.getOrganizzatoreController();
        setupFrame();
        setupEventListeners();
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Gestisci Hackathon");
        frame.setContentPane(gestisciHackathonPnl);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public JFrame getFrame() {
        return frame;
    }
}
