
import java.net.*;
import java.io.*;

public class HTTPEcho {

    public static void main(String[] args) {

        try {

            //Port and server socket
            int port = Integer.parseInt(args[0]);
            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true) {

                Socket connectionSocket = welcomeSocket.accept();

                //Streams
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream());

                //HTTP headers to send back
                outToClient.print("HTTP/1.1 200 OK\r\n");
                outToClient.print("Content-type: text/plain\r\n");
                outToClient.print("Connection: close\r\n");
                outToClient.print("\r\n");

                //Echoing the client
                String s;
                while ((s = inFromClient.readLine()) != null) {
                    if (s.length() == 0) {
                        break;
                    }
                outToClient.print(s + "\r\n");
                //System.out.println(s);
                }

                //Closing streams and socket
                outToClient.close();
                inFromClient.close();
                connectionSocket.close();

            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println(ex);

        }
    }
}
