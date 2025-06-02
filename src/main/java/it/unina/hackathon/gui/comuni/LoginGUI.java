package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.AuthenticationController;
import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.utils.UtenteResponse;

import javax.swing.*;

import static it.unina.hackathon.utils.UtilsUi.showError;
import static it.unina.hackathon.utils.UtilsUi.showSuccess;

public class LoginGUI {
    private final Controller controller;
    private final NavigationController navigationController;
    private final AuthenticationController authenticationController;
    private JFrame frame;
    private JPanel loginPnl;
    private JPanel formPnl;
    private JPanel actionPnl;
    private JPanel pwPnl;
    private JPanel headerPnl;
    private JPanel usernamePnl;
    private JLabel welcomeLbl;
    private JLabel usernameLbl;
    private JLabel pwLbl;
    private JTextField usernameFld;
    private JPasswordField pwFld;
    private JButton loginBtn;
    private JButton registerBtn;

    public LoginGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.authenticationController = controller.getAuthController();
        setupFrame();
        setupEventListeners();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().frame.setVisible(true));
    }

    private void setupFrame() {
        frame = new JFrame("Hackathon Platform - Login");
        frame.setContentPane(loginPnl);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        loginBtn.addActionListener(_ -> effettuaLogin());
        registerBtn.addActionListener(_ -> navigationController.goToRegistrazione(frame));
    }

    private void effettuaLogin() {
        String username = usernameFld.getText();
        String password = new String(pwFld.getPassword());

        UtenteResponse response = authenticationController.login(username, password);

        if (response.utente() != null) {
            navigationController.goToHome(frame, controller.getTipoUtenteUtenteCorrente());
            showSuccess(frame, response.message());
        } else {
            showError(frame, response.message());
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}