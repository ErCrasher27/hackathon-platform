package it.unina.hackathon.controller;

import it.unina.hackathon.gui.comuni.LoginGUI;
import it.unina.hackathon.gui.comuni.RegistrazioneGUI;
import it.unina.hackathon.model.Utente;

import javax.swing.*;

public class NavigationController {
    private final Controller mainController;

    public NavigationController(Controller mainController) {
        this.mainController = mainController;
    }

    public void goToHome(JFrame currentFrame, Utente utente) {
        if (utente == null) {
            JOptionPane.showMessageDialog(currentFrame, "Errore: utente non valido!");
            //return;
        }

//        JFrame newFrame = createHomeFrame(utente.getTipoUtente());
//        if (newFrame != null) {
//            showFrame(newFrame);
//            closeFrame(currentFrame);
//        } else {
//            JOptionPane.showMessageDialog(currentFrame, "Errore: tipo utente non riconosciuto!");
//        }
    }

//    private JFrame createHomeFrame(TipoUtente tipoUtente) {
//        return switch (tipoUtente) {
//            case ORGANIZZATORE -> {
//                HomeOrganizzatoreGUI gui = new HomeOrganizzatoreGUI(mainController);
//                yield gui.getFrame();
//            }
//            case GIUDICE -> {
//                HomeGiudiceGUI gui = new HomeGiudiceGUI(mainController);
//                yield gui.getFrame();
//            }
//            case PARTECIPANTE -> {
//                HomePartecipanteGUI gui = new HomePartecipanteGUI(mainController);
//                yield gui.getFrame();
//            }
//        };
//    }

    public void goToLogin(JFrame currentFrame) {
        LoginGUI loginGUI = new LoginGUI();
        showFrame(loginGUI.getFrame());
        closeFrame(currentFrame);
    }

    public void goToRegistrazione(JFrame currentFrame) {
        RegistrazioneGUI regGUI = new RegistrazioneGUI();
        showFrame(regGUI.getFrame());
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