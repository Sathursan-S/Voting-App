package com.votingapp.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Voters {
    public static void main(String[] args) throws IOException {
        String input, output, voterId, password;
        Scanner scanner = new Scanner(System.in);

        int serverPort = 8080;
        InetAddress serverAddress = InetAddress.getLocalHost();

        Socket socket = new Socket(serverAddress, serverPort);

        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress());
        try (socket; outputStream; inputStream) {
            String message = (String) inputStream.readObject();
            System.out.println("Server says: " + message);
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

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        while(true) {
            try {
                System.out.println("Enter your vote: ");
                input = scanner.nextLine();
                output = "VOTE: " + input;
                outputStream.writeObject(output);
                outputStream.flush();
                System.out.println("Sending vote to server: " + output);
                System.out.println("Server says: " + (String) inputStream.readObject());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
