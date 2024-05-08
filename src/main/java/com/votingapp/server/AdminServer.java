package com.votingapp.server;

import com.votingapp.message.Message;
import com.votingapp.message.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AdminServer {
    private static final int port = 8080;
    private static final List<ClientHandler> clients = new ArrayList<>();

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
}
