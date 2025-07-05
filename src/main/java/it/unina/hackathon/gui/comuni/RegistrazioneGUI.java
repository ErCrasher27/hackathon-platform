package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.AuthenticationController;
import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.responses.UtenteResponse;

import javax.swing.*;
import java.awt.*;

import static it.unina.hackathon.utils.UtilsUi.*;

public class RegistrazioneGUI implements GUIHandler {

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
    private JLabel registerLabel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel emailLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confermaPasswordLabel;

    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confermaPasswordField;

    // Radio buttons for user type
    private JRadioButton organizzatoreRadio;
    private JRadioButton giudiceRadio;
    private JRadioButton partecipanteRadio;
    private ButtonGroup tipoUtenteGroup;

    private JButton registerButton;
    private JButton loginButton;

    public RegistrazioneGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.authenticationController = controller.getAuthController();

        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }

    @Override
    public void initializeComponents() {
        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        applyStdMargin(mainPanel);

        // Header panel
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLabel = new JLabel("Registrati!");
        applyStyleTitleLbl(registerLabel);
        headerPanel.add(registerLabel);

        // Form panel with GridBagLayout for better control
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = row;
        nomeLabel = new JLabel("Nome:");
        formPanel.add(nomeLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeField = new JTextField(20);
        formPanel.add(nomeField, gbc);

        // Cognome
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        cognomeLabel = new JLabel("Cognome:");
        formPanel.add(cognomeLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cognomeField = new JTextField(20);
        formPanel.add(cognomeField, gbc);

        // Email
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        emailLabel = new JLabel("Email:");
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Username
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        usernameLabel = new JLabel("Username:");
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        passwordLabel = new JLabel("Password:");
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Conferma Password
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        confermaPasswordLabel = new JLabel("Conferma Password:");
        formPanel.add(confermaPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        confermaPasswordField = new JPasswordField(20);
        formPanel.add(confermaPasswordField, gbc);

        // Tipo Utente Radio Buttons
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel tipoUtentePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoUtentePanel.setBorder(BorderFactory.createTitledBorder("Tipo Utente"));

        organizzatoreRadio = new JRadioButton(TipoUtente.ORGANIZZATORE.getDisplayName() + " - Posso creare e gestire hackathon");
        giudiceRadio = new JRadioButton(TipoUtente.GIUDICE.getDisplayName() + " - Posso valutare i team negli hackathon");
        partecipanteRadio = new JRadioButton(TipoUtente.PARTECIPANTE.getDisplayName() + " - Posso partecipare agli hackathon");

        // Set default selection
        partecipanteRadio.setSelected(true);

        tipoUtenteGroup = new ButtonGroup();
        tipoUtenteGroup.add(organizzatoreRadio);
        tipoUtenteGroup.add(giudiceRadio);
        tipoUtenteGroup.add(partecipanteRadio);

        // Layout radio buttons vertically
        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        radioPanel.add(organizzatoreRadio);
        radioPanel.add(giudiceRadio);
        radioPanel.add(partecipanteRadio);

        tipoUtentePanel.add(radioPanel);
        formPanel.add(tipoUtentePanel, gbc);

        // Action panel
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        loginButton = new JButton("Sei già registrato? Login!");
        registerButton = new JButton("Registrati");

        // Style buttons
        registerButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setPreferredSize(new Dimension(200, 35));

        actionPanel.add(loginButton);
        actionPanel.add(registerButton);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Registrazione");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);

        // Set default button
        frame.getRootPane().setDefaultButton(registerButton);
    }

    @Override
    public void setupEventListeners() {
        registerButton.addActionListener(_ -> effettuaRegistrazione());
        loginButton.addActionListener(_ -> navigationController.goToLogin(frame));

        // Enter key support for last password field
        confermaPasswordField.addActionListener(_ -> effettuaRegistrazione());
    }

    @Override
    public void loadData() {
        // Set focus on nome field
        SwingUtilities.invokeLater(() -> nomeField.requestFocusInWindow());
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void effettuaRegistrazione() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confermaPassword = new String(confermaPasswordField.getPassword());
        TipoUtente tipoUtente = getTipoUtenteSelezionato();

        // Basic frontend validation
        if (nome.isEmpty()) {
            showError(frame, "Nome è obbligatorio!");
            nomeField.requestFocusInWindow();
            return;
        }

        if (cognome.isEmpty()) {
            showError(frame, "Cognome è obbligatorio!");
            cognomeField.requestFocusInWindow();
            return;
        }

        if (email.isEmpty()) {
            showError(frame, "Email è obbligatoria!");
            emailField.requestFocusInWindow();
            return;
        }

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

        if (!password.equals(confermaPassword)) {
            showError(frame, "Password e conferma password devono coincidere!");
            confermaPasswordField.setText("");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return;
        }

        if (tipoUtente == null) {
            showError(frame, "Seleziona un tipo utente!");
            return;
        }

        // Attempt registration
        UtenteResponse response = authenticationController.register(nome, cognome, email, username, password, confermaPassword, tipoUtente);

        if (response.utente() != null) {
            showSuccess(frame, response.message());
            navigationController.goToLogin(frame);
        } else {
            showError(frame, response.message());

            // Clear sensitive fields on error
            passwordField.setText("");
            confermaPasswordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private TipoUtente getTipoUtenteSelezionato() {
        if (organizzatoreRadio.isSelected()) return TipoUtente.ORGANIZZATORE;
        if (giudiceRadio.isSelected()) return TipoUtente.GIUDICE;
        if (partecipanteRadio.isSelected()) return TipoUtente.PARTECIPANTE;
        return null;
    }
}