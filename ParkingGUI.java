import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ParkingGUI extends JFrame {

    
    private CardLayout cardLayout;
    private JPanel mainPanel;
private Socket socket;
private PrintWriter out;
private BufferedReader in;

    private final Color royalBlue = new Color(25, 50, 110);
    private final Color lightGray = new Color(240, 240, 245);
    private final Color steelGray = new Color(200, 200, 210);

    
    public ParkingGUI() {
        setTitle("Parking Reservation System");
        setSize(520, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Add all pages
        mainPanel.add(createHomePage(), "home");
        mainPanel.add(createRegisterPage(), "register");
        mainPanel.add(createLoginPage(), "login");
        mainPanel.add(createChooseSpotPage(), "chooseSpot");
        mainPanel.add(createReservationPage(), "reserve");
        mainPanel.add(createMyBookingsPage(), "myBookings");

        try {
    socket = new Socket("localhost", 9090); // نفس البورت اللي يستخدمه سيرفركم
    out = new PrintWriter(socket.getOutputStream(), true);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    System.out.println("Connected to server successfully!");
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "⚠️ Failed to connect to server: " + ex.getMessage());
}

        setVisible(true);
    }

    // =========================================
    // Page 1: Home
    // =========================================
    private JPanel createHomePage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(lightGray);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Online Parking Reservation");
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(royalBlue);

        JButton newUser = makeButton("New User");
        JButton existingUser = makeButton("Existing User");

        c.gridx = 0; c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(newUser, c);
        c.gridy = 2; p.add(existingUser, c);

        newUser.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        existingUser.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        return p;
    }

    // =========================================
    // Page 2: Register
    // =========================================
    private JPanel createRegisterPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(steelGray);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Register New User");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(royalBlue);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton connectBtn = makeButton("Connect & Register");
        JButton backBtn = makeButton("Back");

        c.gridx = 0; c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(new JLabel("Username:"), c);
        c.gridy = 2; p.add(userField, c);
        c.gridy = 3; p.add(new JLabel("Password:"), c);
        c.gridy = 4; p.add(passField, c);
        c.gridy = 5; p.add(connectBtn, c);
        c.gridy = 6; p.add(backBtn, c);

        connectBtn.addActionListener(e -> {
            try {
    out.println("REGISTER");
    out.println(userField.getText());
    out.println(new String(passField.getPassword()));

    String response = in.readLine();
    if (response != null && response.contains("successful")) {
        JOptionPane.showMessageDialog(this, "Registered successfully!");
        cardLayout.show(mainPanel, "chooseSpot");
    } else {
        JOptionPane.showMessageDialog(this, "Registration failed: " + response);
    }
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}

            JOptionPane.showMessageDialog(this, "You are connected and registered successfully!");
            cardLayout.show(mainPanel, "chooseSpot");
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        return p;
    }

    // =========================================
    // Page 3: Login
    // =========================================
    private JPanel createLoginPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(lightGray);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Existing User Login");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(royalBlue);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = makeButton("Connect & Log In");
        JButton backBtn = makeButton("Back");

        c.gridx = 0; c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(new JLabel("Username:"), c);
        c.gridy = 2; p.add(userField, c);
        c.gridy = 3; p.add(new JLabel("Password:"), c);
        c.gridy = 4; p.add(passField, c);
        c.gridy = 5; p.add(loginBtn, c);
        c.gridy = 6; p.add(backBtn, c);

        loginBtn.addActionListener(e -> {
           try {
    out.println("LOGIN");
    out.println(userField.getText());
    out.println(new String(passField.getPassword()));

    String response = in.readLine();
    if (response != null && response.contains("Welcome")) {
        JOptionPane.showMessageDialog(this, "Login successful!");
        cardLayout.show(mainPanel, "myBookings");
    } else {
        JOptionPane.showMessageDialog(this, "Login failed. Please check your credentials.");
    }
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}

            JOptionPane.showMessageDialog(this, "You are connected and logged in successfully!");
            cardLayout.show(mainPanel, "myBookings");
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        return p;
    }

    // =========================================
    // Page 4: Choose Parking Spot
    // =========================================
    private JPanel createChooseSpotPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(steelGray);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Choose Parking Spot");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(royalBlue);

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);

        JComboBox<String> parkBox = new JComboBox<>(new String[]{"1", "2", "3"});
        JComboBox<String> slotBox = new JComboBox<>();
        for (int i = 1; i <= 10; i++) slotBox.addItem(String.valueOf(i));

        JButton continueBtn = makeButton("Continue");

        c.gridx = 0; c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(new JLabel("Select Date:"), c);
        c.gridy = 2; p.add(dateSpinner, c);
        c.gridy = 3; p.add(new JLabel("Parking Location (1–3):"), c);
        c.gridy = 4; p.add(parkBox, c);
        c.gridy = 5; p.add(new JLabel("Slot Number (1–10):"), c);
        c.gridy = 6; p.add(slotBox, c);
        c.gridy = 7; p.add(continueBtn, c);

        continueBtn.addActionListener(e -> {
           try {
    out.println("CHOOSE_SPOT");
    out.println(parkBox.getSelectedItem());
    out.println(slotBox.getSelectedItem());
    out.println(dateSpinner.getValue().toString());

    String response = in.readLine();
    JOptionPane.showMessageDialog(this, response != null ? response : "No response from server.");
    cardLayout.show(mainPanel, "reserve");
} catch (Exception ex) {
    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
}

            JOptionPane.showMessageDialog(this, "Spot chosen successfully!");
            cardLayout.show(mainPanel, "reserve");
        });

        return p;
    }

    // =========================================
    // Page 5: Reservation
    // =========================================
    private JPanel createReservationPage() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(lightGray);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("Make a Reservation");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(royalBlue);

        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        JComboBox<String> dayBox = new JComboBox<>(days);
        JTextField durationField = new JTextField(5);
        JButton showBtn = makeButton("Show Available Times");
        JComboBox<String> timeBox = new JComboBox<>();
        JButton bookBtn = makeButton("Book");

        c.gridx = 0; c.gridy = 0; p.add(title, c);
        c.gridy = 1; p.add(new JLabel("Select Day:"), c);
        c.gridy = 2; p.add(dayBox, c);
        c.gridy = 3; p.add(new JLabel("Duration (days):"), c);
        c.gridy = 4; p.add(durationField, c);
        c.gridy = 5; p.add(showBtn, c);
        c.gridy = 6; p.add(timeBox, c);
        c.gridy = 7; p.add(bookBtn, c);

        showBtn.addActionListener(e -> {
    try {
        String selectedDay = (String) dayBox.getSelectedItem();
        String duration = durationField.getText();

        // نرسل الطلب للسيرفر
        out.println("SHOW_TIMES");
        out.println(selectedDay);
        out.println(duration);

        // نستقبل الرد (الأوقات المتاحة)
        timeBox.removeAllItems();
        String line;
        while ((line = in.readLine()) != null && !line.equals("END")) {
            timeBox.addItem(line);
        }

        JOptionPane.showMessageDialog(this, "Available times loaded.");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading times: " + ex.getMessage());
    }
});

