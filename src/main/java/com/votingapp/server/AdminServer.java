package com.votingapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminServer {
    private static final int port = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("Admin Server");
        System.out.println("============");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);

        int clientId = 0;

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                clientHandler.start();

                clientId++;
            }
        } finally {
            serverSocket.close();
        }
    }
}
