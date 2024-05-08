package com.votingapp.server;

import com.votingapp.client.User;
import com.votingapp.message.Message;
import com.votingapp.message.MessageType;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private final Socket socket;
    @Getter
    private final int clientId;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Scanner scanner;
    @Getter
    private boolean isAuthenticated;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            String input;
            String output;
            outputStream.writeObject("Welcome to the server, you are client " + clientId + " Authenticate yourself");
            outputStream.flush();
            User user = (User) inputStream.readObject();
            isAuthenticated = authenticateVoter(user);
            outputStream.writeBoolean(isAuthenticated);
            outputStream.flush();
            if (!isAuthenticated) {
                return;
            }
            AdminServer.addClient(this);
            AdminServer.broadcastMessage("Client " + clientId + " has joined the server");

            while (true) {
                Message message = receiveMessage();
                if (message == null) {
                    break;
                }
                switch (message.getMessageType()) {
                    case VOTING_RESPONSE:
                        int candidateId = (int) message.getMessage();
                        String msg;
                        if(AdminServer.setVote(candidateId)){
                            msg = "Your vote has been recorded";
                        }else {
                            msg = "Voting is closed \nYour vote has not been recorded";
                        }
                        outputStream.writeObject(Message.builder()
                                .message(msg)
                                .messageType(MessageType.INFO)
                                .build()
                        );
                        break;
                    case INFO:
                        System.out.println("INFO : " + message.getMessage());
                        message.setMessage(scanner.nextLine());
                        sendMessage(message);
                        break;
                    case BROADCAST:
                        System.out.println("Server Broadcast: " + message.getMessage());
                        break;
                    default:
                        System.out.println("Server says: " + message.getMessage());
                        break;
                }
//                sendMessage(Message.builder()
//                        .message("Enter your vote: ")
//                        .messageType(MessageType.INFO)
//                        .build()
//                );
//                input = (String) inputStream.readObject();
//                System.out.println("Client " + clientId + " voted: " + input);
            }
        } catch (Exception e) {
            System.out.println("Client " + clientId + " disconnected");
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error 1: " + e.getMessage());
            }
        }
    }

    public boolean authenticateVoter(User user) {
        return user.getVoterId() == 1 && user.getPassword().equals("a");
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("Error 4: " + e.getMessage());
        }
    }

    public Message receiveMessage() {
        try {
            return (Message) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error 3: " + e.getMessage());
            return null;
        }
    }
}
