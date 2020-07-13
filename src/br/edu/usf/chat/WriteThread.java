package br.edu.usf.chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class WriteThread extends Thread {
    //    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
//            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        final Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        final String userName = scanner.next();
        System.out.println();

        client.setUserName(userName);
//        writer.println(userName);

        String text;

        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(userName);

            do {
                System.out.print("[" + userName + "]: ");
                text = scanner.next();
                System.out.println();
                outputStream.writeUTF(text);

            } while (!text.equals("bye"));

            try {
                socket.close();
            } catch (IOException ex) {

                System.out.println("Error writing to server: " + ex.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        do {
//            System.out.print("[" + userName + "]: ");
//            text = scanner.next();
//            System.out.println();
//            writer.println(text);
//
//        } while (!text.equals("bye"));
//
//        try {
//            socket.close();
//        } catch (IOException ex) {
//
//            System.out.println("Error writing to server: " + ex.getMessage());
//        }
    }
}