package br.edu.usf.chat;

import java.io.*;
import java.net.Socket;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    //    private PrintWriter writer;
    DataOutputStream outputStream;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            OutputStream output = socket.getOutputStream();
            outputStream = new DataOutputStream(socket.getOutputStream());

            printUsers();

            String userName = inputStream.readUTF();
            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = inputStream.readUTF();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quitted.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        try {
            outputStream.writeUTF("Connected users: " + server.getUserNames());
            if (server.hasUsers()) {
            } else {
                outputStream.writeUTF("No other users connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}