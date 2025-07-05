package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.OrganizzatoreController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.utils.responses.HackathonResponse;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static it.unina.hackathon.utils.UtilsUi.*;

public class CreaHackathonGUI implements GUIHandler {

    // Controllers
    private final Controller controller;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;

    // Components
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel actionPanel;

    // Header components
    private JLabel titleLabel;

    // Form components
    private JLabel titoloLabel;
    private JTextField titoloField;
    private JLabel descrizioneLabel;
    private JTextArea descrizioneArea;
    private JScrollPane descrizioneScrollPane;
    private JLabel sedeLabel;
    private JTextField sedeField;

    // Date/Time components
    private JLabel dataInizioLabel;
    private JFormattedTextField dataInizioField;
    private JFormattedTextField oraInizioField;
    private JLabel dataFineLabel;
    private JFormattedTextField dataFineField;
    private JFormattedTextField oraFineField;

    // Numeric components
    private JLabel maxIscrittiLabel;
    private JSpinner maxIscrittiSpinner;
    private JLabel maxTeamSizeLabel;
    private JSpinner maxTeamSizeSpinner;

    // Action buttons
    private JButton annullaButton;
    private JButton creaButton;

    public CreaHackathonGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.organizzatoreController = controller.getOrganizzatoreController();

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
        titleLabel = new JLabel("Crea il TUO Hackathon!");
        applyStyleTitleLbl(titleLabel);
        headerPanel.add(titleLabel);

