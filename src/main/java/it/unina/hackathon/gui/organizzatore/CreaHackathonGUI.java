package it.unina.hackathon.gui.organizzatore;

import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.controller.OrganizzatoreController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.utils.HackathonResponse;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static it.unina.hackathon.utils.UtilsUi.*;

public class CreaHackathonGUI implements GUIHandler {
    private final Controller controller;
    private final NavigationController navigationController;
    private final OrganizzatoreController organizzatoreController;
    private JFrame frame;
    private JLabel titleLbl;
    private JTextField titleFld;
    private JTextArea descriptionFld;
    private JPanel titlePnl;
    private JPanel descriptionPnl;
    private JPanel sitePnl;
    private JLabel siteLbl;
    private JTextField siteFld;
    private JPanel startDatePnl;
    private JLabel startDateLbl;
    private JFormattedTextField startDateFld;
    private JLabel descriptionLbl;
    private JPanel endDatePnl;
    private JLabel endDateLbl;
    private JFormattedTextField endDateFld;
    private JPanel maxPnl;
    private JPanel maxSubscriptionPnl;
    private JPanel maxMemberForTeamSubPnl;
    private JLabel maxSubscriptionLbl;
    private JLabel maxMemberForTeamLbl;
    private JButton cancelBtn;
    private JButton createBtn;
    private JPanel creaHackathonPnl;
    private JPanel actionPnl;
    private JPanel headerPnl;
    private JLabel createLbl;
    private JPanel formPnl;
    private JSpinner maxSubscriptionSpn;
    private JSpinner maxMemberForTeamSpn;
    private JFormattedTextField startTimeFld;
    private JFormattedTextField endTimeFld;

    public CreaHackathonGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.organizzatoreController = controller.getOrganizzatoreController();
        setupFrame();
        setupDateFields();
        setupNumberFields();
        setupEventListeners();
    }

    @Override
    public void initializeComponents() {

    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Crea Hackathon");
        frame.setContentPane(creaHackathonPnl);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        createBtn.addActionListener(_ -> creaHackathon());
        cancelBtn.addActionListener(_ -> navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente()));
    }

    @Override
    public void loadData() {
    }

    private void creaHackathon() {
        try {
            String title = titleFld.getText().trim();
            String description = descriptionFld.getText().trim();
            String site = siteFld.getText().trim();

            LocalDateTime startDate = parseDateTime(startDateFld.getText(), startTimeFld.getText());
            LocalDateTime endDate = parseDateTime(endDateFld.getText(), endTimeFld.getText());

            int maxSubscription = (Integer) maxSubscriptionSpn.getValue();
            int maxMemberForTeam = (Integer) maxMemberForTeamSpn.getValue();

            HackathonResponse response = organizzatoreController.creaHackathon(title, description, site, startDate, endDate, maxSubscription, maxMemberForTeam);

            if (response.hackathon() != null) {
                showSuccess(frame, response.message());
                navigationController.goToHome(frame, controller.getUtenteCorrente().getTipoUtente());
            } else {
                showError(frame, response.message());
            }

        } catch (DateTimeParseException e) {
            showError(frame, "Formato data/ora non valido. Usare dd/MM/yyyy per la data e HH:mm per l'ora");
        } catch (Exception e) {
            showError(frame, "Errore imprevisto: " + e.getMessage());
        }
    }

    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(dateStr.trim() + " " + timeStr.trim(), formatter);
        } catch (Exception e) {
            throw new DateTimeParseException("Formato data/ora non valido", dateStr + " " + timeStr, 0);
        }
    }

    private void setupNumberFields() {
        maxSubscriptionSpn = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        maxMemberForTeamSpn = new JSpinner(new SpinnerNumberModel(4, 1, 10, 1));
    }

    private void setupDateFields() {
        try {
            // Crea i formatter
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');

            MaskFormatter timeFormatter = new MaskFormatter("##:##");
            timeFormatter.setPlaceholderCharacter('_');

            // Applica ai componenti esistenti (NON ricrearli!)
            startDateFld.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
            endDateFld.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
            startTimeFld.setFormatterFactory(new DefaultFormatterFactory(timeFormatter));
            endTimeFld.setFormatterFactory(new DefaultFormatterFactory(timeFormatter));

            // Imposta valori di esempio
            startDateFld.setValue("01/07/2025");
            startTimeFld.setValue("09:00");
            endDateFld.setValue("02/07/2025");
            endTimeFld.setValue("18:00");

            // Tooltip
            startDateFld.setToolTipText("Formato: dd/MM/yyyy (es: 15/06/2024)");
            startTimeFld.setToolTipText("Formato: HH:mm (es: 09:30)");
            endDateFld.setToolTipText("Formato: dd/MM/yyyy (es: 16/06/2024)");
            endTimeFld.setToolTipText("Formato: HH:mm (es: 18:00)");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }
}