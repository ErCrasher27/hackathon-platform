package it.unina.hackathon.gui.giudice;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.GiudiceController;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.*;
import it.unina.hackathon.utils.UtilsUi;
import it.unina.hackathon.utils.responses.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class ValutazioneProgettoGUI implements GUIHandler {

    // region Controllers e Parametri

    private final Controller controller;
    private final NavigationController navigationController;
    private final GiudiceController giudiceController;
    private final int hackathonId;

    // endregion

    // region Components
    private final List<Team> teamList;
    private final List<Progresso> progressiList;
    private final List<Commento> commentiList;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;

    // endregion

    // region Header Components
    private JPanel contentPanel;
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;

    // endregion

    // region Left Panel - Team List
    private JLabel titleLabel;
    private JLabel hackathonInfoLabel;
    private JButton backButton;
    private JPanel teamPanel;
    private JTable teamTable;

    // endregion

    // region Right Top Panel - Progressi
    private JScrollPane teamScrollPane;
    private TeamTableModel teamTableModel;
    private JButton aggiornaTeamButton;
    private JPanel progressiPanel;
    private JTable progressiTable;
    private JScrollPane progressiScrollPane;

    // endregion

    // region Right Bottom Panel - Valutazione
    private ProgressiTableModel progressiTableModel;
    private JButton visualizzaDocumentoButton;
    private JButton aggiornaProgressiButton;
    private JPanel valutazionePanel;
    private JTabbedPane valutazioneTabbedPane;
    // Tab Commenti
    private JPanel commentiPanel;
    private JTextArea commentiArea;
    private JScrollPane commentiScrollPane;
    private JTextArea nuovoCommentoArea;
    private JScrollPane nuovoCommentoScrollPane;
    private JButton aggiungiCommentoButton;
    private JButton modificaCommentoButton;
    private JButton eliminaCommentoButton;
    // Tab Voto
    private JPanel votoPanel;
    private JLabel votoLabel;
    private JSlider votoSlider;
    private JLabel votoValueLabel;
    private JLabel criteriLabel;
    private JTextArea criteriArea;
    private JScrollPane criteriScrollPane;

    // endregion

    // region Data
    private JButton assegnaVotoButton;
    private JButton modificaVotoButton;
    private JLabel votoCorrenteLabel;
    private Hackathon hackathonCorrente;
    private Team teamSelezionato;
    private Progresso progressoSelezionato;
    private Voto votoCorrente;

    // endregion

    // region Costruttore

    public ValutazioneProgettoGUI(int hackathonId) {
        this.hackathonId = hackathonId;
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.giudiceController = controller.getGiudiceController();
        this.teamList = new ArrayList<>();
        this.progressiList = new ArrayList<>();
        this.commentiList = new ArrayList<>();

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

        // Content panel with split panes
        createContentPanel();

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Valutazione Progetto");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Header
        backButton.addActionListener(_ -> navigationController.goToHome(frame, controller.getTipoUtenteUtenteCorrente()));

        // Team selection
        teamTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTeamSelectionChanged();
            }
        });

        // Progressi selection
        progressiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onProgressoSelectionChanged();
            }
        });

        // Double click on progressi to view document
        progressiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    visualizzaDocumento();
                }
            }
        });

        // Buttons
        aggiornaTeamButton.addActionListener(_ -> loadTeamData());
        aggiornaProgressiButton.addActionListener(_ -> loadProgressiData());
        visualizzaDocumentoButton.addActionListener(_ -> visualizzaDocumento());

        // Commenti
        aggiungiCommentoButton.addActionListener(_ -> aggiungiCommento());
        modificaCommentoButton.addActionListener(_ -> modificaCommento());
        eliminaCommentoButton.addActionListener(_ -> eliminaCommento());

        // Voto
        votoSlider.addChangeListener(_ -> {
            votoValueLabel.setText(votoSlider.getValue() + "/10");
            updateVotoDescription();
        });
        assegnaVotoButton.addActionListener(_ -> assegnaVoto());
        modificaVotoButton.addActionListener(_ -> modificaVoto());
    }

    @Override
    public void loadData() {
        loadHackathonInfo();
        loadTeamData();
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
        titleLabel = new JLabel("Valutazione Progetto");
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

        // Create team panel (left)
        createTeamPanel();

        // Create progressi panel (right top)
        createProgressiPanel();

        // Create valutazione panel (right bottom)
        createValutazionePanel();

        // Setup split panes
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, progressiPanel, valutazionePanel);
        rightSplitPane.setResizeWeight(0.5);
        rightSplitPane.setDividerLocation(300);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, teamPanel, rightSplitPane);
        mainSplitPane.setResizeWeight(0.3);
        mainSplitPane.setDividerLocation(400);

        contentPanel.add(mainSplitPane, BorderLayout.CENTER);
    }

    private void createTeamPanel() {
        teamPanel = new JPanel(new BorderLayout());
        teamPanel.setBorder(new TitledBorder("Team"));

        // Button panel
        JPanel teamButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiornaTeamButton = new JButton("Aggiorna");
        aggiornaTeamButton.setPreferredSize(new Dimension(100, 30));
        teamButtonPanel.add(aggiornaTeamButton);

        // Table
        teamTableModel = new TeamTableModel();
        teamTable = new JTable(teamTableModel);
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.setRowHeight(25);
        teamScrollPane = new JScrollPane(teamTable);

        teamPanel.add(teamButtonPanel, BorderLayout.NORTH);
        teamPanel.add(teamScrollPane, BorderLayout.CENTER);
    }

    private void createProgressiPanel() {
        progressiPanel = new JPanel(new BorderLayout());
        progressiPanel.setBorder(new TitledBorder("Progressi Team"));

        // Button panel
        JPanel progressiButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiornaProgressiButton = new JButton("Aggiorna");
        visualizzaDocumentoButton = new JButton("Visualizza Documento");

        aggiornaProgressiButton.setPreferredSize(new Dimension(100, 30));
        visualizzaDocumentoButton.setPreferredSize(new Dimension(150, 30));
        visualizzaDocumentoButton.setEnabled(false);

        progressiButtonPanel.add(aggiornaProgressiButton);
        progressiButtonPanel.add(visualizzaDocumentoButton);

        // Table
        progressiTableModel = new ProgressiTableModel();
        progressiTable = new JTable(progressiTableModel);
        progressiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressiTable.setRowHeight(25);
        progressiScrollPane = new JScrollPane(progressiTable);

        progressiPanel.add(progressiButtonPanel, BorderLayout.NORTH);
        progressiPanel.add(progressiScrollPane, BorderLayout.CENTER);
    }

    private void createValutazionePanel() {
        valutazionePanel = new JPanel(new BorderLayout());
        valutazionePanel.setBorder(new TitledBorder("Valutazione"));

        valutazioneTabbedPane = new JTabbedPane();

        // Tab Commenti
        createCommentiTab();

        // Tab Voto
        createVotoTab();

        valutazionePanel.add(valutazioneTabbedPane, BorderLayout.CENTER);
    }

    private void createCommentiTab() {
        commentiPanel = new JPanel(new BorderLayout());

        // Commenti esistenti
        JPanel commentiEsistentiPanel = new JPanel(new BorderLayout());
        commentiEsistentiPanel.setBorder(new TitledBorder("Commenti Esistenti"));

        commentiArea = new JTextArea();
        commentiArea.setEditable(false);
        commentiArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        commentiScrollPane = new JScrollPane(commentiArea);
        commentiScrollPane.setPreferredSize(new Dimension(0, 150));

        commentiEsistentiPanel.add(commentiScrollPane, BorderLayout.CENTER);

        // Nuovo commento
        JPanel nuovoCommentoPanel = new JPanel(new BorderLayout());
        nuovoCommentoPanel.setBorder(new TitledBorder("Nuovo Commento"));

        nuovoCommentoArea = new JTextArea(5, 0);
        nuovoCommentoArea.setLineWrap(true);
        nuovoCommentoArea.setWrapStyleWord(true);
        nuovoCommentoScrollPane = new JScrollPane(nuovoCommentoArea);

        // Buttons
        JPanel commentoButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiungiCommentoButton = new JButton("Aggiungi Commento");
        modificaCommentoButton = new JButton("Modifica Ultimo");
        eliminaCommentoButton = new JButton("Elimina Ultimo");

        aggiungiCommentoButton.setPreferredSize(new Dimension(150, 30));
        modificaCommentoButton.setPreferredSize(new Dimension(120, 30));
        eliminaCommentoButton.setPreferredSize(new Dimension(120, 30));

        // Initially disable modify/delete
        modificaCommentoButton.setEnabled(false);
        eliminaCommentoButton.setEnabled(false);

        commentoButtonPanel.add(aggiungiCommentoButton);
        commentoButtonPanel.add(modificaCommentoButton);
        commentoButtonPanel.add(eliminaCommentoButton);

        nuovoCommentoPanel.add(nuovoCommentoScrollPane, BorderLayout.CENTER);
        nuovoCommentoPanel.add(commentoButtonPanel, BorderLayout.SOUTH);

        commentiPanel.add(commentiEsistentiPanel, BorderLayout.CENTER);
        commentiPanel.add(nuovoCommentoPanel, BorderLayout.SOUTH);

        valutazioneTabbedPane.addTab("Commenti", commentiPanel);
    }

    private void createVotoTab() {
        votoPanel = new JPanel(new BorderLayout());

        // Voto corrente
        JPanel votoCorrentePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        votoCorrentePanel.setBorder(new TitledBorder("Voto Corrente"));
        votoCorrenteLabel = new JLabel("Nessun voto assegnato");
        UtilsUi.applyStyleTitleLbl(votoCorrenteLabel);
        votoCorrentePanel.add(votoCorrenteLabel);

        // Slider panel
        JPanel sliderPanel = new JPanel(new GridBagLayout());
        sliderPanel.setBorder(new TitledBorder("Assegna Voto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Voto slider
        gbc.gridx = 0;
        gbc.gridy = 0;
        votoLabel = new JLabel("Voto:");
        sliderPanel.add(votoLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        votoSlider = new JSlider(0, 10, 6);
        votoSlider.setMajorTickSpacing(2);
        votoSlider.setMinorTickSpacing(1);
        votoSlider.setPaintTicks(true);
        votoSlider.setPaintLabels(true);
        sliderPanel.add(votoSlider, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        votoValueLabel = new JLabel("6/10");
        UtilsUi.applyStyleTitleLbl(votoValueLabel);
        sliderPanel.add(votoValueLabel, gbc);

        // Criteri
        gbc.gridx = 0;
        gbc.gridy = 1;
        criteriLabel = new JLabel("Criteri:");
        sliderPanel.add(criteriLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        criteriArea = new JTextArea(4, 0);
        criteriArea.setLineWrap(true);
        criteriArea.setWrapStyleWord(true);
        criteriScrollPane = new JScrollPane(criteriArea);
        sliderPanel.add(criteriScrollPane, gbc);

        // Buttons
        JPanel votoButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        assegnaVotoButton = new JButton("Assegna Voto");
        modificaVotoButton = new JButton("Modifica Voto");

        assegnaVotoButton.setPreferredSize(new Dimension(120, 30));
        modificaVotoButton.setPreferredSize(new Dimension(120, 30));
        modificaVotoButton.setEnabled(false);

        votoButtonPanel.add(assegnaVotoButton);
        votoButtonPanel.add(modificaVotoButton);

        votoPanel.add(votoCorrentePanel, BorderLayout.NORTH);
        votoPanel.add(sliderPanel, BorderLayout.CENTER);
        votoPanel.add(votoButtonPanel, BorderLayout.SOUTH);

        valutazioneTabbedPane.addTab("Voto", votoPanel);
    }

    // endregion

    // region Data Loading

    private void loadHackathonInfo() {
        try {
            HackathonResponse response = giudiceController.getDettagliHackathon(hackathonId);
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

    private void loadTeamData() {
        try {
            teamTable.setEnabled(false);
            aggiornaTeamButton.setEnabled(false);

            TeamListResponse response = giudiceController.getTeamDaVotare(hackathonId);
            if (response.teams() != null) {
                teamList.clear();
                teamList.addAll(response.teams());
                teamTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento team: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento team: " + e.getMessage());
        } finally {
            teamTable.setEnabled(true);
            aggiornaTeamButton.setEnabled(true);
        }
    }

    private void loadProgressiData() {
        if (teamSelezionato == null) return;

        try {
            progressiTable.setEnabled(false);
            aggiornaProgressiButton.setEnabled(false);

            ProgressoListResponse response = giudiceController.getProgressiTeam(teamSelezionato.getTeamId());
            if (response.progressi() != null) {
                progressiList.clear();
                progressiList.addAll(response.progressi());
                progressiTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento progressi: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento progressi: " + e.getMessage());
        } finally {
            progressiTable.setEnabled(true);
            aggiornaProgressiButton.setEnabled(true);
        }
    }

    private void loadCommentiData() {
        if (progressoSelezionato == null) return;

        try {
            CommentoListResponse response = giudiceController.getCommentiProgresso(progressoSelezionato.getProgressoId());
            if (response.commenti() != null) {
                commentiList.clear();
                commentiList.addAll(response.commenti());
                displayCommenti();
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento commenti: " + e.getMessage());
        }
    }

    private void loadVotoData() {
        if (teamSelezionato == null) return;

        try {
            VotoResponse response = giudiceController.getVotoTeam(hackathonId, teamSelezionato.getTeamId());
            votoCorrente = response.voto();
            updateVotoDisplay();
        } catch (Exception e) {
            votoCorrente = null;
            updateVotoDisplay();
        }
    }

    // endregion

    // region Event Handlers

    private void onTeamSelectionChanged() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow != -1) {
            teamSelezionato = teamList.get(selectedRow);
            loadProgressiData();
            loadVotoData();
        } else {
            teamSelezionato = null;
            progressiList.clear();
            progressiTableModel.fireTableDataChanged();
        }
    }

    private void onProgressoSelectionChanged() {
        int selectedRow = progressiTable.getSelectedRow();
        if (selectedRow != -1) {
            progressoSelezionato = progressiList.get(selectedRow);
            visualizzaDocumentoButton.setEnabled(true);
            loadCommentiData();
        } else {
            progressoSelezionato = null;
            visualizzaDocumentoButton.setEnabled(false);
            commentiList.clear();
            displayCommenti();
        }
    }

    // endregion

    // region Action Methods

    private void visualizzaDocumento() {
        if (progressoSelezionato == null) return;

        // TODO: Implement document viewer
        String message = "Documento: " + progressoSelezionato.getTitolo() + "\n\n" + "Descrizione: " + progressoSelezionato.getDescrizione() + "\n\n" + "File: " + (progressoSelezionato.getDocumentoNome() != null ? progressoSelezionato.getDocumentoNome() : "Nessun file");

        JOptionPane.showMessageDialog(frame, message, "Dettagli Progresso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aggiungiCommento() {
        if (progressoSelezionato == null) {
            showErrorMessage("Seleziona un progresso per aggiungere un commento");
            return;
        }

        String testo = nuovoCommentoArea.getText().trim();
        if (testo.isEmpty()) {
            showErrorMessage("Inserisci il testo del commento");
            return;
        }

        try {
            CommentoResponse response = giudiceController.scriviCommento(progressoSelezionato.getProgressoId(), testo);
            if (response.commento() != null) {
                showInfoMessage("Commento aggiunto con successo!");
                nuovoCommentoArea.setText("");
                loadCommentiData();
            } else {
                showErrorMessage("Errore nell'aggiunta del commento: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nell'aggiunta del commento: " + e.getMessage());
        }
    }

    private void modificaCommento() {
        // TODO: Implement comment modification
        showInfoMessage("Funzionalità di modifica commento non ancora implementata");
    }

    private void eliminaCommento() {
        // TODO: Implement comment deletion
        showInfoMessage("Funzionalità di eliminazione commento non ancora implementata");
    }

    private void assegnaVoto() {
        if (teamSelezionato == null) {
            showErrorMessage("Seleziona un team per assegnare il voto");
            return;
        }

        if (votoCorrente != null) {
            showErrorMessage("Hai già votato questo team. Usa 'Modifica Voto' per cambiare il voto.");
            return;
        }

        int valore = votoSlider.getValue();
        String criteri = criteriArea.getText().trim();

        try {
            VotoResponse response = giudiceController.assegnaVoto(hackathonId, teamSelezionato.getTeamId(), valore, criteri);
            if (response.voto() != null) {
                showInfoMessage("Voto assegnato con successo!");
                loadVotoData();
            } else {
                showErrorMessage("Errore nell'assegnazione del voto: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nell'assegnazione del voto: " + e.getMessage());
        }
    }

    private void modificaVoto() {
        if (votoCorrente == null) {
            showErrorMessage("Nessun voto da modificare");
            return;
        }

        int nuovoValore = votoSlider.getValue();
        String nuoviCriteri = criteriArea.getText().trim();

        try {
            VotoResponse response = giudiceController.modificaVoto(votoCorrente.getVotoId(), nuovoValore, nuoviCriteri);
            if (response.voto() != null) {
                showInfoMessage("Voto modificato con successo!");
                loadVotoData();
            } else {
                showErrorMessage("Errore nella modifica del voto: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nella modifica del voto: " + e.getMessage());
        }
    }

    // endregion

    // region Display Methods

    private void displayCommenti() {
        StringBuilder sb = new StringBuilder();

        if (commentiList.isEmpty()) {
            sb.append("Nessun commento presente per questo progresso.");
        } else {
            for (int i = 0; i < commentiList.size(); i++) {
                Commento commento = commentiList.get(i);
                sb.append("=== Commento ").append(i + 1).append(" ===\n");
                sb.append("Data: ").append(commento.getDataCommento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                if (commento.getGiudice() != null) {
                    sb.append("Giudice: ").append(commento.getGiudice().getNomeCompleto()).append("\n");
                }
                sb.append("Testo: ").append(commento.getTesto()).append("\n\n");
            }
        }

        commentiArea.setText(sb.toString());
        commentiArea.setCaretPosition(0);

        // Enable/disable buttons
        boolean hasCommenti = !commentiList.isEmpty();
        modificaCommentoButton.setEnabled(hasCommenti);
        eliminaCommentoButton.setEnabled(hasCommenti);
    }

    private void updateVotoDisplay() {
        if (votoCorrente == null) {
            votoCorrenteLabel.setText("Nessun voto assegnato");
            assegnaVotoButton.setEnabled(true);
            modificaVotoButton.setEnabled(false);
            votoSlider.setValue(6);
            criteriArea.setText("");
        } else {
            votoCorrenteLabel.setText("Voto: " + votoCorrente.getValore() + "/10 - " + votoCorrente.getValutazioneTestuale());
            assegnaVotoButton.setEnabled(false);
            modificaVotoButton.setEnabled(true);
            votoSlider.setValue(votoCorrente.getValore());
            criteriArea.setText(votoCorrente.getCriteriValutazione() != null ? votoCorrente.getCriteriValutazione() : "");
        }
    }

    private void updateVotoDescription() {
        int valore = votoSlider.getValue();
        String descrizione = "";

        if (valore >= 9) descrizione = "Eccellente";
        else if (valore >= 7) descrizione = "Buono";
        else if (valore >= 6) descrizione = "Sufficiente";
        else if (valore >= 4) descrizione = "Insufficiente";
        else descrizione = "Gravemente insufficiente";

        votoValueLabel.setText(valore + "/10 - " + descrizione);
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

    // region Table Models

    private class TeamTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nome Team", "Membri", "Progressi"};

        @Override
        public int getRowCount() {
            return teamList.size();
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
            Team team = teamList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> team.getNome();
                case 1 -> team.getNumeroMembri() + " membri";
                default -> "";
            };
        }
    }

    private class ProgressiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Data Caricamento", "Autore", "Documento"};

        @Override
        public int getRowCount() {
            return progressiList.size();
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
            Progresso progresso = progressiList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> progresso.getTitolo();
                case 1 -> progresso.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 2 -> progresso.getCaricatoDa() != null ? progresso.getCaricatoDa().getNomeCompleto() : "N/A";
                case 3 -> progresso.getDocumentoNome() != null ? progresso.getDocumentoNome() : "Nessun file";
                default -> "";
            };
        }
    }

    // endregion
}