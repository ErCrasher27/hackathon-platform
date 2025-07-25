package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.HackathonController;
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

    //region Fields
    private final HackathonController controller;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JPanel actionPanel;

    private JLabel titleLabel;

    private JLabel titoloLabel;
    private JTextField titoloField;
    private JLabel descrizioneLabel;
    private JTextArea descrizioneArea;
    private JScrollPane descrizioneScrollPane;
    private JLabel sedeLabel;
    private JTextField sedeField;

    private JLabel dataInizioLabel;
    private JFormattedTextField dataInizioField;
    private JFormattedTextField oraInizioField;
    private JLabel dataFineLabel;
    private JFormattedTextField dataFineField;
    private JFormattedTextField oraFineField;

    private JLabel maxIscrittiLabel;
    private JSpinner maxIscrittiSpinner;
    private JLabel maxTeamSizeLabel;
    private JSpinner maxTeamSizeSpinner;

    private JButton annullaButton;
    private JButton creaButton;
    //endregion

    //region Constructor
    public CreaHackathonGUI() {
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

        createHeaderPanel();
        createFormPanel();
        createActionPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Crea Hackathon");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);
        frame.getRootPane().setDefaultButton(creaButton);
    }

    @Override
    public void setupEventListeners() {
        creaButton.addActionListener(_ -> creaHackathon());
        annullaButton.addActionListener(_ -> controller.vaiAllaHome(frame));
    }

    @Override
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            titoloField.requestFocusInWindow();
            dataInizioField.setValue("01/07/2025");
            oraInizioField.setValue("09:00");
            dataFineField.setValue("02/07/2025");
            oraFineField.setValue("18:00");
        });
    }
    //endregion

    //region Private Methods
    private void createHeaderPanel() {
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Crea il TUO Hackathon!");
        applyStyleTitleLbl(titleLabel);
        headerPanel.add(titleLabel);
    }

    private void createFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        addFormField(gbc, row++, "Titolo:", titoloLabel = new JLabel("Titolo:"), titoloField = new JTextField(30), 1, 2);

        addDescrizioneField(gbc, row++);

        addFormField(gbc, row++, "Sede:", sedeLabel = new JLabel("Sede:"), sedeField = new JTextField(30), 1, 2);

        addDateTimeField(gbc, row++, "Data Inizio:", true);
        addDateTimeField(gbc, row++, "Data Fine:", false);

        addNumericFields(gbc, row);
    }

    private void addFormField(GridBagConstraints gbc, int row, String labelText, JLabel label, JComponent field, int gridwidth, int colSpan) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(label, gbc);

        gbc.gridx = gridwidth;
        gbc.gridwidth = colSpan;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(field, gbc);
    }

    private void addDescrizioneField(GridBagConstraints gbc, int row) {
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

        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
    }

    private void addDateTimeField(GridBagConstraints gbc, int row, String labelText, boolean isInizio) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel label = new JLabel(labelText);
        if (isInizio) {
            dataInizioLabel = label;
        } else {
            dataFineLabel = label;
        }
        formPanel.add(label, gbc);

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JFormattedTextField dateField = new JFormattedTextField();
        JFormattedTextField timeField = new JFormattedTextField();

        if (isInizio) {
            dataInizioField = dateField;
            oraInizioField = timeField;
        } else {
            dataFineField = dateField;
            oraFineField = timeField;
        }

        setupDateTimeFields(dateField, timeField);

        dateTimePanel.add(dateField);
        dateTimePanel.add(new JLabel("ore"));
        dateTimePanel.add(timeField);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(dateTimePanel, gbc);
    }

    private void addNumericFields(GridBagConstraints gbc, int row) {
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
    }

    private void createActionPanel() {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        annullaButton = new JButton("Annulla");
        annullaButton.setPreferredSize(new Dimension(120, 35));

        creaButton = new JButton("Crea Hackathon");
        creaButton.setPreferredSize(new Dimension(150, 35));

        actionPanel.add(annullaButton);
        actionPanel.add(creaButton);
    }

    private void setupDateTimeFields(JFormattedTextField dateField, JFormattedTextField timeField) {
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dateField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
            dateField.setColumns(10);
            dateField.setToolTipText("Formato: dd/MM/yyyy (es: 15/06/2025)");

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
            String titolo = titoloField.getText().trim();
            String descrizione = descrizioneArea.getText().trim();
            String sede = sedeField.getText().trim();

            if (!validateForm(titolo, sede)) {
                return;
            }

            LocalDateTime dataInizio = parseDateTime(dataInizioField.getText(), oraInizioField.getText());
            LocalDateTime dataFine = parseDateTime(dataFineField.getText(), oraFineField.getText());

            if (!validateDates(dataInizio, dataFine)) {
                return;
            }

            int maxIscritti = (Integer) maxIscrittiSpinner.getValue();
            int maxTeamSize = (Integer) maxTeamSizeSpinner.getValue();

            creaButton.setEnabled(false);
            creaButton.setText("Creazione in corso...");

            HackathonResponse response = controller.creaHackathon(titolo, descrizione, sede, dataInizio, dataFine, maxIscritti, maxTeamSize);

            if (response.hackathon() != null) {
                showSuccess(frame, response.message());
                controller.vaiAllaHome(frame);
            } else {
                showError(frame, response.message());
            }

        } catch (DateTimeParseException e) {
            showError(frame, "Formato data/ora non valido. Usare dd/MM/yyyy per la data e HH:mm per l'ora");
        } catch (Exception e) {
            showError(frame, "Errore imprevisto: " + e.getMessage());
        } finally {
            creaButton.setEnabled(true);
            creaButton.setText("Crea Hackathon");
        }
    }

    private boolean validateForm(String titolo, String sede) {
        if (titolo.isEmpty()) {
            showError(frame, "Titolo è obbligatorio!");
            titoloField.requestFocusInWindow();
            return false;
        }

        if (sede.isEmpty()) {
            showError(frame, "Sede è obbligatoria!");
            sedeField.requestFocusInWindow();
            return false;
        }

        if (dataInizioField.getText().contains("_") || oraInizioField.getText().contains("_")) {
            showError(frame, "Data e ora di inizio sono obbligatorie!");
            dataInizioField.requestFocusInWindow();
            return false;
        }

        if (dataFineField.getText().contains("_") || oraFineField.getText().contains("_")) {
            showError(frame, "Data e ora di fine sono obbligatorie!");
            dataFineField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private boolean validateDates(LocalDateTime dataInizio, LocalDateTime dataFine) {
        if (dataInizio.isAfter(dataFine)) {
            showError(frame, "La data di inizio deve essere precedente alla data di fine!");
            dataInizioField.requestFocusInWindow();
            return false;
        }

        if (dataInizio.isBefore(LocalDateTime.now().plusDays(1))) {
            showError(frame, "L'hackathon deve essere programmato almeno 1 giorno nel futuro!");
            dataInizioField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(dateStr.trim() + " " + timeStr.trim(), formatter);
        } catch (Exception e) {
            throw new DateTimeParseException("Formato data/ora non valido", dateStr + " " + timeStr, 0);
        }
    }
    //endregion
}