package it.unina.hackathon.gui;

import javax.swing.*;

public interface GUIHandler {

    void initializeComponents();

    void setupFrame();

    void setupEventListeners();

    void loadData();

    JFrame getFrame();
}
