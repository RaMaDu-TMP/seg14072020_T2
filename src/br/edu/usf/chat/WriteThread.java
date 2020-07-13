package br.edu.usf.chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread {
    private final Socket socket;
    private final ChatClient client;
    private final DataOutputStream outputStream;

    public WriteThread(Socket socket, ChatClient client) throws IOException {
        this.socket = socket;
        this.client = client;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        final Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        final String userName = scanner.next();

        client.setUserName(userName);

        try {
            outputStream.writeUTF(userName);

            String text;
            do {
                System.out.print("[" + userName + "]: ");
                text = scanner.next();
                outputStream.writeUTF(text);

            } while (!text.equals(":q"));

            try {
                socket.close();

            } catch (IOException ex) {
                System.out.println("Error writing to server: " + ex.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}