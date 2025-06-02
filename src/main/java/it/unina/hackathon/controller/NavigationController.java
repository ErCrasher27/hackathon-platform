package it.unina.hackathon.controller;

import it.unina.hackathon.gui.comuni.LoginGUI;
import it.unina.hackathon.gui.comuni.RegistrazioneGUI;
import it.unina.hackathon.gui.giudice.HomeGiudiceGUI;
import it.unina.hackathon.gui.organizzatore.CreaHackathonGUI;
import it.unina.hackathon.gui.organizzatore.GestisciHackathonGUI;
import it.unina.hackathon.gui.organizzatore.HomeOrganizzatoreGUI;
import it.unina.hackathon.gui.partecipante.HomePartecipanteGUI;
import it.unina.hackathon.model.enums.TipoUtente;

import javax.swing.*;

public class NavigationController {
    private final Controller mainController;

    public NavigationController(Controller mainController) {
        this.mainController = mainController;
    }

    public void goToHome(JFrame currentFrame, TipoUtente tipoUtente) {
        JFrame newFrame = createHomeFrame(tipoUtente);
        if (newFrame != null) {
            showFrame(newFrame);
            closeFrame(currentFrame);
        } else {
            JOptionPane.showMessageDialog(currentFrame, "Errore: tipo utente non riconosciuto!");
        }
    }

    private JFrame createHomeFrame(TipoUtente tipoUtente) {
        return switch (tipoUtente) {
            case ORGANIZZATORE -> {
                HomeOrganizzatoreGUI gui = new HomeOrganizzatoreGUI();
                yield gui.getFrame();
            }
            case GIUDICE -> {
                HomeGiudiceGUI gui = new HomeGiudiceGUI();
                yield gui.getFrame();
            }
            case PARTECIPANTE -> {
                HomePartecipanteGUI gui = new HomePartecipanteGUI();
                yield gui.getFrame();
            }
        };
    }

    public void goToLogin(JFrame currentFrame) {
        LoginGUI newGui = new LoginGUI();
        showFrame(newGui.getFrame());
        closeFrame(currentFrame);
    }

    public void goToRegistrazione(JFrame currentFrame) {
        RegistrazioneGUI newGui = new RegistrazioneGUI();
        showFrame(newGui.getFrame());
        closeFrame(currentFrame);
    }

    public void goToCreateHackathon(JFrame currentFrame) {
        CreaHackathonGUI newGui = new CreaHackathonGUI();
        showFrame(newGui.getFrame());
        closeFrame(currentFrame);
    }

    public void goToGestisciHackathon(JFrame currentFrame) {
        GestisciHackathonGUI newGui = new GestisciHackathonGUI();
        showFrame(newGui.getFrame());
        closeFrame(currentFrame);
    }

    private void showFrame(JFrame frame) {
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void closeFrame(JFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }
}