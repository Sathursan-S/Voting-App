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
                sendMessage(Message.builder()
                        .message("Enter your vote: ")
                        .messageType(MessageType.INFO)
                        .build()
                );
                input = (String) inputStream.readObject();
                System.out.println("Client " + clientId + " voted: " + input);
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
}
