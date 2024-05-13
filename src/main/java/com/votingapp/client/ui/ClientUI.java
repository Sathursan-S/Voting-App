package com.votingapp.client.ui;

import com.votingapp.client.ClientSocketManager;
import com.votingapp.client.User;
import com.votingapp.common.Message;
import com.votingapp.common.MessageType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);
    private ClientSocketManager socketManager;
    private JTextArea messagesArea;
    private ConcurrentHashMap<Integer, String> candidates; // Assuming this is updated based on messages received

    public ClientUI(String serverAddress, int serverPort) {
        initializeUI();
        setupNetworking(serverAddress, serverPort);
    }

    private void initializeUI() {
        setTitle("Voting Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panels
        JPanel loginPanel = createLoginPanel();
        JPanel dashboardPanel = createDashboardPanel();
        JPanel votingPanel = createVotingPanel();

        cards.add(loginPanel, "Login");
        cards.add(dashboardPanel, "Dashboard");
        cards.add(votingPanel, "Voting");

        getContentPane().add(cards);
        cardLayout.show(cards, "Login");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JTextField voterIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> attemptLogin(voterIdField.getText(), new String(passwordField.getPassword())));

        panel.add(voterIdField);
        panel.add(passwordField);
        panel.add(loginButton);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        messagesArea = new JTextArea(10, 40);
        messagesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        messagesArea.append("Welcome to the voting system!\n");
        messagesArea.append("Please wait for vote.\n");
        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1)); // Adjust layout as needed
        JButton submitButton = new JButton("Submit Vote");
        ButtonGroup group = new ButtonGroup();
        if (candidates != null) {
            for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
                JRadioButton button = new JRadioButton(entry.getValue());
                button.setActionCommand(String.valueOf(entry.getKey()));
                group.add(button);
                panel.add(button);
            }
        }
        submitButton.addActionListener(e -> {
            String selectedCandidateId = group.getSelection().getActionCommand();
            submitVote(Integer.parseInt(selectedCandidateId));
            submitButton.setText("Vote Submitted");
            submitButton.setEnabled(false);
        });

        panel.add(submitButton);
        return panel;
    }

    private void setupNetworking(String serverAddress, int serverPort) {
        socketManager = new ClientSocketManager(serverAddress, serverPort, this::handleServerMessage);
        try {
            socketManager.connect();
            System.out.println("Connected to server.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to the server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptLogin(String voterId, String password) {
        Message loginMessage = new Message(MessageType.LOGIN_REQUEST, new User(voterId, password, false));
        socketManager.sendMessage(loginMessage);
    }

    private void submitVote(int candidateId) {
        Message voteMessage = new Message(MessageType.VOTING_RESPONSE, candidateId);
        socketManager.sendMessage(voteMessage);
    }

    private void handleServerMessage(Message message) {
        SwingUtilities.invokeLater(() -> {
            switch (message.getMessageType()) {
                case LOGIN_RESPONSE:
                    boolean loginSuccess = (boolean) message.getMessage();
                    if (loginSuccess) {
                        cardLayout.show(cards, "Dashboard");
                    } else {
                        JOptionPane.showMessageDialog(this, "Login failed. Please try again.",
                                "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case VOTING_UPDATE:
                    System.out.println("Received voting update: ");
                    candidates = (ConcurrentHashMap<Integer, String>) message.getMessage();
                    cardLayout.show(cards, "Voting");
                    break;
                case VOTING_REQUEST:
                    System.out.println("Received voting request: ");
                    candidates = (ConcurrentHashMap<Integer, String>) message.getMessage();
                    System.out.println("Candidates: " + candidates);
                    updateVotingPanel();
                    cardLayout.show(cards, "Voting");
                    break;
                case VOTING_RESULTS:
                    System.out.println("Received voting results: ");
                    cardLayout.show(cards, "Dashboard");
                    String results = (String) message.getMessage();
                    messagesArea.append("\t\t RESULT \n" + results + "\n");
                    break;
                case BROADCAST:
                    String broadcastMessage = (String) message.getMessage();
                    System.out.println("Received broadcast: " + broadcastMessage);
                    messagesArea.append("Server Broadcast: \n\t" + broadcastMessage + "\n");
                    break;
                case INFO:
                    messagesArea.append("Server Info: \n\t\t" + message.getMessage() + "\n");
                    break;
                case ERROR:
                    JOptionPane.showMessageDialog(this, "Server Error: " + message.getMessage(),
                            "Server Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case VOTE_SUBMIT:
                    cardLayout.show(cards, "Dashboard");
                    boolean voteSubmitted = (boolean) message.getMessage();
                    if (voteSubmitted) {
                        JOptionPane.showMessageDialog(this, "Vote submitted successfully.",
                                "Vote Submitted", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Vote submission failed. \nVoting may be closed.",
                                "Vote Submission Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    System.out.println("Unhandled message type: " + message.getMessageType());
            }
        });
    }

    private void updateVotingPanel() {
        JPanel votingPanel = (JPanel) cards.getComponent(2); // Assuming the voting panel is the third panel
        votingPanel.removeAll(); // Remove all existing components

        JButton submitButton = new JButton("Submit Vote");
        ButtonGroup group = new ButtonGroup();
        if (candidates != null) {
            for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
                JRadioButton button = new JRadioButton(entry.getValue());
                button.setActionCommand(String.valueOf(entry.getKey()));
                group.add(button);
                votingPanel.add(button);
            }
        }
        submitButton.addActionListener(e -> {
            String selectedCandidateId = group.getSelection().getActionCommand();
            submitVote(Integer.parseInt(selectedCandidateId));
        });

        votingPanel.add(submitButton);
        votingPanel.revalidate();
        votingPanel.repaint();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ClientUI clientUI = new ClientUI("localhost", 8080);
            clientUI.setVisible(true);
        });
    }
}
