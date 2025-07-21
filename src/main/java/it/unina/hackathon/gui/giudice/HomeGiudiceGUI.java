package it.unina.hackathon.gui.giudice;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.InvitoGiudice;
import it.unina.hackathon.model.enums.StatoInvito;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.HackathonResponse;
import it.unina.hackathon.utils.responses.InvitoGiudiceListResponse;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class HomeGiudiceGUI implements GUIHandler {

    // region Controllers

    private final HackathonController controller;

    // endregion

    // region Components
    private final List<InvitoGiudice> inviti;
    private final List<Hackathon> hackathonAssegnati;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;

    // endregion

    // region Header Components
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;

    // endregion

    // region Tab Inviti Ricevuti
    private JLabel welcomeLabel;
    private JButton logoutButton;
    private JPanel invitiPanel;
    private JPanel invitiButtonPanel;
    private JTable invitiTable;
    private JScrollPane invitiScrollPane;
    private InvitiTableModel invitiTableModel;
    private JButton accettaInvitoButton;
    private JButton rifiutaInvitoButton;

    // endregion

    // region Tab Hackathon Assegnati
    private JButton aggiornaInvitiButton;
    private JPanel hackathonAssegnatiPanel;
    private JPanel hackathonAssegnatiButtonPanel;
    private JTable hackathonAssegnatiTable;
    private JScrollPane hackathonAssegnatiScrollPane;
    private HackathonAssegnatiTableModel hackathonAssegnatiTableModel;
    private JButton valutaProgettoButton;
    private JButton pubblicaProblemaButton;

    // endregion

    // region Tab Statistiche
    private JButton aggiornaHackathonButton;

    // endregion

    // region Costruttore

    public HomeGiudiceGUI() {
        this.controller = HackathonController.getInstance();

        this.inviti = new ArrayList<>();
        this.hackathonAssegnati = new ArrayList<>();

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
        createInvitiTab();
        createHackathonAssegnatiTab();

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Home Giudice");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Header
        logoutButton.addActionListener(_ -> {
            controller.effettuaLogout();
            controller.vaiAlLogin(frame);
        });

        // Tab Inviti
        setupInvitiEvents();

        // Tab Hackathon Assegnati
        setupHackathonAssegnatiEvents();
    }

    @Override
    public void loadData() {
        loadInviti();
        loadHackathonAssegnati();
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

    private void createInvitiTab() {
        invitiPanel = new JPanel(new BorderLayout());

        // Button panel
        invitiButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaInvitiButton = new JButton("Aggiorna");
        accettaInvitoButton = new JButton("Accetta");
        rifiutaInvitoButton = new JButton("Rifiuta");

        // Style buttons
        aggiornaInvitiButton.setPreferredSize(new Dimension(100, 35));
        accettaInvitoButton.setPreferredSize(new Dimension(100, 35));
        rifiutaInvitoButton.setPreferredSize(new Dimension(100, 35));

        // Initially disable action buttons
        accettaInvitoButton.setEnabled(false);
        rifiutaInvitoButton.setEnabled(false);

        invitiButtonPanel.add(aggiornaInvitiButton);
        invitiButtonPanel.add(accettaInvitoButton);
        invitiButtonPanel.add(rifiutaInvitoButton);

        // Table
        invitiTableModel = new InvitiTableModel();
        invitiTable = new JTable(invitiTableModel);
        setupInvitiTable();

        invitiScrollPane = new JScrollPane(invitiTable);
        invitiScrollPane.setBorder(BorderFactory.createTitledBorder("Inviti Ricevuti"));

        invitiPanel.add(invitiButtonPanel, BorderLayout.NORTH);
        invitiPanel.add(invitiScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Inviti Ricevuti", invitiPanel);
    }

    private void createHackathonAssegnatiTab() {
        hackathonAssegnatiPanel = new JPanel(new BorderLayout());

        // Button panel
        hackathonAssegnatiButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaHackathonButton = new JButton("Aggiorna");
        valutaProgettoButton = new JButton("Valuta Progetti");
        pubblicaProblemaButton = new JButton("Pubblica Problema");

        // Style buttons
        aggiornaHackathonButton.setPreferredSize(new Dimension(100, 35));
        valutaProgettoButton.setPreferredSize(new Dimension(130, 35));
        pubblicaProblemaButton.setPreferredSize(new Dimension(150, 35));

        // Initially disable action buttons
        valutaProgettoButton.setEnabled(false);
        pubblicaProblemaButton.setEnabled(false);

        hackathonAssegnatiButtonPanel.add(aggiornaHackathonButton);
        hackathonAssegnatiButtonPanel.add(valutaProgettoButton);
        hackathonAssegnatiButtonPanel.add(pubblicaProblemaButton);

        // Table
        hackathonAssegnatiTableModel = new HackathonAssegnatiTableModel();
        hackathonAssegnatiTable = new JTable(hackathonAssegnatiTableModel);
        setupHackathonAssegnatiTable();

        hackathonAssegnatiScrollPane = new JScrollPane(hackathonAssegnatiTable);
        hackathonAssegnatiScrollPane.setBorder(BorderFactory.createTitledBorder("Hackathon Assegnati"));

        hackathonAssegnatiPanel.add(hackathonAssegnatiButtonPanel, BorderLayout.NORTH);
        hackathonAssegnatiPanel.add(hackathonAssegnatiScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Hackathon Assegnati", hackathonAssegnatiPanel);
    }

    // endregion

    // region Table Setup

    private void setupInvitiTable() {
        invitiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invitiTable.setRowHeight(25);
        invitiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invitiTable.getSelectedRow();
                boolean hasSelection = selectedRow != -1;

                if (hasSelection) {
                    InvitoGiudice invito = inviti.get(selectedRow);
                    boolean isPending = invito.getStatoInvito() == StatoInvito.PENDING;
                    accettaInvitoButton.setEnabled(isPending);
                    rifiutaInvitoButton.setEnabled(isPending);
                } else {
                    accettaInvitoButton.setEnabled(false);
                    rifiutaInvitoButton.setEnabled(false);
                }
            }
        });

        // Double click to view details
        invitiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && invitiTable.getSelectedRow() != -1) {
                    accettaInvito();
                }
            }
        });

        // Custom renderer for status
        invitiTable.getColumnModel().getColumn(4).setCellRenderer(new StatoInvitoRenderer());
    }

    private void setupHackathonAssegnatiTable() {
        hackathonAssegnatiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonAssegnatiTable.setRowHeight(25);
        hackathonAssegnatiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = hackathonAssegnatiTable.getSelectedRow() != -1;
                valutaProgettoButton.setEnabled(hasSelection);
                pubblicaProblemaButton.setEnabled(hasSelection);
            }
        });

        // Double click to evaluate
        hackathonAssegnatiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && hackathonAssegnatiTable.getSelectedRow() != -1) {
                    valutaProgetti();
                }
            }
        });
    }

    // endregion

    // region Event Listeners

    private void setupInvitiEvents() {
        aggiornaInvitiButton.addActionListener(_ -> loadInviti());
        accettaInvitoButton.addActionListener(_ -> accettaInvito());
        rifiutaInvitoButton.addActionListener(_ -> rifiutaInvito());
    }

    private void setupHackathonAssegnatiEvents() {
        aggiornaHackathonButton.addActionListener(_ -> loadHackathonAssegnati());
        valutaProgettoButton.addActionListener(_ -> valutaProgetti());
        pubblicaProblemaButton.addActionListener(_ -> pubblicaProblema());
    }

    // endregion

    // region Data Loading

    private void loadInviti() {
        try {
            invitiTable.setEnabled(false);
            aggiornaInvitiButton.setText("Caricamento...");
            aggiornaInvitiButton.setEnabled(false);

            InvitoGiudiceListResponse response = controller.getInvitiGiudiceRicevuti();
            if (response.invitiGiudice() != null) {
                inviti.clear();
                inviti.addAll(response.invitiGiudice());
                invitiTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento degli inviti: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore durante il caricamento: " + e.getMessage());
        } finally {
            invitiTable.setEnabled(true);
            aggiornaInvitiButton.setText("Aggiorna");
            aggiornaInvitiButton.setEnabled(true);
        }
    }

    private void loadHackathonAssegnati() {
        try {
            hackathonAssegnatiTable.setEnabled(false);
            aggiornaHackathonButton.setText("Caricamento...");
            aggiornaHackathonButton.setEnabled(false);

            HackathonListResponse response = controller.getHackathonAssegnati();
            if (response.hackathons() != null) {
                hackathonAssegnati.clear();
                hackathonAssegnati.addAll(response.hackathons());
                hackathonAssegnatiTableModel.fireTableDataChanged();
            } else {
                showErrorMessage("Errore nel caricamento hackathon assegnati: " + response.message());
            }
        } catch (Exception e) {
            showErrorMessage("Errore durante il caricamento: " + e.getMessage());
        } finally {
            hackathonAssegnatiTable.setEnabled(true);
            aggiornaHackathonButton.setText("Aggiorna");
            aggiornaHackathonButton.setEnabled(true);
        }
    }

    // endregion

    // region Action Methods

    private void accettaInvito() {
        int selectedRow = invitiTable.getSelectedRow();
        if (selectedRow != -1) {
            InvitoGiudice invito = inviti.get(selectedRow);

            if (invito.getStatoInvito() != StatoInvito.PENDING) {
                showErrorMessage("Puoi accettare solo inviti in stato 'In Attesa'");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi accettare l'invito per questo hackathon?", "Conferma Accettazione", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = controller.accettaInvitoGiudice(invito.getInvitoId());
                    if (response.result()) {
                        showInfoMessage("Invito accettato con successo!");
                        loadInviti();
                        loadHackathonAssegnati();
                    } else {
                        showErrorMessage("Errore nell'accettazione: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nell'accettazione: " + e.getMessage());
                }
            }
        }
    }

    private void rifiutaInvito() {
        int selectedRow = invitiTable.getSelectedRow();
        if (selectedRow != -1) {
            InvitoGiudice invito = inviti.get(selectedRow);

            if (invito.getStatoInvito() != StatoInvito.PENDING) {
                showErrorMessage("Puoi rifiutare solo inviti in stato 'In Attesa'");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame, "Vuoi rifiutare l'invito per questo hackathon?", "Conferma Rifiuto", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    var response = controller.rifiutaInvitoGiudice(invito.getInvitoId());
                    if (response.result()) {
                        showInfoMessage("Invito rifiutato.");
                        loadInviti();
                    } else {
                        showErrorMessage("Errore nel rifiuto: " + response.message());
                    }
                } catch (Exception e) {
                    showErrorMessage("Errore nel rifiuto: " + e.getMessage());
                }
            }
        }
    }

    private void valutaProgetti() {
        int selectedRow = hackathonAssegnatiTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = hackathonAssegnati.get(selectedRow);
            controller.vaiAValutareProgetto(frame, hackathon.getHackathonId());
        }
    }

    private void pubblicaProblema() {
        int selectedRow = hackathonAssegnatiTable.getSelectedRow();
        if (selectedRow != -1) {
            Hackathon hackathon = hackathonAssegnati.get(selectedRow);
            controller.vaiAGestireProblemi(frame, hackathon.getHackathonId());
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

    private class InvitiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Hackathon", "Organizzatore", "Data Invito", "Sede", "Stato"};

        @Override
        public int getRowCount() {
            return inviti.size();
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
            InvitoGiudice invito = inviti.get(rowIndex);
            HackathonResponse hackathon = controller.getDettagliHackathon(invito.getHackathonId());
            return switch (columnIndex) {
                case 0 -> hackathon.hackathon() != null ? hackathon.hackathon().getTitolo() : "N/A";
                case 1 -> invito.getInvitante() != null ? invito.getInvitante().getNomeCompleto() : "N/A";
                case 2 -> invito.getDataInvito().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                case 3 -> hackathon.hackathon() != null ? hackathon.hackathon().getSede() : "N/A";
                case 4 -> invito.getStatoInvito().getDisplayName();
                default -> "";
            };
        }
    }

    private class HackathonAssegnatiTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Titolo", "Sede", "Data Inizio", "Data Fine", "Stato"};

        @Override
        public int getRowCount() {
            return hackathonAssegnati.size();
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
            Hackathon hackathon = hackathonAssegnati.get(rowIndex);
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

    // endregion

    // region Custom Renderers

    private class StatoInvitoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof String statoText) {
                switch (statoText) {
                    case "In Attesa" -> c.setForeground(Color.ORANGE);
                    case "Accettato" -> c.setForeground(Color.GREEN);
                    case "Rifiutato" -> c.setForeground(Color.RED);
                }
            }

            return c;
        }
    }

    // endregion
}