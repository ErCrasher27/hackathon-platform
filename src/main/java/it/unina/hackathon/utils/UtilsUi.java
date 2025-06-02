package it.unina.hackathon.utils;

import javax.swing.*;

public class UtilsUi {

    public static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }
}
