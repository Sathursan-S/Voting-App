package com.votingapp.server;

import com.votingapp.server.ui.CandidateManagePanel;
import com.votingapp.server.ui.VoteCounterPanel;
import com.votingapp.server.ui.VoterManagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminUI extends JFrame {
    private VoteManager voteManager;
    private VotersManager voterManager;

    public AdminUI(VoteManager voteManager, VotersManager voterManager) {
        this.voteManager = voteManager;
        this.voterManager = voterManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Voting System Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dashboardPanel = createDashboardPanel();
        JPanel candidateManagePanel = createCandidateManagePanel();
        JPanel voteCounterPanel = createVoteCounterPanel();
        JPanel votersManagerPanel = createVotersManagerPanel();

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Manage Candidates", candidateManagePanel);
        tabbedPane.addTab("Vote Counter", voteCounterPanel);
        tabbedPane.addTab("Manage Voters", votersManagerPanel);

        add(tabbedPane);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); // Simple grid layout

        JButton openVotingButton = new JButton("Open Voting");
        openVotingButton.addActionListener(e -> voteManager.startVoting());

        JButton closeVotingButton = new JButton("Close Voting");
        closeVotingButton.addActionListener(e -> voteManager.stopVoting());

        JButton broadcastButton = new JButton("Broadcast Message");
        broadcastButton.addActionListener(this::broadcastMessage);

        JButton stopServerButton = new JButton("Stop Server");
        stopServerButton.addActionListener(e -> System.exit(0)); // Stops the server and exits the app

        panel.add(openVotingButton);
        panel.add(closeVotingButton);
        panel.add(broadcastButton);
        panel.add(stopServerButton);

        return panel;
    }

    private JPanel createCandidateManagePanel() {
        CandidateManagePanel panel = new CandidateManagePanel(voteManager);
        return panel;
    }

    private JPanel createVoteCounterPanel() {
        VoteCounterPanel panel = new VoteCounterPanel();
        return panel;
    }

    private JPanel createVotersManagerPanel() {
        VoterManagePanel panel = new VoterManagePanel(voterManager);
        return panel;
    }

    private void broadcastMessage(ActionEvent e) {
        String message = JOptionPane.showInputDialog(this, "Enter broadcast message:");
        if (message != null && !message.isEmpty()) {
            System.out.println("Broadcasting message: " + message); // Replace with actual broadcast logic
        }
    }

    public static void main(String[] args) {
        VoteManager voteManager = new VoteManager();
        VotersManager voterManager = new VotersManager();
        //run server in new thread
        EventQueue.invokeLater(() -> {
            AdminUI adminUI = new AdminUI(voteManager, voterManager);
            adminUI.setVisible(true);
        });
        new Thread(() -> {
            int port = 8080; // Default port for the server
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]); // Allow port number to be passed as an argument
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number provided, using default port " + port);
                }
            }
            Server server = new Server(port, voteManager, voterManager);
            server.start();
        }).start();
    }
}
