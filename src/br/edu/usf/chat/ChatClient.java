package br.edu.usf.chat;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Security;

public class ChatClient {
    private final String hostname;
    private final int port;

    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute(boolean ssl) {
        if (ssl) {
            try {
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);

                System.out.println("Connected to the chat server");

                new ReadThread(socket, this).start();
                new WriteThread(socket, this).start();

            } catch (UnknownHostException ex) {
                System.out.println("Server not found: " + ex.getMessage());
            } catch (IOException e) {
                System.out.println("I/O Error: " + e.getMessage());
//            } finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
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
        if (args.length < 2) {
            System.out.println("Missing arguments! <host> <port-number>");
            System.exit(0);
        }

        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.trustStore", "group_chat_trust_store");
        System.setProperty("javax.net.ssl.trustStorePassword", "abcd1234");

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final boolean ssl = args.length == 3;

        new ChatClient(host, port).execute(ssl);
    }

//    public static void main(String[] args) throws IOException {
//        System.setProperty("javax.net.ssl.trustStore", "chat.store");
//        final Socket socket = SSLSocketFactory.getDefault().createSocket("localhost", 8989);
//
//        final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        final PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
//
//        final BufferedReader commandReader = new BufferedReader(new InputStreamReader(System.in));
//
//        System.out.print("Enter your username: ");
//        socketWriter.println(commandReader.readLine());
//        System.out.println();
//
//        while (true) {
//            System.out.print("Type your message: ");
//            final String message = commandReader.readLine();
//            if ("\\q".equalsIgnoreCase(message)) {
//                socket.close();
//                break;
//            }
//
//            socketWriter.println(message);
//            System.out.println("[Reply from server: '" + socketReader.readLine() + "' ]");
//        }
//    }
}