package com.votingapp.server.ui;

import com.votingapp.client.User;
import com.votingapp.server.VotersManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VoterManagePanel extends JPanel {
    private JTextField voterIdField, voterPasswordField;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JTable votersTable;
    private DefaultTableModel tableModel;
    private VotersManager votersManager;

    public VoterManagePanel(VotersManager votersManager) {
        this.votersManager = votersManager;
        initializeComponents();
        setupLayout();
        setupTable();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        voterIdField = new JTextField();
        voterPasswordField = new JTextField();

        addButton = new JButton("Add Voter");
        addButton.addActionListener(this::addVoter);

        updateButton = new JButton("Update Voter");
        updateButton.addActionListener(this::updateVoter);

        deleteButton = new JButton("Delete Voter");
        deleteButton.addActionListener(this::deleteVoter);

        refreshButton = new JButton("Refresh Voters");
        refreshButton.addActionListener(this::refreshVoters);

        formPanel.add(new JLabel("Voter ID:"));
        formPanel.add(voterIdField);
        formPanel.add(new JLabel("Voter Password:"));
        formPanel.add(voterPasswordField);
        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(refreshButton);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Voter ID", "Password", "Logged-in"}, 0);
        votersTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(votersTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        // Customization for layout could be added here if needed
    }

    private void setupTable() {
        refreshVoters(null);
    }

    private void addVoter(ActionEvent event) {
        String voterId = voterIdField.getText();
        String password = voterPasswordField.getText();
        if (!voterId.isEmpty() && !password.isEmpty()) {
            votersManager.addVoter(new User(voterId, password, false));
            voterIdField.setText("");
            voterPasswordField.setText("");
            refreshVoters(null);
        }
    }

    private void updateVoter(ActionEvent event) {
        if (votersTable.getSelectedRow() != -1) {
            String voterId = (String) tableModel.getValueAt(votersTable.getSelectedRow(), 0);
            String password = (String) tableModel.getValueAt(votersTable.getSelectedRow(), 1);
            votersManager.updateVoter(new User(voterId, password, false));
            refreshVoters(null);
        }
    }

    private void deleteVoter(ActionEvent event) {
        if (votersTable.getSelectedRow() != -1) {
            String voterId = (String) tableModel.getValueAt(votersTable.getSelectedRow(), 0);
            votersManager.deleteVoter(voterId);
            refreshVoters(null);
        }
    }

    private void refreshVoters(ActionEvent event) {
        List<User> voters = votersManager.getVoters();
        tableModel.setRowCount(0);
        for (User voter : voters) {
            tableModel.addRow(new Object[]{voter.getVoterID(), voter.getPassword(), voter.isLogin()? "Yes" : "No"});
        }
    }


}
