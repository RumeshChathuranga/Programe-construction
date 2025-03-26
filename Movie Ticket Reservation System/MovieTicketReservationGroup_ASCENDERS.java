/**
 * Movie Ticket Reservation System
  
 * Functional Requirements:
 * 1. Browse movies with unique movie codes
 * 2. View available dates and showtimes for movies
 * 3. Select a movie, date, and showtime
 * 4. Specify number of tickets
 * 5. Choose seats
 * 6. Handle error scenarios using custom exceptions:
 *    - InvalidMovieCodeException for nonexistent movie codes
 *    - InvalidDateException for invalid dates
 *    - InvalidShowtimeException for invalid showtimes
 *    - InvalidTicketQuantityException for invalid ticket quantities
 *    - OverbookingException for attempting to book more tickets than available
 * 7. Handle user inactivity by saving session
 * 8. Calculate total bill
 * 9. Collect user email for billing
 * 10. Generate bill and simulate email
 
 * Non-Functional Requirements:
 * 1. Usability - Clear error messages
 * 2. Reliability - Proper exception handling
 * 3. Performance - Efficient processing
 * 4. Maintainability - Documented code
 * 5. Security - Basic data validation
 
 */

 import java.io.*;
 import java.util.*;
 
 /**
  * Main class for the Movie Ticket Reservation System.
  */
 public class MovieTicketReservationGroup_ASCENDERS {
    
     public static void main(String[] args) {
         System.out.println("Welcome to the Movie Ticket Reservation System!");
         
         MovieDatabase movieDB = null;
         Scanner scanner = new Scanner(System.in);
         
         try {
             // Load movies from CSV file
             movieDB = new MovieDatabase("C:\\Users\\rumar\\Desktop\\Movie Reservation Dataset.csv");
             
             // Display all movies
             System.out.println("\nAvailable Movies:");
             ArrayList<Movie> movies = movieDB.getAllMovies();
             for (Movie movie : movies) {
                 System.out.println(movie);
             }
             
             // Get movie selection
             Movie selectedMovie = null;
             while (selectedMovie == null) {
                 try {
                     System.out.print("\nEnter movie code: ");
                     String movieCode = scanner.nextLine();
                     selectedMovie = movieDB.getMovieByCode(movieCode);
                 } catch (InvalidMovieCodeException e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             }
             
             System.out.println("Selected movie: " + selectedMovie.getName());
             
             // Get date selection
             String selectedDate = null;
             ArrayList<String> dates = selectedMovie.getAvailableDates();
             System.out.println("\nAvailable Dates:");
             for (int i = 0; i < dates.size(); i++) {
                 System.out.println((i+1) + ". " + dates.get(i));
             }
             
             while (selectedDate == null) {
                 try {
                     System.out.print("\nSelect date (enter number): ");
                     int dateChoice = Integer.parseInt(scanner.nextLine());
                     
                     if (dateChoice < 1 || dateChoice > dates.size()) {
                         throw new InvalidDateException("Invalid date selection. Please choose between 1 and " + dates.size());
                     }
                     
                     selectedDate = dates.get(dateChoice - 1);
                 } catch (NumberFormatException e) {
                     System.out.println("Error: Please enter a valid number");
                 } catch (InvalidDateException e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             }
             
             System.out.println("Selected date: " + selectedDate);
             
             // Get showtime selection
             Showtime selectedShowtime = null;
             ArrayList<Showtime> showtimes = selectedMovie.getShowtimesForDate(selectedDate);
             
             System.out.println("\nAvailable Showtimes:");
             for (int i = 0; i < showtimes.size(); i++) {
                 System.out.println((i+1) + ". " + showtimes.get(i));
             }
             
             while (selectedShowtime == null) {
                 try {
                     System.out.print("\nSelect showtime (enter number): ");
                     int showtimeChoice = Integer.parseInt(scanner.nextLine());
                     
                     if (showtimeChoice < 1 || showtimeChoice > showtimes.size()) {
                         throw new InvalidShowtimeException("Invalid showtime selection. Please choose between 1 and " + showtimes.size());
                     }
                     
                     selectedShowtime = showtimes.get(showtimeChoice - 1);
                 } catch (NumberFormatException e) {
                     System.out.println("Error: Please enter a valid number");
                 } catch (InvalidShowtimeException e) {
                     System.out.println("Error: " + e.getMessage());
                 }
             }
             
             System.out.println("Selected showtime: " + selectedShowtime.getTime() + 
                     " (Available seats: " + selectedShowtime.getAvailableSeats() + ")");
             
             // Get number of tickets
             int numTickets = 0;
             while (numTickets <= 0) {
                 try {
                     System.out.print("\nEnter number of tickets: ");
                     numTickets = Integer.parseInt(scanner.nextLine());
                     
                     if (numTickets <= 0) {
                         throw new InvalidTicketQuantityException("Number of tickets must be positive");
                     }
                     
                     if (numTickets > selectedShowtime.getAvailableSeats()) {
                         throw new OverbookingException("Not enough seats available. Available: " + 
                                 selectedShowtime.getAvailableSeats());
                     }
                     
                 } catch (NumberFormatException e) {
                     System.out.println("Error: Please enter a valid number");
                 } catch (InvalidTicketQuantityException | OverbookingException e) {
                     System.out.println("Error: " + e.getMessage());
                     numTickets = 0;
                 }
             }
             
             // Create booking
             Booking booking = new Booking(selectedMovie, selectedDate, selectedShowtime, numTickets);
             
             // Simulate inactivity timer
             System.out.println("\nSimulating inactivity timer (in a real system this would monitor user activity)");
             System.out.println("Session will be saved if user is inactive for too long");
             
             // Select seats (simple implementation)
             System.out.println("\nSelect your seats:");
             for (int i = 0; i < numTickets; i++) {
                 boolean validSeat = false;
                 while (!validSeat) {
                     try {
                         System.out.print("Seat " + (i+1) + " (row,column format e.g. 1,3): ");
                         String seatInput = scanner.nextLine();
                         String[] parts = seatInput.split(",");
                         
                         if (parts.length != 2) {
                             throw new InvalidTicketQuantityException("Invalid seat format. Use row,column (e.g. 1,3)");
                         }
                         
                         int row = Integer.parseInt(parts[0].trim());
                         int col = Integer.parseInt(parts[1].trim());
                         
                         // Check if seat is valid (simple check)
                         if (row < 0 || row >= 10 || col < 0 || col >= 10) {
                             throw new InvalidTicketQuantityException("Invalid seat. Row and column must be between 0 and 9");
                         }
                         
                         String seat = row + "," + col;
                         booking.addSeat(seat);
                         validSeat = true;
                         
                     } catch (NumberFormatException e) {
                         System.out.println("Error: Please enter valid numbers for row and column");
                     } catch (InvalidTicketQuantityException e) {
                         System.out.println("Error: " + e.getMessage());
                     }
                 }
             }
             
             // Calculate total
             double totalAmount = numTickets * selectedShowtime.getTicketPrice();
             booking.setTotalAmount(totalAmount);
             
             // Display booking details
             System.out.println("\n--- Booking Details ---");
             System.out.println(booking);
             
             // Get email
             String email = "";
             while (email.isEmpty()) {
                 System.out.print("\nEnter your email for billing: ");
                 email = scanner.nextLine();
                 
                 // Simple validation
                 if (!email.contains("@") || !email.contains(".")) {
                     System.out.println("Error: Please enter a valid email address");
                     email = "";
                 }
             }
             
             // Confirm booking
             System.out.print("\nConfirm booking? (y/n): ");
             String confirm = scanner.nextLine();
             
             if (confirm.equalsIgnoreCase("y")) {
                 System.out.println("\nBooking confirmed!");
                 System.out.println("A PDF bill would be generated and sent to " + email + " in a real system");
             } else {
                 System.out.println("\nBooking cancelled");
             }
             
         } catch (IOException e) {
             System.out.println("Error loading movie database: " + e.getMessage());
         } finally {
             if (scanner != null) {
                 scanner.close();
             }
         }
     }
 }
 
 /**
  * Custom exception for invalid movie codes.
  */
 class InvalidMovieCodeException extends Exception {
     
     public InvalidMovieCodeException(String message) {
         super(message);
     }
 }
 
 /**
  * Custom exception for invalid dates.
  */
 class InvalidDateException extends Exception {
     
     public InvalidDateException(String message) {
         super(message);
     }
 }
 
 /**
  * Custom exception for invalid showtimes.
  */
 class InvalidShowtimeException extends Exception {
     
     public InvalidShowtimeException(String message) {
         super(message);
     }
 }
 
 /**
  * Custom exception for invalid ticket quantities.
  */
 class InvalidTicketQuantityException extends Exception {
     
     public InvalidTicketQuantityException(String message) {
         super(message);
     }
 }
 
 /**
  * Custom exception for overbooking situations.
  */
 class OverbookingException extends Exception {
     
     public OverbookingException(String message) {
         super(message);
     }
 }
 
 /**
  * Class representing a Movie.
  */
 class Movie {
     private String code;
     private String name;
     private String language;
     private String genre;
     private HashMap<String, ArrayList<Showtime>> showtimes;
     
     /**
      * Constructor for Movie.
      */
     public Movie(String code, String name, String language, String genre) {
         this.code = code;
         this.name = name;
         this.language = language;
         this.genre = genre;
         this.showtimes = new HashMap<>();
     }
     
     /**
      * Add a showtime for this movie.
      */
     public void addShowtime(String date, Showtime showtime) {
         if (!showtimes.containsKey(date)) {
             showtimes.put(date, new ArrayList<>());
         }
         showtimes.get(date).add(showtime);
     }
     
     /**
      * Get available dates for this movie.
      */
     public ArrayList<String> getAvailableDates() {
         return new ArrayList<>(showtimes.keySet());
     }
     
     /**
      * Get showtimes for a specific date.
      */
     public ArrayList<Showtime> getShowtimesForDate(String date) {
         return showtimes.getOrDefault(date, new ArrayList<>());
     }
     
     /**
      * Get movie code.
      */
     public String getCode() {
         return code;
     }
     
     /**
      * Get movie name.
      */
     public String getName() {
         return name;
     }
     
     /**
      * Get movie language.
      */
     public String getLanguage() {
         return language;
     }
     
     /**
      * Get movie genre.
      */
     public String getGenre() {
         return genre;
     }
     
     public String toString() {
         return code + " - " + name + " (" + language + ", " + genre + ")";
     }
 }
 
 /**
  * Class representing a Showtime.
  */
 class Showtime {
     private String time;
     private int totalSeats;
     private int availableSeats;
     private double ticketPrice;
     
     /**
      * Constructor for Showtime.
      */
     public Showtime(String time, int totalSeats, int availableSeats, double ticketPrice) {
         this.time = time;
         this.totalSeats = totalSeats;
         this.availableSeats = availableSeats;
         this.ticketPrice = ticketPrice;
     }
     
     /**
      * Get showtime.
      */
     public String getTime() {
         return time;
     }
     
     /**
      * Get total seats.
      */
     public int getTotalSeats() {
         return totalSeats;
     }
     
     /**
      * Get available seats.
      */
     public int getAvailableSeats() {
         return availableSeats;
     }
     
     /**
      * Get ticket price.
      */
     public double getTicketPrice() {
         return ticketPrice;
     }
     
     /**
      * Book seats.
      */
     public void bookSeats(int numSeats) throws OverbookingException {
         if (numSeats > availableSeats) {
             throw new OverbookingException("Not enough seats available");
         }
         availableSeats -= numSeats;
     }
     

     public String toString() {
         return time + " - Available: " + availableSeats + "/" + totalSeats + " - Price: $" + ticketPrice;

     }
 }
 
 /**
  * Class representing a Movie Database that loads data from CSV.
  */
 class MovieDatabase {
     private HashMap<String, Movie> movies;
     
     /**
      * Constructor for MovieDatabase.
      */
     public MovieDatabase(String csvFile) throws IOException {
         movies = new HashMap<>();
         loadMoviesFromCSV(csvFile);
     }
     
     /**
      * Load movies from CSV file.
      */
     private void loadMoviesFromCSV(String csvFile) throws IOException {
         BufferedReader reader = null;
         
         try {
             reader = new BufferedReader(new FileReader(csvFile));
             String line;
             
             // Skip header line
             reader.readLine();
             
             while ((line = reader.readLine()) != null) {
                 String[] data = line.split(",");
                 
                 // Parse data
                 String movieCode = data[0];
                 String movieName = data[1];
                 String date = data[2];
                 String showtime = data[3];
                 int totalSeats = Integer.parseInt(data[4]);
                 int availableSeats = Integer.parseInt(data[5]);
                 double ticketPrice = Double.parseDouble(data[6]);
                 String language = data[7];
                 String genre = data[8];

                 // Get or create movie
                 Movie movie;
                 if (movies.containsKey(movieCode)) {
                     movie = movies.get(movieCode);
                 } else {
                     movie = new Movie(movieCode, movieName, language, genre);
                     movies.put(movieCode, movie);
                 }
                 
                 // Add showtime
                 Showtime show = new Showtime(showtime, totalSeats, availableSeats, ticketPrice);
                 movie.addShowtime(date, show);
             }
         } finally {
             if (reader != null) {
                 reader.close();
             }
         }
     }
     
     /**
      * Get all movies.
      */
     public ArrayList<Movie> getAllMovies() {
         return new ArrayList<>(movies.values());
     }
     
     /**
      * Get movie by code.
      */
     public Movie getMovieByCode(String code) throws InvalidMovieCodeException {
         if (!movies.containsKey(code)) {
             throw new InvalidMovieCodeException("Movie code " + code + " not found");
         }
         return movies.get(code);
     }
 }
 
 /**
  * Class representing a booking.
  */
 class Booking {
     private Movie movie;
     private String date;
     private Showtime showtime;
     private int numTickets;
     private ArrayList<String> seats;
     private double totalAmount;
     private String bookingId;
     
     /**
      * Constructor for Booking.
      */
     public Booking(Movie movie, String date, Showtime showtime, int numTickets) {
         this.movie = movie;
         this.date = date;
         this.showtime = showtime;
         this.numTickets = numTickets;
         this.seats = new ArrayList<>();
         // Generate simple booking ID
         this.bookingId = "BK" + (int)(Math.random() * 10000);
     }
     
     /**
      * Add a seat to the booking.
      */
     public void addSeat(String seat) {
         seats.add(seat);
     }
     
     /**
      * Set total amount for booking.
      */
     public void setTotalAmount(double amount) {
         this.totalAmount = amount;
     }
     
    
     public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("Booking ID: ").append(bookingId).append("\n");
         sb.append("Movie: ").append(movie.getName()).append("\n");
         sb.append("Date: ").append(date).append("\n");
         sb.append("Time: ").append(showtime.getTime()).append("\n");
         sb.append("Tickets: ").append(numTickets).append("\n");
         sb.append("Seats: ");
         for (String seat : seats) {
             sb.append(seat).append(" ");
         }
         sb.append("\n");
         sb.append("Total Amount: $").append(totalAmount);
         return sb.toString();
     }
 }