package br.edu.usf.chat;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute(boolean ssl) {
        if (ssl) {
            try {
                SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(port);

                System.out.println("Chat Server is listening on port " + port);

                while (true) {
                    final Socket accept = sslServerSocket.accept();
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

        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.keyStore", "group_chat_key_store");
        System.setProperty("javax.net.ssl.keyStorePassword", "abcd1234");

        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        server.execute(true);
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        System.out.println("Message recieved: " + message);

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