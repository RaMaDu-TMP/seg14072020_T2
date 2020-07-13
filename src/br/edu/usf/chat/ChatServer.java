package br.edu.usf.chat;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private final Set<String> userNames = new HashSet<>();
    private final Set<UserThread> userThreads = new HashSet<>();

    public void execute(final int port, final boolean ssl) {
        final ServerSocket serverSocket;
        try {
            if (ssl) {
                serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port);
            } else {
                serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            }

            System.out.println("Chat Server is listening on port " + port);

            do {
                final Socket accept = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(accept, this);
                userThreads.add(newUser);
                newUser.start();

            } while (!serverSocket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number> <ssl>[optional]");
            System.exit(0);
        }

        //noinspection deprecation
        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.keyStore", "group_chat_key_store");
        System.setProperty("javax.net.ssl.keyStorePassword", "abcd1234");

        final int port = Integer.parseInt(args[0]);
        final boolean ssl = args.length == 3 && "ssl".equalsIgnoreCase(args[2]);

        new ChatServer().execute(port, ssl);
    }

    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        System.out.println("Message received: " + message);

        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        userNames.add(userName);
    }

    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quited");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}