package it.unina.hackathon.utils;

import javax.swing.*;
import java.awt.*;

public class UtilsUi {

    public static void applyStyleFrame(JFrame frame) {
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);
    }

    public static void applyStyleTitleLbl(JLabel titleLbl) {
        titleLbl.setFont(new Font("Arial", Font.BOLD, 18));
    }

    public static void applyStdMargin(JComponent cmp) {
        cmp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "Successo", JOptionPane.INFORMATION_MESSAGE);
    }
}
