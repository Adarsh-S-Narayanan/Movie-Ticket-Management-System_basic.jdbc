import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieTicketGUI extends Application {

    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    private TableView<Movie> movieTable = new TableView<>();
    private TableView<Booking> bookingTable = new TableView<>();
    private ComboBox<Movie> movieComboBox = new ComboBox<>();

    private Label statusLabel = new Label("System Ready");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Movie Ticket Management System");

        // Top Header
        HBox header = createHeader();

        // Main Tab Pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab moviesTab = new Tab("🎬 Movies", createMoviesTabContent());
        Tab bookTab = new Tab("🎟️ Book Ticket", createBookTicketTabContent());
        Tab bookingsTab = new Tab("📋 View Bookings", createBookingsTabContent());

        tabPane.getTabs().addAll(moviesTab, bookTab, bookingsTab);

        // Bottom Status Bar
        HBox statusBar = createStatusBar();

        // Main Root Layout
        VBox root = new VBox(header, tabPane, statusBar);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 1050, 680);
        applyModernTheme(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial Data Load
        refreshMovies();
        refreshBookings();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(18, 25, 18, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1e1b4b, #312e81);");

        Label titleLabel = new Label("🎬 Cinema Management System");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dbStatus = new Label("● Database Connected");
        dbStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        dbStatus.setStyle("-fx-text-fill: #34d399; -fx-background-color: rgba(52, 211, 153, 0.15); -fx-padding: 6 12 6 12; -fx-background-radius: 15;");

        header.getChildren().addAll(titleLabel, spacer, dbStatus);
        return header;
    }

    private VBox createMoviesTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Movie Table setup
        setupMovieTable();

        FilteredList<Movie> filteredMovies = new FilteredList<>(movieList, p -> true);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by movie name, genre, or language...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMovies.setPredicate(movie -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerFilter = newValue.toLowerCase();
                return movie.getMovieName().toLowerCase().contains(lowerFilter) ||
                        movie.getGenre().toLowerCase().contains(lowerFilter) ||
                        movie.getLanguage().toLowerCase().contains(lowerFilter);
            });
        });

        movieTable.setItems(filteredMovies);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setOnAction(e -> refreshMovies());

        Button deleteBtn = new Button("🗑️ Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> deleteSelectedMovie());

        HBox tableControls = new HBox(12, searchField, refreshBtn, deleteBtn);
        tableControls.setAlignment(Pos.CENTER_LEFT);

        // Add / Update Movie Form Card
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(18));
        formGrid.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 4);");

        Label formTitle = new Label("Add New Movie / Update");
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        formTitle.setStyle("-fx-text-fill: #1e293b;");
        formGrid.add(formTitle, 0, 0, 4, 1);

        TextField nameInput = new TextField();
        nameInput.setPromptText("Movie Name");

        TextField langInput = new TextField();
        langInput.setPromptText("Language");

        TextField genreInput = new TextField();
        genreInput.setPromptText("Genre");

        TextField durationInput = new TextField();
        durationInput.setPromptText("Duration (min)");

        TextField priceInput = new TextField();
        priceInput.setPromptText("Ticket Price ($)");

        TextField seatsInput = new TextField();
        seatsInput.setPromptText("Available Seats");

        formGrid.add(new Label("Name:"), 0, 1);
        formGrid.add(nameInput, 1, 1);
        formGrid.add(new Label("Language:"), 2, 1);
        formGrid.add(langInput, 3, 1);

        formGrid.add(new Label("Genre:"), 0, 2);
        formGrid.add(genreInput, 1, 2);
        formGrid.add(new Label("Duration:"), 2, 2);
        formGrid.add(durationInput, 3, 2);

        formGrid.add(new Label("Price:"), 0, 3);
        formGrid.add(priceInput, 1, 3);
        formGrid.add(new Label("Seats:"), 2, 3);
        formGrid.add(seatsInput, 3, 3);

        Button addBtn = new Button("➕ Add Movie");
        addBtn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> {
            try {
                String name = nameInput.getText().trim();
                String lang = langInput.getText().trim();
                String genre = genreInput.getText().trim();
                int duration = Integer.parseInt(durationInput.getText().trim());
                double price = Double.parseDouble(priceInput.getText().trim());
                int seats = Integer.parseInt(seatsInput.getText().trim());

                if (name.isEmpty() || lang.isEmpty() || genre.isEmpty()) {
                    showError("Please fill in all text fields.");
                    return;
                }

                saveMovie(name, lang, genre, duration, price, seats);

                nameInput.clear();
                langInput.clear();
                genreInput.clear();
                durationInput.clear();
                priceInput.clear();
                seatsInput.clear();

            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for Duration, Price, and Seats.");
            }
        });

        Button updateSelectedBtn = new Button("✏️ Update Selected Movie");
        updateSelectedBtn.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold;");
        updateSelectedBtn.setOnAction(e -> {
            Movie selected = movieTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Please select a movie from the table first.");
                return;
            }
            try {
                double price = Double.parseDouble(priceInput.getText().trim());
                int seats = Integer.parseInt(seatsInput.getText().trim());
                updateMovieInDb(selected.getMovieId(), price, seats);
            } catch (NumberFormatException ex) {
                showError("Please enter price and seats to update the selected movie.");
            }
        });

        HBox actionBtns = new HBox(10, addBtn, updateSelectedBtn);
        formGrid.add(actionBtns, 1, 4, 3, 1);

        content.getChildren().addAll(tableControls, movieTable, formGrid);
        VBox.setVgrow(movieTable, Priority.ALWAYS);
        return content;
    }

    private void setupMovieTable() {
        TableColumn<Movie, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("movieId"));
        idCol.setPrefWidth(60);

        TableColumn<Movie, String> nameCol = new TableColumn<>("Movie Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        nameCol.setPrefWidth(220);

        TableColumn<Movie, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        langCol.setPrefWidth(120);

        TableColumn<Movie, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setPrefWidth(120);

        TableColumn<Movie, Integer> durCol = new TableColumn<>("Duration (min)");
        durCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durCol.setPrefWidth(110);

        TableColumn<Movie, Double> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
        priceCol.setPrefWidth(100);

        TableColumn<Movie, Integer> seatsCol = new TableColumn<>("Available Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        seatsCol.setPrefWidth(120);

        movieTable.getColumns().addAll(idCol, nameCol, langCol, genreCol, durCol, priceCol, seatsCol);
        movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private VBox createBookTicketTabContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(18);
        card.setMaxWidth(550);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 12, 0, 0, 6);");

        Label cardTitle = new Label("🎟️ Book Movie Ticket");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        cardTitle.setStyle("-fx-text-fill: #1e1b4b;");

        // Form Fields
        Label movieLbl = new Label("Select Movie:");
        movieComboBox.setPromptText("Choose a movie...");
        movieComboBox.setMaxWidth(Double.MAX_VALUE);

        // Display movie name in combobox
        movieComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Movie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMovieName() + " ($" + item.getTicketPrice() + " | " + item.getAvailableSeats() + " seats left)");
                }
            }
        });
        movieComboBox.setButtonCell(movieComboBox.getCellFactory().call(null));

        Label customerLbl = new Label("Customer Name:");
        TextField customerInput = new TextField();
        customerInput.setPromptText("e.g. John Doe");

        Label timeLbl = new Label("Show Time:");
        TextField timeInput = new TextField();
        timeInput.setPromptText("e.g. 06:30 PM");

        Label seatsLbl = new Label("Number of Seats:");
        Spinner<Integer> seatsSpinner = new Spinner<>(1, 50, 1);
        seatsSpinner.setEditable(true);
        seatsSpinner.setMaxWidth(Double.MAX_VALUE);

        // Price Preview Box
        HBox pricePreviewBox = new HBox();
        pricePreviewBox.setPadding(new Insets(12));
        pricePreviewBox.setAlignment(Pos.CENTER);
        pricePreviewBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");
        Label totalLabel = new Label("Total Amount: $0.00");
        totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        totalLabel.setStyle("-fx-text-fill: #4338ca;");
        pricePreviewBox.getChildren().add(totalLabel);

        // Calculate live price total
        Runnable calculateTotal = () -> {
            Movie sel = movieComboBox.getValue();
            if (sel != null && seatsSpinner.getValue() != null) {
                double total = sel.getTicketPrice() * seatsSpinner.getValue();
                totalLabel.setText(String.format("Total Amount: $%.2f", total));
            } else {
                totalLabel.setText("Total Amount: $0.00");
            }
        };

        movieComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal.run());
        seatsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal.run());

        Button confirmBtn = new Button("🎟️ Confirm Booking");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10 0 10 0;");
        confirmBtn.setOnAction(e -> {
            Movie selectedMovie = movieComboBox.getValue();
            String customer = customerInput.getText().trim();
            String showTime = timeInput.getText().trim();
            Integer seats = seatsSpinner.getValue();

            if (selectedMovie == null) {
                showError("Please select a movie.");
                return;
            }
            if (customer.isEmpty() || showTime.isEmpty()) {
                showError("Please provide customer name and show time.");
                return;
            }
            if (seats == null || seats <= 0) {
                showError("Number of seats must be greater than zero.");
                return;
            }
            if (seats > selectedMovie.getAvailableSeats()) {
                showError("Not enough seats available! Only " + selectedMovie.getAvailableSeats() + " seats left.");
                return;
            }

            processBooking(selectedMovie, customer, showTime, seats);

            customerInput.clear();
            timeInput.clear();
            seatsSpinner.getValueFactory().setValue(1);
        });

        card.getChildren().addAll(
                cardTitle,
                movieLbl, movieComboBox,
                customerLbl, customerInput,
                timeLbl, timeInput,
                seatsLbl, seatsSpinner,
                pricePreviewBox,
                confirmBtn
        );

        content.getChildren().add(card);
        return content;
    }

    private VBox createBookingsTabContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Booking Table setup
        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        idCol.setPrefWidth(90);

        TableColumn<Booking, String> custCol = new TableColumn<>("Customer Name");
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        custCol.setPrefWidth(200);

        TableColumn<Booking, String> movieCol = new TableColumn<>("Movie Name");
        movieCol.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        movieCol.setPrefWidth(220);

        TableColumn<Booking, String> timeCol = new TableColumn<>("Show Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("showTime"));
        timeCol.setPrefWidth(130);

        TableColumn<Booking, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
        seatsCol.setPrefWidth(80);

        TableColumn<Booking, Double> amountCol = new TableColumn<>("Total Amount ($)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        amountCol.setPrefWidth(120);

        bookingTable.getColumns().addAll(idCol, custCol, movieCol, timeCol, seatsCol, amountCol);
        bookingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        FilteredList<Booking> filteredBookings = new FilteredList<>(bookingList, b -> true);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by customer or movie name...");
        searchField.setPrefWidth(320);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredBookings.setPredicate(b -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return b.getCustomerName().toLowerCase().contains(filter) ||
                        b.getMovieName().toLowerCase().contains(filter);
            });
        });

        bookingTable.setItems(filteredBookings);

        Button refreshBtn = new Button("🔄 Refresh Bookings");
        refreshBtn.setOnAction(e -> refreshBookings());

        HBox controls = new HBox(12, searchField, refreshBtn);
        controls.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(controls, bookingTable);
        VBox.setVgrow(bookingTable, Priority.ALWAYS);
        return content;
    }

    private HBox createStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(8, 15, 8, 15));
        bar.setStyle("-fx-background-color: #0f172a;");
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setTextFill(Color.web("#94a3b8"));
        bar.getChildren().add(statusLabel);
        return bar;
    }

    // --- Database Operations ---

    private void refreshMovies() {
        movieList.clear();
        String sql = "SELECT * FROM movies";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                movieList.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("movie_name"),
                        rs.getString("language"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getDouble("ticket_price"),
                        rs.getInt("available_seats")
                ));
            }
            movieComboBox.setItems(movieList);
            updateStatus("Movies loaded successfully (" + movieList.size() + " movies)");
        } catch (SQLException e) {
            showError("Database Error loading movies: " + e.getMessage());
        }
    }

    private void refreshBookings() {
        bookingList.clear();
        String sql = "SELECT b.booking_id, b.customer_name, m.movie_name, b.show_time, b.seats, b.total_amount " +
                "FROM bookings b INNER JOIN movies m ON b.movie_id = m.movie_id " +
                "ORDER BY b.booking_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookingList.add(new Booking(
                        rs.getInt("booking_id"),
                        rs.getString("customer_name"),
                        rs.getString("movie_name"),
                        rs.getString("show_time"),
                        rs.getInt("seats"),
                        rs.getDouble("total_amount")
                ));
            }
            updateStatus("Bookings refreshed (" + bookingList.size() + " total bookings)");
        } catch (SQLException e) {
            showError("Database Error loading bookings: " + e.getMessage());
        }
    }

    private void saveMovie(String name, String lang, String genre, int duration, double price, int seats) {
        String sql = "INSERT INTO movies (movie_name, language, genre, duration, ticket_price, available_seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, lang);
            ps.setString(3, genre);
            ps.setInt(4, duration);
            ps.setDouble(5, price);
            ps.setInt(6, seats);

            ps.executeUpdate();
            showInfo("Success", "Movie '" + name + "' added successfully!");
            refreshMovies();
        } catch (SQLException e) {
            showError("Database Error adding movie: " + e.getMessage());
        }
    }

    private void updateMovieInDb(int movieId, double price, int seats) {
        String sql = "UPDATE movies SET ticket_price = ?, available_seats = ? WHERE movie_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, price);
            ps.setInt(2, seats);
            ps.setInt(3, movieId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                showInfo("Updated", "Movie updated successfully.");
                refreshMovies();
            } else {
                showError("Movie ID not found.");
            }
        } catch (SQLException e) {
            showError("Database Error updating movie: " + e.getMessage());
        }
    }

    private void deleteSelectedMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a movie from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete movie '" + selected.getMovieName() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String sql = "DELETE FROM movies WHERE movie_id = ?";
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, selected.getMovieId());
                    ps.executeUpdate();
                    showInfo("Deleted", "Movie deleted successfully.");
                    refreshMovies();
                } catch (SQLException e) {
                    showError("Cannot delete movie: It may have existing bookings attached.\n" + e.getMessage());
                }
            }
        });
    }

    private void processBooking(Movie movie, String customerName, String showTime, int seats) {
        double total = seats * movie.getTicketPrice();
        String bookingSql = "INSERT INTO bookings (customer_name, movie_id, show_time, seats, total_amount) VALUES (?, ?, ?, ?, ?)";
        String updateMovieSql = "UPDATE movies SET available_seats = ? WHERE movie_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // Transaction

            try (PreparedStatement bookingPs = con.prepareStatement(bookingSql);
                 PreparedStatement updatePs = con.prepareStatement(updateMovieSql)) {

                bookingPs.setString(1, customerName);
                bookingPs.setInt(2, movie.getMovieId());
                bookingPs.setString(3, showTime);
                bookingPs.setInt(4, seats);
                bookingPs.setDouble(5, total);
                bookingPs.executeUpdate();

                updatePs.setInt(1, movie.getAvailableSeats() - seats);
                updatePs.setInt(2, movie.getMovieId());
                updatePs.executeUpdate();

                con.commit();
                showInfo("Booking Confirmed", String.format("Ticket booked for %s!\nTotal: $%.2f", customerName, total));
                refreshMovies();
                refreshBookings();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            showError("Database Error booking ticket: " + e.getMessage());
        }
    }

    // --- Helpers & Styling ---

    private void updateStatus(String msg) {
        statusLabel.setText("Status: " + msg);
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void applyModernTheme(Scene scene) {
        String css = """
            .root {
                -fx-font-family: 'Segoe UI', Helvetica, Arial, sans-serif;
                -fx-background-color: #f8fafc;
            }
            .tab-pane .tab-header-area .tab-header-background {
                -fx-background-color: #1e1b4b;
            }
            .tab {
                -fx-background-color: transparent;
                -fx-padding: 10 20 10 20;
            }
            .tab-label {
                -fx-text-fill: #94a3b8;
                -fx-font-weight: bold;
                -fx-font-size: 13px;
            }
            .tab:selected {
                -fx-background-color: #312e81;
            }
            .tab:selected .tab-label {
                -fx-text-fill: #ffffff;
            }
            .table-view {
                -fx-background-color: #ffffff;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
                -fx-border-color: #e2e8f0;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 3);
            }
            .table-view .column-header-background {
                -fx-background-color: #f1f5f9;
            }
            .table-view .column-header {
                -fx-background-color: transparent;
                -fx-size: 40px;
            }
            .table-view .column-header .label {
                -fx-text-fill: #334155;
                -fx-font-weight: bold;
            }
            .table-row-cell {
                -fx-cell-size: 38px;
            }
            .table-row-cell:odd {
                -fx-background-color: #f8fafc;
            }
            .table-row-cell:selected {
                -fx-background-color: #e0e7ff;
            }
            .table-row-cell:selected .text {
                -fx-fill: #3730a3;
            }
            .button {
                -fx-background-radius: 6;
                -fx-padding: 8 16 8 16;
                -fx-cursor: hand;
            }
            .text-field {
                -fx-background-radius: 6;
                -fx-border-radius: 6;
                -fx-border-color: #cbd5e1;
                -fx-padding: 7 10 7 10;
            }
            .text-field:focused {
                -fx-border-color: #6366f1;
            }
        """;
        scene.getStylesheets().add("data:text/css," + css.replace("\n", " ").replace(" ", "%20"));
    }
}
