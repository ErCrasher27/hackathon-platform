package it.unina.hackathon.gui.partecipante;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.base.ResponseIntResult;

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

    //region Fields
    private final HackathonController controller;
    private final List<Hackathon> hackathonDisponibili;
    private final List<Hackathon> mieiHackathon;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;

    private JLabel welcomeLabel;
    private JButton logoutButton;

    private JPanel hackathonDisponibiliPanel;
    private JPanel hackathonButtonPanel;
    private JTable hackathonDisponibiliTable;
    private JScrollPane hackathonDisponibiliScrollPane;
    private HackathonDisponibiliTableModel hackathonDisponibiliTableModel;
    private JButton registratiButton;
    private JButton aggiornaHackathonButton;

    private JPanel mieiHackathonPanel;
    private JPanel mieiHackathonButtonPanel;
    private JTable mieiHackathonTable;
    private JScrollPane mieiHackathonScrollPane;
    private MieiHackathonTableModel mieiHackathonTableModel;
    private JButton gestisciProgettoButton;
    private JButton annullaRegistrazioneButton;
    private JButton aggiornaMieiHackathonButton;
    //endregion

    //region Constructor
    public HomePartecipanteGUI() {
        this.controller = HackathonController.getInstance();
        this.hackathonDisponibili = new ArrayList<>();
        this.mieiHackathon = new ArrayList<>();

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

        tabbedPane = new JTabbedPane();
        createHackathonDisponibiliTab();
        createMieiHackathonTab();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

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
        logoutButton.addActionListener(_ -> {
            controller.effettuaLogout();
            controller.vaiAlLogin(frame);
        });

        setupHackathonDisponibiliEvents();
        setupMieiHackathonEvents();
    }

    @Override
    public void loadData() {
        loadHackathonDisponibili();
        loadMieiHackathon();
    }
    //endregion

    //region Private Methods
    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());

        welcomeLabel = new JLabel("Ciao " + controller.getUtenteCorrente().getNome() + "!");
        applyStyleTitleLbl(welcomeLabel);

        logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(80, 30));

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
    }

    private void createHackathonDisponibiliTab() {
        hackathonDisponibiliPanel = new JPanel(new BorderLayout());

        hackathonButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaHackathonButton = new JButton("Aggiorna");
        registratiButton = new JButton("Registrati");

        aggiornaHackathonButton.setPreferredSize(new Dimension(100, 35));
        registratiButton.setPreferredSize(new Dimension(100, 35));

        registratiButton.setEnabled(false);

        hackathonButtonPanel.add(aggiornaHackathonButton);
        hackathonButtonPanel.add(registratiButton);

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

        mieiHackathonButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaMieiHackathonButton = new JButton("Aggiorna");
        gestisciProgettoButton = new JButton("Gestisci Progetto");
        annullaRegistrazioneButton = new JButton("Annulla Registrazione");

        aggiornaMieiHackathonButton.setPreferredSize(new Dimension(100, 35));
        gestisciProgettoButton.setPreferredSize(new Dimension(150, 35));
        annullaRegistrazioneButton.setPreferredSize(new Dimension(170, 35));

        gestisciProgettoButton.setEnabled(false);
        annullaRegistrazioneButton.setEnabled(false);

        mieiHackathonButtonPanel.add(aggiornaMieiHackathonButton);
        mieiHackathonButtonPanel.add(gestisciProgettoButton);
        mieiHackathonButtonPanel.add(annullaRegistrazioneButton);

        mieiHackathonTableModel = new MieiHackathonTableModel();
        mieiHackathonTable = new JTable(mieiHackathonTableModel);
        setupMieiHackathonTable();

        mieiHackathonScrollPane = new JScrollPane(mieiHackathonTable);
        mieiHackathonScrollPane.setBorder(BorderFactory.createTitledBorder("I Miei Hackathon"));

        mieiHackathonPanel.add(mieiHackathonButtonPanel, BorderLayout.NORTH);
        mieiHackathonPanel.add(mieiHackathonScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("I Miei Hackathon", mieiHackathonPanel);
    }

    private void setupHackathonDisponibiliTable() {
        hackathonDisponibiliTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonDisponibiliTable.setRowHeight(25);
        hackathonDisponibiliTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = hackathonDisponibiliTable.getSelectedRow() != -1;
                registratiButton.setEnabled(hasSelection);
            }
        });

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

        mieiHackathonTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && mieiHackathonTable.getSelectedRow() != -1) {
                    gestisciProgetto();
                }
            }
        });
    }

    private void setupHackathonDisponibiliEvents() {
        aggiornaHackathonButton.addActionListener(_ -> loadHackathonDisponibili());
        registratiButton.addActionListener(_ -> registratiAdHackathon());
    }

    private void setupMieiHackathonEvents() {
        aggiornaMieiHackathonButton.addActionListener(_ -> loadMieiHackathon());
        gestisciProgettoButton.addActionListener(_ -> gestisciProgetto());
        annullaRegistrazioneButton.addActionListener(_ -> annullaRegistrazione());
    }

    private void loadHackathonDisponibili() {
        try {
            hackathonDisponibiliTable.setEnabled(false);
            aggiornaHackathonButton.setText("Caricamento...");
            aggiornaHackathonButton.setEnabled(false);

            HackathonListResponse response = controller.getHackathonDisponibili();
            if (response.hackathons() != null) {
                hackathonDisponibili.clear();
                hackathonDisponibili.addAll(response.hackathons());
                hackathonDisponibiliTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento degli hackathon disponibili: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore durante il caricamento: " + e.getMessage());
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

            HackathonListResponse response = controller.getHackathonRegistrati();
            if (response.hackathons() != null) {
                mieiHackathon.clear();
                mieiHackathon.addAll(response.hackathons());
                mieiHackathonTableModel.fireTableDataChanged();
            } else {
                showError(frame, "Errore nel caricamento dei tuoi hackathon: " + response.message());
            }
        } catch (Exception e) {
            showError(frame, "Errore durante il caricamento: " + e.getMessage());
        } finally {
            mieiHackathonTable.setEnabled(true);
            aggiornaMieiHackathonButton.setText("Aggiorna");
            aggiornaMieiHackathonButton.setEnabled(true);
        }
    }

    private void registratiAdHackathon() {
        int selectedRow = hackathonDisponibiliTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = hackathonDisponibili.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi registrarti all'hackathon '" + hackathon.getTitolo() + "'?", "Conferma Registrazione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = controller.registratiAdHackathon(hackathon.getHackathonId());
                    if (response.registrazione() != null) {
                        showSuccess(frame, "Registrazione completata con successo!");
                        loadHackathonDisponibili();
                        loadMieiHackathon();
                    } else {
                        showError(frame, "Errore durante la registrazione: " + response.message());
                    }
                } catch (Exception e) {
                    showError(frame, "Errore durante la registrazione: " + e.getMessage());
                }
            }
        }
    }

    private void gestisciProgetto() {
        int selectedRow = mieiHackathonTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = mieiHackathon.get(selectedRow);
            controller.vaiAGestireProgetto(frame, hackathon.getHackathonId());
        }
    }

    private void annullaRegistrazione() {
        int selectedRow = mieiHackathonTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = mieiHackathon.get(selectedRow);

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi annullare la registrazione all'hackathon '" + hackathon.getTitolo() + "'?", "Conferma Annullamento", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = controller.annullaRegistrazione(hackathon.getHackathonId());
                    if (response.result()) {
                        showSuccess(frame, "Registrazione annullata con successo!");
                        loadHackathonDisponibili();
                        loadMieiHackathon();
                    } else {
                        showError(frame, "Errore durante l'annullamento: " + response.message());
                    }
                } catch (Exception e) {
                    showError(frame, "Errore durante l'annullamento: " + e.getMessage());
                }
            }
        }
    }
    //endregion

    //region Private Classes
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
            ResponseIntResult numPartecipanti = controller.contaPartecipantiRegistrati(hackathon.getHackathonId());
            return switch (columnIndex) {
                case 0 -> hackathon.getTitolo();
                case 1 -> hackathon.getSede();
                case 2 -> hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 3 -> hackathon.getDataFine().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 4 -> numPartecipanti.result() + "/" + hackathon.getMaxIscritti();
                case 5 -> hackathon.getStatus().getDisplayName();
                default -> "";
            };
        }
    }

    private class MieiHackathonTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Sede", "Data Inizio", "Data Fine", "Stato"};

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
                default -> "";
            };
        }
    }
    //endregion
}