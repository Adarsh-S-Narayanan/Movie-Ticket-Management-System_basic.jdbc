import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MovieTicketManagementSystem {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        DBConnection.testConnection();

        int choice;

        do {
            System.out.println("\n===== MOVIE TICKET MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Movie");
            System.out.println("2. View Movies");
            System.out.println("3. Update Movie");
            System.out.println("4. Delete Movie");
            System.out.println("5. Book Ticket");
            System.out.println("6. View Bookings");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    viewMovies();
                    break;
                case 3:
                    updateMovie();
                    break;
                case 4:
                    deleteMovie();
                    break;
                case 5:
                    bookTicket();
                    break;
                case 6:
                    viewBookings();
                    break;
                case 7:
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);

        sc.close();
    }

    // =========================================================================
    // SET 1: CREATE / INSERT OPERATIONS (All Insert Methods Together)
    // =========================================================================

    // CREATE 1: Insert a new movie into database
    static void addMovie() {
        try {
            System.out.print("Movie name: ");
            String name = sc.nextLine();

            System.out.print("Language: ");
            String language = sc.nextLine();

            System.out.print("Genre: ");
            String genre = sc.nextLine();

            System.out.print("Duration in minutes: ");
            int duration = sc.nextInt();

            System.out.print("Ticket price: ");
            double price = sc.nextDouble();

            System.out.print("Available seats: ");
            int seats = sc.nextInt();
            sc.nextLine();

            String sql = "INSERT INTO movies " +
                    "(movie_name, language, genre, duration, ticket_price, available_seats) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, language);
            ps.setString(3, genre);
            ps.setInt(4, duration);
            ps.setDouble(5, price);
            ps.setInt(6, seats);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Movie added successfully.");
            }

            ps.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    // CREATE 2: Insert a new booking record into database
    static void createBookingRecord(Connection con, String customerName, int movieId, String showTime, int seats, double total) throws SQLException {
        String bookingSql = "INSERT INTO bookings (customer_name, movie_id, show_time, seats, total_amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement bookingPs = con.prepareStatement(bookingSql)) {
            bookingPs.setString(1, customerName);
            bookingPs.setInt(2, movieId);
            bookingPs.setString(3, showTime);
            bookingPs.setInt(4, seats);
            bookingPs.setDouble(5, total);
            bookingPs.executeUpdate();
        }
    }

    // =========================================================================
    // SET 2: READ / SELECT OPERATIONS (All Select Methods Together)
    // =========================================================================

    // READ 1: Select and view all movies
    static void viewMovies() {
        String sql = "SELECT * FROM movies";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID  Movie                 Language     Genre       Price   Seats");

            while (rs.next()) {
                System.out.printf("%-3d %-21s %-12s %-10s %-7.2f %-5d%n",
                        rs.getInt("movie_id"),
                        rs.getString("movie_name"),
                        rs.getString("language"),
                        rs.getString("genre"),
                        rs.getDouble("ticket_price"),
                        rs.getInt("available_seats"));
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // READ 2: Select and view all customer bookings
    static void viewBookings() {
        String sql = "SELECT b.booking_id, b.customer_name, m.movie_name, " +
                "b.show_time, b.seats, b.total_amount " +
                "FROM bookings b INNER JOIN movies m ON b.movie_id = m.movie_id";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nID  Customer             Movie                 Show       Seats   Amount");

            while (rs.next()) {
                System.out.printf("%-3d %-20s %-21s %-10s %-7d %.2f%n",
                        rs.getInt("booking_id"),
                        rs.getString("customer_name"),
                        rs.getString("movie_name"),
                        rs.getString("show_time"),
                        rs.getInt("seats"),
                        rs.getDouble("total_amount"));
            }

            rs.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // READ 3: Select ticket price and available seats for a specific movie ID
    static MovieBookingInfo fetchMovieForBooking(Connection con, int movieId) throws SQLException {
        String movieSql = "SELECT ticket_price, available_seats FROM movies WHERE movie_id = ?";
        try (PreparedStatement moviePs = con.prepareStatement(movieSql)) {
            moviePs.setInt(1, movieId);
            try (ResultSet rs = moviePs.executeQuery()) {
                if (rs.next()) {
                    return new MovieBookingInfo(rs.getDouble("ticket_price"), rs.getInt("available_seats"));
                }
            }
        }
        return null;
    }

    // =========================================================================
    // SET 3: UPDATE OPERATIONS (All Update Methods Together)
    // =========================================================================

    // UPDATE 1: Update ticket price and seats for a movie
    static void updateMovie() {
        try {
            System.out.print("Enter movie ID to update: ");
            int id = sc.nextInt();

            System.out.print("Enter new ticket price: ");
            double price = sc.nextDouble();

            System.out.print("Enter new available seats: ");
            int seats = sc.nextInt();
            sc.nextLine();

            String sql = "UPDATE movies SET ticket_price = ?, available_seats = ? " +
                    "WHERE movie_id = ?";

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, price);
            ps.setInt(2, seats);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Movie updated successfully.");
            } else {
                System.out.println("Movie ID not found.");
            }

            ps.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }

    // UPDATE 2: Update remaining available seats count for a movie after booking
    static void updateMovieSeats(Connection con, int movieId, int newSeatCount) throws SQLException {
        String updateSql = "UPDATE movies SET available_seats = ? WHERE movie_id = ?";
        try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
            updatePs.setInt(1, newSeatCount);
            updatePs.setInt(2, movieId);
            updatePs.executeUpdate();
        }
    }

    // =========================================================================
    // SET 4: DELETE OPERATIONS (All Delete Methods Together)
    // =========================================================================

    // DELETE 1: Delete a movie record from database
    static void deleteMovie() {
        try {
            System.out.print("Enter movie ID to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String sql = "DELETE FROM movies WHERE movie_id = ?";

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Movie deleted successfully.");
            } else {
                System.out.println("Movie ID not found.");
            }

            ps.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Cannot delete this movie if it has bookings.");
            System.out.println(e.getMessage());
        }
    }

    // =========================================================================
    // HELPER & MAIN WORKFLOW METHOD
    // =========================================================================

    // Helper data structure for booking operation
    static class MovieBookingInfo {
        double ticketPrice;
        int availableSeats;

        MovieBookingInfo(double ticketPrice, int availableSeats) {
            this.ticketPrice = ticketPrice;
            this.availableSeats = availableSeats;
        }
    }

    // Orchestration method that calls the individual operations in sequence
    static void bookTicket() {
        try {
            viewMovies();

            System.out.print("\nEnter movie ID: ");
            int movieId = sc.nextInt();
            sc.nextLine();

            System.out.print("Customer name: ");
            String customerName = sc.nextLine();

            System.out.print("Show time: ");
            String showTime = sc.nextLine();

            System.out.print("Number of seats: ");
            int seats = sc.nextInt();
            sc.nextLine();

            Connection con = DBConnection.getConnection();

            // 1. READ operation: fetch movie details
            MovieBookingInfo movieInfo = fetchMovieForBooking(con, movieId);

            if (movieInfo == null) {
                System.out.println("Movie not found.");
                con.close();
                return;
            }

            if (seats <= 0) {
                System.out.println("Number of seats must be greater than zero.");
            } else if (seats > movieInfo.availableSeats) {
                System.out.println("Not enough seats available.");
            } else {
                double total = seats * movieInfo.ticketPrice;

                // 2. CREATE operation: insert booking record
                createBookingRecord(con, customerName, movieId, showTime, seats, total);

                // 3. UPDATE operation: update movie seats
                updateMovieSeats(con, movieId, movieInfo.availableSeats - seats);

                System.out.println("Ticket booked successfully.");
                System.out.println("Total amount: " + total);
            }

            con.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input.");
            sc.nextLine();
        }
    }
}
