public class Movie {
    private int movieId;
    private String movieName;
    private String language;
    private String genre;
    private int duration;
    private double ticketPrice;
    private int availableSeats;

    public Movie() {
    }

    public Movie(String movieName, String language, String genre,
                 int duration, double ticketPrice, int availableSeats) {
        this.movieName = movieName;
        this.language = language;
        this.genre = genre;
        this.duration = duration;
        this.ticketPrice = ticketPrice;
        this.availableSeats = availableSeats;
    }

    public Movie(int movieId, String movieName, String language, String genre,
                 int duration, double ticketPrice, int availableSeats) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.language = language;
        this.genre = genre;
        this.duration = duration;
        this.ticketPrice = ticketPrice;
        this.availableSeats = availableSeats;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getLanguage() {
        return language;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}
