package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.OrganizzatoreController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.ResponseResult;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class HomeOrganizzatoreGUI implements GUIHandler {

    private final Controller controller;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;
    private JFrame frame;

    private JPanel homeOrganizzatorePnl;
    private JTable tabellaGestioneHackathon;
    private JButton btnAggiungiHackathon;
    private JButton btnDettagli;
    private JButton btnAggiorna;

    private HackathonTableModel tableModel;
    private List<Hackathon> hackathonList;

    public HomeOrganizzatoreGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.organizzatoreController = controller.getOrganizzatoreController();
        this.hackathonList = new ArrayList<>();

        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }

    @Override
    public void initializeComponents() {
        // Panel superiore con titolo e pulsanti
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Ciao " + controller.getUtenteCorrente().getNome() + "!");
        applyStdMargin(homeOrganizzatorePnl);
        applyStyleTitleLbl(titleLabel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAggiungiHackathon = new JButton("+ Nuovo Hackathon");
        btnDettagli = new JButton("Dettagli/Modifica");
        btnAggiorna = new JButton("Aggiorna");

        btnDettagli.setEnabled(false); // Disabilitato fino a selezione

        buttonPanel.add(btnAggiorna);
        buttonPanel.add(btnDettagli);
        buttonPanel.add(btnAggiungiHackathon);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Tabella
        tableModel = new HackathonTableModel();
        tabellaGestioneHackathon = new JTable(tableModel);
        tabellaGestioneHackathon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaGestioneHackathon.setRowHeight(30);
        setupTable();

        JScrollPane scrollPane = new JScrollPane(tabellaGestioneHackathon);
        applyStdMargin(scrollPane);

        homeOrganizzatorePnl.add(topPanel, BorderLayout.NORTH);
        homeOrganizzatorePnl.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Home (Organizzatore)");
        frame.setContentPane(homeOrganizzatorePnl);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        // Listener per selezione tabella
        tabellaGestioneHackathon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnDettagli.setEnabled(tabellaGestioneHackathon.getSelectedRow() != -1);
            }
        });

        // Doppio click sulla tabella
        tabellaGestioneHackathon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tabellaGestioneHackathon.getSelectedRow();
                    if (selectedRow != -1) {
                        apriDettagliHackathon(selectedRow);
                    }
                }
            }
        });

        // Pulsante nuovo hackathon
        btnAggiungiHackathon.addActionListener(_ -> apriCreazioneHackathon());

        // Pulsante dettagli
        btnDettagli.addActionListener(_ -> {
            int selectedRow = tabellaGestioneHackathon.getSelectedRow();
            if (selectedRow != -1) {
                apriDettagliHackathon(selectedRow);
            }
        });

        // Pulsante aggiorna
        btnAggiorna.addActionListener(_ -> loadData());
    }

    @Override
    public void loadData() {
        try {
            hackathonList = organizzatoreController.getAllHackathonByOrganizzatore(controller.getIdUtenteCorrente());
            tableModel.setHackathonList(hackathonList);
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento degli hackathon: " + e.getMessage());
        }
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    private void setupTable() {
        tabellaGestioneHackathon.getColumnModel().getColumn(8).setCellRenderer(new StatusComboBoxRenderer());
        tabellaGestioneHackathon.getColumnModel().getColumn(8).setCellEditor(new StatusComboBoxEditor());

        tabellaGestioneHackathon.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabellaGestioneHackathon.getColumnModel().getColumn(1).setPreferredWidth(150); // Titolo
        tabellaGestioneHackathon.getColumnModel().getColumn(2).setPreferredWidth(200); // Descrizione
        tabellaGestioneHackathon.getColumnModel().getColumn(3).setPreferredWidth(100); // Sede
        tabellaGestioneHackathon.getColumnModel().getColumn(4).setPreferredWidth(120); // Data Inizio
        tabellaGestioneHackathon.getColumnModel().getColumn(5).setPreferredWidth(120); // Data Fine
        tabellaGestioneHackathon.getColumnModel().getColumn(6).setPreferredWidth(80);  // Max Iscritti
        tabellaGestioneHackathon.getColumnModel().getColumn(7).setPreferredWidth(80);  // Max Team
        tabellaGestioneHackathon.getColumnModel().getColumn(8).setPreferredWidth(150); // Status
    }

    private void apriDettagliHackathon(int row) {
        Hackathon selectedHackathon = hackathonList.get(row);
        // TODO: Aprire la GUI per i dettagli dell'hackathon
        // DettagliHackathonGUI dettagliGUI = new DettagliHackathonGUI(selectedHackathon.getHackathonId());
        // dettagliGUI.getFrame().setVisible(true);

        // Per ora mostra solo un messaggio
        JOptionPane.showMessageDialog(frame, "Apertura dettagli per Hackathon ID: " + selectedHackathon.getHackathonId(), "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void apriCreazioneHackathon() {
        navigationController.goToCreateHackathon(frame);
    }

    private void cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        ResponseResult result = organizzatoreController.cambiaStatoHackathon(hackathonId, nuovoStato);

        if (result.result()) {
            showSuccess(frame, "Stato hackathon aggiornato con successo!");
            loadData(); // Ricarica i dati
        } else {
            showError(frame, "Errore nell'aggiornamento dello stato: " + result.message());
        }
    }

    // Table Model personalizzato
    private class HackathonTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID", "Titolo", "Descrizione", "Sede", "Data Inizio", "Data Fine", "Max Iscritti", "Max Team", "Status"};
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        private List<Hackathon> hackathons = new ArrayList<>();

        public void setHackathonList(List<Hackathon> hackathons) {
            this.hackathons = hackathons != null ? new ArrayList<>(hackathons) : new ArrayList<>();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return hackathons.size();
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
        public Class<?> getColumnClass(int column) {
            if (column == 8) return HackathonStatus.class; // Status column
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 8; // Solo la colonna Status è editabile
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= hackathons.size()) return null;

            Hackathon hackathon = hackathons.get(row);
            return switch (column) {
                case 0 -> hackathon.getHackathonId();
                case 1 -> hackathon.getTitolo();
                case 2 -> hackathon.getDescrizione();
                case 3 -> hackathon.getSede();
                case 4 -> hackathon.getDataInizio() != null ? hackathon.getDataInizio().format(dateFormatter) : "";
                case 5 -> hackathon.getDataFine() != null ? hackathon.getDataFine().format(dateFormatter) : "";
                case 6 -> hackathon.getMaxIscritti();
                case 7 -> hackathon.getMaxDimensioneTeam();
                case 8 -> hackathon.getStatus();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 8 && value instanceof HackathonStatus nuovoStato) {
                Hackathon hackathon = hackathons.get(row);

                if (hackathon.getStatus() != nuovoStato) {
                    cambiaStatoHackathon(hackathon.getHackathonId(), nuovoStato);
                }
            }
        }
    }

    // Renderer per la combo box dello status
    private class StatusComboBoxRenderer extends JComboBox<HackathonStatus> implements TableCellRenderer {
        public StatusComboBoxRenderer() {
            super(HackathonStatus.values());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof HackathonStatus) {
                setSelectedItem(value);
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            return this;
        }
    }

    // Editor per la combo box dello status
    private class StatusComboBoxEditor extends DefaultCellEditor {
        public StatusComboBoxEditor() {
            super(new JComboBox<>(HackathonStatus.values()));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            JComboBox<HackathonStatus> comboBox = (JComboBox<HackathonStatus>) getComponent();
            if (value instanceof HackathonStatus) {
                comboBox.setSelectedItem(value);
            }

            return comboBox;
        }
    }
}