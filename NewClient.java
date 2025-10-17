
package labnetwork;

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

// List of reserved spots 
private static ArrayList<String> reservationRecords = new ArrayList<>();

// List of users
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
        boolean successfulRegistration = false ;
        String username;
        String password;
        do {
            out.println("Enter username:");
             username = in.readLine();
            
            out.println("Enter password:");
             password = in.readLine();
            
            //check if user exists before
            boolean exists = false;
            for (int i = 0; i < registeredUsers.size() ; i++) {
                String userRecord = registeredUsers.get(i);
                String nameInRecord = userRecord.substring(0,userRecord.indexOf(":"));
                if (username.equals(nameInRecord)) {
                    exists = true;
                    break;
                }
            }//if exists
            if (exists) {
                out.println("Username already exists!");
            } else {//if not we'll add it to the registred users list
                registeredUsers.add(username + ":" + password);
                out.println("Registration successful! Welcome, " + username + ".");
                successfulRegistration = true;
              }
            
            }while(! successfulRegistration);
            
            
            //Reservation request
            out.println("Enter parking location (1-3):");
            int parkNumber = Integer.parseInt(in.readLine());

            
             out.println("Enter day (from sunday to saturday):");
            String dayName= in.readLine().trim().toLowerCase();

            out.println("Enter duration in days:");
            int duration = Integer.parseInt(in.readLine());
            
            //convert day from string to an int
            int day = 0;
                 switch (dayName) {
                     case "sunday":
                         day = 0;    break;
                     case "monday":
                         day = 1;    break;
                     case "tuesday":
                         day = 2;    break;
                     case "wednesday":
                         day = 3;    break;
                     case "thursday":
                         day = 4;    break;

                     case "friday":
                         day = 5;    break;
                     case "saturday":
                         day = 6;    break;}
            
            // Show available slots for this park across the selected day+duration
            String available = "";
            for (int i = 1; i <= 10; i++) {
                if (isRangeFree(parkNumber, i, day, duration)) {
                    if (!available.isEmpty()) available += ", ";
                    available +=  i;
                }
            }//if nothing is available
            if (available.isEmpty()) {
            out.println("none");
            continue;}
            
            out.println(available);

        
        
 
            //choose a slot from the available slots list        
            out.println("Enter slot number (1-10):");
            int slotNumber = Integer.parseInt(in.readLine());
            
            
            boolean canReserve = true;
            String record;
            int dayNumber;
            // make sure all days within requested duration are available 
            for (int i = 0; i < duration; i++) {
                 dayNumber = ((day + i) % 7) + 1; //(mod 7) to loop back to sunday after saturday
                 record = "park=" + parkNumber
                        + "-slot=" + slotNumber
                        + "-startDay=" + dayNumber ;

                if (isReservedSpot(reservationRecords, record)) {//if any of them is reserved 
                    out.println("Sorry, one or more of those days are already reserved.\n");
                    canReserve = false; break; }
            }

            //if all of them are available: reserve
            if (canReserve) { 
                for (int i = 0; i < duration; i++) {
                     dayNumber = ((day  + i) % 7) + 1;
                     record = "user=" + username 
                        + "-park=" + parkNumber
                        + "-slot=" + slotNumber
                        + "-startDay=" + dayNumber ;

                    reservationRecords.add(record);
                }
                out.println("Reservation confirmed for slot " + slotNumber + " for " + duration + " day(s), starting from: " + dayName);

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


    public static boolean isReservedSpot(ArrayList<String> list, String requestedSlot) {
        for (int i = 0; i < list.size(); i++) {
            String record = list.get(i);  //to compare records starting from slot details
            String slotRecord = record.substring(record.indexOf("park="));
            if (slotRecord.equals(requestedSlot)) {
                return true; // The parking space is reserved
            }
        }
        return false; //Parking is available
    }
    
// True if this park/slot is free for ALL days in the range
    private static boolean isRangeFree(int parkNumber, int slotNumber, int startDay0to6, int duration) {
        for (int i = 0; i < duration; i++) {
            int dayNum = ((startDay0to6 + i) % 7) + 1; // 1..7
            String requested = "park=" + parkNumber + "-slot=" + slotNumber + "-startDay=" + dayNum;
            if (isReservedSpot(reservationRecords, requested)) return false;
        }
        return true;
    }

}