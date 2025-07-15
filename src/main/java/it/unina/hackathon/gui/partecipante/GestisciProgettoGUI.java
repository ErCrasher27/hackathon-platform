package it.unina.hackathon.gui.partecipante;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.PartecipanteController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.*;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.UtilsUi;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class GestisciProgettoGUI implements GUIHandler {

    // region Controllers e Parametri
    private final Controller controller;

    // endregion
    private final NavigationController navigationController;
    private final PartecipanteController partecipanteController;
    private final int hackathonId;
    // region Data Lists
    private final List<MembroTeam> membriList;

    // endregion
    private final List<Progresso> progressiList;
    private final List<Progresso> documentiList;
    private final List<Team> teamDisponibili;
    private final List<InvitoTeam> invitiRicevuti;
    // region Components
    private JFrame frame;

    // endregion
    private JPanel mainPanel;
    // region Header Components
    private JPanel headerPanel;

    // endregion
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JLabel titleLabel;
    private JLabel hackathonInfoLabel;
    private JLabel teamInfoLabel;
    private JButton backButton;
    // region Tab Team - Dynamic Components
    private JPanel teamPanel;

    // endregion
    private CardLayout teamCardLayout;
    // Nessun Team Panel
    private JPanel nessunTeamPanel;
    private JTextField nomeTeamField;
    private JButton creaTeamButton;
    private JTable teamDisponibiliTable;
    private JScrollPane teamDisponibiliScrollPane;
    private TeamDisponibiliTableModel teamDisponibiliTableModel;
    private JButton aggiornaTeamDispButton;
    private JButton richiestaIngressoButton;
    // Inviti Panel
    private JPanel invitiPanel;
    private JTable invitiTable;
    private JScrollPane invitiScrollPane;
    private InvitiTableModel invitiTableModel;
    private JButton aggiornaInvitiButton;
    private JButton accettaInvitoButton;
    private JButton rifiutaInvitoButton;
    // Gestione Team Panel
    private JPanel gestioneTeamPanel;
    private JPanel teamInfoPanel;
    private JPanel membriPanel;
    private JTable membriTable;
    private JScrollPane membriScrollPane;
    private MembriTableModel membriTableModel;
    private JButton aggiornaMembriButton;
    private JButton invitaMembroButton;
    private JButton rimuoviMembroButton;
    private JButton abbandonaTeamButton;
    // region Tab Progressi
    private JPanel progressiPanel;

    // endregion
    private JPanel progressiListPanel;
    private JPanel nuovoProgressoPanel;
    private JTable progressiTable;
    private JScrollPane progressiScrollPane;
    private ProgressiTableModel progressiTableModel;
    private JButton aggiornaProgressiButton;
    private JButton eliminaProgressoButton;
    // Form nuovo progresso
    private JTextField titoloProgressoField;
    private JTextArea descrizioneProgressoArea;
    private JScrollPane descrizioneProgressoScrollPane;
    private JButton selezionaFileButton;
    private JLabel fileSelezionatoLabel;
    private JButton caricaProgressoButton;
    // region Tab Documenti
    private JPanel documentiPanel;

    // endregion
    private JTable documentiTable;
    private JScrollPane documentiScrollPane;
    private DocumentiTableModel documentiTableModel;
    private JButton aggiornaDocumentiButton;
    private JButton visualizzaDocumentoButton;
    private JButton scaricaDocumentoButton;
    private JButton eliminaDocumentoButton;
    // region Data
    private StatoTeam statoTeamCorrente;

    // endregion
    private Hackathon hackathonCorrente;
    private Team teamCorrente;
    private File fileSelezionato;
    private Progresso progressoSelezionato;

    // region Costruttore
    public GestisciProgettoGUI(int hackathonId) {
        this.hackathonId = hackathonId;
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.partecipanteController = controller.getPartecipanteController();

        this.membriList = new ArrayList<>();
        this.progressiList = new ArrayList<>();
        this.documentiList = new ArrayList<>();
        this.teamDisponibili = new ArrayList<>();
        this.invitiRicevuti = new ArrayList<>();

        this.statoTeamCorrente = StatoTeam.NESSUN_TEAM;

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

        // Content panel with tabs
        createContentPanel();

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // endregion

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Gestisci Progetto");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Header
        backButton.addActionListener(_ -> navigationController.goToHome(frame, controller.getTipoUtenteUtenteCorrente()));

        // Team events
        setupTeamEvents();

        // Progressi events
        setupProgressiEvents();

        // Documenti events
        setupDocumentiEvents();
    }

    @Override
    public void loadData() {
        loadHackathonInfo();
        loadTeamInfo();
        loadProgressiData();
        loadDocumentiData();
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    // region Component Creation
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());

        // Title and info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Gestione Progetto");
        applyStyleTitleLbl(titleLabel);
        hackathonInfoLabel = new JLabel("Caricamento...");
        teamInfoLabel = new JLabel("Team: Caricamento...");
        UtilsUi.applyStyleTitleLbl(teamInfoLabel);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(hackathonInfoLabel);
        titlePanel.add(teamInfoLabel);

        // Back button
        backButton = new JButton("← Torna alla Home");
        backButton.setPreferredSize(new Dimension(150, 35));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
    }

    // endregion

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Create tabs
        createTeamTab();
        createProgressiTab();
        createDocumentiTab();

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTeamTab() {
        teamPanel = new JPanel();
        teamCardLayout = new CardLayout();
        teamPanel.setLayout(teamCardLayout);

        // Create different panels for different states
        createNessunTeamPanel();
        createInvitiPanel();
        createGestioneTeamPanel();

        teamPanel.add(nessunTeamPanel, "NESSUN_TEAM");
        teamPanel.add(invitiPanel, "INVITI_PENDENTI");
        teamPanel.add(gestioneTeamPanel, "HA_TEAM");

        tabbedPane.addTab("Team", teamPanel);
    }

    private void createNessunTeamPanel() {
        nessunTeamPanel = new JPanel(new BorderLayout());

        // Header info
        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setBorder(new TitledBorder("Gestione Team"));
        JLabel infoLabel = new JLabel("Non fai parte di nessun team per questo hackathon");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerInfoPanel.add(infoLabel);

        // Main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Top: Create team
        JPanel creaTeamPanel = new JPanel(new BorderLayout());
        creaTeamPanel.setBorder(new TitledBorder("Crea Nuovo Team"));

        JPanel formCreaTeam = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formCreaTeam.add(new JLabel("Nome Team:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nomeTeamField = new JTextField(25);
        formCreaTeam.add(nomeTeamField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        creaTeamButton = new JButton("Crea Team");
        creaTeamButton.setPreferredSize(new Dimension(120, 30));
        formCreaTeam.add(creaTeamButton, gbc);

        creaTeamPanel.add(formCreaTeam, BorderLayout.CENTER);

        // Bottom: Available teams
        JPanel teamDispPanel = new JPanel(new BorderLayout());
        teamDispPanel.setBorder(new TitledBorder("Team Disponibili"));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiornaTeamDispButton = new JButton("Aggiorna");
        richiestaIngressoButton = new JButton("Richiedi Ingresso");

        aggiornaTeamDispButton.setPreferredSize(new Dimension(100, 30));
        richiestaIngressoButton.setPreferredSize(new Dimension(150, 30));
        richiestaIngressoButton.setEnabled(false);

        btnPanel.add(aggiornaTeamDispButton);
        btnPanel.add(richiestaIngressoButton);

        // Table
        teamDisponibiliTableModel = new TeamDisponibiliTableModel();
        teamDisponibiliTable = new JTable(teamDisponibiliTableModel);
        teamDisponibiliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamDisponibiliTable.setRowHeight(25);
        teamDisponibiliScrollPane = new JScrollPane(teamDisponibiliTable);

        teamDispPanel.add(btnPanel, BorderLayout.NORTH);
        teamDispPanel.add(teamDisponibiliScrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(creaTeamPanel);
        splitPane.setBottomComponent(teamDispPanel);
        splitPane.setResizeWeight(0.3);

        nessunTeamPanel.add(headerInfoPanel, BorderLayout.NORTH);
        nessunTeamPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void createInvitiPanel() {
        invitiPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerInfoPanel = new JPanel();
        headerInfoPanel.setBorder(new TitledBorder("Inviti Ricevuti"));
        JLabel infoLabel = new JLabel("Hai ricevuto degli inviti per entrare in un team");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerInfoPanel.add(infoLabel);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiornaInvitiButton = new JButton("Aggiorna");
        accettaInvitoButton = new JButton("Accetta");
        rifiutaInvitoButton = new JButton("Rifiuta");

        aggiornaInvitiButton.setPreferredSize(new Dimension(100, 30));
        accettaInvitoButton.setPreferredSize(new Dimension(100, 30));
        rifiutaInvitoButton.setPreferredSize(new Dimension(100, 30));

        accettaInvitoButton.setEnabled(false);
        rifiutaInvitoButton.setEnabled(false);

        btnPanel.add(aggiornaInvitiButton);
        btnPanel.add(accettaInvitoButton);
        btnPanel.add(rifiutaInvitoButton);

        // Table
        invitiTableModel = new InvitiTableModel();
        invitiTable = new JTable(invitiTableModel);
        invitiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invitiTable.setRowHeight(25);
        invitiScrollPane = new JScrollPane(invitiTable);

        invitiPanel.add(headerInfoPanel, BorderLayout.NORTH);
        invitiPanel.add(btnPanel, BorderLayout.NORTH);
        invitiPanel.add(invitiScrollPane, BorderLayout.CENTER);
    }

    private void createGestioneTeamPanel() {
        gestioneTeamPanel = new JPanel(new BorderLayout());

        // Team info panel
        createTeamInfoPanel();

        // Members panel
        createMembriPanel();

        gestioneTeamPanel.add(teamInfoPanel, BorderLayout.NORTH);
        gestioneTeamPanel.add(membriPanel, BorderLayout.CENTER);
    }

    private void createTeamInfoPanel() {
        teamInfoPanel = new JPanel(new GridBagLayout());
        teamInfoPanel.setBorder(new TitledBorder("Informazioni Team"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Team buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        abbandonaTeamButton = new JButton("Abbandona Team");
        abbandonaTeamButton.setPreferredSize(new Dimension(150, 30));
        abbandonaTeamButton.setBackground(Color.RED);
        abbandonaTeamButton.setForeground(Color.WHITE);
        buttonPanel.add(abbandonaTeamButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        teamInfoPanel.add(buttonPanel, gbc);
    }

    private void createMembriPanel() {
        membriPanel = new JPanel(new BorderLayout());
        membriPanel.setBorder(new TitledBorder("Membri del Team"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaMembriButton = new JButton("Aggiorna");
        invitaMembroButton = new JButton("Invita Membro");
        rimuoviMembroButton = new JButton("Rimuovi Membro");

        aggiornaMembriButton.setPreferredSize(new Dimension(100, 30));
        invitaMembroButton.setPreferredSize(new Dimension(130, 30));
        rimuoviMembroButton.setPreferredSize(new Dimension(140, 30));

        rimuoviMembroButton.setEnabled(false);

        buttonPanel.add(aggiornaMembriButton);
        buttonPanel.add(invitaMembroButton);
        buttonPanel.add(rimuoviMembroButton);

        // Table
        membriTableModel = new MembriTableModel();
        membriTable = new JTable(membriTableModel);
        membriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membriTable.setRowHeight(25);
        membriScrollPane = new JScrollPane(membriTable);

        membriPanel.add(buttonPanel, BorderLayout.NORTH);
        membriPanel.add(membriScrollPane, BorderLayout.CENTER);
    }

    private void createProgressiTab() {
        progressiPanel = new JPanel(new BorderLayout());

        // Split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Top: Lista progressi
        createProgressiListPanel();

        // Bottom: Nuovo/Modifica progresso
        createNuovoProgressoPanel();

        splitPane.setTopComponent(progressiListPanel);
        splitPane.setBottomComponent(nuovoProgressoPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(300);

        progressiPanel.add(splitPane, BorderLayout.CENTER);

        tabbedPane.addTab("Progressi", progressiPanel);
    }

    private void createProgressiListPanel() {
        progressiListPanel = new JPanel(new BorderLayout());
        progressiListPanel.setBorder(new TitledBorder("Progressi del Team"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaProgressiButton = new JButton("Aggiorna");
        eliminaProgressoButton = new JButton("Elimina");

        aggiornaProgressiButton.setPreferredSize(new Dimension(100, 30));
        eliminaProgressoButton.setPreferredSize(new Dimension(100, 30));

        eliminaProgressoButton.setEnabled(false);

        buttonPanel.add(aggiornaProgressiButton);
        buttonPanel.add(eliminaProgressoButton);

        // Table
        progressiTableModel = new ProgressiTableModel();
        progressiTable = new JTable(progressiTableModel);
        progressiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressiTable.setRowHeight(25);
        progressiScrollPane = new JScrollPane(progressiTable);

        progressiListPanel.add(buttonPanel, BorderLayout.NORTH);
        progressiListPanel.add(progressiScrollPane, BorderLayout.CENTER);
    }

    private void createNuovoProgressoPanel() {
        nuovoProgressoPanel = new JPanel(new BorderLayout());
        nuovoProgressoPanel.setBorder(new TitledBorder("Nuovo Progresso"));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Titolo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Titolo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titoloProgressoField = new JTextField(30);
        formPanel.add(titoloProgressoField, gbc);

        // Descrizione
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Descrizione:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descrizioneProgressoArea = new JTextArea(4, 30);
        descrizioneProgressoArea.setLineWrap(true);
        descrizioneProgressoArea.setWrapStyleWord(true);
        descrizioneProgressoScrollPane = new JScrollPane(descrizioneProgressoArea);
        formPanel.add(descrizioneProgressoScrollPane, gbc);

        // File
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("File:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selezionaFileButton = new JButton("Seleziona File");
        fileSelezionatoLabel = new JLabel("Nessun file selezionato");
        filePanel.add(selezionaFileButton);
        filePanel.add(fileSelezionatoLabel);
        formPanel.add(filePanel, gbc);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        caricaProgressoButton = new JButton("Carica Progresso");

        caricaProgressoButton.setPreferredSize(new Dimension(150, 35));

        actionPanel.add(caricaProgressoButton);

        nuovoProgressoPanel.add(formPanel, BorderLayout.CENTER);
        nuovoProgressoPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    private void createDocumentiTab() {
        documentiPanel = new JPanel(new BorderLayout());
        documentiPanel.setBorder(new TitledBorder("Documenti del Team"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaDocumentiButton = new JButton("Aggiorna");
        visualizzaDocumentoButton = new JButton("Visualizza");
        scaricaDocumentoButton = new JButton("Scarica");
        eliminaDocumentoButton = new JButton("Elimina");

        aggiornaDocumentiButton.setPreferredSize(new Dimension(100, 30));
        visualizzaDocumentoButton.setPreferredSize(new Dimension(100, 30));
        scaricaDocumentoButton.setPreferredSize(new Dimension(100, 30));
        eliminaDocumentoButton.setPreferredSize(new Dimension(100, 30));

        visualizzaDocumentoButton.setEnabled(false);
        scaricaDocumentoButton.setEnabled(false);
        eliminaDocumentoButton.setEnabled(false);

        buttonPanel.add(aggiornaDocumentiButton);
        buttonPanel.add(visualizzaDocumentoButton);
        buttonPanel.add(scaricaDocumentoButton);
        buttonPanel.add(eliminaDocumentoButton);

        // Table
        documentiTableModel = new DocumentiTableModel();
        documentiTable = new JTable(documentiTableModel);
        documentiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentiTable.setRowHeight(25);
        documentiScrollPane = new JScrollPane(documentiTable);

        documentiPanel.add(buttonPanel, BorderLayout.NORTH);
        documentiPanel.add(documentiScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Documenti", documentiPanel);
    }

    // region Event Listeners
    private void setupTeamEvents() {
        // Nessun team events
        creaTeamButton.addActionListener(_ -> creaTeam());
        aggiornaTeamDispButton.addActionListener(_ -> loadTeamDisponibili());
        richiestaIngressoButton.addActionListener(_ -> richiestaIngresso());

        teamDisponibiliTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                richiestaIngressoButton.setEnabled(teamDisponibiliTable.getSelectedRow() != -1);
            }
        });

        // Inviti events
        aggiornaInvitiButton.addActionListener(_ -> loadInviti());
        accettaInvitoButton.addActionListener(_ -> gestisciInvito(true));
        rifiutaInvitoButton.addActionListener(_ -> gestisciInvito(false));

        invitiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = invitiTable.getSelectedRow() != -1;
                accettaInvitoButton.setEnabled(hasSelection);
                rifiutaInvitoButton.setEnabled(hasSelection);
            }
        });

        // Gestione team events
        aggiornaMembriButton.addActionListener(_ -> loadMembriData());
        invitaMembroButton.addActionListener(_ -> invitaMembro());
        rimuoviMembroButton.addActionListener(_ -> rimuoviMembro());
        abbandonaTeamButton.addActionListener(_ -> abbandonaTeam());

        membriTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = membriTable.getSelectedRow();
                rimuoviMembroButton.setEnabled(selectedRow != -1);
            }
        });
    }

    // endregion

    private void setupProgressiEvents() {
        // Progressi table selection
        progressiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = progressiTable.getSelectedRow();
                boolean hasSelection = selectedRow != -1;
                eliminaProgressoButton.setEnabled(hasSelection);

                if (hasSelection) {
                    progressoSelezionato = progressiList.get(selectedRow);
                } else {
                    progressoSelezionato = null;
                }
            }
        });

        // Buttons
        aggiornaProgressiButton.addActionListener(_ -> loadProgressiData());
        eliminaProgressoButton.addActionListener(_ -> eliminaProgresso());
        caricaProgressoButton.addActionListener(_ -> caricaProgresso());
        selezionaFileButton.addActionListener(_ -> selezionaFile());
    }

    private void setupDocumentiEvents() {
        // Documenti table selection
        documentiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = documentiTable.getSelectedRow();
                boolean hasSelection = selectedRow != -1;
                visualizzaDocumentoButton.setEnabled(hasSelection);
                scaricaDocumentoButton.setEnabled(hasSelection);
                eliminaDocumentoButton.setEnabled(hasSelection);
            }
        });

        // Double click to view
        documentiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    visualizzaDocumento();
                }
            }
        });

        // Buttons
        aggiornaDocumentiButton.addActionListener(_ -> loadDocumentiData());
        visualizzaDocumentoButton.addActionListener(_ -> visualizzaDocumento());
        scaricaDocumentoButton.addActionListener(_ -> scaricaDocumento());
        eliminaDocumentoButton.addActionListener(_ -> eliminaDocumento());
    }

    // region Data Loading
    private void loadHackathonInfo() {
        try {
            HackathonResponse response = partecipanteController.getDettagliHackathon(hackathonId);
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

    // endregion

    private void loadTeamInfo() {
        try {
            // Prima controlla gli inviti
            loadInviti();

            if (!invitiRicevuti.isEmpty()) {
                statoTeamCorrente = StatoTeam.INVITI_PENDENTI;
                teamInfoLabel.setText("Hai " + invitiRicevuti.size() + " inviti pendenti");
                mostraPanel("INVITI_PENDENTI");
                return;
            }

            // Poi controlla se ha un team
            TeamResponse response = partecipanteController.getTeamCorrente(hackathonId);
            if (response.team() != null) {
                teamCorrente = response.team();
                statoTeamCorrente = StatoTeam.HA_TEAM;
                teamInfoLabel.setText("Team: " + teamCorrente.getNome() + " (" + teamCorrente.getNumeroMembri() + " membri)");
                mostraPanel("HA_TEAM");
                loadMembriData();
            } else {
                statoTeamCorrente = StatoTeam.NESSUN_TEAM;
                teamInfoLabel.setText("Non fai parte di nessun team");
                mostraPanel("NESSUN_TEAM");
                loadTeamDisponibili();
            }
        } catch (Exception e) {
            teamInfoLabel.setText("Errore nel caricamento");
            statoTeamCorrente = StatoTeam.NESSUN_TEAM;
            mostraPanel("NESSUN_TEAM");
        }
    }

    private void mostraPanel(String panelName) {
        teamCardLayout.show(teamPanel, panelName);

        // Abilita/disabilita altri tab
        boolean hasTeam = panelName.equals("HA_TEAM");
        tabbedPane.setEnabledAt(1, hasTeam); // Progressi
        tabbedPane.setEnabledAt(2, hasTeam); // Documenti
    }

    private void loadTeamDisponibili() {
        try {
            teamDisponibiliTable.setEnabled(false);
            aggiornaTeamDispButton.setEnabled(false);

            var response = partecipanteController.getTeamDisponibili(hackathonId);
            if (response.teams() != null) {
                teamDisponibili.clear();
                teamDisponibili.addAll(response.teams());
                teamDisponibiliTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento team: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento team: " + e.getMessage());
        } finally {
            teamDisponibiliTable.setEnabled(true);
            aggiornaTeamDispButton.setEnabled(true);
        }
    }

    private void loadInviti() {
        try {
            var response = partecipanteController.getInvitiRicevuti();
            if (response.invitiTeam() != null) {
                invitiRicevuti.clear();
                invitiRicevuti.addAll(response.invitiTeam());
                if (invitiTableModel != null) {
                    invitiTableModel.fireTableDataChanged();
                }
            }
        } catch (Exception e) {
            // Silent fail durante inizializzazione
        }
    }

    private void loadMembriData() {
        if (teamCorrente == null) return;

        try {
            membriTable.setEnabled(false);
            aggiornaMembriButton.setEnabled(false);

            var response = partecipanteController.getMembriTeam(teamCorrente.getTeamId());
            if (response.membri() != null) {
                membriList.clear();
                membriList.addAll(response.membri());
                membriTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento membri: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento membri: " + e.getMessage());
        } finally {
            membriTable.setEnabled(true);
            aggiornaMembriButton.setEnabled(true);
        }
    }

    private void loadProgressiData() {
        if (teamCorrente == null) return;

        try {
            progressiTable.setEnabled(false);
            aggiornaProgressiButton.setEnabled(false);

            ProgressoListResponse response = partecipanteController.getProgressiTeam(teamCorrente.getTeamId());
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

    private void loadDocumentiData() {
        if (teamCorrente == null) return;

        try {
            documentiTable.setEnabled(false);
            aggiornaDocumentiButton.setEnabled(false);

            // Filter progressi with documents
            documentiList.clear();
            for (Progresso progresso : progressiList) {
                if (progresso.getDocumentoNome() != null && !progresso.getDocumentoNome().isEmpty()) {
                    documentiList.add(progresso);
                }
            }
            documentiTableModel.fireTableDataChanged();

        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento documenti: " + e.getMessage());
        } finally {
            documentiTable.setEnabled(true);
            aggiornaDocumentiButton.setEnabled(true);
        }
    }

    // region Team Action Methods
    private void creaTeam() {
        String nomeTeam = nomeTeamField.getText().trim();
        if (nomeTeam.isEmpty()) {
            showErrorMessage("Il nome del team è obbligatorio");
            nomeTeamField.requestFocus();
            return;
        }

        try {
            var response = partecipanteController.creaTeam(hackathonId, nomeTeam);
            if (response.team() != null) {
                showInfoMessage("Team creato con successo!");
                nomeTeamField.setText("");
                loadTeamInfo();
            } else {
                showErrorMessage("Errore nella creazione: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nella creazione: " + e.getMessage());
        }
    }

    // endregion

    private void richiestaIngresso() {
        int selectedRow = teamDisponibiliTable.getSelectedRow();
        if (selectedRow != -1) {
            Team team = teamDisponibili.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi richiedere di entrare nel team '" + team.getNome() + "'?", "Conferma Richiesta", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = partecipanteController.richiediPartecipazione(team.getTeamId());
                    if (response.result()) {
                        showInfoMessage("Richiesta inviata con successo!");
                        loadTeamDisponibili();
                    } else {
                        showErrorMessage("Errore nell'invio: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nell'invio: " + e.getMessage());
                }
            }
        }
    }

    private void gestisciInvito(boolean accetta) {
        int selectedRow = invitiTable.getSelectedRow();
        if (selectedRow != -1) {
            InvitoTeam invito = invitiRicevuti.get(selectedRow);

            String azione = accetta ? "accettare" : "rifiutare";
            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi " + azione + " l'invito del team '" + invito.getTeam().getNome() + "'?", "Conferma", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ResponseResult response;
                    if (accetta) {
                        response = partecipanteController.rispondiInvito(invito.getInvitoId(), StatoInvito.ACCEPTED);
                        partecipanteController.accettaInTeam(invito.getInvitoId());
                    } else {
                        response = partecipanteController.rispondiInvito(invito.getInvitoId(), StatoInvito.DECLINED);
                    }
                    if (response.result()) {
                        showInfoMessage("Invito " + (accetta ? "accettato" : "rifiutato") + " con successo!");
                        loadTeamInfo();
                    } else {
                        showErrorMessage("Errore: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore: " + e.getMessage());
                }
            }
        }
    }

    private void invitaMembro() {
        try {
            var response = partecipanteController.getPartecipantiDisponibili(hackathonId);

            if (response.utenti() == null || response.utenti().isEmpty()) {
                showErrorMessage("Non ci sono partecipanti registrati all'hackathon!");
                return;
            }

            // Filtra i partecipanti rimuovendo quelli già membri del team
            List<Utente> partecipantiInvitabili = response.utenti().stream().filter(partecipante -> {
                // Controlla se il partecipante è già membro del team
                return membriList.stream().noneMatch(membro -> membro.getUtente() != null && membro.getUtente().getUtenteId() == partecipante.getUtenteId());
            }).toList();

            if (partecipantiInvitabili.isEmpty()) {
                showErrorMessage("Non ci sono partecipanti disponibili da invitare!\n" + "Tutti i partecipanti sono già membri del team o hanno inviti pendenti.");
                return;
            }

            String[] nomiPartecipanti = partecipantiInvitabili.stream().map(p -> p.getNome() + " " + p.getCognome() + " (" + p.getUsername() + ")").toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(frame, "Seleziona il partecipante da invitare:", "Invita Membro", JOptionPane.QUESTION_MESSAGE, null, nomiPartecipanti, nomiPartecipanti[0]);

            if (selected != null) {
                int selectedIndex = -1;
                for (int i = 0; i < nomiPartecipanti.length; i++) {
                    if (nomiPartecipanti[i].equals(selected)) {
                        selectedIndex = i;
                        break;
                    }
                }

                if (selectedIndex >= 0) {
                    Utente partecipanteSelezionato = partecipantiInvitabili.get(selectedIndex);

                    // Dialog per messaggio personalizzato
                    JTextArea messageArea = new JTextArea(4, 30);
                    messageArea.setLineWrap(true);
                    messageArea.setWrapStyleWord(true);
                    messageArea.setText("Ciao! Ti invitiamo a unirti al nostro team \"" + teamCorrente.getNome() + "\" per l'hackathon. Saremmo felici di averti con noi!");

                    JScrollPane scrollPane = new JScrollPane(messageArea);
                    scrollPane.setPreferredSize(new Dimension(350, 100));

                    Object[] message = {"Messaggio per " + partecipanteSelezionato.getNome() + " " + partecipanteSelezionato.getCognome() + ":", scrollPane};

                    int option = JOptionPane.showConfirmDialog(frame, message, "Messaggio di Invito", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String messaggioInvito = messageArea.getText().trim();

                        if (messaggioInvito.isEmpty()) {
                            messaggioInvito = "Ti invitiamo a unirti al nostro team!";
                        }

                        var inviteResult = partecipanteController.invitaInTeam(teamCorrente.getTeamId(), partecipanteSelezionato.getUtenteId(), messaggioInvito);

                        if (inviteResult.result()) {
                            showInfoMessage("Invito inviato con successo a " + partecipanteSelezionato.getNome() + " " + partecipanteSelezionato.getCognome());
                        } else {
                            showErrorMessage("Errore nell'invito: " + inviteResult.message());
                        }
                    }
                }
            }
        } catch (Exception e) {
            showErrorMessage("Errore nell'invito: " + e.getMessage());
        }
    }

    private void rimuoviMembro() {
        int selectedRow = membriTable.getSelectedRow();
        if (selectedRow != -1) {
            MembroTeam membro = membriList.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi rimuovere " + membro.getNomeUtente() + " dal team?", "Conferma Rimozione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = partecipanteController.rimuoviMembroTeam(membro.getUtenteId());
                    if (response.result()) {
                        showInfoMessage("Membro rimosso con successo!");
                        loadMembriData();
                    } else {
                        showErrorMessage("Errore nella rimozione: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nella rimozione: " + e.getMessage());
                }
            }
        }
    }

    private void abbandonaTeam() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler abbandonare il team '" + teamCorrente.getNome() + "'?", "Conferma Abbandono Team", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var response = partecipanteController.abbandonaTeam(teamCorrente.getTeamId());
                if (response.result()) {
                    showInfoMessage("Hai abbandonato il team con successo!");
                    teamCorrente = null;
                    loadTeamInfo();
                } else {
                    showErrorMessage("Errore nell'abbandono: " + response.message());
                }
            } catch (Exception e) {
                showErrorMessage("Errore nell'abbandono: " + e.getMessage());
            }
        }
    }

    // region Progressi Action Methods
    private void caricaProgresso() {
        if (!validaFormProgresso()) return;

        try {
            String titolo = titoloProgressoField.getText().trim();
            String descrizione = descrizioneProgressoArea.getText().trim();
            String url = fileSelezionato != null ? fileSelezionato.getAbsolutePath() : null;

            ProgressoResponse response = partecipanteController.caricaProgresso(teamCorrente.getTeamId(), titolo, descrizione, url);

            if (response.progresso() != null) {
                showInfoMessage("Progresso caricato con successo!");
                clearFormProgresso();
                loadProgressiData();
                loadDocumentiData();
            } else {
                showErrorMessage("Errore nel caricamento: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore nel caricamento: " + e.getMessage());
        }
    }

    // endregion

    private void eliminaProgresso() {
        if (progressoSelezionato == null) return;

        int confirm = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler eliminare il progresso?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var response = partecipanteController.eliminaProgresso(progressoSelezionato.getProgressoId());
                if (response.result()) {
                    showInfoMessage("Progresso eliminato con successo!");
                    loadProgressiData();
                    loadDocumentiData();
                } else {
                    showErrorMessage("Errore nell'eliminazione: " + response.message());
                }
            } catch (Exception e) {
                showErrorMessage("Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    private void selezionaFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Documenti supportati (PDF, DOC, DOCX, ZIP, TXT)", "pdf", "doc", "docx", "zip", "txt"));

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            fileSelezionato = fileChooser.getSelectedFile();
            fileSelezionatoLabel.setText(fileSelezionato.getName());
        }
    }

    // region Documenti Action Methods
    private void visualizzaDocumento() {
        int selectedRow = documentiTable.getSelectedRow();
        if (selectedRow != -1) {
            Progresso documento = documentiList.get(selectedRow);

            String message = "Documento: " + documento.getDocumentoNome() + "\n\n" + "Caricato: " + documento.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + "Autore: " + (documento.getCaricatoDa() != null ? documento.getCaricatoDa().getNomeCompleto() : "N/A");

            JOptionPane.showMessageDialog(frame, message, "Dettagli Documento", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // endregion

    private void scaricaDocumento() {
        int selectedRow = documentiTable.getSelectedRow();
        if (selectedRow != -1) {
            Progresso documento = documentiList.get(selectedRow);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(documento.getDocumentoNome()));

            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File destinazione = fileChooser.getSelectedFile();
                    var response = partecipanteController.getProgresso(documento.getProgressoId());
                    if (response.progresso() != null) {
                        showInfoMessage("Documento scaricato con successo!");
                    } else {
                        showErrorMessage("Errore nel download: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nel download: " + e.getMessage());
                }
            }
        }
    }

    private void eliminaDocumento() {
        int selectedRow = documentiTable.getSelectedRow();
        if (selectedRow != -1) {
            Progresso documento = documentiList.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi eliminare il documento '" + documento.getDocumentoNome() + "'?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = partecipanteController.eliminaProgresso(documento.getProgressoId());
                    if (response.result()) {
                        showInfoMessage("Documento eliminato con successo!");
                        loadProgressiData();
                        loadDocumentiData();
                    } else {
                        showErrorMessage("Errore nell'eliminazione: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nell'eliminazione: " + e.getMessage());
                }
            }
        }
    }

    // region Form Management
    private void popolaFormProgresso() {
        if (progressoSelezionato == null) return;

        if (progressoSelezionato.getDocumentoNome() != null) {
            fileSelezionatoLabel.setText(progressoSelezionato.getDocumentoNome());
            fileSelezionato = null; // Clear file selection for existing progress
        } else {
            fileSelezionatoLabel.setText("Nessun file selezionato");
            fileSelezionato = null;
        }
    }

    // endregion

    private void clearFormProgresso() {
        titoloProgressoField.setText("");
        descrizioneProgressoArea.setText("");
        fileSelezionatoLabel.setText("Nessun file selezionato");
        fileSelezionato = null;
        progressoSelezionato = null;
    }

    private boolean validaFormProgresso() {
        String titolo = titoloProgressoField.getText().trim();
        String descrizione = descrizioneProgressoArea.getText().trim();

        if (titolo.isEmpty()) {
            showErrorMessage("Il titolo è obbligatorio");
            titoloProgressoField.requestFocus();
            return false;
        }

        if (titolo.length() > 200) {
            showErrorMessage("Il titolo non può superare i 200 caratteri");
            titoloProgressoField.requestFocus();
            return false;
        }

        if (descrizione.isEmpty()) {
            showErrorMessage("La descrizione è obbligatoria");
            descrizioneProgressoArea.requestFocus();
            return false;
        }

        return true;
    }

    // region Utility Methods
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

    // endregion

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

    // region States
    private enum StatoTeam {
        NESSUN_TEAM, INVITI_PENDENTI, HA_TEAM
    }

    // endregion

    // region Table Models
    private class TeamDisponibiliTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nome Team", "Membri", "Descrizione", "Leader"};

        @Override
        public int getRowCount() {
            return teamDisponibili.size();
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
            Team team = teamDisponibili.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> team.getNome();
                case 1 -> team.getNumeroMembri() + "/" + team.getMaxDimensione();
                case 2 -> team.getLeader() != null ? team.getLeader().getNomeCompleto() : "N/A";
                default -> "";
            };
        }
    }

    private class InvitiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Team", "Da", "Data Invito", "Messaggio"};

        @Override
        public int getRowCount() {
            return invitiRicevuti.size();
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
            InvitoTeam invito = invitiRicevuti.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> invito.getTeam().getNome();
                case 1 ->
                        invito.getInvitante().getNomeCompleto() != null ? invito.getInvitante().getNomeCompleto() : "N/A";
                case 2 -> invito.getDataInvito().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 3 -> invito.getMessaggioMotivazionale() != null ? invito.getMessaggioMotivazionale() : "";
                default -> "";
            };
        }
    }

    private class MembriTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nome", "Username", "Ruolo", "Data Ingresso"};

        @Override
        public int getRowCount() {
            return membriList.size();
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
            MembroTeam membro = membriList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> membro.getUtente() != null ? membro.getUtente().getNomeCompleto() : "N/A";
                case 1 -> membro.getUtente() != null ? membro.getUtente().getUsername() : "N/A";
                case 2 -> membro.getRuolo() != null ? membro.getRuolo().getDisplayName() : "N/A";
                case 3 -> membro.getDataIngresso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
                case 0 -> progresso.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 1 -> progresso.getCaricatoDa() != null ? progresso.getCaricatoDa().getNomeCompleto() : "N/A";
                case 2 -> progresso.getDocumentoNome() != null ? progresso.getDocumentoNome() : "Nessun file";
                default -> "";
            };
        }
    }

    private class DocumentiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nome File", "Progresso", "Dimensione", "Data Caricamento"};

        @Override
        public int getRowCount() {
            return documentiList.size();
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
            Progresso documento = documentiList.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> documento.getDocumentoNome();
                case 1 -> documento.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                default -> "";
            };
        }
    }

    // endregion
}