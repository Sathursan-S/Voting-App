package com.votingapp.server;

import com.votingapp.server.ui.CandidateManagePanel;
import com.votingapp.server.ui.VoteCounterPanel;
import com.votingapp.server.ui.VoterManagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class AdminUI extends JFrame {
    private final VoteManager voteManager;
    private final VotersManager voterManager;

    public AdminUI( VotersManager voterManager) {
        this.voteManager = VoteManager.getInstance();
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
        openVotingButton.addActionListener(this::openVoting);

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

    private void openVoting(ActionEvent actionEvent) {
        if(voteManager.isVotingOpen()) {
            JOptionPane.showMessageDialog(this, "Voting is already open", "Voting Open", JOptionPane.INFORMATION_MESSAGE);
        } else if(voteManager.getVoteBallot().getCandidates().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No candidates to vote for", "No Candidates", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                voteManager.startVoting();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createCandidateManagePanel() {
        CandidateManagePanel panel = new CandidateManagePanel();
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
        String message = JOptionPane.showInputDialog(this, "Enter message to broadcast:");
        if (message != null) {
            try {
                Server.broadcastMessage(message);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {
        final VotersManager voterManager = new VotersManager();

        EventQueue.invokeLater(() -> {
            AdminUI adminUI = new AdminUI(voterManager);
            adminUI.setVisible(true);
        });
        new Thread(() -> {
            int port = 4196;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number provided, using default port " + port);
                }
            }
            Server server = new Server(port, voterManager);
            server.start();
        }).start();
    }
}
