package it.unina.hackathon.gui.partecipante;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.*;
import it.unina.hackathon.model.enums.RuoloTeam;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.ProgressoListResponse;
import it.unina.hackathon.utils.responses.ProgressoResponse;
import it.unina.hackathon.utils.responses.TeamResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
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

    //region Fields
    private final HackathonController controller;
    private final int hackathonId;
    private final List<Registrazione> membriList;
    private final List<Progresso> progressiList;
    private final List<Problema> problemiList;
    private final List<Team> teams;
    private final List<InvitoTeam> invitiRicevuti;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;

    private JLabel titleLabel;
    private JLabel hackathonInfoLabel;
    private JLabel teamInfoLabel;
    private JButton backButton;

    private JPanel teamPanel;
    private CardLayout teamCardLayout;

    private JPanel nessunTeamPanel;
    private JTextField nomeTeamField;
    private JButton creaTeamButton;
    private JTable teamTable;
    private JScrollPane teamsScrollPane;
    private TeamsTableModel teamsTableModel;
    private JButton aggiornaTeamButton;
    private JTable invitiRicevutiTable;
    private JScrollPane invitiRicevutiScrollPane;
    private InvitiTableModel invitiRicevutiTableModel;
    private JButton accettaInvitoButton;
    private JButton rifiutaInvitoButton;

    private JPanel gestioneTeamPanel;
    private JPanel membriPanel;
    private JTable membriTable;
    private JScrollPane membriScrollPane;
    private MembriTableModel membriTableModel;
    private JButton aggiornaMembriButton;
    private JButton invitaMembroButton;
    private JButton rimuoviMembroButton;
    private JButton abbandonaSciogleTeamButton;

    private JPanel progressiPanel;
    private JPanel progressiListPanel;
    private JPanel nuovoProgressoPanel;
    private JTable progressiTable;
    private JScrollPane progressiScrollPane;
    private ProgressiTableModel progressiTableModel;
    private JButton aggiornaProgressiButton;
    private JButton eliminaProgressoButton;
    private JButton selezionaFileButton;
    private JLabel fileSelezionatoLabel;
    private JButton caricaProgressoButton;

    private JPanel problemiPanel;
    private JTable problemiTable;
    private JScrollPane problemiScrollPane;
    private ProblemiTableModel problemiTableModel;
    private JButton aggiornaProblemiButton;
    private JButton visualizzaProblemaButton;

    private Hackathon hackathonCorrente;
    private Team teamCorrente;
    private File fileSelezionato;
    private Progresso progressoSelezionato;
    private Problema problemaSelezionato;
    private boolean isLeader = false;
    //endregion

    //region Constructor
    public GestisciProgettoGUI(int hackathonId) {
        this.hackathonId = hackathonId;
        this.controller = HackathonController.getInstance();
        this.membriList = new ArrayList<>();
        this.progressiList = new ArrayList<>();
        this.problemiList = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.invitiRicevuti = new ArrayList<>();

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
        createContentPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

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
        backButton.addActionListener(_ -> controller.vaiAllaHome(frame));
        setupTeamEvents();
        setupProgressiEvents();
        setupProblemiEvents();
    }

    @Override
    public void loadData() {
        loadHackathonInfo();
        loadTeamInfo();
        loadProgressiData();
        loadProblemiData();
    }
    //endregion

    //region Private Methods
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Gestione Progetto");
        applyStyleTitleLbl(titleLabel);
        hackathonInfoLabel = new JLabel("Caricamento...");
        teamInfoLabel = new JLabel("Team: Caricamento...");
        applyStyleTitleLbl(teamInfoLabel);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(hackathonInfoLabel);
        titlePanel.add(teamInfoLabel);

        backButton = new JButton("← Torna alla Home");
        backButton.setPreferredSize(new Dimension(150, 35));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();

        createTeamTab();
        createProgressiTab();
        createProblemiTab();

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void createTeamTab() {
        teamPanel = new JPanel();
        teamCardLayout = new CardLayout();
        teamPanel.setLayout(teamCardLayout);

        createNessunTeamPanel();
        createGestioneTeamPanel();

        teamPanel.add(nessunTeamPanel, "NESSUN_TEAM");
        teamPanel.add(gestioneTeamPanel, "HA_TEAM");

        tabbedPane.addTab("Team", teamPanel);
    }

    private void createNessunTeamPanel() {
        nessunTeamPanel = new JPanel(new BorderLayout());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        JPanel creaTeamPanel = createCreaTeamPanel();
        JPanel teamDispPanel = createTeamsDisponibiliPanel();
        JPanel invitiRicevutiPanel = createInvitiRicevutiPanel();

        bottomSplitPane.setTopComponent(teamDispPanel);
        bottomSplitPane.setBottomComponent(invitiRicevutiPanel);
        bottomSplitPane.setResizeWeight(0.5);

        mainSplitPane.setTopComponent(creaTeamPanel);
        mainSplitPane.setBottomComponent(bottomSplitPane);
        mainSplitPane.setResizeWeight(0.2);

        nessunTeamPanel.add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createCreaTeamPanel() {
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
        return creaTeamPanel;
    }

    private JPanel createTeamsDisponibiliPanel() {
        JPanel teamDispPanel = new JPanel(new BorderLayout());
        teamDispPanel.setBorder(new TitledBorder("Teams"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aggiornaTeamButton = new JButton("Aggiorna");
        aggiornaTeamButton.setPreferredSize(new Dimension(100, 30));
        btnPanel.add(aggiornaTeamButton);

        teamsTableModel = new TeamsTableModel();
        teamTable = new JTable(teamsTableModel);
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.setRowHeight(25);
        teamsScrollPane = new JScrollPane(teamTable);

        teamDispPanel.add(btnPanel, BorderLayout.NORTH);
        teamDispPanel.add(teamsScrollPane, BorderLayout.CENTER);
        return teamDispPanel;
    }

    private JPanel createInvitiRicevutiPanel() {
        JPanel invitiRicevutiPanel = new JPanel(new BorderLayout());
        invitiRicevutiPanel.setBorder(new TitledBorder("Inviti Ricevuti"));

        JPanel invitiBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accettaInvitoButton = new JButton("Accetta");
        rifiutaInvitoButton = new JButton("Rifiuta");

        accettaInvitoButton.setPreferredSize(new Dimension(100, 30));
        rifiutaInvitoButton.setPreferredSize(new Dimension(100, 30));
        accettaInvitoButton.setEnabled(false);
        rifiutaInvitoButton.setEnabled(false);

        invitiBtnPanel.add(accettaInvitoButton);
        invitiBtnPanel.add(rifiutaInvitoButton);

        invitiRicevutiTableModel = new InvitiTableModel();
        invitiRicevutiTable = new JTable(invitiRicevutiTableModel);
        invitiRicevutiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invitiRicevutiTable.setRowHeight(25);
        invitiRicevutiScrollPane = new JScrollPane(invitiRicevutiTable);

        invitiRicevutiPanel.add(invitiBtnPanel, BorderLayout.NORTH);
        invitiRicevutiPanel.add(invitiRicevutiScrollPane, BorderLayout.CENTER);
        return invitiRicevutiPanel;
    }

    private void createGestioneTeamPanel() {
        gestioneTeamPanel = new JPanel(new BorderLayout());

        createMembriPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(membriPanel);
        splitPane.setResizeWeight(0.6);

        gestioneTeamPanel.add(splitPane, BorderLayout.CENTER);
    }

    private void createMembriPanel() {
        membriPanel = new JPanel(new BorderLayout());
        membriPanel.setBorder(new TitledBorder("Membri del Team"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaMembriButton = new JButton("Aggiorna");
        invitaMembroButton = new JButton("Invita Membro");
        rimuoviMembroButton = new JButton("Rimuovi Membro");
        abbandonaSciogleTeamButton = new JButton("Abbandona Team");

        aggiornaMembriButton.setPreferredSize(new Dimension(100, 30));
        invitaMembroButton.setPreferredSize(new Dimension(130, 30));
        rimuoviMembroButton.setPreferredSize(new Dimension(140, 30));
        abbandonaSciogleTeamButton.setPreferredSize(new Dimension(150, 30));

        abbandonaSciogleTeamButton.setBackground(Color.RED);
        abbandonaSciogleTeamButton.setForeground(Color.WHITE);

        rimuoviMembroButton.setEnabled(false);

        buttonPanel.add(aggiornaMembriButton);
        buttonPanel.add(invitaMembroButton);
        buttonPanel.add(rimuoviMembroButton);
        buttonPanel.add(abbandonaSciogleTeamButton);

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

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        createProgressiListPanel();
        createNuovoProgressoPanel();

        splitPane.setTopComponent(progressiListPanel);
        splitPane.setBottomComponent(nuovoProgressoPanel);
        splitPane.setResizeWeight(0.7);

        progressiPanel.add(splitPane, BorderLayout.CENTER);

        tabbedPane.addTab("Progressi", progressiPanel);
    }

    private void createProgressiListPanel() {
        progressiListPanel = new JPanel(new BorderLayout());
        progressiListPanel.setBorder(new TitledBorder("Progressi del Team"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaProgressiButton = new JButton("Aggiorna");
        eliminaProgressoButton = new JButton("Elimina");

        aggiornaProgressiButton.setPreferredSize(new Dimension(100, 30));
        eliminaProgressoButton.setPreferredSize(new Dimension(100, 30));

        eliminaProgressoButton.setEnabled(false);

        buttonPanel.add(aggiornaProgressiButton);
        buttonPanel.add(eliminaProgressoButton);

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
        nuovoProgressoPanel.setBorder(new TitledBorder("Carica Documento"));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        selezionaFileButton = new JButton("Seleziona File");
        selezionaFileButton.setPreferredSize(new Dimension(150, 35));

        fileSelezionatoLabel = new JLabel("Nessun file selezionato");
        fileSelezionatoLabel.setFont(fileSelezionatoLabel.getFont().deriveFont(Font.ITALIC));

        caricaProgressoButton = new JButton("Carica Progresso");
        caricaProgressoButton.setPreferredSize(new Dimension(150, 35));

        formPanel.add(selezionaFileButton);
        formPanel.add(Box.createHorizontalStrut(20));
        formPanel.add(fileSelezionatoLabel);
        formPanel.add(Box.createHorizontalStrut(20));
        formPanel.add(caricaProgressoButton);

        nuovoProgressoPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void createProblemiTab() {
        problemiPanel = new JPanel(new BorderLayout());
        problemiPanel.setBorder(new TitledBorder("Problemi dell'Hackathon"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        aggiornaProblemiButton = new JButton("Aggiorna");
        visualizzaProblemaButton = new JButton("Visualizza");

        aggiornaProblemiButton.setPreferredSize(new Dimension(100, 30));
        visualizzaProblemaButton.setPreferredSize(new Dimension(100, 30));

        visualizzaProblemaButton.setEnabled(false);

        buttonPanel.add(aggiornaProblemiButton);
        buttonPanel.add(visualizzaProblemaButton);

        problemiTableModel = new ProblemiTableModel();
        problemiTable = new JTable(problemiTableModel);
        problemiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problemiTable.setRowHeight(25);
        problemiScrollPane = new JScrollPane(problemiTable);

        problemiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    visualizzaProblema();
                }
            }
        });

        problemiPanel.add(buttonPanel, BorderLayout.NORTH);
        problemiPanel.add(problemiScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Problemi", problemiPanel);
    }

    private void setupTeamEvents() {
        creaTeamButton.addActionListener(_ -> creaTeam());
        aggiornaTeamButton.addActionListener(_ -> loadTeams());

        accettaInvitoButton.addActionListener(_ -> gestisciInvito(true));
        rifiutaInvitoButton.addActionListener(_ -> gestisciInvito(false));

        invitiRicevutiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = invitiRicevutiTable.getSelectedRow() != -1;
                accettaInvitoButton.setEnabled(hasSelection);
                rifiutaInvitoButton.setEnabled(hasSelection);
            }
        });

        aggiornaMembriButton.addActionListener(_ -> loadMembriData());
        invitaMembroButton.addActionListener(_ -> invitaMembro());
        rimuoviMembroButton.addActionListener(_ -> rimuoviMembro());
        abbandonaSciogleTeamButton.addActionListener(_ -> abbandonaSciogleTeam());

        membriTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = membriTable.getSelectedRow();
                if (isLeader && selectedRow != -1) {
                    Registrazione membro = membriList.get(selectedRow);
                    rimuoviMembroButton.setEnabled(membro.getRuolo() != RuoloTeam.LEADER);
                } else {
                    rimuoviMembroButton.setEnabled(false);
                }
            }
        });
    }

    private void setupProgressiEvents() {
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

        aggiornaProgressiButton.addActionListener(_ -> loadProgressiData());
        eliminaProgressoButton.addActionListener(_ -> eliminaProgresso());
        caricaProgressoButton.addActionListener(_ -> caricaProgresso());
        selezionaFileButton.addActionListener(_ -> selezionaFile());
    }

    private void setupProblemiEvents() {
        problemiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = problemiTable.getSelectedRow();
                boolean hasSelection = selectedRow != -1;
                visualizzaProblemaButton.setEnabled(hasSelection);

                if (hasSelection) {
                    problemaSelezionato = problemiList.get(selectedRow);
                } else {
                    problemaSelezionato = null;
                }
            }
        });

        aggiornaProblemiButton.addActionListener(_ -> loadProblemiData());
        visualizzaProblemaButton.addActionListener(_ -> visualizzaProblema());
    }

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

    private void loadTeamInfo() {
        try {
            TeamResponse response = controller.getTeamPartecipante(hackathonId);
            if (response.team() != null) {
                teamCorrente = response.team();
                isLeader = controller.verificaLeaderTeam(teamCorrente.getTeamId()).result();

                ResponseIntResult contaNumeroMembri = controller.contaNumeroMembri(teamCorrente.getTeamId());
                teamInfoLabel.setText("Team: " + teamCorrente.getNome() + " (" + ((contaNumeroMembri != null) ? contaNumeroMembri.result() : "N/A") + " registrazioni)");

                updateUIForRole();
                mostraPanel("HA_TEAM");
                loadMembriData();

            } else {
                teamInfoLabel.setText("Non fai parte di nessun team");
                isLeader = false;
                mostraPanel("NESSUN_TEAM");
                loadTeams();
                loadInviti();
            }
        } catch (Exception e) {
            teamInfoLabel.setText("Errore nel caricamento");
            isLeader = false;
            mostraPanel("NESSUN_TEAM");
        }
    }

    private void updateUIForRole() {
        rimuoviMembroButton.setVisible(isLeader);
        abbandonaSciogleTeamButton.setText(isLeader ? "Sciogli Team" : "Abbandona Team");
    }

    private void mostraPanel(String panelName) {
        teamCardLayout.show(teamPanel, panelName);
        boolean hasTeam = panelName.equals("HA_TEAM");
        tabbedPane.setEnabledAt(1, hasTeam);
        tabbedPane.setEnabledAt(2, true);
    }

    private void loadTeams() {
        try {
            teamTable.setEnabled(false);
            aggiornaTeamButton.setEnabled(false);

            var response = controller.getTeamsHackathon(hackathonId);
            if (response.teams() != null) {
                teams.clear();
                teams.addAll(response.teams());
                teamsTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento team: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento team: " + e.getMessage());
        } finally {
            teamTable.setEnabled(true);
            aggiornaTeamButton.setEnabled(true);
        }
    }

    private void loadInviti() {
        try {
            var response = controller.getInvitiTeamRicevuti(hackathonId);
            if (response.invitiTeam() != null) {
                invitiRicevuti.clear();
                invitiRicevuti.addAll(response.invitiTeam());
                if (invitiRicevutiTableModel != null) {
                    invitiRicevutiTableModel.fireTableDataChanged();
                }
            }
        } catch (Exception e) {
        }
    }

    private void loadMembriData() {
        if (teamCorrente == null) return;

        try {
            membriTable.setEnabled(false);
            aggiornaMembriButton.setEnabled(false);

            var response = controller.getMembriTeam(teamCorrente.getTeamId());
            if (response.registrazioni() != null) {
                membriList.clear();
                membriList.addAll(response.registrazioni());
                membriTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento registrazioni: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento registrazioni: " + e.getMessage());
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

            ProgressoListResponse response = controller.getProgressiTeam(teamCorrente.getTeamId());
            if (response.progressi() != null) {
                progressiList.clear();
                progressiList.addAll(response.progressi());
                progressiTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento progressi: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento progressi: " + e.getMessage());
        } finally {
            progressiTable.setEnabled(true);
            aggiornaProgressiButton.setEnabled(true);
        }
    }

    private void loadProblemiData() {
        try {
            problemiTable.setEnabled(false);
            aggiornaProblemiButton.setEnabled(false);

            var response = controller.getProblemiHackathon(hackathonId);
            if (response.problemi() != null) {
                problemiList.clear();
                problemiList.addAll(response.problemi());
                problemiTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento problemi: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento problemi: " + e.getMessage());
        } finally {
            problemiTable.setEnabled(true);
            aggiornaProblemiButton.setEnabled(true);
        }
    }

    private void creaTeam() {
        String nomeTeam = nomeTeamField.getText().trim();
        if (nomeTeam.isEmpty()) {
            showError(frame, "Il nome del team è obbligatorio");
            nomeTeamField.requestFocus();
            return;
        }

        try {
            var response = controller.creaTeam(hackathonId, nomeTeam);
            if (response.team() != null) {
                showSuccess(frame, "Team creato con successo!");
                nomeTeamField.setText("");
                loadTeamInfo();
            } else {
                showError(frame, "Errore nella creazione: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nella creazione: " + e.getMessage());
        }
    }

    private void gestisciInvito(boolean accetta) {
        int selectedRow = invitiRicevutiTable.getSelectedRow();
        if (selectedRow != -1) {
            InvitoTeam invito = invitiRicevuti.get(selectedRow);

            String azione = accetta ? "accettare" : "rifiutare";
            TeamResponse teamInvito = controller.getTeam(invito.getRegistrazioneInvitante().getRegistrazioneId());
            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi " + azione + " l'invito del team '" + ((teamInvito.team() != null) ? teamInvito.team().getNome() : "N/A") + "'?", "Conferma", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ResponseResult response;
                    if (accetta) {
                        response = controller.rispondiInvitoTeam(invito.getInvitoId(), StatoInvito.ACCEPTED);
                    } else {
                        response = controller.rispondiInvitoTeam(invito.getInvitoId(), StatoInvito.DECLINED);
                    }
                    if (response.result()) {
                        showSuccess(frame, "Invito " + (accetta ? "accettato" : "rifiutato") + " con successo!");
                        loadTeamInfo();
                    } else {
                        showError(frame, "Errore: " + response.message());
                    }
                } catch (Exception e) {
                    showError(frame, "Errore: " + e.getMessage());
                }
            }
        }
    }

    private void invitaMembro() {
        try {
            var response = controller.getPartecipantiDisponibili(hackathonId);

            if (response.registrazioni() == null) {
                showError(frame, "Non ci sono partecipanti registrati all'hackathon!");
                return;
            }

            List<Registrazione> partecipantiInvitabili = response.registrazioni().stream().filter(partecipante -> membriList.stream().noneMatch(membro -> membro.getUtentePartecipante() != null && membro.getUtentePartecipante().getUtenteId() == partecipante.getUtentePartecipanteId())).toList();

            if (partecipantiInvitabili.isEmpty()) {
                showError(frame, "Non ci sono partecipanti disponibili da invitare!");
                return;
            }

            String[] nomiPartecipanti = partecipantiInvitabili.stream().map(p -> p.getUtentePartecipante().getNome() + " " + p.getUtentePartecipante().getCognome() + " (" + p.getUtentePartecipante().getUsername() + ")").toArray(String[]::new);

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
                    Registrazione partecipanteSelezionato = partecipantiInvitabili.get(selectedIndex);

                    JTextArea messageArea = new JTextArea(4, 30);
                    messageArea.setLineWrap(true);
                    messageArea.setWrapStyleWord(true);
                    messageArea.setText("Ciao! Ti invitiamo a unirti al nostro team \"" + teamCorrente.getNome() + "\" per l'hackathon.");

                    JScrollPane scrollPane = new JScrollPane(messageArea);
                    scrollPane.setPreferredSize(new Dimension(350, 100));

                    Object[] message = {"Messaggio per " + partecipanteSelezionato.getUtentePartecipante().getNome() + " " + partecipanteSelezionato.getUtentePartecipante().getCognome() + ":", scrollPane};

                    int option = JOptionPane.showConfirmDialog(frame, message, "Messaggio di Invito", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String messaggioInvito = messageArea.getText().trim();

                        if (messaggioInvito.isEmpty()) {
                            messaggioInvito = "Ti invitiamo a unirti al nostro team!";
                        }

                        var inviteResult = controller.invitaUtenteInTeam(hackathonId, partecipanteSelezionato.getUtentePartecipanteId(), messaggioInvito);

                        if (inviteResult.result()) {
                            showSuccess(frame, "Invito inviato con successo a " + partecipanteSelezionato.getUtentePartecipante().getNome() + " " + partecipanteSelezionato.getUtentePartecipante().getCognome());
                        } else {
                            showError(frame, "Errore nell'invito: " + inviteResult.message());
                        }
                    }
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nell'invito: " + e.getMessage());
        }
    }

    private void rimuoviMembro() {
        int selectedRow = membriTable.getSelectedRow();
        if (selectedRow != -1) {
            Registrazione membro = membriList.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi rimuovere " + membro.getUtentePartecipante().getNomeCompleto() + " dal team?", "Conferma Rimozione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = controller.rimuoviDalTeam(membro.getUtentePartecipanteId(), hackathonId);
                    if (response.result()) {
                        showSuccess(frame, "Membro rimosso con successo!");
                        loadMembriData();
                    } else {
                        showError(frame, "Errore nella rimozione: " + response.message());
                    }
                } catch (Exception e) {
                    showError(frame, "Errore nella rimozione: " + e.getMessage());
                }
            }
        }
    }

    private void abbandonaSciogleTeam() {
        String azione = isLeader ? "sciogliere" : "abbandonare";
        String messaggio = isLeader ? "Sei sicuro di voler sciogliere il team '" + teamCorrente.getNome() + "'?\n" + "Questa azione rimuoverà tutti i registrazioni dal team!" : "Sei sicuro di voler abbandonare il team '" + teamCorrente.getNome() + "'?";

        int confirm = JOptionPane.showConfirmDialog(frame, messaggio, "Conferma " + (isLeader ? "Scioglimento" : "Abbandono") + " Team", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ResponseResult response = controller.abbandonaTeam(teamCorrente.getHackathonId());

                if (response.result()) {
                    showSuccess(frame, "Hai " + (isLeader ? "sciolto" : "abbandonato") + " il team con successo!");
                    teamCorrente = null;
                    loadTeamInfo();
                } else {
                    showError(frame, "Errore nell'" + azione + ": " + response.message());
                }
            } catch (Exception e) {
                showError(frame, "Errore nell'" + azione + ": " + e.getMessage());
            }
        }
    }

    private void caricaProgresso() {
        if (fileSelezionato == null) {
            showError(frame, "Seleziona un file da caricare");
            return;
        }

        try {
            String url = fileSelezionato.getAbsolutePath();
            String docNome = fileSelezionato.getName();

            ProgressoResponse response = controller.caricaProgresso(hackathonId, url, docNome);

            if (response.progresso() != null) {
                showSuccess(frame, "Progresso caricato con successo!");
                clearFormProgresso();
                loadProgressiData();
            } else {
                showError(frame, "Errore nel caricamento: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento: " + e.getMessage());
        }
    }

    private void eliminaProgresso() {
        if (progressoSelezionato == null) return;

        int confirm = JOptionPane.showConfirmDialog(frame, "Sei sicuro di voler eliminare il progresso?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                var response = controller.eliminaProgresso(progressoSelezionato.getProgressoId());
                if (response.result()) {
                    showSuccess(frame, "Progresso eliminato con successo!");
                    loadProgressiData();
                } else {
                    showError(frame, "Errore nell'eliminazione: " + response.message());
                }
            } catch (Exception e) {
                showError(frame, "Errore nell'eliminazione: " + e.getMessage());
            }
        }
    }

    private void selezionaFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("File di testo (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            fileSelezionato = fileChooser.getSelectedFile();
            fileSelezionatoLabel.setText(fileSelezionato.getName());
        }
    }

    private void visualizzaProblema() {
        if (problemaSelezionato == null) return;

        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(problemaSelezionato.getTitolo());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea descrizioneArea = new JTextArea(problemaSelezionato.getDescrizione());
        descrizioneArea.setEditable(false);
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(descrizioneArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        String info = "Pubblicato il: " + problemaSelezionato.getDataPubblicazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        if (problemaSelezionato.getPubblicatoDaGiudiceHackathon() != null) {
            info += " da " + problemaSelezionato.getPubblicatoDaGiudiceHackathon().getUtenteGiudice().getNomeCompleto();
        }
        JLabel infoLabel = new JLabel(info);
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(frame, panel, "Dettagli Problema", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFormProgresso() {
        fileSelezionatoLabel.setText("Nessun file selezionato");
        fileSelezionato = null;
        progressoSelezionato = null;
    }
    //endregion

    //region Private Classes
    private class TeamsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nome Team", "Membri"};

        @Override
        public int getRowCount() {
            return teams.size();
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
            Team team = teams.get(rowIndex);
            ResponseIntResult contaNumeroMembri = controller.contaNumeroMembri(team.getTeamId());
            return switch (columnIndex) {
                case 0 -> team.getNome();
                case 1 -> ((contaNumeroMembri != null) ? contaNumeroMembri.result() : "N/A");
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
            TeamResponse teamInvito = controller.getTeam(invito.getRegistrazioneInvitante().getRegistrazioneId());
            return switch (columnIndex) {
                case 0 -> ((teamInvito.team() != null) ? teamInvito.team().getNome() : "N/A");
                case 1 ->
                        invito.getRegistrazioneInvitante().getUtentePartecipante().getNomeCompleto() != null ? invito.getRegistrazioneInvitante().getUtentePartecipante().getNomeCompleto() : "N/A";
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
            Registrazione membro = membriList.get(rowIndex);
            return switch (columnIndex) {
                case 0 ->
                        membro.getUtentePartecipante() != null ? membro.getUtentePartecipante().getNomeCompleto() : "N/A";
                case 1 -> membro.getUtentePartecipante() != null ? membro.getUtentePartecipante().getUsername() : "N/A";
                case 2 -> membro.getRuolo() != null ? membro.getRuolo().getDisplayName() : "N/A";
                case 3 -> membro.getDataIngressoTeam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                default -> "";
            };
        }
    }

    private class ProgressiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Documento", "Data Caricamento", "Autore"};

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
                case 0 -> progresso.getDocumentoNome() != null ? progresso.getDocumentoNome() : "Nessun file";
                case 1 -> progresso.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 2 ->
                        progresso.getCaricatoDaRegistrazione() != null ? progresso.getCaricatoDaRegistrazione().getUtentePartecipante().getNomeCompleto() : "N/A";
                default -> "";
            };
        }
    }

    private class ProblemiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Data Pubblicazione"};

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
                default -> "";
            };
        }
    }
    //endregion
}