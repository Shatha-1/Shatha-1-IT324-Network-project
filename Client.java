
package com.mycompany.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String Server_IP="localhost";
    private static final int Server_port=9090;
 
        public static void main(String[] args) throws IOException{
          try(Socket socket = new Socket (Server_IP,Server_port)) {
              ServerConnection servcon=new ServerConnection(socket);
              BufferedReader keyboard=new BufferedReader (new InputStreamReader(System.in));
              PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
              new Thread (servcon).start(); 
              try{
                  while(true){
                      String userInput = keyboard.readLine();                     
                      if(userInput.equals("quit")) break;
                      out.println(userInput); 
                  } // end of while loop
              } catch (Exception e){
                  e.printStackTrace();
              }
          }
              System.exit(0); }
}

