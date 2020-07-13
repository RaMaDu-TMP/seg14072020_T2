package br.edu.usf.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserThread extends Thread {
    private final Socket socket;
    private final ChatServer server;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;

    public UserThread(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public void run() {
        try {
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

            } while (!clientMessage.equals(":q"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quited.";
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
            if (!server.hasUsers()) {
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