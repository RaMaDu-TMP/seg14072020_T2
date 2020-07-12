package br.edu.usf.chat;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the chat client program.
 * Type 'bye' to terminte the program.
 *
 * @author www.codejava.net
 */
public class ChatClient {
    private String hostname;
    private int port;
    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute(boolean ssl) {

        if (ssl) {

            try (final Socket socket = SSLSocketFactory.getDefault().createSocket(hostname, port)) {

                System.out.println("Connected to the chat server");

                new ReadThread(socket, this).start();
                new WriteThread(socket, this).start();

            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
            }

        } else {
            try {
                Socket socket = new Socket(hostname, port);

                System.out.println("Connected to the chat server");

                new ReadThread(socket, this).start();
                new WriteThread(socket, this).start();

            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O Error: " + ex.getMessage());
            }
        }
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }

    public static void main(String[] args) {
        if (args.length < 2) return;
        System.setProperty("javax.net.ssl.trustStore", "chat.store");

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute(true);
    }
}