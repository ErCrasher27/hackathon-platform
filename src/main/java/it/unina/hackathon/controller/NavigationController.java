package it.unina.hackathon.controller;

import it.unina.hackathon.gui.comuni.LoginGUI;
import it.unina.hackathon.gui.comuni.RegistrazioneGUI;
import it.unina.hackathon.gui.tmp.giudice.HomeGiudiceGUI;
import it.unina.hackathon.gui.organizzatore.CreaHackathonGUI;
import it.unina.hackathon.gui.organizzatore.GestisciHackathonGUI;
import it.unina.hackathon.gui.organizzatore.HomeOrganizzatoreGUI;
import it.unina.hackathon.gui.tmp.partecipante.HomePartecipanteGUI;
import it.unina.hackathon.model.enums.TipoUtente;

import javax.swing.*;

import static it.unina.hackathon.utils.UtilsUi.applyStyleFrame;

public class NavigationController {

    public NavigationController(Controller mainController) {
    }

    public void goToHome(JFrame currentFrame, TipoUtente tipoUtente) {
        JFrame newFrame = createHomeFrame(tipoUtente);
        if (newFrame != null) {
            switchFrame(currentFrame, newFrame);
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
        switchFrame(currentFrame, new LoginGUI().getFrame());
    }

    public void goToRegistrazione(JFrame currentFrame) {
        switchFrame(currentFrame, new RegistrazioneGUI().getFrame());
    }

    public void goToCreateHackathon(JFrame currentFrame) {
        switchFrame(currentFrame, new CreaHackathonGUI().getFrame());
    }

    public void goToGestisciHackathon(JFrame currentFrame) {
        switchFrame(currentFrame, new GestisciHackathonGUI().getFrame());
    }

    private void switchFrame(JFrame currentFrame, JFrame newFrame) {
        showFrame(newFrame);
        closeFrame(currentFrame);
    }

    private void showFrame(JFrame frame) {
        applyStyleFrame(frame);
        frame.setVisible(true);
    }

    private void closeFrame(JFrame frame) {
        frame.setVisible(false);
        frame.dispose();
    }
}