        // Form panel with GridBagLayout for precise control
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Titolo
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        titoloLabel = new JLabel("Titolo:");
        formPanel.add(titoloLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titoloField = new JTextField(30);
        formPanel.add(titoloField, gbc);

        // Descrizione
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        descrizioneLabel = new JLabel("Descrizione:");
        formPanel.add(descrizioneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        descrizioneArea = new JTextArea(4, 30);
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneArea.setBorder(BorderFactory.createLoweredBevelBorder());
        descrizioneScrollPane = new JScrollPane(descrizioneArea);
        formPanel.add(descrizioneScrollPane, gbc);

        // Sede
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        sedeLabel = new JLabel("Sede:");
        formPanel.add(sedeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        sedeField = new JTextField(30);
        formPanel.add(sedeField, gbc);

        // Data/Ora Inizio
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dataInizioLabel = new JLabel("Data Inizio:");
        formPanel.add(dataInizioLabel, gbc);

        // Panel for date and time fields
        JPanel dataInizioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dataInizioField = new JFormattedTextField();
        oraInizioField = new JFormattedTextField();
        setupDateTimeFields(dataInizioField, oraInizioField);

        dataInizioPanel.add(dataInizioField);
        dataInizioPanel.add(new JLabel("ore"));
        dataInizioPanel.add(oraInizioField);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(dataInizioPanel, gbc);

        // Data/Ora Fine
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dataFineLabel = new JLabel("Data Fine:");
        formPanel.add(dataFineLabel, gbc);

        JPanel dataFinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dataFineField = new JFormattedTextField();
        oraFineField = new JFormattedTextField();
        setupDateTimeFields(dataFineField, oraFineField);

        dataFinePanel.add(dataFineField);
        dataFinePanel.add(new JLabel("ore"));
        dataFinePanel.add(oraFineField);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(dataFinePanel, gbc);

        // Max Iscritti and Max Team Size in same row
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        maxIscrittiLabel = new JLabel("Max Iscritti:");
        formPanel.add(maxIscrittiLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        maxIscrittiSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        maxIscrittiSpinner.setPreferredSize(new Dimension(100, 25));
        formPanel.add(maxIscrittiSpinner, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        maxTeamSizeLabel = new JLabel("Max Team Size:");
        formPanel.add(maxTeamSizeLabel, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        maxTeamSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 10, 1));
        maxTeamSizeSpinner.setPreferredSize(new Dimension(100, 25));
        formPanel.add(maxTeamSizeSpinner, gbc);

        // Action panel
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        annullaButton = new JButton("Annulla");
        creaButton = new JButton("Crea Hackathon");

        // Style buttons
        annullaButton.setPreferredSize(new Dimension(120, 35));
        creaButton.setPreferredSize(new Dimension(150, 35));

        actionPanel.add(annullaButton);
        actionPanel.add(creaButton);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Crea Hackathon");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);

        // Set default button
        frame.getRootPane().setDefaultButton(creaButton);
    }

    @Override
    public void setupEventListeners() {
        creaButton.addActionListener(_ -> creaHackathon());
        annullaButton.addActionListener(_ -> navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente()));
    }

    @Override
    public void loadData() {
        // Set focus on title field and populate with example values
        SwingUtilities.invokeLater(() -> {
            titoloField.requestFocusInWindow();

            // Set default example values
            dataInizioField.setValue("01/07/2025");
            oraInizioField.setValue("09:00");
            dataFineField.setValue("02/07/2025");
            oraFineField.setValue("18:00");
        });
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void setupDateTimeFields(JFormattedTextField dateField, JFormattedTextField timeField) {
        try {
            // Date formatter
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dateField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
            dateField.setColumns(10);
            dateField.setToolTipText("Formato: dd/MM/yyyy (es: 15/06/2025)");

            // Time formatter
            MaskFormatter timeFormatter = new MaskFormatter("##:##");
            timeFormatter.setPlaceholderCharacter('_');
            timeField.setFormatterFactory(new DefaultFormatterFactory(timeFormatter));
            timeField.setColumns(5);
            timeField.setToolTipText("Formato: HH:mm (es: 09:30)");

        } catch (ParseException e) {
            showError(frame, "Errore nell'inizializzazione dei campi data/ora: " + e.getMessage());
        }
    }

    private void creaHackathon() {
        try {
            // Get and validate form data
            String titolo = titoloField.getText().trim();
            String descrizione = descrizioneArea.getText().trim();
            String sede = sedeField.getText().trim();

            // Basic frontend validation
            if (titolo.isEmpty()) {
                showError(frame, "Titolo è obbligatorio!");
                titoloField.requestFocusInWindow();
                return;
            }

            if (sede.isEmpty()) {
                showError(frame, "Sede è obbligatoria!");
                sedeField.requestFocusInWindow();
                return;
            }

            if (dataInizioField.getText().contains("_") || oraInizioField.getText().contains("_")) {
                showError(frame, "Data e ora di inizio sono obbligatorie!");
                dataInizioField.requestFocusInWindow();
                return;
            }

            if (dataFineField.getText().contains("_") || oraFineField.getText().contains("_")) {
                showError(frame, "Data e ora di fine sono obbligatorie!");
                dataFineField.requestFocusInWindow();
                return;
            }

            // Parse dates
            LocalDateTime dataInizio = parseDateTime(dataInizioField.getText(), oraInizioField.getText());
            LocalDateTime dataFine = parseDateTime(dataFineField.getText(), oraFineField.getText());

            // Validate date logic
            if (dataInizio.isAfter(dataFine)) {
                showError(frame, "La data di inizio deve essere precedente alla data di fine!");
                dataInizioField.requestFocusInWindow();
                return;
            }

            if (dataInizio.isBefore(LocalDateTime.now().plusDays(1))) {
                showError(frame, "L'hackathon deve essere programmato almeno 1 giorno nel futuro!");
                dataInizioField.requestFocusInWindow();
                return;
            }

            int maxIscritti = (Integer) maxIscrittiSpinner.getValue();
            int maxTeamSize = (Integer) maxTeamSizeSpinner.getValue();

            // Show loading state
            creaButton.setEnabled(false);
            creaButton.setText("Creazione in corso...");

            // Call controller
            HackathonResponse response = organizzatoreController.creaHackathon(titolo, descrizione, sede, dataInizio, dataFine, maxIscritti, maxTeamSize);

            if (response.hackathon() != null) {
                showSuccess(frame, response.message());
                navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente());
            } else {
                showError(frame, response.message());
            }

        } catch (DateTimeParseException e) {
            showError(frame, "Formato data/ora non valido. Usare dd/MM/yyyy per la data e HH:mm per l'ora");
        } catch (Exception e) {
            showError(frame, "Errore imprevisto: " + e.getMessage());
        } finally {
            // Restore button state
            creaButton.setEnabled(true);
            creaButton.setText("Crea Hackathon");
        }
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(dateStr.trim() + " " + timeStr.trim(), formatter);
        } catch (Exception e) {
            throw new DateTimeParseException("Formato data/ora non valido", dateStr + " " + timeStr, 0);
        }
    }
}