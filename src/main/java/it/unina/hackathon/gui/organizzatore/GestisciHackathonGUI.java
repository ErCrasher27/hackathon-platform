package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.OrganizzatoreController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.GiudiceHackathon;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.responses.GiudiceHackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.TeamListResponse;
import it.unina.hackathon.utils.responses.UtenteListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import static it.unina.hackathon.utils.UtilsUi.*;

public class GestisciHackathonGUI implements GUIHandler {

    // Controllers
    private final Controller controller;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;
    private final int hackathonId;

    // Components
    private JFrame frame;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel actionPanel;

    // Current hackathon data
    private Hackathon hackathonCorrente;

    // Tab Statistiche components
    private JPanel statistichePanel;
    private JLabel iscrittiLabel;
    private JLabel teamLabel;
    private JLabel giudiciLabel;

    // Tab Giudici components
    private JPanel giudiciPanel;
    private JTable giudiciTable;
    private DefaultTableModel giudiciTableModel;

    // Tab Partecipanti components
    private JPanel partecipantiPanel;
    private JTable partecipantiTable;
    private DefaultTableModel partecipantiTableModel;
    private JTable teamTable;
    private DefaultTableModel teamTableModel;

    // Action buttons
    private JButton backButton;

    public GestisciHackathonGUI(int hackathonId) {
        this.hackathonId = hackathonId;
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

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Create all tabs
        createStatisticheTab();
        createGiudiciTab();
        createPartecipantiTab();

        // Add tabs to tabbed pane
        tabbedPane.addTab("Statistiche", statistichePanel);
        tabbedPane.addTab("Gestione Giudici", giudiciPanel);
        tabbedPane.addTab("Partecipanti & Team", partecipantiPanel);

        // Action panel
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        backButton = new JButton("← Indietro");

        // Style buttons
        backButton.setPreferredSize(new Dimension(120, 35));

        actionPanel.add(backButton);

        // Assemble main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Gestisci Hackathon");
        frame.setContentPane(mainPanel);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        backButton.addActionListener(_ -> navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente()));
    }

    @Override
    public void loadData() {
        try {
            // Load hackathon details
            HackathonResponse response = organizzatoreController.getDettagliHackathon(hackathonId);

            if (response.hackathon() != null) {
                hackathonCorrente = response.hackathon();
                frame.setTitle("Hackathon Platform - Gestisci: " + hackathonCorrente.getTitolo());
            } else {
                showError(frame, "Errore nel caricamento dei dettagli: " + response.message());
                navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente());
                return;
            }

            // Load statistics
            aggiornaStatistiche();

            // Load other tabs data
            caricaGiudiciInvitati();
            caricaPartecipanti();
            caricaTeam();

        } catch (Exception e) {
            showError(frame, "Errore imprevisto: " + e.getMessage());
        }
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void createStatisticheTab() {
        statistichePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.WEST;

        Font statisticFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);

        // Iscritti
        gbc.gridx = 0;
        gbc.gridy = 0;
        statistichePanel.add(new JLabel("Partecipanti iscritti:"), gbc);
        gbc.gridx = 1;
        iscrittiLabel = new JLabel("0 / 0");
        iscrittiLabel.setFont(statisticFont);
        iscrittiLabel.setForeground(Color.BLUE);
        statistichePanel.add(iscrittiLabel, gbc);

        // Team
        gbc.gridx = 0;
        gbc.gridy = 1;
        statistichePanel.add(new JLabel("Team formati:"), gbc);
        gbc.gridx = 1;
        teamLabel = new JLabel("0");
        teamLabel.setFont(statisticFont);
        teamLabel.setForeground(Color.GREEN);
        statistichePanel.add(teamLabel, gbc);

        // Giudici
        gbc.gridx = 0;
        gbc.gridy = 2;
        statistichePanel.add(new JLabel("Giudici confermati:"), gbc);
        gbc.gridx = 1;
        giudiciLabel = new JLabel("0");
        giudiciLabel.setFont(statisticFont);
        giudiciLabel.setForeground(Color.ORANGE);
        statistichePanel.add(giudiciLabel, gbc);

        // Refresh button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton refreshStatsButton = new JButton("Aggiorna Statistiche");
        refreshStatsButton.addActionListener(_ -> aggiornaStatistiche());
        statistichePanel.add(refreshStatsButton, gbc);
    }

    private void createGiudiciTab() {
        giudiciPanel = new JPanel(new BorderLayout());

        // Top panel with buttons
        JPanel giudiciTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Styling dei pulsanti
        JButton invitaButton = new JButton("+ Invita Giudice");
        invitaButton.setBackground(new Color(46, 125, 50)); // Verde
        invitaButton.setForeground(Color.WHITE);
        invitaButton.setPreferredSize(new Dimension(140, 35));

        JButton rimuoviButton = new JButton("🗑 Rimuovi Invito");
        rimuoviButton.setBackground(new Color(211, 47, 47)); // Rosso
        rimuoviButton.setForeground(Color.WHITE);
        rimuoviButton.setPreferredSize(new Dimension(150, 35));

        JButton aggiornaGiudiciButton = new JButton("🔄 Aggiorna");
        aggiornaGiudiciButton.setPreferredSize(new Dimension(120, 35));

        // Inizialmente disabilita il pulsante rimuovi
        rimuoviButton.setEnabled(false);

        giudiciTopPanel.add(invitaButton);
        giudiciTopPanel.add(rimuoviButton);
        giudiciTopPanel.add(aggiornaGiudiciButton);

        // Table setup con listener per abilitare/disabilitare il pulsante rimuovi
        String[] giudiciColumns = {"Nome", "Cognome", "Email", "Stato Invito", "Data Invito"};
        giudiciTableModel = new DefaultTableModel(giudiciColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        giudiciTable = new JTable(giudiciTableModel);
        giudiciTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        giudiciTable.setRowHeight(30);

        // Listener per abilitare/disabilitare pulsante rimuovi
        giudiciTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = giudiciTable.getSelectedRow() != -1;
                rimuoviButton.setEnabled(hasSelection);
            }
        });

        JScrollPane giudiciScrollPane = new JScrollPane(giudiciTable);
        giudiciScrollPane.setBorder(BorderFactory.createTitledBorder("Giudici Invitati"));

        giudiciPanel.add(giudiciTopPanel, BorderLayout.NORTH);
        giudiciPanel.add(giudiciScrollPane, BorderLayout.CENTER);

        // Event listeners
        invitaButton.addActionListener(_ -> apriDialogInvitaGiudice());
        rimuoviButton.addActionListener(_ -> rimuoviGiudiceSelezionato());
        aggiornaGiudiciButton.addActionListener(_ -> caricaGiudiciInvitati());
    }

    private void createPartecipantiTab() {
        partecipantiPanel = new JPanel(new BorderLayout());

        // Split pane for partecipanti and team
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Partecipanti table (top)
        String[] partecipantiColumns = {"Nome", "Cognome", "Username", "Email", "Data Registrazione"};
        partecipantiTableModel = new DefaultTableModel(partecipantiColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        partecipantiTable = new JTable(partecipantiTableModel);
        partecipantiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        partecipantiTable.setRowHeight(30);
        JScrollPane partecipantiScrollPane = new JScrollPane(partecipantiTable);
        partecipantiScrollPane.setBorder(BorderFactory.createTitledBorder("Partecipanti Registrati"));
        partecipantiScrollPane.setPreferredSize(new Dimension(0, 300));

        // Team table (bottom)
        String[] teamColumns = {"Nome Team", "Numero Membri", "Stato", "Data Creazione"};
        teamTableModel = new DefaultTableModel(teamColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        teamTable = new JTable(teamTableModel);
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.setRowHeight(30);
        JScrollPane teamScrollPane = new JScrollPane(teamTable);
        teamScrollPane.setBorder(BorderFactory.createTitledBorder("Team Formati"));
        teamScrollPane.setPreferredSize(new Dimension(0, 200));

        splitPane.setTopComponent(partecipantiScrollPane);
        splitPane.setBottomComponent(teamScrollPane);
        splitPane.setResizeWeight(0.6);

        // Button panel
        JPanel partecipantiButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton aggiornaPartecipantiButton = new JButton("Aggiorna Dati");
        partecipantiButtonPanel.add(aggiornaPartecipantiButton);

        partecipantiPanel.add(partecipantiButtonPanel, BorderLayout.NORTH);
        partecipantiPanel.add(splitPane, BorderLayout.CENTER);

        // Event listeners
        aggiornaPartecipantiButton.addActionListener(_ -> {
            caricaPartecipanti();
            caricaTeam();
        });
    }

    private void aggiornaStatistiche() {
        try {
            ResponseIntResult numPartecipanti = organizzatoreController.contaPartecipanti(hackathonId);
            ResponseIntResult numTeam = organizzatoreController.contaTeam(hackathonId);
            ResponseIntResult numGiudici = organizzatoreController.contaGiudiciAccettati(hackathonId);

            int partecipanti = Math.max(numPartecipanti.result(), 0);
            int team = Math.max(numTeam.result(), 0);
            int giudici = Math.max(numGiudici.result(), 0);

            iscrittiLabel.setText(partecipanti + " / " + (hackathonCorrente != null ? hackathonCorrente.getMaxIscritti() : "?"));
            teamLabel.setText(String.valueOf(team));
            giudiciLabel.setText(String.valueOf(giudici));
        } catch (Exception e) {
            iscrittiLabel.setText("Errore");
            teamLabel.setText("Errore");
            giudiciLabel.setText("Errore");
        }
    }

    private void caricaGiudiciInvitati() {
        try {
            giudiciTableModel.setRowCount(0);

            GiudiceHackathonListResponse response = organizzatoreController.getAllGiudiciInvitatiInHackathon(hackathonId);

            if (response.giudiciHackathon() != null) {
                for (GiudiceHackathon gh : response.giudiciHackathon()) {
                    Object[] row = {gh.getGiudice() != null ? gh.getGiudice().getNome() : "N/A", gh.getGiudice() != null ? gh.getGiudice().getCognome() : "N/A", gh.getGiudice() != null ? gh.getGiudice().getEmail() : "N/A", gh.getStatoInvito().getDisplayName(), gh.getDataInvito().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))};
                    giudiciTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento giudici: " + e.getMessage());
        }
    }

    private void caricaPartecipanti() {
        try {
            partecipantiTableModel.setRowCount(0);

            UtenteListResponse response = organizzatoreController.getPartecipantiHackathon(hackathonId);

            if (response.utenti() != null) {
                for (Utente partecipante : response.utenti()) {
                    Object[] row = {partecipante.getNome(), partecipante.getCognome(), partecipante.getUsername(), partecipante.getEmail(), partecipante.getDataRegistrazione() != null ? partecipante.getDataRegistrazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"};
                    partecipantiTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento partecipanti: " + e.getMessage());
        }
    }

    private void caricaTeam() {
        try {
            teamTableModel.setRowCount(0);

            TeamListResponse response = organizzatoreController.getTeamHackathon(hackathonId);

            if (response.teams() != null) {
                for (Team team : response.teams()) {
                    Object[] row = {team.getNome(), team.getNumeroMembri(), team.isDefinitivo() ? "Definitivo" : "In formazione", team.getDataCreazione() != null ? team.getDataCreazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"};
                    teamTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento team: " + e.getMessage());
        }
    }

    private void apriDialogInvitaGiudice() {
        try {
            UtenteListResponse response = organizzatoreController.getAllGiudiciNonInvitatiInHackathon(hackathonId);

            if (response.utenti() == null || response.utenti().isEmpty()) {
                showError(frame, "Non ci sono giudici disponibili da invitare!");
                return;
            }

            String[] nomiGiudici = response.utenti().stream().map(g -> g.getNome() + " " + g.getCognome() + " (" + g.getUsername() + ")").toArray(String[]::new);

            String selected = (String) JOptionPane.showInputDialog(frame, "Seleziona il giudice da invitare:", "Invita Giudice", JOptionPane.QUESTION_MESSAGE, null, nomiGiudici, nomiGiudici[0]);

            if (selected != null) {
                int selectedIndex = -1;
                for (int i = 0; i < nomiGiudici.length; i++) {
                    if (nomiGiudici[i].equals(selected)) {
                        selectedIndex = i;
                        break;
                    }
                }

                if (selectedIndex >= 0) {
                    Utente giudiceSelezionato = response.utenti().get(selectedIndex);
                    ResponseResult result = organizzatoreController.invitaGiudice(hackathonId, giudiceSelezionato.getUtenteId());

                    if (result.result()) {
                        showSuccess(frame, result.message());
                        caricaGiudiciInvitati();
                        aggiornaStatistiche();
                    } else {
                        showError(frame, result.message());
                    }
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nell'invito giudice: " + e.getMessage());
        }
    }

    private boolean puoRimuovereInvito(String statoInvito) {
        // Puoi rimuovere inviti solo se sono in stato PENDING o DECLINED
        // Non permettere rimozione di inviti ACCEPTED per mantenere l'integrità
        return "In Attesa".equals(statoInvito) || "Rifiutato".equals(statoInvito);
    }

    private void rimuoviGiudiceSelezionato() {
        int selectedRow = giudiciTable.getSelectedRow();
        if (selectedRow == -1) {
            showError(frame, "Seleziona un giudice da rimuovere!");
            return;
        }

        String nomeGiudice = (String) giudiciTableModel.getValueAt(selectedRow, 0);
        String cognomeGiudice = (String) giudiciTableModel.getValueAt(selectedRow, 1);
        String statoInvito = (String) giudiciTableModel.getValueAt(selectedRow, 3);

        // Validazione dello stato dell'invito
        if (!puoRimuovereInvito(statoInvito)) {
            showError(frame, "Non è possibile rimuovere un invito accettato. " + "Il giudice fa già parte dell'hackathon!");
            return;
        }

        // Messaggio di conferma personalizzato in base allo stato
        String messaggioConferma;
        if ("In Attesa".equals(statoInvito)) {
            messaggioConferma = String.format("Rimuovere l'invito in attesa per %s %s?\n" + "Il giudice non potrà più accettare questo invito.", nomeGiudice, cognomeGiudice);
        } else {
            messaggioConferma = String.format("Rimuovere l'invito rifiutato da %s %s?\n" + "Questo eliminerà definitivamente il record dell'invito.", nomeGiudice, cognomeGiudice);
        }

        int confirm = JOptionPane.showConfirmDialog(frame, messaggioConferma, "Conferma Rimozione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                GiudiceHackathonListResponse giudiciResponse = organizzatoreController.getAllGiudiciInvitatiInHackathon(hackathonId);

                if (giudiciResponse.giudiciHackathon() != null && selectedRow < giudiciResponse.giudiciHackathon().size()) {
                    GiudiceHackathon giudiceSelezionato = giudiciResponse.giudiciHackathon().get(selectedRow);
                    int giudiceId = giudiceSelezionato.getGiudiceId();

                    ResponseResult result = organizzatoreController.rimuoviInvitoGiudice(hackathonId, giudiceId);

                    if (result.result()) {
                        showSuccess(frame, result.message());
                        caricaGiudiciInvitati();
                        aggiornaStatistiche();
                    } else {
                        showError(frame, "Errore nella rimozione dell'invito: " + result.message());
                    }
                } else {
                    showError(frame, "Errore nel recupero dei dati del giudice selezionato!");
                }
            } catch (Exception e) {
                showError(frame, "Errore imprevisto durante la rimozione: " + e.getMessage());
            }
        }
    }

}