bookBtn.addActionListener(e -> {
    try {
        String selectedDay = (String) dayBox.getSelectedItem();
        String duration = durationField.getText();
        String time = (String) timeBox.getSelectedItem();

        if (time == null) {
            JOptionPane.showMessageDialog(this, "Please choose a time slot first.");
            return;
        }

        // نرسل بيانات الحجز للسيرفر
        out.println("BOOK_SLOT");
        out.println(selectedDay);
        out.println(duration);
        out.println(time);

        // نقرأ الرد من السيرفر
        String response = in.readLine();
        JOptionPane.showMessageDialog(this, response);

        // نرجع للصفحة الرئيسية
        cardLayout.show(mainPanel, "home");

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error sending booking: " + ex.getMessage());
    }
});

        return p;
    }

    // =========================================
    // Page 6: My Bookings
    // =========================================
    private JPanel createMyBookingsPage() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(lightGray);

        JTextArea area = new JTextArea("User bookings will appear here...");
        area.setFont(new Font("Serif", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(steelGray);
        JButton cancelBtn = makeButton("Cancel Reservation");
        JButton newBtn = makeButton("Make a New Reservation");
        btnPanel.add(cancelBtn);
        btnPanel.add(newBtn);

        newBtn.addActionListener(e -> cardLayout.show(mainPanel, "home"));

try {
    out.println("SHOW_BOOKINGS");

    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = in.readLine()) != null && !line.equals("END")) {
        sb.append(line).append("\n");
    }

    area.setText(sb.toString());
} catch (Exception ex) {
    area.setText("Error loading bookings: " + ex.getMessage());
}

        p.add(scroll, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    // =========================================
    // method for buttons
    // =========================================
    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(royalBlue);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 190), 1, true));
        b.setPreferredSize(new Dimension(220, 35));
        return b;
    }

   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkingGUI::new);
    }
}
