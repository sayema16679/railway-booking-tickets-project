import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RailwaySystem().setVisible(true));
    }
}
class InvalidPassengerException extends Exception {
    InvalidPassengerException(String message) { super(message); }
}
class SeatNotAvailableException extends Exception {
    SeatNotAvailableException(String message) { super(message); }
}
class BookingNotFoundException extends Exception {
    BookingNotFoundException(String message) { super(message); }
}
interface Ticket {
    String getCategory();
    double getFare();
}
class CoachTicket implements Ticket {
    private final String category;
    private final double fare;
    CoachTicket(String category, double fare) { this.category = category; this.fare = fare; }
    public String getCategory() { return category; }
    public double getFare() { return fare; }
}
class Train {
    private final int number;
    private final String name, from, to;
    private final int totalSeats;
    private int availableSeats;
    Train(int number, String name, String from, String to, int seats) {
        this.number = number; this.name = name; this.from = from; this.to = to;
        totalSeats = seats; availableSeats = seats;
    }
    synchronized void bookSeat() throws SeatNotAvailableException {
        if (availableSeats <= 0) throw new SeatNotAvailableException("No seats available on " + name);
        availableSeats--;
    }
    synchronized void cancelSeat() { if (availableSeats < totalSeats) availableSeats++; }
    synchronized int getAvailableSeats() { return availableSeats; }
    int getNumber() { return number; }
    String getName() { return name; }
    String getFrom() { return from; }
    String getTo() { return to; }
    int getTotalSeats() { return totalSeats; }
    public String toString() {
        return number + " - " + name + " | Seats: " + getAvailableSeats() + "/" + totalSeats;
    }
}
class Booking {
    final String aadhaar, name, gender, date;
    final int age;
    final Train train;
    final Ticket ticket;
    Booking(String aadhaar, String name, int age, String gender, String date, Train train, Ticket ticket) {
        this.aadhaar = aadhaar; this.name = name; this.age = age; this.gender = gender;
        this.date = date; this.train = train; this.ticket = ticket;
    }
}

class RailwaySystem extends JFrame {
    private final JTextField aadhaarField = new JTextField(14);
    private final JTextField nameField = new JTextField(14);
    private final JTextField ageField = new JTextField(5);
    private final JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
    private final JTextField cancelAadhaarField = new JTextField(12);

    private final JComboBox<String> fromCombo = new JComboBox<>(new String[]{
        "Mumbai", "Pune", "Ahmedabad", "Karnataka", "Rajasthan", "Delhi"});
    private final JComboBox<String> toCombo = new JComboBox<>(new String[]{
        "Pune", "Mumbai", "Ahmedabad", "Karnataka", "Rajasthan", "Delhi"});
    private final JComboBox<String> genderCombo = new JComboBox<>(new String[]{
        "Female", "Male", "Other", "Prefer not to say"});
    private final JComboBox<Train> trainCombo = new JComboBox<>();
    private final JComboBox<String> coachCombo = new JComboBox<>(new String[]{
        "General", "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC 1 Tier"});

    private final JLabel trainCountLabel = new JLabel("Available trains: 0");
    private final JLabel seatsLabel = new JLabel("Available seats: -");
    private final JLabel fareLabel = new JLabel("Ticket price: -");

