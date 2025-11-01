package labnetwork;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class NewClient implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;

    // List of reserved spots 
    private static final ArrayList<String> reservationRecords = new ArrayList<>();

    // List of users info(username:password)
    private static final ArrayList<String> registeredUsers = new ArrayList<>();
    
     // receives the socket and sets up input/output streams
    public NewClient(Socket c, ArrayList<NewClient> clients) throws IOException {
        this.client = c;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true); // auto-flush
    }

    @Override
    public void run() {
        try {
            // ensures registration happens only once per connection
            boolean registered = false;
            String username = null;

            while (true) {
                
                
                //Registration 
                if (!registered) {
                    boolean ok = false;
                    String password;

                    do {
                        out.println("Enter username:");
                        username = in.readLine();

                        out.println("Enter password:");
                        password = in.readLine();

                        if (username == null || password == null) {
                            return; 
                        }
                        // Check if username already exists in the registered users list
                        boolean exists = false;
                        synchronized (registeredUsers) {
                            for (String userRecord : registeredUsers) {
                                int colon = userRecord.indexOf(":");
                                if (colon > 0) {
                                    String nameInRecord = userRecord.substring(0, colon);
                                    if (username.equals(nameInRecord)) { exists = true; break; }
                                }
                            }
                        }
                        
                       // If username exists, reject
                        if (exists) {
                            out.println("Username already exists!");
                        } else {
                            
                         // Store username and password
                            synchronized (registeredUsers) {
                                registeredUsers.add(username + ":" + password);
                            }
                            out.println("Registration successful! Welcome, " + username + ".");
                            ok = true;
                        }

                    } while (!ok);

                    registered = true;  
                }

                // Reservation 
                
                // Request parking location
                out.println("Enter parking location (1-3):");
                String parkLine = in.readLine();
                if (parkLine == null) break;
                int parkNumber = Integer.parseInt(parkLine.trim());

                // Request day
                out.println("Enter day (from sunday to saturday):");
                String dayName = safeLower(in.readLine());
                if (dayName == null) break;
                
                // Request reservation duration
                out.println("Enter duration in days:");
                String durLine = in.readLine();
                if (durLine == null) break;
                int duration = Integer.parseInt(durLine.trim());

                 // Convert day name to index (0-6)
                int day = dayToIdx(dayName);  

                // check availability
                StringBuilder available = new StringBuilder();
                for (int i = 1; i <= 5; i++) {
                    if (isRangeFree(parkNumber, i, day, duration)) {
                        if (available.length() > 0) available.append(", ");
                        available.append(i);
                    }
                }

                if (available.length() == 0) {
                    out.println("none");
                    continue; // ask for location/day/duration again
                }
                
                // Send available slots to client
                out.println(available.toString());

                // ask for slot or RESET(to reselect input)
                out.println("Enter slot number (1-5) or type RESET:");
                String slotLine = in.readLine();
                if (slotLine == null) break;

                if ("RESET".equalsIgnoreCase(slotLine.trim())) {
                    //reselesct input
                    continue;
                }

                int slotNumber = Integer.parseInt(slotLine.trim());

                // Check if chosen slot is available for all days (agian for extra check)
                boolean canReserve = true;
                for (int i = 0; i < duration; i++) {
                    int dayNumber = ((day + i) % 7) + 1; // 1..7
                    String requested = "park=" + parkNumber + "-slot=" + slotNumber + "-startDay=" + dayNumber;
                    
                     // If any day is already reserved
                    if (isReservedSpot(reservationRecords, requested)) {
                        out.println("Sorry, one or more of those days are already reserved.\n");
                        canReserve = false;
                        break;
                    }
                }
                    //if all available 
                if (canReserve) {
                    synchronized (reservationRecords) {
                        for (int i = 0; i < duration; i++) {
                            int dayNumber = ((day + i) % 7) + 1;
                            String record = "user=" + username
                                    + "-park=" + parkNumber
                                    + "-slot=" + slotNumber
                                    + "-startDay=" + dayNumber;
                            reservationRecords.add(record);
                        }
                    }
                    
                    out.println("Reservation confirmed for slot " + slotNumber
                            + " for " + duration + " day(s), starting from: " + dayName);
                }
            }
        } catch (Exception e) {
            System.err.println("IO exception in NewClient");
            e.printStackTrace();
        } finally {
             // Clean up resources when client disconnects
            try { in.close(); } catch (Exception ignore) {}
            try { out.close(); } catch (Exception ignore) {}
            try { client.close(); } catch (Exception ignore) {}
        }
    }

    private static String safeLower(String s) { return s == null ? null : s.trim().toLowerCase(); }

    private static int dayToIdx(String d) {
        switch (d) {
            case "sunday": return 0;
            case "monday": return 1;
            case "tuesday": return 2;
            case "wednesday": return 3;
            case "thursday": return 4;
            case "friday": return 5;
            case "saturday": return 6;
        }
        return 0;
    }

    public static boolean isReservedSpot(ArrayList<String> list, String requestedSlot) {
        synchronized (list) {
            for (int i = 0; i < list.size(); i++) {
                String record = list.get(i);
                String slotRecord = record.substring(record.indexOf("park=")); // compare from park
                if (slotRecord.equals(requestedSlot)) return true;
            }
            return false;
        }
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
