package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.HackathonController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.Hackathon;
import it.unina.hackathon.model.enums.HackathonStatus;
import it.unina.hackathon.utils.responses.HackathonListResponse;
import it.unina.hackathon.utils.responses.base.ResponseResult;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static it.unina.hackathon.utils.UtilsUi.*;

public class HomeOrganizzatoreGUI implements GUIHandler {

    //region Fields
    private final HackathonController controller;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel buttonPanel;

    private JLabel welcomeLabel;
    private JButton logoutButton;

    private JTable hackathonTable;
    private JScrollPane tableScrollPane;
    private HackathonTableModel tableModel;

    private JButton nuovoHackathonButton;
    private JButton dettagliButton;
    private JButton aggiornaButton;

    private List<Hackathon> hackathonList;
    //endregion

    //region Constructor
    public HomeOrganizzatoreGUI() {
        this.controller = HackathonController.getInstance();
        this.hackathonList = new ArrayList<>();

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
        createButtonPanel();
        createTablePanel();

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Home Organizzatore");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        logoutButton.addActionListener(_ -> {
            controller.effettuaLogout();
            controller.vaiAlLogin(frame);
        });

        hackathonTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                dettagliButton.setEnabled(hackathonTable.getSelectedRow() != -1);
            }
        });

        hackathonTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && hackathonTable.getSelectedRow() != -1) {
                    apriDettagliHackathon();
                }
            }
        });

        nuovoHackathonButton.addActionListener(_ -> controller.vaiACreareHackathon(frame));
        dettagliButton.addActionListener(_ -> apriDettagliHackathon());
        aggiornaButton.addActionListener(_ -> loadData());
    }

    @Override
    public void loadData() {
        try {
            hackathonTable.setEnabled(false);
            aggiornaButton.setText("Caricamento...");
            aggiornaButton.setEnabled(false);

            HackathonListResponse response = controller.getHackathonOrganizzatore();

            if (response.hackathons() != null) {
                hackathonList = response.hackathons();
                tableModel.setHackathonList(hackathonList);
                frame.setTitle("Hackathon Platform - Home Organizzatore (" + hackathonList.size() + " hackathon)");
            } else {
                showError(frame, "Errore nel caricamento degli hackathon: " + response.message());
                hackathonList.clear();
                tableModel.setHackathonList(hackathonList);
            }
        } catch (Exception e) {
            showError(frame, "Errore nel caricamento degli hackathon: " + e.getMessage());
            hackathonList.clear();
            tableModel.setHackathonList(hackathonList);
        } finally {
            hackathonTable.setEnabled(true);
            aggiornaButton.setText("Aggiorna");
            aggiornaButton.setEnabled(true);
        }
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

    private void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        aggiornaButton = new JButton("Aggiorna");
        dettagliButton = new JButton("Dettagli");
        nuovoHackathonButton = new JButton("+ Nuovo Hackathon");

        aggiornaButton.setPreferredSize(new Dimension(100, 35));
        dettagliButton.setPreferredSize(new Dimension(150, 35));
        nuovoHackathonButton.setPreferredSize(new Dimension(170, 35));

        dettagliButton.setEnabled(false);

        buttonPanel.add(aggiornaButton);
        buttonPanel.add(dettagliButton);
        buttonPanel.add(nuovoHackathonButton);
    }

    private void createTablePanel() {
        tableModel = new HackathonTableModel();
        hackathonTable = new JTable(tableModel);
        setupTable();

        tableScrollPane = new JScrollPane(hackathonTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("I Tuoi Hackathon"));
    }

    private void setupTable() {
        hackathonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonTable.setRowHeight(35);
        hackathonTable.getTableHeader().setReorderingAllowed(false);

        hackathonTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        hackathonTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        hackathonTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        hackathonTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        hackathonTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        hackathonTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        hackathonTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        hackathonTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        hackathonTable.getColumnModel().getColumn(8).setPreferredWidth(150);

        hackathonTable.getColumnModel().getColumn(8).setCellRenderer(new StatusComboBoxRenderer());
        hackathonTable.getColumnModel().getColumn(8).setCellEditor(new StatusComboBoxEditor());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        hackathonTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        hackathonTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        hackathonTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
    }

    private void apriDettagliHackathon() {
        int selectedRow = hackathonTable.getSelectedRow();
        if (selectedRow == -1) {
            showError(frame, "Seleziona un hackathon!");
            return;
        }

        Hackathon selectedHackathon = hackathonList.get(selectedRow);
        controller.vaiAGestireHackathon(frame, selectedHackathon.getHackathonId());
    }

    private void cambiaStatoHackathon(int hackathonId, HackathonStatus nuovoStato) {
        try {
            ResponseResult result = controller.modificaStatoHackathon(hackathonId, nuovoStato);

            if (result.result()) {
                showSuccess(frame, "Stato hackathon aggiornato con successo!");
                loadData();
            } else {
                showError(frame, "Errore nell'aggiornamento dello stato: " + result.message());
                loadData();
            }
        } catch (Exception e) {
            showError(frame, "Errore nell'aggiornamento dello stato: " + e.getMessage());
            loadData();
        }
    }
    //endregion

    //region Private Classes
    private static class StatusComboBoxRenderer extends JComboBox<HackathonStatus> implements TableCellRenderer {
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

    private static class StatusComboBoxEditor extends DefaultCellEditor {
        public StatusComboBoxEditor() {
            super(new JComboBox<>(HackathonStatus.values()));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            @SuppressWarnings("unchecked") JComboBox<HackathonStatus> comboBox = (JComboBox<HackathonStatus>) getComponent();
            if (value instanceof HackathonStatus) {
                comboBox.setSelectedItem(value);
            }

            return comboBox;
        }
    }

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
            if (column == 8) return HackathonStatus.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 8;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (row >= hackathons.size()) return null;

            Hackathon hackathon = hackathons.get(row);
            return switch (column) {
                case 0 -> hackathon.getHackathonId();
                case 1 -> hackathon.getTitolo();
                case 2 -> hackathon.getDescrizione() != null ? hackathon.getDescrizione() : "";
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
                    SwingUtilities.invokeLater(() -> cambiaStatoHackathon(hackathon.getHackathonId(), nuovoStato));
                }
            }
        }
    }
    //endregion
}