import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Movie Ticket Reservation System with improved navigation
 */
public class MovieTicketReservationGroup_ASCENDERS2 {
    
    // GUI components
    private static JFrame frame;
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    
    // Selected data
    private static String selectedMovieCode = "";
    private static String selectedMovieName = "";
    private static String selectedDate = "";
    private static String selectedShowtime = "";
    private static int selectedTickets = 1;
    private static String userEmail = "";
    
    /**
     * Main method - entry point of the application.
     */
    public static void main(String[] args) {
        try {
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create GUI on Event Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create and display the application GUI.
     */
    private static void createAndShowGUI() {
        // Create main frame
        frame = new JFrame("Movie Ticket Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        
        // Create card layout for navigation
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // Create panels for each step
        cardPanel.add(createMovieSelectionPanel(), "SelectMovie");
        cardPanel.add(createBookTicketsPanel(), "BookTickets");
        cardPanel.add(createConfirmationPanel(), "Confirmation");
        
        // Add panel to frame
        frame.add(cardPanel);
        
        // Show frame
        frame.setVisible(true);
    }
    
    /**
     * Create the movie selection panel.
     */
    private static JPanel createMovieSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Available Movies", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Movie table
        String[] columnNames = {"Code", "Name", "Language", "Genre"};
        Object[][] data = {
            {"M001", "Avengers: Endgame", "English", "Action"},
            {"M002", "The Lion King", "English", "Animation"},
            {"M003", "Joker", "English", "Drama"},
            {"M004", "Parasite", "Korean", "Thriller"}
        };
        
        JTable movieTable = new JTable(data, columnNames);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(movieTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nextButton = new JButton("Next");
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = movieTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Save selected movie info
                    selectedMovieCode = (String) movieTable.getValueAt(selectedRow, 0);
                    selectedMovieName = (String) movieTable.getValueAt(selectedRow, 1);
                    
                    // Update booking panel with selected movie
                    updateBookingPanel();
                    
                    // Show next panel
                    cardLayout.show(cardPanel, "BookTickets");
                } else {
                    JOptionPane.showMessageDialog(panel, 
                        "Please select a movie first", 
                        "No Selection", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the ticket booking panel.
     */
    private static JPanel createBookTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Book Tickets", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Movie info panel
        JPanel movieInfoPanel = new JPanel();
        movieInfoPanel.setLayout(new BoxLayout(movieInfoPanel, BoxLayout.Y_AXIS));
        JLabel movieLabel = new JLabel("Movie: ");
        movieLabel.setFont(new Font("Arial", Font.BOLD, 14));
        movieInfoPanel.add(movieLabel);
        movieInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Top panel with title and movie info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(movieInfoPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Date selection
        JLabel dateLabel = new JLabel("Date:", JLabel.LEFT);
        String[] dates = {"2023-03-26", "2023-03-27", "2023-03-28"};
        JComboBox<String> dateCombo = new JComboBox<>(dates);
        dateCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDate = (String) dateCombo.getSelectedItem();
            }
        });
        formPanel.add(dateLabel);
        formPanel.add(dateCombo);
        
        // Showtime selection
        JLabel showtimeLabel = new JLabel("Showtime:", JLabel.LEFT);
        String[] showtimes = {"10:00 AM", "2:00 PM", "6:00 PM", "9:00 PM"};
        JComboBox<String> showtimeCombo = new JComboBox<>(showtimes);
        showtimeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedShowtime = (String) showtimeCombo.getSelectedItem();
            }
        });
        formPanel.add(showtimeLabel);
        formPanel.add(showtimeCombo);
        
        // Ticket quantity
        JLabel ticketsLabel = new JLabel("Number of Tickets:", JLabel.LEFT);
        SpinnerNumberModel ticketModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner ticketSpinner = new JSpinner(ticketModel);
        ticketSpinner.addChangeListener(e -> {
            selectedTickets = (Integer) ticketSpinner.getValue();
        });
        formPanel.add(ticketsLabel);
        formPanel.add(ticketSpinner);
        
        // Email
        JLabel emailLabel = new JLabel("Email:", JLabel.LEFT);
        JTextField emailField = new JTextField();
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "SelectMovie"));
        
        JButton nextButton = new JButton("Book Tickets");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate and save email
                userEmail = emailField.getText().trim();
                
                if (userEmail.isEmpty() || !userEmail.contains("@") || !userEmail.contains(".")) {
                    JOptionPane.showMessageDialog(panel, 
                        "Please enter a valid email address", 
                        "Invalid Email", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Update confirmation details
                updateConfirmationPanel();
                
                // Show confirmation panel
                cardLayout.show(cardPanel, "Confirmation");
            }
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create the confirmation panel.
     */
    private static JPanel createConfirmationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Booking Confirmation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Confirmation text area
        JTextArea confirmationArea = new JTextArea();
        confirmationArea.setEditable(false);
        confirmationArea.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmationArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(confirmationArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "BookTickets"));
        
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookingId = "BK" + (int)(Math.random() * 10000);
                
                JOptionPane.showMessageDialog(panel, 
                    "Booking Confirmed!\n\n" +
                    "Booking ID: " + bookingId + "\n\n" +
                    "A PDF bill has been sent to " + userEmail,
                    "Booking Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reset and go back to first step
                resetBookingData();
                cardLayout.show(cardPanel, "SelectMovie");
            }
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Update the booking panel with selected movie information.
     */
    private static void updateBookingPanel() {
        // Get the booking panel
        JPanel bookingPanel = (JPanel) cardPanel.getComponent(1);
        
        // Get the top panel
        JPanel topPanel = (JPanel) bookingPanel.getComponent(0);
        
        // Get the movie info panel
        JPanel movieInfoPanel = (JPanel) topPanel.getComponent(1);
        
        // Update movie label
        JLabel movieLabel = (JLabel) movieInfoPanel.getComponent(0);
        movieLabel.setText("Movie: " + selectedMovieName + " (" + selectedMovieCode + ")");
    }
    
    /**
     * Update the confirmation panel with booking details.
     */
    private static void updateConfirmationPanel() {
        // Get confirmation panel
        JPanel confirmPanel = (JPanel) cardPanel.getComponent(2);
        
        // Get the text area from the scroll pane
        JScrollPane scrollPane = (JScrollPane) confirmPanel.getComponent(1);
        JTextArea confirmationArea = (JTextArea) scrollPane.getViewport().getView();
        
        // Calculate total price (assume $10 per ticket)
        double totalPrice = selectedTickets * 10.0;
        
        // Update confirmation text
        String confirmationText = "Booking Details:\n\n";
        confirmationText += "Movie: " + selectedMovieName + " (" + selectedMovieCode + ")\n";
        confirmationText += "Date: " + selectedDate + "\n";
        confirmationText += "Showtime: " + selectedShowtime + "\n";
        confirmationText += "Number of Tickets: " + selectedTickets + "\n";
        confirmationText += "Total Price: $" + totalPrice + "\n";
        confirmationText += "Email: " + userEmail + "\n\n";
        confirmationText += "Please review your booking details above and click 'Confirm Booking' to finalize.";
        
        confirmationArea.setText(confirmationText);
    }
    
    /**
     * Reset all booking data for a new booking.
     */
    private static void resetBookingData() {
        selectedMovieCode = "";
        selectedMovieName = "";
        selectedDate = "";
        selectedShowtime = "";
        selectedTickets = 1;
        userEmail = "";
    }
}