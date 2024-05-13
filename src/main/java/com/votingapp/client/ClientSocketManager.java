package com.votingapp.client;

import com.votingapp.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ClientSocketManager {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private final String serverAddress;
    private final int serverPort;

    private final BlockingQueue<Message> outgoingMessages = new LinkedBlockingQueue<>();
    private final Consumer<Message> onMessageReceived;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private boolean running = true;

    public ClientSocketManager(String serverAddress, int serverPort, Consumer<Message> onMessageReceived) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.onMessageReceived = onMessageReceived;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        startMessageListener();
        startMessageSender();
    }

    private void startMessageListener() {
        Runnable listenerTask = () -> {
            while (running) {
                try {
                    Message message = (Message) inputStream.readObject();
                    onMessageReceived.accept(message);
                    System.out.println("Received message from server: " + message.getMessageType());
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error reading message from server: " + e.getMessage());
                    running = false;
                }
            }
            closeConnection();
        };
        executorService.submit(listenerTask);
    }

    private void startMessageSender() {
        Runnable senderTask = () -> {
            while (running) {
                try {
                    Message message = outgoingMessages.take();
                    outputStream.writeObject(message);
                    outputStream.flush();
                    System.out.println("Sent message to server: " + message.getMessageType());
                } catch (InterruptedException | IOException e) {
                    System.err.println("Error sending message to server: " + e.getMessage());
                }
            }
        };
        executorService.submit(senderTask);
    }

    public void sendMessage(Message message) {
        try {
            outgoingMessages.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public void closeConnection() {
        running = false;
        executorService.shutdownNow();
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}
