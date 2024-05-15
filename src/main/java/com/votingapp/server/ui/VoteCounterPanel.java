package com.votingapp.server.ui;

import com.votingapp.server.VoteManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class VoteCounterPanel extends JPanel {
    private JButton startCountingButton, broadcastResultsButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private VoteManager voteManager;

    public VoteCounterPanel(VoteManager voteManager) {
        this.voteManager = voteManager;
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
        if(!voteManager.isVotingOpen()) {
            voteManager.startCounting();
            ConcurrentHashMap<Integer, Integer> results = voteManager.getResults();
            tableModel.setRowCount(0);
            results.forEach((candidateId, votes) -> {
                tableModel.addRow(new Object[]{candidateId, votes});
            });        } else {
            JOptionPane.showMessageDialog(this, "Voting is ongoing!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void broadcastResults(ActionEvent event) {
        if(voteManager.isVotingOpen()) {
            JOptionPane.showMessageDialog(this, "Voting is not yet closed!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
          voteManager.reportResults();
        }
    }


}
