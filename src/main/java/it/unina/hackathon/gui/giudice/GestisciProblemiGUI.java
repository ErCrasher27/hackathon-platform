package it.unina.hackathon.gui.giudice;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.Problema;
import it.unina.hackathon.utils.UtilsUi;
import it.unina.hackathon.utils.responses.GiudiceHackathonResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.ProblemaListResponse;
import it.unina.hackathon.utils.responses.ProblemaResponse;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class GestisciProblemiGUI implements GUIHandler {

    // region Controllers e Parametri

    private final HackathonController controller;
    private final int hackathonId;

    // endregion

    // region Components
    private final List<Problema> problemiList;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;

    // endregion

    // region Header Components
    private JSplitPane mainSplitPane;
    private JLabel titleLabel;
    private JLabel hackathonInfoLabel;

    // endregion

    // region Left Panel - Lista Problemi
    private JButton backButton;
    private JPanel problemiPanel;
    private JTable problemiTable;
    private JScrollPane problemiScrollPane;
    private ProblemiTableModel problemiTableModel;
    private JButton nuovoProblemaButton;
    private JButton eliminaProblemaButton;

    // endregion

    // region Right Panel - Dettagli/Modifica
    private JButton aggiornaProblemiButton;
    private JPanel dettagliPanel;
    private JPanel formPanel;
    private JPanel actionPanel;
    // Form components
    private JLabel titoloLabel;
    private JTextField titoloField;
    private JLabel descrizioneLabel;
    private JTextArea descrizioneArea;
    private JScrollPane descrizioneScrollPane;
    private JLabel dataPubblicazioneLabel;
    private JLabel dataPubblicazioneValueLabel;
    private JLabel pubblicatoDaLabel;
    private JLabel pubblicatoDaValueLabel;

    // endregion

    // region Data
    private JButton pubblicaButton;
    private Hackathon hackathonCorrente;
    private Problema problemaSelezionato;
    private boolean isModificaMode;

    // endregion

    // region Costruttore

    public GestisciProblemiGUI(int hackathonId) {
        this.hackathonId = hackathonId;
        this.controller = HackathonController.getInstance();
        this.problemiList = new ArrayList<>();
        this.isModificaMode = false;

        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }

    // endregion

    // region GUIHandler Implementation

    @Override
    public void initializeComponents() {
        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        applyStdMargin(mainPanel);

        // Header panel
        createHeaderPanel();

        // Content panel with split pane
        createContentPanel();

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Gestisci Problemi");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Header
        backButton.addActionListener(_ -> controller.vaiAllaHome(frame));

        // Problemi table
        problemiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onProblemaSelectionChanged();
            }
        });

        // Buttons
        aggiornaProblemiButton.addActionListener(_ -> loadProblemiData());
        nuovoProblemaButton.addActionListener(_ -> nuovoProblema());
        eliminaProblemaButton.addActionListener(_ -> eliminaProblema());

        // Form buttons
        pubblicaButton.addActionListener(_ -> pubblicaProblema());
    }

    @Override
    public void loadData() {
        loadHackathonInfo();
        loadProblemiData();
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    // endregion

    // region Component Creation

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());

        // Title and info
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleLabel = new JLabel("Gestione Problemi");
        applyStyleTitleLbl(titleLabel);
        hackathonInfoLabel = new JLabel("Caricamento...");

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalStrut(20));
        titlePanel.add(hackathonInfoLabel);

        // Back button
        backButton = new JButton("← Torna alla Home");
        backButton.setPreferredSize(new Dimension(150, 35));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());

        // Create left panel (problemi list)
        createProblemiPanel();

        // Create right panel (dettagli/form)
        createDettagliPanel();

        // Setup split pane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, problemiPanel, dettagliPanel);
        mainSplitPane.setResizeWeight(0.4);
        mainSplitPane.setDividerLocation(500);

        contentPanel.add(mainSplitPane, BorderLayout.CENTER);
    }

    private void createProblemiPanel() {
        problemiPanel = new JPanel(new BorderLayout());
        problemiPanel.setBorder(new TitledBorder("Problemi Pubblicati"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaProblemiButton = new JButton("Aggiorna");
        nuovoProblemaButton = new JButton("Nuovo Problema");
        eliminaProblemaButton = new JButton("Elimina");

        // Style buttons
        aggiornaProblemiButton.setPreferredSize(new Dimension(100, 30));
        nuovoProblemaButton.setPreferredSize(new Dimension(130, 30));
        eliminaProblemaButton.setPreferredSize(new Dimension(100, 30));

        // Initially disable action buttons
        eliminaProblemaButton.setEnabled(false);

        buttonPanel.add(aggiornaProblemiButton);
        buttonPanel.add(nuovoProblemaButton);
        buttonPanel.add(eliminaProblemaButton);

        // Table
        problemiTableModel = new ProblemiTableModel();
        problemiTable = new JTable(problemiTableModel);
        problemiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problemiTable.setRowHeight(25);
        problemiScrollPane = new JScrollPane(problemiTable);

        problemiPanel.add(buttonPanel, BorderLayout.NORTH);
        problemiPanel.add(problemiScrollPane, BorderLayout.CENTER);
    }

    private void createDettagliPanel() {
        dettagliPanel = new JPanel(new BorderLayout());
        dettagliPanel.setBorder(new TitledBorder("Dettagli Problema"));

        // Form panel
        createFormPanel();

        // Action panel
        createActionPanel();

        dettagliPanel.add(formPanel, BorderLayout.CENTER);
        dettagliPanel.add(actionPanel, BorderLayout.SOUTH);

        // Initially show empty state
        setFormEnabled(false);
    }

    private void createFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Titolo
        gbc.gridx = 0;
        gbc.gridy = 0;
        titoloLabel = new JLabel("Titolo:");
        formPanel.add(titoloLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titoloField = new JTextField(30);
        formPanel.add(titoloField, gbc);

        // Descrizione
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        descrizioneLabel = new JLabel("Descrizione:");
        formPanel.add(descrizioneLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descrizioneArea = new JTextArea(10, 30);
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneScrollPane = new JScrollPane(descrizioneArea);
        formPanel.add(descrizioneScrollPane, gbc);

        // Info pubblicazione (read-only)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        dataPubblicazioneLabel = new JLabel("Data Pubblicazione:");
        formPanel.add(dataPubblicazioneLabel, gbc);

        gbc.gridx = 1;
        dataPubblicazioneValueLabel = new JLabel("-");
        UtilsUi.applyStyleTitleLbl(dataPubblicazioneValueLabel);
        formPanel.add(dataPubblicazioneValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pubblicatoDaLabel = new JLabel("Pubblicato da:");
        formPanel.add(pubblicatoDaLabel, gbc);

        gbc.gridx = 1;
        pubblicatoDaValueLabel = new JLabel("-");
        UtilsUi.applyStyleTitleLbl(pubblicatoDaValueLabel);
        formPanel.add(pubblicatoDaValueLabel, gbc);
    }

    private void createActionPanel() {
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        pubblicaButton = new JButton("Pubblica");

        // Style buttons
        pubblicaButton.setPreferredSize(new Dimension(100, 35));

        actionPanel.add(pubblicaButton);
    }

    // endregion

    // region Data Loading

    private void loadHackathonInfo() {
        try {
            HackathonResponse response = controller.getDettagliHackathon(hackathonId);
            if (response.hackathon() != null) {
                hackathonCorrente = response.hackathon();
                hackathonInfoLabel.setText("Hackathon: " + hackathonCorrente.getTitolo() + " | " + hackathonCorrente.getSede());
            } else {
                hackathonInfoLabel.setText("Errore nel caricamento hackathon");
            }
        } catch (Exception e) {
            hackathonInfoLabel.setText("Errore nel caricamento hackathon");
        }
    }

    private void loadProblemiData() {
        try {
            problemiTable.setEnabled(false);
            aggiornaProblemiButton.setEnabled(false);

            ProblemaListResponse response = controller.getProblemiHackathon(hackathonId);
            if (response.problemi() != null) {
                problemiList.clear();
                problemiList.addAll(response.problemi());
                problemiTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento problemi: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento problemi: " + e.getMessage());
        } finally {
            problemiTable.setEnabled(true);
            aggiornaProblemiButton.setEnabled(true);
        }
    }

    // endregion

    // region Event Handlers

    private void onProblemaSelectionChanged() {
        int selectedRow = problemiTable.getSelectedRow();
        if (selectedRow != -1) {
            problemaSelezionato = problemiList.get(selectedRow);
            eliminaProblemaButton.setEnabled(true);
            displayProblemaDetails();
        } else {
            problemaSelezionato = null;
            eliminaProblemaButton.setEnabled(false);
            clearForm();
        }
    }

    // endregion

    // region Action Methods

    private void nuovoProblema() {
        problemaSelezionato = null;
        isModificaMode = false;
        clearForm();
        setFormEnabled(true);
        updateFormMode();
        titoloField.requestFocus();
    }

    private void eliminaProblema() {
        if (problemaSelezionato == null) return;

        int confirm = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler eliminare il problema '" + problemaSelezionato.getTitolo() + "'?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var response = controller.eliminaProblema(problemaSelezionato.getProblemaId());
                if (response.result()) {
                    showInfoMessage("Problema eliminato con successo!");
                    loadProblemiData();
                    clearForm();
                    setFormEnabled(false);
                } else {
                    showErrorMessage("Errore nell'eliminazione: " + response.message());
                }
            } catch (Exception e) {
                showErrorMessage("Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    private void pubblicaProblema() {
        if (!validaForm()) return;

        try {
            String titolo = titoloField.getText().trim();
            String descrizione = descrizioneArea.getText().trim();

            GiudiceHackathonResponse gh = controller.getGiudiceHackathon(hackathonId);
            if (gh != null) {
                ProblemaResponse response = controller.pubblicaProblema(gh.giudiceHackathon().getGiudiceHackathonId(), titolo, descrizione);

                if (response.problema() != null) {
                    showInfoMessage("Problema pubblicato con successo!");
                    loadProblemiData();
                } else {
                    showErrorMessage("Errore nella pubblicazione: " + response.message());
                }
            }
        } catch (Exception e) {
            showErrorMessage("Errore nella pubblicazione: " + e.getMessage());
        }
    }

    // endregion

    // region Form Management

    private void displayProblemaDetails() {
        if (problemaSelezionato == null) return;

        titoloField.setText(problemaSelezionato.getTitolo());
        descrizioneArea.setText(problemaSelezionato.getDescrizione());

        dataPubblicazioneValueLabel.setText(problemaSelezionato.getDataPubblicazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        pubblicatoDaValueLabel.setText(problemaSelezionato.getPubblicatoDaGiudiceHackathon() != null ? problemaSelezionato.getPubblicatoDaGiudiceHackathon().getUtenteGiudice().getNomeCompleto() : "N/A");

        setFormEnabled(false);
    }

    private void clearForm() {
        titoloField.setText("");
        descrizioneArea.setText("");
        dataPubblicazioneValueLabel.setText("-");
        pubblicatoDaValueLabel.setText("-");
    }

    private void setFormEnabled(boolean enabled) {
        titoloField.setEnabled(enabled);
        descrizioneArea.setEnabled(enabled);

        pubblicaButton.setEnabled(enabled);
    }

    private void updateFormMode() {
        if (isModificaMode) {
            dettagliPanel.setBorder(new TitledBorder("Modifica Problema"));
            pubblicaButton.setText("Aggiorna");
        } else {
            dettagliPanel.setBorder(new TitledBorder(problemaSelezionato == null ? "Nuovo Problema" : "Dettagli Problema"));
            pubblicaButton.setText("Pubblica");
        }
    }

    private boolean validaForm() {
        String titolo = titoloField.getText().trim();
        String descrizione = descrizioneArea.getText().trim();

        if (titolo.isEmpty()) {
            showErrorMessage("Il titolo è obbligatorio");
            titoloField.requestFocus();
            return false;
        }

        if (titolo.length() > 200) {
            showErrorMessage("Il titolo non può superare i 200 caratteri");
            titoloField.requestFocus();
            return false;
        }

        if (descrizione.isEmpty()) {
            showErrorMessage("La descrizione è obbligatoria");
            descrizioneArea.requestFocus();
            return false;
        }

        if (descrizione.length() > 2000) {
            showErrorMessage("La descrizione non può superare i 2000 caratteri");
            descrizioneArea.requestFocus();
            return false;
        }

        return true;
    }

    // endregion

    // region Utility Methods

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

    // endregion

    // region Table Model

    private class ProblemiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Data Pubblicazione", "Autore"};

        @Override
        public int getRowCount() {
            return problemiList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Problema problema = problemiList.get(rowIndex);
            return switch (columnIndex) {

                case 0 -> problema.getTitolo();
                case 1 -> problema.getDataPubblicazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 2 ->
                        problema.getPubblicatoDaGiudiceHackathon() != null ? problema.getPubblicatoDaGiudiceHackathon().getUtenteGiudice().getNomeCompleto() : "N/A";
                default -> "";
            };
        }
    }

    // endregion
}