package com.votingapp.server.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class VoteCounterPanel extends JPanel {
    private JButton startCountingButton, broadcastResultsButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public VoteCounterPanel() {
        initializeComponents();
        setupLayout();
        setupTable();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        startCountingButton = new JButton("Start Vote Counting");
        startCountingButton.addActionListener(this::startVoteCounting);

        broadcastResultsButton = new JButton("Broadcast Results");
        broadcastResultsButton.addActionListener(this::broadcastResults);

        JPanel northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.add(startCountingButton);
        northPanel.add(broadcastResultsButton);

        tableModel = new DefaultTableModel(new Object[]{"Candidate ID", "Votes"}, 0);
        resultsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        // Customization for layout could be added here if needed
    }

    private void setupTable() {
        // Initialize the table or refresh it here as needed
    }

    private void startVoteCounting(ActionEvent event) {

        // After starting counting, the server should send back results which should update the table
    }

    private void broadcastResults(ActionEvent event) {

        // This would trigger the server to send the results to all clients
    }


}
