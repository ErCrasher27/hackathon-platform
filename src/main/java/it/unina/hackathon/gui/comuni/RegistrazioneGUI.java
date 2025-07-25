package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.responses.UtenteResponse;

import javax.swing.*;
import java.awt.*;

import static it.unina.hackathon.utils.UtilsUi.*;

public class RegistrazioneGUI implements GUIHandler {

    //region Fields
    private final HackathonController controller;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel actionPanel;

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

    private JRadioButton organizzatoreRadio;
    private JRadioButton giudiceRadio;
    private JRadioButton partecipanteRadio;
    private ButtonGroup tipoUtenteGroup;

    private JButton registerButton;
    private JButton loginButton;
    //endregion

    //region Constructor
    public RegistrazioneGUI() {
        this.controller = HackathonController.getInstance();
        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }
    //endregion

    //region Public Methods
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
        frame = new JFrame("Hackathon Platform - Registrazione");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);
        frame.getRootPane().setDefaultButton(registerButton);
    }

    @Override
    public void setupEventListeners() {
        registerButton.addActionListener(_ -> effettuaRegistrazione());
        loginButton.addActionListener(_ -> controller.vaiAlLogin(frame));
        confermaPasswordField.addActionListener(_ -> effettuaRegistrazione());
    }

    @Override
    public void loadData() {
        SwingUtilities.invokeLater(() -> nomeField.requestFocusInWindow());
    }
    //endregion

    //region Private Methods
    private void setupHeaderPanel() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerLabel = new JLabel("Registrati!");
        applyStyleTitleLbl(registerLabel);
        headerPanel.add(registerLabel);
    }

    private void setupFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createDefaultConstraints();

        nomeLabel = new JLabel("Nome:");
        cognomeLabel = new JLabel("Cognome:");
        emailLabel = new JLabel("Email:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        confermaPasswordLabel = new JLabel("Conferma Password:");

        nomeField = new JTextField(20);
        cognomeField = new JTextField(20);
        emailField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confermaPasswordField = new JPasswordField(20);

        addFormField(gbc, 0, nomeLabel, nomeField);
        addFormField(gbc, 1, cognomeLabel, cognomeField);
        addFormField(gbc, 2, emailLabel, emailField);
        addFormField(gbc, 3, usernameLabel, usernameField);
        addFormField(gbc, 4, passwordLabel, passwordField);
        addFormField(gbc, 5, confermaPasswordLabel, confermaPasswordField);

        addTipoUtentePanel(gbc, 6);
    }

    private GridBagConstraints createDefaultConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addFormField(GridBagConstraints gbc, int row, JLabel label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(field, gbc);
    }

    private void addTipoUtentePanel(GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel tipoUtentePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoUtentePanel.setBorder(BorderFactory.createTitledBorder("Tipo Utente"));

        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        organizzatoreRadio = new JRadioButton(TipoUtente.ORGANIZZATORE.getDisplayName() + " - Posso creare e gestire hackathon");
        giudiceRadio = new JRadioButton(TipoUtente.GIUDICE.getDisplayName() + " - Posso valutare i team negli hackathon");
        partecipanteRadio = new JRadioButton(TipoUtente.PARTECIPANTE.getDisplayName() + " - Posso partecipare agli hackathon");
        partecipanteRadio.setSelected(true);

        tipoUtenteGroup = new ButtonGroup();
        tipoUtenteGroup.add(organizzatoreRadio);
        tipoUtenteGroup.add(giudiceRadio);
        tipoUtenteGroup.add(partecipanteRadio);

        radioPanel.add(organizzatoreRadio);
        radioPanel.add(giudiceRadio);
        radioPanel.add(partecipanteRadio);

        tipoUtentePanel.add(radioPanel);
        formPanel.add(tipoUtentePanel, gbc);
    }

    private void setupActionPanel() {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        loginButton = new JButton("Sei già registrato? Login!");
        loginButton.setPreferredSize(new Dimension(200, 35));

        registerButton = new JButton("Registrati");
        registerButton.setPreferredSize(new Dimension(120, 35));

        actionPanel.add(loginButton);
        actionPanel.add(registerButton);
    }

    private void effettuaRegistrazione() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confermaPassword = new String(confermaPasswordField.getPassword());
        TipoUtente tipoUtente = getTipoUtenteSelezionato();

        if (!validateRegistrationForm(nome, cognome, email, username, password, confermaPassword, tipoUtente)) {
            return;
        }

        UtenteResponse response = controller.effettuaRegistrazione(nome, cognome, email, username, password, confermaPassword, tipoUtente);

        if (response.utente() != null) {
            showSuccess(frame, response.message());
            controller.vaiAlLogin(frame);
        } else {
            showError(frame, response.message());
            passwordField.setText("");
            confermaPasswordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private boolean validateRegistrationForm(String nome, String cognome, String email, String username, String password, String confermaPassword, TipoUtente tipoUtente) {
        if (nome.isEmpty()) {
            showError(frame, "Nome è obbligatorio!");
            nomeField.requestFocusInWindow();
            return false;
        }

        if (cognome.isEmpty()) {
            showError(frame, "Cognome è obbligatorio!");
            cognomeField.requestFocusInWindow();
            return false;
        }

        if (email.isEmpty()) {
            showError(frame, "Email è obbligatoria!");
            emailField.requestFocusInWindow();
            return false;
        }

        if (username.isEmpty()) {
            showError(frame, "Username è obbligatorio!");
            usernameField.requestFocusInWindow();
            return false;
        }

        if (password.isEmpty()) {
            showError(frame, "Password è obbligatoria!");
            passwordField.requestFocusInWindow();
            return false;
        }

        if (!password.equals(confermaPassword)) {
            showError(frame, "Password e conferma password devono coincidere!");
            confermaPasswordField.setText("");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
            return false;
        }

        if (tipoUtente == null) {
            showError(frame, "Seleziona un tipo utente!");
            return false;
        }

        return true;
    }

    private TipoUtente getTipoUtenteSelezionato() {
        if (organizzatoreRadio.isSelected()) return TipoUtente.ORGANIZZATORE;
        if (giudiceRadio.isSelected()) return TipoUtente.GIUDICE;
        if (partecipanteRadio.isSelected()) return TipoUtente.PARTECIPANTE;
        return null;
    }
    //endregion
}