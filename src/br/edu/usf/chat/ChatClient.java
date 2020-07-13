package br.edu.usf.chat;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;

@SuppressWarnings("deprecation")
public class ChatClient {
    private String userName;

    public void execute(final String hostname, final int port, final boolean ssl) {
        try {
            final Socket socket;
            if (ssl) {
                socket = SSLSocketFactory.getDefault().createSocket(hostname, port);
            } else {
                socket = SocketFactory.getDefault().createSocket(hostname, port);
            }

            System.out.println("Connected to the chat server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Missing arguments! <host> <port-number> <ssl>[optional]");
            System.exit(0);
        }

        //noinspection deprecation
        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.trustStore", "group_chat_trust_store");
        System.setProperty("javax.net.ssl.trustStorePassword", "abcd1234");

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final boolean ssl = args.length == 3 && "ssl".equalsIgnoreCase(args[2]);

        new ChatClient().execute(host, port, ssl);
    }
}