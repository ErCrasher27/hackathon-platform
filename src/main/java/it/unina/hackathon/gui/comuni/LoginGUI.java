package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.utils.responses.UtenteResponse;

import javax.swing.*;
import java.awt.*;

import static it.unina.hackathon.utils.UtilsUi.*;

public class LoginGUI implements GUIHandler {

    //region Fields
    private final HackathonController controller;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel actionPanel;

    private JLabel welcomeLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    //endregion

    //region Constructor
    public LoginGUI() {
        this.controller = HackathonController.getInstance();
        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }
    //endregion

    //region Public Methods
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().showFrame());
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    @Override
    public void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        applyStdMargin(mainPanel);

        setupHeaderPanel();
        setupFormPanel();
        setupActionPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Login");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);
        frame.getRootPane().setDefaultButton(loginButton);
    }

    @Override
    public void setupEventListeners() {
        loginButton.addActionListener(_ -> effettuaLogin());
        registerButton.addActionListener(_ -> controller.vaiAllaRegistrazione(frame));
        passwordField.addActionListener(_ -> effettuaLogin());
    }

    @Override
    public void loadData() {
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }
    //endregion

    //region Private Methods
    private void setupHeaderPanel() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomeLabel = new JLabel("Benvenuto nella piattaforma");
        applyStyleTitleLbl(welcomeLabel);
        headerPanel.add(welcomeLabel);
    }

    private void setupFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
    }

    private void setupActionPanel() {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        registerButton = new JButton("Nuovo utente? Registrati!");
        registerButton.setPreferredSize(new Dimension(200, 35));

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 35));

        actionPanel.add(registerButton);
        actionPanel.add(loginButton);
    }

    private void effettuaLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError(frame, "Username è obbligatorio!");
            usernameField.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            showError(frame, "Password è obbligatoria!");
            passwordField.requestFocusInWindow();
            return;
        }

        UtenteResponse response = controller.effettuaLogin(username, password);

        if (response.utente() != null) {
            controller.vaiAllaHome(frame);
            showSuccess(frame, response.message());
        } else {
            showError(frame, response.message());
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }
    //endregion
}