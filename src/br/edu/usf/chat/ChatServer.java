package br.edu.usf.chat;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute(boolean ssl) {
        if (ssl) {

            try (final ServerSocket serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(8989)) {
                System.out.println("Chat Server is listening on port " + port);

                while (true) {
                    final Socket accept = serverSocket.accept();
                    System.out.println("New user connected");

                    UserThread newUser = new UserThread(accept, this);
                    userThreads.add(newUser);
                    newUser.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try (ServerSocket serverSocket = new ServerSocket(port)) {

                System.out.println("Chat Server is listening on port " + port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("New user connected");

                    UserThread newUser = new UserThread(socket, this);
                    userThreads.add(newUser);
                    newUser.start();

                }

            } catch (IOException ex) {
                System.out.println("Error in the server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }

        System.setProperty("javax.net.ssl.keyStore", "chat.store");
        System.setProperty("javax.net.ssl.keyStorePassword", "abcd1234");

        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute(true);
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not count the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}