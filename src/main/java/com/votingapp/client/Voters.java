package com.votingapp.client;

import com.votingapp.message.Message;
import com.votingapp.message.MessageType;
import com.votingapp.message.VoteBallet;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Voters {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String input, output, voterId, password;
        Scanner scanner = new Scanner(System.in);

        int serverPort = 8080;
        InetAddress serverAddress = InetAddress.getLocalHost();

        Socket socket = new Socket(serverAddress, serverPort);

        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress());
        try (socket; outputStream; inputStream) {
            String msg = (String) inputStream.readObject();
            System.out.println("Server says: " + msg);
            System.out.println("Enter your voter ID: ");
            voterId = scanner.nextLine();
            System.out.println("Enter your password: ");
            password = scanner.nextLine();

            User user = new User(Integer.parseInt(voterId), password);
            outputStream.writeObject(user);
            outputStream.flush();

            if (inputStream.readBoolean()) {
                System.out.println("You are a registered voter");
            } else {
                System.out.println("You are not a registered voter");
                System.exit(0);
            }

            while(true) {
                Message message = (Message) inputStream.readObject();
                switch (message.getMessageType()) {
                    case VOTING_REQUEST:
                        Map<Integer,String> candidates = (Map<Integer, String>) message.getMessage();
                        System.out.println("Here are the candidates: ");
                        for (Map.Entry<Integer, String> entry : candidates.entrySet()) {
                            System.out.println(entry.getKey() + " : " + entry.getValue());
                        }
                        System.out.println("Enter the candidate ID you want to vote for: ");
                        int candidateId = Integer.parseInt(scanner.nextLine());
                        Message voteResponse = Message.builder()
                                .messageType(MessageType.VOTING_RESPONSE)
                                .message(candidateId)
                                .build();
                        outputStream.writeObject(voteResponse);
                        outputStream.flush();
                        break;
                    case INFO:
                        System.out.println("INFO : " + message.getMessage());
                        break;
                    case BROADCAST:
                        System.out.println("Server Broadcast: " + message.getMessage());
                        break;
                    default:
                        System.out.println("Server says: " + message.getMessage());
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error 2: " + e.getMessage());
            e.printStackTrace();
        } finally {
            inputStream.close();
            outputStream.close();
            socket.close();
            scanner.close();
        }

    }
}
