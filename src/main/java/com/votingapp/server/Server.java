package com.votingapp.server;

import com.votingapp.common.Message;
import com.votingapp.common.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private final int port;
    private final ExecutorService pool;
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private final VotersManager votersManager;

    public Server(int port, VotersManager votersManager) {
        this.port = port;
        this.pool = Executors.newCachedThreadPool();
        this.votersManager = votersManager;
    }

    public static void broadcastMessage(Message electionResults) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(electionResults);
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, votersManager);
                    clientHandlers.add(clientHandler);
                    pool.execute(clientHandler);
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port + ": " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            pool.shutdown();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.err.println("Error closing the server: " + e.getMessage());
        }
    }

    public static void broadcastMessage(String message) throws IOException {
        Message broadcastMessage = new Message(MessageType.BROADCAST, message);
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(broadcastMessage);
        }
    }

    public static void startVoting(Message message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    public static void removeClientHandler(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}