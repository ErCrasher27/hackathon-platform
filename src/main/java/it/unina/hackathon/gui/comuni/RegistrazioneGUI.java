package it.unina.hackathon.gui.comuni;

import it.unina.hackathon.controller.AuthenticationController;
import it.unina.hackathon.controller.Controller;
import it.unina.hackathon.controller.NavigationController;
import it.unina.hackathon.gui.GUIHandler;
import it.unina.hackathon.model.enums.TipoUtente;
import it.unina.hackathon.utils.UtenteResponse;

import javax.swing.*;

import static it.unina.hackathon.utils.UtilsUi.*;

public class RegistrazioneGUI implements GUIHandler {
    private final Controller controller;
    private final NavigationController navigationController;
    private final AuthenticationController authenticationController;
    private JFrame frame;
    private JPanel registrazionePnl;
    private JPanel headerPnl;
    private JLabel registerLbl;
    private JPanel formPnl;
    private JPanel usernamePnl;
    private JLabel usernameLbl;
    private JTextField usernameFld;
    private JPanel pwPnl;
    private JLabel pwLbl;
    private JPasswordField pwFld;
    private JPanel emailPnl;
    private JLabel emailLbl;
    private JTextField emailFld;
    private JPanel nomePnl;
    private JLabel nomeLbl;
    private JTextField nomeFld;
    private JPanel cognomePnl;
    private JLabel cognomeLbl;
    private JTextField cognomeFld;
    private JPanel TipoUtenteBtnGroup;
    private JRadioButton organizzatoreRb;
    private JRadioButton giudiceRb;
    private JRadioButton partecipanteRb;
    private JPanel confermaPwPnl;
    private JLabel confermaPwLbl;
    private JPasswordField confermaPwFld;
    private JPanel actionPnl;
    private JButton registerBtn;
    private JButton loginBtn;

    public RegistrazioneGUI() {
        this.controller = Controller.getInstance();
        this.navigationController = controller.getNavigationController();
        this.authenticationController = controller.getAuthController();
        initializeComponents();
        setupFrame();
        setupEventListeners();
        loadData();
    }

    @Override
    public void initializeComponents() {
        applyStdMargin(registrazionePnl);
        applyStyleTitleLbl(registerLbl);
        organizzatoreRb.setText(TipoUtente.ORGANIZZATORE.getDisplayName() + " - Posso creare e gestire hackathon");
        giudiceRb.setText(TipoUtente.GIUDICE.getDisplayName() + " - Posso valutare i team negli hackathon");
        partecipanteRb.setText(TipoUtente.PARTECIPANTE.getDisplayName() + " - Posso partecipare agli hackathon");
    }

    @Override
    public void setupFrame() {
        frame = new JFrame("Hackathon Platform - Registrazione");
        frame.setContentPane(registrazionePnl);
        applyStyleFrame(frame);
    }

    @Override
    public void setupEventListeners() {
        registerBtn.addActionListener(_ -> effettuaRegistrazione());
        loginBtn.addActionListener(_ -> navigationController.goToLogin(frame));
    }

    @Override
    public void loadData() {
    }

    private void effettuaRegistrazione() {
        String nome = nomeFld.getText();
        String cognome = cognomeFld.getText();
        String email = emailFld.getText();
        String username = usernameFld.getText();
        String password = new String(pwFld.getPassword());
        String confermaPassword = new String(confermaPwFld.getPassword());
        TipoUtente tipoUtente = getTipoUtenteSelezionato();

        UtenteResponse response = authenticationController.register(nome, cognome, email, username, password, confermaPassword, tipoUtente);

        if (response.utente() != null) {
            navigationController.goToLogin(frame);
            showSuccess(frame, response.message());
        } else {
            showError(frame, response.message());
        }
    }

    private TipoUtente getTipoUtenteSelezionato() {
        if (organizzatoreRb.isSelected()) return TipoUtente.ORGANIZZATORE;
        if (giudiceRb.isSelected()) return TipoUtente.GIUDICE;
        if (partecipanteRb.isSelected()) return TipoUtente.PARTECIPANTE;
        return null;
    }

    public JFrame getFrame() {
        return frame;
    }
}