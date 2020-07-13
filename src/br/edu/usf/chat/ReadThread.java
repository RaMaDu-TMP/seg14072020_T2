package br.edu.usf.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 *
 * @author www.codejava.net
 */
public class ReadThread extends Thread {
    private final ChatClient client;
    private final DataInputStream inputStream;

    public ReadThread(Socket socket, ChatClient client) throws IOException {
        this.client = client;
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public void run() {
        while (true) {
            try {
                String response = inputStream.readUTF();
                System.out.println("\n" + response);

                // prints the username after displaying the server's message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}