    private final String[] columns = {
        "Aadhaar No.", "Passenger Name", "Age", "Gender", "From", "To",
        "Travel Date", "Train", "Seat Status", "Coach Category", "Amount (Rs.)"
    };
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable bookingTable = new JTable(tableModel);
    private final List<Train> allTrains = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    RailwaySystem() {
        setTitle("Railway Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(235, 243, 250));

        addTrain(12901,"Mumbai Delhi Rajdhani Express","Mumbai","Delhi",60);
        addTrain(12902,"Mumbai Delhi Central Express","Mumbai","Delhi",45);
        addTrain(19019,"Mumbai Delhi Superfast Express","Mumbai","Delhi",50);
        addTrain(12125,"Mumbai Pune Intercity Express","Mumbai","Pune",40);
        addTrain(11007,"Deccan Express","Mumbai","Pune",35);
        addTrain(22105,"Indrayani Express","Mumbai","Pune",30);
        addTrain(12931,"Ahmedabad Mumbai Double Decker","Ahmedabad","Mumbai",45);
        addTrain(12009,"Ahmedabad Shatabdi Express","Ahmedabad","Mumbai",35);
        addTrain(22954,"Gujarat Express","Ahmedabad","Mumbai",50);
        addTrain(19035,"Ahmedabad Pune Express","Ahmedabad","Pune",30);
        addTrain(11049,"Ahmedabad Kolhapur Express","Ahmedabad","Pune",25);
        addTrain(19401,"Ahmedabad Pune Special","Ahmedabad","Pune",20);
        addTrain(12627,"Karnataka Express","Karnataka","Delhi",50);
        addTrain(22691,"Bengaluru Rajdhani Express","Karnataka","Delhi",40);
        addTrain(12781,"Swarnajayanti Express","Karnataka","Delhi",35);
        addTrain(12916,"Ashram Express","Delhi","Ahmedabad",45);
        addTrain(19412,"Delhi Ahmedabad Express","Delhi","Ahmedabad",35);
        addTrain(20914,"Delhi Sabarmati Express","Delhi","Ahmedabad",30);
        addTrain(12463,"Rajasthan Delhi Superfast","Rajasthan","Delhi",35);
        addTrain(20473,"Chetak Express","Rajasthan","Delhi",30);
        addTrain(22986,"Rajasthan Sampark Kranti","Rajasthan","Delhi",25);
        addTrain(12958,"Swarn Jayanti Rajdhani","Delhi","Rajasthan",40);
        addTrain(19610,"Udaipur Delhi Express","Delhi","Rajasthan",35);
        addTrain(25014,"Rajasthan Express","Delhi","Rajasthan",25);
        addTrain(22686,"Karnataka Sampark Kranti","Karnataka","Mumbai",35);
        addTrain(11302,"Udyan Express","Karnataka","Mumbai",40);
        addTrain(16506,"Gandhidham Bengaluru Express","Ahmedabad","Karnataka",30);
        addTrain(16507,"Ahmedabad Bengaluru Express","Ahmedabad","Karnataka",25);
        addTrain(12934,"Karnavati Express","Mumbai","Ahmedabad",40);
        addTrain(19023,"Mumbai Ahmedabad Express","Mumbai","Ahmedabad",35);
        addTrain(20907,"Sayajinagari Express","Mumbai","Ahmedabad",30);
        addTrain(12956,"Jaipur Mumbai Superfast","Rajasthan","Mumbai",35);
        addTrain(12239,"Hisar Mumbai Duronto","Rajasthan","Mumbai",25);
        addTrain(12955,"Mumbai Jaipur Express","Mumbai","Rajasthan",35);
        addTrain(19708,"Amrapur Aravali Express","Mumbai","Rajasthan",30);
        addTrain(12124,"Deccan Queen","Pune","Mumbai",40);
        addTrain(11010,"Sinhagad Express","Pune","Mumbai",35);
        addTrain(22106,"Indrayani Express","Pune","Mumbai",30);
        addTrain(12136,"Nagpur Pune Express","Pune","Ahmedabad",25);
        addTrain(19430,"Pune Ahmedabad Express","Pune","Ahmedabad",30);
        addTrain(11090,"Pune Bhuj Express","Pune","Ahmedabad",20);
        addTrain(12628,"Karnataka Express","Delhi","Karnataka",45);
        addTrain(22692,"Bengaluru Rajdhani Express","Delhi","Karnataka",40);
        addTrain(12782,"Swarnajayanti Express","Delhi","Karnataka",30);
        addTrain(12957,"Swarn Jayanti Rajdhani","Rajasthan","Delhi",35);
        addTrain(12464,"Rajasthan Delhi Superfast","Delhi","Rajasthan",40);
        addTrain(20474,"Chetak Express","Delhi","Rajasthan",30);

        buildUI();
        refreshTrains();
    }

    private void addTrain(int number, String name, String from, String to, int seats) {
        allTrains.add(new Train(number, name, from, to, seats));
    }
    private void addRow(JPanel panel, String label, Component component) {
        panel.add(new JLabel(label)); panel.add(component);
    }
    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(235, 243, 250));
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel("RAILWAY TICKET BOOKING SYSTEM", SwingConstants.CENTER);
        heading.setOpaque(true);
        heading.setBackground(new Color(30, 64, 110));
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 7));
        form.setBackground(new Color(248, 251, 255));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createTitledBorder("Passenger and journey details")));
        addRow(form, "Aadhaar number (12 digits):", aadhaarField);
        addRow(form, "Passenger name:", nameField);
        addRow(form, "Age:", ageField);
        addRow(form, "Gender:", genderCombo);
        addRow(form, "From station:", fromCombo);
        addRow(form, "To station:", toCombo);
        addRow(form, "Travel date (YYYY-MM-DD):", dateField);
        addRow(form, "Available trains on route:", trainCountLabel);
        addRow(form, "Select train:", trainCombo);
        addRow(form, "Available seats:", seatsLabel);
        addRow(form, "Coach category:", coachCombo);
        addRow(form, "Ticket amount:", fareLabel);

        JButton bookButton = styledButton("Book Ticket", new Color(36, 145, 78));
        bookButton.addActionListener(e -> bookTicket());
        form.add(bookButton);
        JButton clearButton = styledButton("Clear Fields", new Color(65, 105, 145));
        clearButton.addActionListener(e -> clearFields());
        form.add(clearButton);

        fromCombo.addActionListener(e -> refreshTrains());
        toCombo.addActionListener(e -> refreshTrains());
        trainCombo.addActionListener(e -> updateSeatAndFare());
        coachCombo.addActionListener(e -> updateSeatAndFare());

        JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        cancelPanel.setBackground(new Color(248, 251, 255));
        cancelPanel.setBorder(BorderFactory.createTitledBorder("Cancel ticket"));
        cancelPanel.add(new JLabel("Aadhaar number:"));
        cancelPanel.add(cancelAadhaarField);
        JButton cancelButton = styledButton("Cancel Ticket", new Color(190, 45, 55));
        cancelButton.addActionListener(e -> cancelTicket());
        cancelPanel.add(cancelButton);

        bookingTable.setRowHeight(26);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        bookingTable.setFillsViewportHeight(true);
        bookingTable.getTableHeader().setBackground(new Color(30, 64, 110));
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        bookingTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        int[] widths = {120,130,45,90,90,90,100,220,85,110,100};
        for (int i = 0; i < widths.length; i++)
            bookingTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane tableScroll = new JScrollPane(bookingTable);
        tableScroll.setPreferredSize(new Dimension(1100, 220));
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(248, 251, 255));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Booked passenger details"));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(new Color(235, 243, 250));
        top.add(heading, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(new Color(235, 243, 250));
        center.add(form, BorderLayout.CENTER);
        center.add(cancelPanel, BorderLayout.SOUTH);
        top.add(center, BorderLayout.CENTER);
        main.add(top, BorderLayout.NORTH);
        main.add(tablePanel, BorderLayout.CENTER);
        setContentPane(main);
    }

    private void refreshTrains() {
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();
        trainCombo.removeAllItems();
        int count = 0;
        if (from != null && to != null && !from.equals(to)) {
            for (Train train : allTrains) {
                if (train.getFrom().equals(from) && train.getTo().equals(to)) {
                    trainCombo.addItem(train);
                    count++;
                }
            }
        }
        trainCountLabel.setText("Available trains: " + count);
        if (count == 0) {
            seatsLabel.setText("No train available");
            fareLabel.setText("-");
        } else updateSeatAndFare();
    }

    private double getFare(String category) {
        switch (category) {
            case "General": return 100;
            case "Sleeper": return 250;
            case "AC 3 Tier": return 600;
            case "AC 2 Tier": return 900;
            case "AC 1 Tier": return 1500;
            default: return 0;
        }
    }
    private void updateSeatAndFare() {
        Train train = (Train) trainCombo.getSelectedItem();
        String category = (String) coachCombo.getSelectedItem();
        seatsLabel.setText(train == null ? "No train available" :
            train.getAvailableSeats() + " / " + train.getTotalSeats());
        fareLabel.setText(category == null ? "-" :
            "Rs. " + String.format("%.2f", getFare(category)));
    }

    private void bookTicket() {
        try {
            String aadhaar = aadhaarField.getText().trim().replaceAll("\\s+", "");
            String name = nameField.getText().trim();
            String ageText = ageField.getText().trim();
            String date = dateField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            Train train = (Train) trainCombo.getSelectedItem();
            String category = (String) coachCombo.getSelectedItem();

            if (!aadhaar.matches("\\d{12}"))
                throw new InvalidPassengerException("Enter a 12-digit dummy Aadhaar number for testing. Do not use real Aadhaar.");
            if (name.isEmpty()) throw new InvalidPassengerException("Enter passenger name.");
            if (ageText.isEmpty()) throw new InvalidPassengerException("Enter passenger age.");
            int age = Integer.parseInt(ageText);
            if (age < 1 || age > 120) throw new InvalidPassengerException("Age must be between 1 and 120.");
            if (from.equals(to)) throw new InvalidPassengerException("From and To cannot be the same.");
            LocalDate parsedDate;
            try { parsedDate = LocalDate.parse(date); }
            catch (DateTimeParseException ex) {
                throw new InvalidPassengerException("Date format must be YYYY-MM-DD, e.g. 2026-11-05.");
            }
            if (parsedDate.isBefore(LocalDate.now()))
                throw new InvalidPassengerException("Travel date cannot be in the past.");
            if (train == null) throw new InvalidPassengerException("No train available for this route.");
            synchronized (bookings) {
                for (Booking b : bookings)
                    if (b.aadhaar.equals(aadhaar) && b.date.equals(date))
                        throw new InvalidPassengerException("This Aadhaar already has a booking for this date.");
            }

            Ticket ticket = new CoachTicket(category, getFare(category));
            new Thread(() -> {
                try {
                    synchronized (bookings) {
                        for (Booking b : bookings)
                            if (b.aadhaar.equals(aadhaar) && b.date.equals(date))
                                throw new InvalidPassengerException("This Aadhaar already has a booking for this date.");
                        train.bookSeat();
                        bookings.add(new Booking(aadhaar, name, age, gender, date, train, ticket));
                    }
                    SwingUtilities.invokeLater(() -> {
                        refreshTrains();
                        refreshBookingTable();
                        JOptionPane.showMessageDialog(this, "Ticket booked successfully!",
                            "Booking successful", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    });
                } catch (InvalidPassengerException | SeatNotAvailableException ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Booking error", JOptionPane.ERROR_MESSAGE));
                }
            }, "BookingThread").start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a whole number.", "Input error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidPassengerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid details", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBookingTable() {
        tableModel.setRowCount(0);
        synchronized (bookings) {
            for (Booking b : bookings) {
                String maskedAadhaar = "XXXX-XXXX-" + b.aadhaar.substring(8);
                tableModel.addRow(new Object[] {
                    maskedAadhaar, b.name, b.age, b.gender, b.train.getFrom(), b.train.getTo(),
                    b.date, b.train.getNumber() + " - " + b.train.getName(),
                    b.train.getAvailableSeats() + " seats left", b.ticket.getCategory(),
                    String.format("%.2f", b.ticket.getFare())
                });
            }
        }
        updateSeatAndFare();
    }

    private void cancelTicket() {
        String aadhaar = cancelAadhaarField.getText().trim().replaceAll("\\s+", "");
        try {
            if (!aadhaar.matches("\\d{12}"))
                throw new BookingNotFoundException("Enter the same 12-digit dummy Aadhaar number used for booking.");
            Booking found = null;
            synchronized (bookings) {
                for (Booking b : bookings) {
                    if (b.aadhaar.equals(aadhaar)) { found = b; break; }
                }
                if (found == null) throw new BookingNotFoundException("No booking found for this Aadhaar number.");
                bookings.remove(found);
            }
            found.train.cancelSeat();
            refreshTrains();
            refreshBookingTable();
            JOptionPane.showMessageDialog(this, "Ticket cancelled successfully.",
                "Cancellation successful", JOptionPane.INFORMATION_MESSAGE);
            cancelAadhaarField.setText("");
        } catch (BookingNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        aadhaarField.setText("");
        nameField.setText("");
        ageField.setText("");
    }
}
