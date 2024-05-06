package com.votingapp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int clientId;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Scanner scanner;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
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
            outputStream.writeUTF("Welcome to the server, you are client " + clientId + " Authenticate yourself");
            outputStream.flush();
            String voterId = inputStream.readUTF();
            String password = inputStream.readUTF();
            boolean isAuthenticated = authenticateVoter(voterId, password);
            outputStream.writeBoolean(isAuthenticated);
            if (!isAuthenticated) {
                return;
            }

            while (true) {
                input = inputStream.readUTF();
                System.out.println("Client " + clientId + ": " + input);
                System.out.println("Server: ");
                output = scanner.nextLine();
                outputStream.writeUTF(output);
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
                e.printStackTrace();
            }
        }
    }

    public boolean authenticateVoter(String voterId, String password) {
        return voterId.equals("1") && password.equals("a");
    }
}
