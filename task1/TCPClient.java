package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public static String askServer(String hostname, int port, String ToServer) throws  IOException {
        
        String FromServer;
        StringBuilder sb = new StringBuilder();
        
        Socket clientSocket = new Socket(hostname, port);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       
        outToServer.writeBytes(ToServer + '\n');
         
        while ((FromServer = inFromServer.readLine()) != null){
            sb.append(FromServer + '\n');
            
            if (sb.length() > 1500) {
                break;
            }
        }

        clientSocket.setSoTimeout(10000);
        clientSocket.close();
        
        return sb.toString();
        
    }

    public static String askServer(String hostname, int port) throws  IOException {
        
        return askServer(hostname, port, "");
        
    }
}

