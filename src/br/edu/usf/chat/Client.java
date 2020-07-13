package br.edu.usf.chat;

import com.sun.net.ssl.internal.ssl.Provider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.Security;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        int serverPort = 35786;
        String serverName = "localhost";

        Security.addProvider(new Provider());

        System.setProperty("javax.net.ssl.trustStore", "group_chat_trust_store");
        System.setProperty("javax.net.ssl.trustStorePassword", "abcd1234");

        try (final Scanner scanner = new Scanner(System.in)) {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            //Create SSLSocket using SSLServerFactory already established ssl context and connect to server
            SSLSocket sslSocket = (SSLSocket) sslsocketfactory.createSocket(serverName, serverPort);
            //Create OutputStream to send message to server
            DataOutputStream outputStream = new DataOutputStream(sslSocket.getOutputStream());
            //Create InputStream to read messages send by the server
            DataInputStream inputStream = new DataInputStream(sslSocket.getInputStream());
            //read the first message send by the server after being connected
            System.out.println(inputStream.readUTF());
            //Keep sending sending the server the message entered by the client unless the it is "close"
            while (true) {
                System.out.println("Write a Message : ");
                String messageToSend = scanner.next();
                outputStream.writeUTF(messageToSend);
                System.err.println(inputStream.readUTF());
                if (messageToSend.equals("close")) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}