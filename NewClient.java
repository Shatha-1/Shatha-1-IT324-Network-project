
package com.mycompany.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class NewClient implements Runnable
{
private Socket client;
private BufferedReader in;
private PrintWriter out;
private ArrayList<NewClient> clients;

// FOR parking 
private static ArrayList<String> reservedSpots = new ArrayList<>();

// For users
private static ArrayList<String> registeredUsers = new ArrayList<>();

  public NewClient (Socket c,ArrayList<NewClient> clients) throws IOException
  {
    this.client = c;
    this.clients=clients;
    in= new BufferedReader (new InputStreamReader(client.getInputStream())); 
    out=new PrintWriter(client.getOutputStream(),true); 
  }

@Override
  public void run ()
  {
   try{
    while (true){
        //User registration
            out.println("Enter username:");
            String username = in.readLine();
            
            out.println("Enter password:");
            String password = in.readLine();
            
            String userData = username + ":" + password;
            registeredUsers.add(userData);
            out.println(" Registration successful! Welcome, " + username + ".");
            
            //Reservation request
            out.println("Enter parking location (1-3):");
            int parkNumber = Integer.parseInt(in.readLine());

            out.println("Enter slot number (1-10):");
            int slotNumber = Integer.parseInt(in.readLine());

            String spot = "Park" + parkNumber + "-Slot" + slotNumber;
            
            //Confirm or reject the reservation
            if (!checkSpot(reservedSpots, spot)) {
                reservedSpots = addSpot(reservedSpots, spot);
                out.println(" Reservation confirmed! You booked " + spot);
            } else {
                out.println(" Sorry, " + spot + " is already reserved.");
            }
    }
}
   catch (IOException e){
       System.err.println("IO exception in new client class");
       System.err.println(e.getStackTrace());
   }
finally{
    out.close();
       try {
           in.close();
       } catch (IOException ex) {
          ex.printStackTrace();
       }
}
  }
    private void outToAll(String substring) {
for (NewClient aclient:clients){
   aclient.out.println(substring); 
}
    }

    public static boolean checkSpot(ArrayList<String> list, String value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                return true; // The parking space is reserved
            }
        }
        return false; //Parking is available
    }
    
     public static ArrayList<String> addSpot(ArrayList<String> list, String value) {
        ArrayList<String> newList = new ArrayList<>();

        // We copy all the old items.
        for (int i = 0; i < list.size(); i++) {
            newList.add(list.get(i));
        }

        // We add the new element .
        newList.add(value);

        return newList;
    }
}
