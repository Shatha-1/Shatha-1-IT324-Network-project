
package com.mycompany.lab3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;
    
      
    public ServerConnection (Socket s) throws IOException{
        server=s;
        in= new BufferedReader (new InputStreamReader(server.getInputStream())); 
    }
        @Override
    public void run(){
        
            String serverResponse;
            try {
                while(true){
                serverResponse = in.readLine(); 
                if (serverResponse==null) break;
                System.out.println("Server says: "+serverResponse);
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }     
    }           
}
