package com.votingapp.server;

import com.votingapp.common.Message;
import com.votingapp.common.MessageType;
import com.votingapp.common.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler extends Thread {
    private final Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final VoteManager voteManager;
    private final AtomicBoolean authenticated = new AtomicBoolean(false);
    private final VotersManager votersManager;
    private String voterId;

    public ClientHandler(Socket socket, VotersManager votersManager) {
        this.socket = socket;
        this.voteManager = VoteManager.getInstance();
        this.votersManager = votersManager;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
            closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    processMessage(message);
                } else {
                    break;  // End the loop if message is null
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client message: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void processMessage(Message message) throws IOException {
        switch (message.getMessageType()) {
            case LOGIN_REQUEST:
                handleLogin((User) message.getMessage());
                break;
            case VOTING_RESPONSE:
                if (authenticated.get()) {
                    handleVote(Integer.parseInt(message.getMessage().toString()));
                } else {
                    sendMessage(new Message(MessageType.ERROR, "Authentication required."));
                }
                break;
            default:
                sendMessage(new Message(MessageType.ERROR, "Invalid message type."));
                break;
        }
    }

    private void handleLogin(User user) throws IOException {
        if (authenticate(user)) {
            authenticated.set(true);
            voterId = user.getVoterID();
            sendMessage(new Message(MessageType.LOGIN_RESPONSE, authenticated.get()));
        } else {
            sendMessage(new Message(MessageType.LOGIN_RESPONSE, authenticated.get()));
        }
    }

    private boolean authenticate(User user) {
        return votersManager.authenticateVoter(user);
    }

    private void handleVote(int candidateId) throws IOException {
        if (voteManager.castVote(candidateId)) {
            sendMessage(new Message(MessageType.VOTE_SUBMIT, true));
        } else {
            sendMessage(new Message(MessageType.VOTE_SUBMIT, false));
        }
    }

    void sendMessage(Message message) throws IOException {
        if (!socket.isClosed()) {
            outputStream.writeObject(message);
            outputStream.flush();
        } else {
            System.err.println("Cannot send message, socket is closed");
        }
    }

    private void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            votersManager.removeClient(voterId);
            Server.removeClientHandler(this);
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

}
