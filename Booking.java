public class Booking {
    private int bookingId;
    private String customerName;
    private String movieName;
    private String showTime;
    private int seats;
    private double totalAmount;

    public Booking() {
    }

    public Booking(int bookingId, String customerName, String movieName, String showTime, int seats, double totalAmount) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.movieName = movieName;
        this.showTime = showTime;
        this.seats = seats;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getShowTime() {
        return showTime;
    }

    public int getSeats() {
        return seats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
