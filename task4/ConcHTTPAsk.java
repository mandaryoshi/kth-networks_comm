import java.net.*;
import java.io.*;


public class ConcHTTPAsk implements Runnable {
    
    Socket socket;
    
    
    public ConcHTTPAsk(Socket s){
        socket = s;
    }
    
    public void run() {
        try {
            //Streams
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(socket.getOutputStream());
            
            //Initialising variables
            String s;
            String hostName = null;
            int portName = 0;
            String toServer = null;
            
            //Starting the splitting of the GET request
            s = inFromClient.readLine();
            
            String removeHTTP[] = s.split("HTTP/1.1");
            String hostname[] = removeHTTP[0].split("host=");
            
            if (hostname.length > 1) {
                String portname[] = hostname[1].split("&port=");
                hostName = portname[0];
                if (portname.length > 1) {
                    String stringname[]=portname[1].split("&string=");
                    portName =  Integer.parseInt(stringname[0].replaceAll("[\\D]", ""));
                    if (stringname.length > 1) {
                        toServer = stringname[1];
                    }
                }
            }
            
            //Sending back webpages
            if (hostName != null && portName != 0){
                outToClient.print("HTTP/1.1 200 OK\r\n");
                outToClient.print("Content-type: text/plain\r\n");
                outToClient.print("Connection: close\r\n");
                outToClient.print("\r\n");
                try {
                    outToClient.print(askServer(hostName, portName, toServer));
                }
                catch(IOException e){
                    outToClient.print("HTTP/1.1 404 Not Found\r\n");
                    outToClient.print("Content-type: text/plain\r\n");
                    outToClient.print("Connection: close\r\n");
                    outToClient.print("\r\n");
                }
            }
            else if (removeHTTP[0].contains("/ask") == false) {
                outToClient.print("HTTP/1.1 400 Bad Request\r\n");
                outToClient.print("Content-type: text/plain\r\n");
                outToClient.print("Connection: close\r\n");
                outToClient.print("\r\n");
            }
            else {
                outToClient.print("HTTP/1.1 404 Not Found\r\n");
                outToClient.print("Content-type: text/plain\r\n");
                outToClient.print("Connection: close\r\n");
                outToClient.print("\r\n");
            }
            
            //Closing streams and socket
            outToClient.close();
            inFromClient.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        
        try {

            //Port and server socket
            int port = Integer.parseInt(args[0]);
            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                
                //Client socket
                Runnable r = new ConcHTTPAsk(connectionSocket);
                new Thread(r).start();

            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println(ex);
        }

        
    }
    
    
    public static String askServer(String hostname, int port, String ToServer) throws  IOException {
        
        String FromServer;
        StringBuilder sb = new StringBuilder();
        
        Socket clientSocket = new Socket(hostname, port);
        
        clientSocket.setSoTimeout(10000);
        
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
       
        outToServer.writeBytes(ToServer + '\n');
         
        while ((FromServer = inFromServer.readLine()) != null){
            sb.append(FromServer + '\n');
            
            if (sb.length() > 5000) {
                break;
            }
        }

        clientSocket.close();
        
        return sb.toString();
        
    }

    public static String askServer(String hostname, int port) throws  IOException {
        
        return askServer(hostname, port, "");
        
    }

    
}

