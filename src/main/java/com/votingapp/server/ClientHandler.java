package com.votingapp.server;

import com.votingapp.client.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int clientId;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Scanner scanner;

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
            boolean isAuthenticated = authenticateVoter(user);
            outputStream.writeBoolean(isAuthenticated);
            outputStream.flush();
            if (!isAuthenticated) {
                return;
            }

            while (true) {
                input = (String) inputStream.readObject();
                System.out.println("Client " + clientId + ": " + input);
                System.out.print("Server: ");
                output = scanner.nextLine();
                outputStream.writeObject(output);
                outputStream.flush();
            }
        } catch (Exception e) {
            System.out.println("Client " + clientId + " disconnected");
        } finally {
            try {
                socket.close();
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public boolean authenticateVoter(User user) {
        return user.getVoterId() == 1 && user.getPassword().equals("a");
    }
}
