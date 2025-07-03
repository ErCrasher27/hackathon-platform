package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.AuthenticationController;
import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.utils.UtenteResponse;

import javax.swing.*;
import java.awt.*;

import static it.unina.hackathon.utils.UtilsUi.*;

public class LoginGUI implements GUIHandler {

    // Controllers
    private final Controller controller;
    private final NavigationController navigationController;
    private final AuthenticationController authenticationController;

    // Components
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel actionPanel;

    // Form Components
    private JLabel welcomeLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.authenticationController = controller.getAuthController();

        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().showFrame());
    }

    @Override
    public void initializeComponents() {
        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        applyStdMargin(mainPanel);

        // Header panel
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomeLabel = new JLabel("Benvenuto nella piattaforma");
        applyStyleTitleLbl(welcomeLabel);
        headerPanel.add(welcomeLabel);

        // Form panel
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username row
        gbc.gridx = 0;
        gbc.gridy = 0;
        usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password row
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

        // Action panel
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        registerButton = new JButton("Nuovo utente? Registrati!");
        loginButton = new JButton("Login");

        // Style buttons
        loginButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setPreferredSize(new Dimension(200, 35));

        actionPanel.add(registerButton);
        actionPanel.add(loginButton);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Login");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);

        // Set default button
        frame.getRootPane().setDefaultButton(loginButton);
    }

    @Override
    public void setupEventListeners() {
        loginButton.addActionListener(_ -> effettuaLogin());
        registerButton.addActionListener(_ -> navigationController.goToRegistrazione(frame));

        // Enter key support for password field
        passwordField.addActionListener(_ -> effettuaLogin());
    }

    @Override
    public void loadData() {
        // Set focus on username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void effettuaLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
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

        // Attempt login
        UtenteResponse response = authenticationController.login(username, password);

        if (response.utente() != null) {
            navigationController.goToHome(frame, controller.getTipoUtenteUtenteCorrente());
            showSuccess(frame, response.message());
        } else {
            showError(frame, response.message());
            // Clear password field and focus on it
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }
}