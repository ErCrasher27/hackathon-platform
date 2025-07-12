package it.unina.hackathon.gui.partecipante;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.PartecipanteController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.Team;
import it.unina.hackathon.utils.responses.HackathonListResponse;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class HomePartecipanteGUI implements GUIHandler {

    // region Controllers

    private final Controller controller;
    private final NavigationController navigationController;
    private final PartecipanteController partecipanteController;

    // endregion

    // region Components
    private final List<Hackathon> hackathonDisponibili;
    private final List<Hackathon> mieiHackathon;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;

    // endregion

    // region Header Components
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;

    // endregion

    // region Tab Hackathon Disponibili
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JPanel hackathonDisponibiliPanel;
    private JPanel hackathonButtonPanel;
    private JTable hackathonDisponibiliTable;
    private JScrollPane hackathonDisponibiliScrollPane;
    private HackathonDisponibiliTableModel hackathonDisponibiliTableModel;
    private JButton registratiButton;

    // endregion

    // region Tab Miei Hackathon
    private JButton aggiornaHackathonButton;
    private JPanel mieiHackathonPanel;
    private JPanel mieiHackathonButtonPanel;
    private JTable mieiHackathonTable;
    private JScrollPane mieiHackathonScrollPane;
    private MieiHackathonTableModel mieiHackathonTableModel;
    private JButton gestisciProgettoButton;

    // endregion

    // region Tab Team
    private JButton annullaRegistrazioneButton;
    private JButton aggiornaMieiHackathonButton;

    // endregion

    // region Data
    private Team teamCorrente;

    // endregion

    // region Costruttore

    public HomePartecipanteGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.partecipanteController = controller.getPartecipanteController();
        this.hackathonDisponibili = new ArrayList<>();
        this.mieiHackathon = new ArrayList<>();

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

        // Tabbed pane
        tabbedPane = new JTabbedPane();

        // Create tabs
        createHackathonDisponibiliTab();
        createMieiHackathonTab();

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Home Partecipante");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Header
        logoutButton.addActionListener(_ -> {
            controller.getAuthController().logout();
            navigationController.goToLogin(frame);
        });

        // Tab Hackathon Disponibili
        setupHackathonDisponibiliEvents();

        // Tab Miei Hackathon
        setupMieiHackathonEvents();
    }

    @Override
    public void loadData() {
        loadHackathonDisponibili();
        loadMieiHackathon();
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    // endregion

    // region Header Creation

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());

        welcomeLabel = new JLabel("Ciao " + controller.getUtenteCorrente().getNome() + "!");
        applyStyleTitleLbl(welcomeLabel);

        logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(80, 30));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
    }

    // endregion

    // region Tab Creation

    private void createHackathonDisponibiliTab() {
        hackathonDisponibiliPanel = new JPanel(new BorderLayout());

        // Button panel
        hackathonButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaHackathonButton = new JButton("Aggiorna");
        registratiButton = new JButton("Registrati");

        // Style buttons
        aggiornaHackathonButton.setPreferredSize(new Dimension(100, 35));
        registratiButton.setPreferredSize(new Dimension(100, 35));

        // Initially disable action buttons
        registratiButton.setEnabled(false);

        hackathonButtonPanel.add(aggiornaHackathonButton);
        hackathonButtonPanel.add(registratiButton);

        // Table
        hackathonDisponibiliTableModel = new HackathonDisponibiliTableModel();
        hackathonDisponibiliTable = new JTable(hackathonDisponibiliTableModel);
        setupHackathonDisponibiliTable();

        hackathonDisponibiliScrollPane = new JScrollPane(hackathonDisponibiliTable);
        hackathonDisponibiliScrollPane.setBorder(BorderFactory.createTitledBorder("Hackathon Disponibili"));

        hackathonDisponibiliPanel.add(hackathonButtonPanel, BorderLayout.NORTH);
        hackathonDisponibiliPanel.add(hackathonDisponibiliScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Hackathon Disponibili", hackathonDisponibiliPanel);
    }

    private void createMieiHackathonTab() {
        mieiHackathonPanel = new JPanel(new BorderLayout());

        // Button panel
        mieiHackathonButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaMieiHackathonButton = new JButton("Aggiorna");
        gestisciProgettoButton = new JButton("Gestisci Progetto");
        annullaRegistrazioneButton = new JButton("Annulla Registrazione");

        // Style buttons
        aggiornaMieiHackathonButton.setPreferredSize(new Dimension(100, 35));
        gestisciProgettoButton.setPreferredSize(new Dimension(150, 35));
        annullaRegistrazioneButton.setPreferredSize(new Dimension(170, 35));

        // Initially disable action buttons
        gestisciProgettoButton.setEnabled(false);
        annullaRegistrazioneButton.setEnabled(false);

        mieiHackathonButtonPanel.add(aggiornaMieiHackathonButton);
        mieiHackathonButtonPanel.add(gestisciProgettoButton);
        mieiHackathonButtonPanel.add(annullaRegistrazioneButton);

        // Table
        mieiHackathonTableModel = new MieiHackathonTableModel();
        mieiHackathonTable = new JTable(mieiHackathonTableModel);
        setupMieiHackathonTable();

        mieiHackathonScrollPane = new JScrollPane(mieiHackathonTable);
        mieiHackathonScrollPane.setBorder(BorderFactory.createTitledBorder("I Miei Hackathon"));

        mieiHackathonPanel.add(mieiHackathonButtonPanel, BorderLayout.NORTH);
        mieiHackathonPanel.add(mieiHackathonScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("I Miei Hackathon", mieiHackathonPanel);
    }

    // endregion

    // region Table Setup

    private void setupHackathonDisponibiliTable() {
        hackathonDisponibiliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonDisponibiliTable.setRowHeight(25);
        hackathonDisponibiliTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = hackathonDisponibiliTable.getSelectedRow() != -1;
                registratiButton.setEnabled(hasSelection);
            }
        });

        // Double click to view details
        hackathonDisponibiliTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && hackathonDisponibiliTable.getSelectedRow() != -1) {
                    registratiAdHackathon();
                }
            }
        });
    }

    private void setupMieiHackathonTable() {
        mieiHackathonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mieiHackathonTable.setRowHeight(25);
        mieiHackathonTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = mieiHackathonTable.getSelectedRow() != -1;
                gestisciProgettoButton.setEnabled(hasSelection);
                annullaRegistrazioneButton.setEnabled(hasSelection);
            }
        });

        // Double click to manage project
        mieiHackathonTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && mieiHackathonTable.getSelectedRow() != -1) {
                    gestisciProgetto();
                }
            }
        });
    }

    // endregion

    // region Event Listeners

    private void setupHackathonDisponibiliEvents() {
        aggiornaHackathonButton.addActionListener(_ -> loadHackathonDisponibili());
        registratiButton.addActionListener(_ -> registratiAdHackathon());
    }

    private void setupMieiHackathonEvents() {
        aggiornaMieiHackathonButton.addActionListener(_ -> loadMieiHackathon());
        gestisciProgettoButton.addActionListener(_ -> gestisciProgetto());
        annullaRegistrazioneButton.addActionListener(_ -> annullaRegistrazione());
    }

    // endregion

    // region Data Loading

    private void loadHackathonDisponibili() {
        try {
            hackathonDisponibiliTable.setEnabled(false);
            aggiornaHackathonButton.setText("Caricamento...");
            aggiornaHackathonButton.setEnabled(false);

            HackathonListResponse response = partecipanteController.getHackathonDisponibili();
            if (response.hackathons() != null) {
                hackathonDisponibili.clear();
                hackathonDisponibili.addAll(response.hackathons());
                hackathonDisponibiliTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento degli hackathon disponibili: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore durante il caricamento: " + e.getMessage());
        } finally {
            hackathonDisponibiliTable.setEnabled(true);
            aggiornaHackathonButton.setText("Aggiorna");
            aggiornaHackathonButton.setEnabled(true);
        }
    }

    private void loadMieiHackathon() {
        try {
            mieiHackathonTable.setEnabled(false);
            aggiornaMieiHackathonButton.setText("Caricamento...");
            aggiornaMieiHackathonButton.setEnabled(false);

            HackathonListResponse response = partecipanteController.getHackathonRegistrati(controller.getIdUtenteCorrente());
            if (response.hackathons() != null) {
                mieiHackathon.clear();
                mieiHackathon.addAll(response.hackathons());
                mieiHackathonTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento dei tuoi hackathon: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore durante il caricamento: " + e.getMessage());
        } finally {
            mieiHackathonTable.setEnabled(true);
            aggiornaMieiHackathonButton.setText("Aggiorna");
            aggiornaMieiHackathonButton.setEnabled(true);
        }
    }

    // endregion

    // region Action Methods

    private void registratiAdHackathon() {
        int selectedRow = hackathonDisponibiliTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = hackathonDisponibili.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi registrarti all'hackathon '" + hackathon.getTitolo() + "'?", "Conferma Registrazione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = partecipanteController.registratiAdHackathon(hackathon.getHackathonId());
                    if (response.result()) {
                        showInfoMessage("Registrazione completata con successo!");
                        loadHackathonDisponibili();
                        loadMieiHackathon();
                    } else {
                        showErrorMessage("Errore durante la registrazione: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore durante la registrazione: " + e.getMessage());
                }
            }
        }
    }

    private void gestisciProgetto() {
        int selectedRow = mieiHackathonTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = mieiHackathon.get(selectedRow);
            navigationController.goToGestisciProgetto(frame, hackathon.getHackathonId());
        }
    }

    private void annullaRegistrazione() {
        int selectedRow = mieiHackathonTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = mieiHackathon.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi annullare la registrazione all'hackathon '" + hackathon.getTitolo() + "'?", "Conferma Annullamento", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = partecipanteController.annullaRegistrazione(hackathon.getHackathonId());
                    if (response.result()) {
                        showInfoMessage("Registrazione annullata con successo!");
                        loadHackathonDisponibili();
                        loadMieiHackathon();
                    } else {
                        showErrorMessage("Errore durante l'annullamento: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore durante l'annullamento: " + e.getMessage());
                }
            }
        }
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

    private class HackathonDisponibiliTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Sede", "Data Inizio", "Data Fine", "Iscritti", "Stato"};

        @Override
        public int getRowCount() {
            return hackathonDisponibili.size();
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
            Hackathon hackathon = hackathonDisponibili.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> hackathon.getTitolo();
                case 1 -> hackathon.getSede();
                case 2 -> hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 3 -> hackathon.getDataFine().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 4 -> hackathon.getMaxIscritti() + "/" + hackathon.getMaxIscritti();
                case 5 -> hackathon.getStatus().getDisplayName();
                default -> "";
            };
        }
    }

    private class MieiHackathonTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Sede", "Data Inizio", "Data Fine", "Stato", "Team"};

        @Override
        public int getRowCount() {
            return mieiHackathon.size();
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
            Hackathon hackathon = mieiHackathon.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> hackathon.getTitolo();
                case 1 -> hackathon.getSede();
                case 2 -> hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 3 -> hackathon.getDataFine().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 4 -> hackathon.getStatus().getDisplayName();
                case 5 -> teamCorrente != null ? teamCorrente.getNome() : "Nessun team";
                default -> "";
            };
        }
    }

    // endregion
}