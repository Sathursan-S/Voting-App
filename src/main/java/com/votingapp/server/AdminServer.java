package com.votingapp.server;

import com.votingapp.message.Message;
import com.votingapp.message.MessageType;
import com.votingapp.message.VoteBallet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class AdminServer {
    private static final int port = 8080;
    private static final List<ClientHandler> clients = new ArrayList<>();
    static boolean isLoginOpen = true;
    static boolean isVotingOpen = false;
    static VoteBallet voteBallet;

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("Admin Server");
        System.out.println("============");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        int clientId = 0;

        try {
            while (isLoginOpen) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                clientHandler.start();

                if(clientId==3){
                    startVoting();
                }

                clientId++;
            }

            while (isVotingOpen) {
                if(scanner.nextLine().equals("stop")) {
                    System.out.println("Voting stopped");
                    stopVoting();
                    break;
                }
            }

            broadcastMessage("Thank you for voting\nHave a nice day!");

        } finally {
            serverSocket.close();
        }
    }

    public static void addClient(ClientHandler client) {
        clients.add(client);
        System.out.println(clients);
    }

    public static void broadcastMessage(String msg) {
        for (ClientHandler client : clients) {
            client.sendMessage(Message.builder()
                    .message(msg)
                    .messageType(MessageType.BROADCAST)
                    .build()
            );
        }
    }

    public static void startVoting() {
        isLoginOpen = false;
        isVotingOpen = true;

        Map<Integer, String> candidates = new HashMap<>();
        System.out.println("Enter the no of candidates: ");
        int noOfCandidates = scanner.nextInt();
        System.out.println("Enter the candidates: ");
        for (int i = 0; i < noOfCandidates; i++) {
            String candidate = scanner.next();
            candidates.put(i+1, candidate);
        }
        voteBallet = VoteBallet.builder()
                .candidates(candidates)
                .votes(new HashMap<>())
                .build();
        voteBallet.initVotes();

        broadcastMessage("Voting has started");
        for (ClientHandler client : clients) {
            client.sendMessage(Message.builder()
                    .message(voteBallet.getCandidates())
                    .messageType(MessageType.VOTING_REQUEST)
                    .build()
            );
        }
    }

    public static void stopVoting() {
        isVotingOpen = false;

        int maxVotes = 0;
        int winner = 0;
        for (Map.Entry<Integer, Integer> entry : voteBallet.getVotes().entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        broadcastMessage("The winner is: " + voteBallet.getCandidates().get(winner));
        System.out.println("The winner is: " + voteBallet.getCandidates().get(winner));

        for (Map.Entry<Integer, Integer> entry : voteBallet.getVotes().entrySet()) {
            broadcastMessage(voteBallet.getCandidates().get(entry.getKey()) + " : " + entry.getValue());
            System.out.println(voteBallet.getCandidates().get(entry.getKey()) + " : " + entry.getValue());
        }
    }

    public static boolean setVote(int candidateId) {
        if(isVotingOpen) {
            voteBallet.addVote(candidateId);
            return true;
        }else {
            return false;
        }
    }
}
