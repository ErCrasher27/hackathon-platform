package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;

import javax.swing.*;

public class HomeOrganizzatoreGUI {

    private final Controller controller;
    private final NavigationController navigationController;
    private JFrame frame;

    private JButton createHackathon;
    private JButton manageHackathon;
    private JPanel homeOrganizzatorePnl;

    public HomeOrganizzatoreGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        setupFrame();
        setupEventListeners();
    }

    private void setupFrame() {
        frame = new JFrame("Hackathon Platform - Home (Organizzatore)");
        frame.setContentPane(homeOrganizzatorePnl);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        createHackathon.addActionListener(_ -> navigationController.goToCreateHackathon(frame));
        manageHackathon.addActionListener(_ -> navigationController.goToGestisciHackathon(frame));
    }

    public JFrame getFrame() {
        return frame;
    }
}
