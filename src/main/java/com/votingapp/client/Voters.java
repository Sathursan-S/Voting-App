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

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        String message = inputStream.readUTF();
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        System.out.println("Connected to server at " + socket.getInetAddress().getHostAddress());
        try{
            System.out.println("Server says: " + message);
            System.out.println("Enter your voter ID: ");
            voterId = scanner.nextLine();
            outputStream.writeUTF(voterId);
            outputStream.flush();
            System.out.println("Enter your password: ");
            password = scanner.nextLine();
            outputStream.writeUTF(password);
            outputStream.flush();

            if(inputStream.readBoolean()){
                System.out.println("You are a registered voter");
            } else {
                System.out.println("You are not a registered voter");
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        while(true) {
            try {
                System.out.println("Enter your vote: ");
                input = scanner.nextLine();
                output = "VOTE: " + input;
                outputStream.writeUTF(output);
                outputStream.flush();
                System.out.println("Sending vote to server: " + output);
                System.out.println("Server says: " + inputStream.readUTF());
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
