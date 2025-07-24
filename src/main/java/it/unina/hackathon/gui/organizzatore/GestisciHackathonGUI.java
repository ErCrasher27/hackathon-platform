package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.ClassificaTeam;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.model.Utente;
import it.unina.hackathon.utils.responses.*;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import static it.unina.hackathon.utils.UtilsUi.*;

public class GestisciHackathonGUI implements GUIHandler {

    // Controllers
    private final HackathonController controller;
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
    private JTable classificaTable;
    private DefaultTableModel classificaTableModel;

    public GestisciHackathonGUI(int hackathonId) {
        this.hackathonId = hackathonId;
        this.controller = HackathonController.getInstance();

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
        backButton.addActionListener(_ -> controller.vaiAllaHome(frame));
    }

    @Override
    public void loadData() {
        try {
            // Load hackathon details
            HackathonResponse response = controller.getDettagliHackathon(hackathonId);

            if (response.hackathon() != null) {
                hackathonCorrente = response.hackathon();
                frame.setTitle("Hackathon Platform - Gestisci: " + hackathonCorrente.getTitolo());
            } else {
                showError(frame, "Errore nel caricamento dei dettagli: " + response.message());
                controller.vaiAllaHome(frame);
                return;
            }

            // Load statistics
            aggiornaStatistiche();

            // Load other tabs data
            caricaGiudiciInvitati();
            caricaPartecipanti();
            caricaTeam();
            caricaClassifica();

        } catch (Exception e) {
            showError(frame, "Errore imprevisto: " + e.getMessage());
        }
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void createStatisticheTab() {
        statistichePanel = new JPanel(new BorderLayout());

        // Top panel con statistiche esistenti
        JPanel statsTopPanel = new JPanel(new GridBagLayout());
        statsTopPanel.setBorder(BorderFactory.createTitledBorder("Statistiche Generali"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Iscritti
        JLabel iscrittiTitleLabel = new JLabel("Partecipanti iscritti:");
        applyStyleTitleLbl(iscrittiTitleLabel);
        iscrittiLabel = new JLabel("Caricamento...");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        statsTopPanel.add(iscrittiTitleLabel, gbc);
        gbc.gridx = 1;
        statsTopPanel.add(iscrittiLabel, gbc);

        // Team
        JLabel teamTitleLabel = new JLabel("Team formati:");
        applyStyleTitleLbl(teamTitleLabel);
        teamLabel = new JLabel("Caricamento...");

        gbc.gridx = 0;
        gbc.gridy = 1;
        statsTopPanel.add(teamTitleLabel, gbc);
        gbc.gridx = 1;
        statsTopPanel.add(teamLabel, gbc);

        // Giudici
        JLabel giudiciTitleLabel = new JLabel("Giudici accettati:");
        applyStyleTitleLbl(giudiciTitleLabel);
        giudiciLabel = new JLabel("Caricamento...");

        gbc.gridx = 0;
        gbc.gridy = 2;
        statsTopPanel.add(giudiciTitleLabel, gbc);
        gbc.gridx = 1;
        statsTopPanel.add(giudiciLabel, gbc);

        // Sezione classifica
        JPanel classificaPanel = createClassificaPanel();

        // Button panel per aggiornare
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton aggiornaStatsButton = new JButton("Aggiorna Statistiche");
        buttonPanel.add(aggiornaStatsButton);

        // Layout principale
        statistichePanel.add(statsTopPanel, BorderLayout.NORTH);
        statistichePanel.add(classificaPanel, BorderLayout.CENTER);
        statistichePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Event listener
        aggiornaStatsButton.addActionListener(_ -> {
            aggiornaStatistiche();
            caricaClassifica();
        });
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

        JButton aggiornaGiudiciButton = new JButton("🔄 Aggiorna");
        aggiornaGiudiciButton.setPreferredSize(new Dimension(120, 35));

        giudiciTopPanel.add(invitaButton);
        giudiciTopPanel.add(aggiornaGiudiciButton);

        String[] giudiciColumns = {"Nome", "Cognome", "Email", "Stato", "Data Invito"};
        giudiciTableModel = new DefaultTableModel(giudiciColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        giudiciTable = new JTable(giudiciTableModel);
        giudiciTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        giudiciTable.setRowHeight(30);

        JScrollPane giudiciScrollPane = new JScrollPane(giudiciTable);
        giudiciScrollPane.setBorder(BorderFactory.createTitledBorder("Giudici Invitati"));

        giudiciPanel.add(giudiciTopPanel, BorderLayout.NORTH);
        giudiciPanel.add(giudiciScrollPane, BorderLayout.CENTER);

        // Event listeners
        invitaButton.addActionListener(_ -> apriDialogInvitaGiudice());
        aggiornaGiudiciButton.addActionListener(_ -> {
            caricaGiudiciInvitati();
            caricaClassifica();
        });
    }

    private void createPartecipantiTab() {
        partecipantiPanel = new JPanel(new BorderLayout());

        // Split pane for partecipanti and team
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Partecipanti table (top)
        String[] partecipantiColumns = {"Nome", "Cognome", "Username", "Email", "Team", "Data Registrazione"};
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
        String[] teamColumns = {"Nome Team", "Stato", "Data Creazione"};
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
            caricaClassifica();
        });
    }

    // Metodo per creare il panel della classifica
    private JPanel createClassificaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Classifica"));

        // Tabella classifica
        String[] classificaColumns = {"Posizione", "Team", "Media Voti", "Numero Voti"};
        classificaTableModel = new DefaultTableModel(classificaColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classificaTable = new JTable(classificaTableModel);
        classificaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classificaTable.setRowHeight(30);

        // Imposta larghezza colonne
        classificaTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Posizione
        classificaTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Team
        classificaTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Media
        classificaTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Numero Voti

        JScrollPane scrollPane = new JScrollPane(classificaTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel infoLabel = new JLabel("La classifica mostra solo i team definitivi che hanno ricevuto almeno un voto");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, 11f));
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Metodo per caricare la classifica
    private void caricaClassifica() {
        try {
            classificaTableModel.setRowCount(0);

            ClassificaListResponse response = controller.getClassificaHackathon(hackathonId);

            if (response.classifica() != null && !response.classifica().isEmpty()) {
                for (ClassificaTeam classificaTeam : response.classifica()) {
                    // Ora possiamo accedere direttamente ai campi
                    Integer posizione = classificaTeam.getPosizione();
                    String nomeTeam = classificaTeam.getNomeTeam() != null ? classificaTeam.getNomeTeam() : "N/A";
                    String mediaVoti = classificaTeam.getMediaVoti() != null ? String.format("%.2f", classificaTeam.getMediaVoti()) : "N/A";
                    String numeroVoti = classificaTeam.getNumeroVoti() != null ? classificaTeam.getNumeroVoti().toString() : "0";

                    Object[] row = {posizione, nomeTeam, mediaVoti, numeroVoti};
                    classificaTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento classifica: " + e.getMessage());
            // Aggiungi riga di errore
            Object[] row = {"--", "Errore nel caricamento", "--", "--"};
            classificaTableModel.addRow(row);
        }
    }

    private void aggiornaStatistiche() {
        try {
            ResponseIntResult numPartecipanti = controller.contaPartecipantiRegistrati(hackathonId);
            ResponseIntResult numTeam = controller.contaTeamFormati(hackathonId);
            ResponseIntResult numGiudici = controller.contaGiudiciAccettati(hackathonId);

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

            UtenteListResponse response = controller.getGiudiciInvitati(hackathonId);

            if (response.utenti() != null) {
                for (Utente giudice : response.utenti()) {
                    InvitoGiudiceResponse invitoGiudice = controller.getInvitoByInvitatoHackathon(giudice.getUtenteId(), hackathonId);
                    Object[] row = {giudice.getNome(), giudice.getCognome(), giudice.getEmail(), invitoGiudice.invitoGiudice() != null ? invitoGiudice.invitoGiudice().getStatoInvito() : "N/A", invitoGiudice.invitoGiudice() != null ? invitoGiudice.invitoGiudice().getDataInvito().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"};
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

            UtenteListResponse response = controller.getPartecipantiHackathon(hackathonId);

            if (response.utenti() != null) {
                for (Utente partecipante : response.utenti()) {
                    TeamResponse team = controller.getTeamPartecipante(partecipante.getUtenteId(), hackathonId);
                    Object[] row = {partecipante.getNome(), partecipante.getCognome(), partecipante.getUsername(), partecipante.getEmail(), team.team() != null ? team.team().getNome() : "N/A", partecipante.getDataRegistrazione() != null ? partecipante.getDataRegistrazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"};
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

            TeamListResponse response = controller.getTeamsHackathon(hackathonId);

            if (response.teams() != null) {
                for (Team team : response.teams()) {
                    Object[] row = {team.getNome(), team.isDefinitivo() ? "Definitivo" : "In formazione", team.getDataCreazione() != null ? team.getDataCreazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"};
                    teamTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento team: " + e.getMessage());
        }
    }

    private void apriDialogInvitaGiudice() {
        try {
            UtenteListResponse response = controller.getGiudiciNonInvitati(hackathonId);

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
                    ResponseResult result = controller.invitaGiudice(giudiceSelezionato.getUtenteId(), hackathonId);

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

}