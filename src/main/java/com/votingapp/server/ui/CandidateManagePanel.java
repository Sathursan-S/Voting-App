package com.votingapp.server.ui;

import com.votingapp.common.VoteBallot;
import com.votingapp.server.VoteManager;
import com.votingapp.server.VotersManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CandidateManagePanel extends JPanel {
    private JTextField candidateNameField, candidateIdField;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JTable candidatesTable;
    private DefaultTableModel tableModel;
    private VoteManager voteManager;

    public CandidateManagePanel(VoteManager voteManager) {
        this.voteManager = voteManager;
        initializeComponents();
        setupLayout();
        setupTable();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // North panel for inputs and operations
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(0, 2, 5, 5));

        candidateIdField = new JTextField();
        candidateNameField = new JTextField();

        addButton = new JButton("Add Candidate");
        addButton.addActionListener(this::addCandidate);

        updateButton = new JButton("Update Candidate");
        updateButton.addActionListener(this::updateCandidate);

        deleteButton = new JButton("Delete Candidate");
        deleteButton.addActionListener(this::deleteCandidate);

        refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(this::refreshCandidates);

        northPanel.add(new JLabel("Candidate ID:"));
        northPanel.add(candidateIdField);
        northPanel.add(new JLabel("Candidate Name:"));
        northPanel.add(candidateNameField);
        northPanel.add(addButton);
        northPanel.add(updateButton);
        northPanel.add(deleteButton);
        northPanel.add(refreshButton);

        add(northPanel, BorderLayout.NORTH);

        // Center panel for candidate table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
        candidatesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(candidatesTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        // Add layout customization here if needed
    }

    private void setupTable() {
        refreshCandidates(null);
    }

    private void addCandidate(ActionEvent event) {
        Integer id = Integer.parseInt(candidateIdField.getText());
        String name = candidateNameField.getText();
        if (!name.isEmpty()) {
            voteManager.addCandidate(id, name);
            tableModel.addRow(new Object[]{id, name});
            candidateIdField.setText("");
            candidateNameField.setText("");
        }
    }

    private void updateCandidate(ActionEvent event) {
        String id = candidateIdField.getText();
        String name = candidateNameField.getText();
        if (!id.isEmpty() && !name.isEmpty()) {
            voteManager.updateCandidate(Integer.parseInt(id), name);
            candidateIdField.setText("");
            candidateNameField.setText("");
            refreshCandidates(null);
        }
    }

    private void deleteCandidate(ActionEvent event) {
        String id = candidateIdField.getText();
        if (!id.isEmpty()) {
            voteManager.deleteCandidate(Integer.parseInt(id));
            refreshCandidates(null);
        }else if (candidatesTable.getSelectedRow() != -1) {
            id = (String) tableModel.getValueAt(candidatesTable.getSelectedRow(), 0);
            voteManager.deleteCandidate(Integer.parseInt(id));
            refreshCandidates(null);
        }
    }

    private void refreshCandidates(ActionEvent event) {
        tableModel.setRowCount(0);
        VoteBallot ballot = voteManager.getVoteBallot();
        for (Integer id : ballot.getCandidates().keySet()) {
            tableModel.addRow(new Object[]{id, ballot.getCandidates().get(id)});
        }
    }

}
