CREATE DATABASE IF NOT EXISTS movie_ticket_db;
USE movie_ticket_db;

CREATE TABLE IF NOT EXISTS movies (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_name VARCHAR(100) NOT NULL,
    language VARCHAR(30) NOT NULL,
    genre VARCHAR(30) NOT NULL,
    duration INT NOT NULL,
    ticket_price DOUBLE NOT NULL,
    available_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    movie_id INT NOT NULL,
    show_time VARCHAR(30) NOT NULL,
    seats INT NOT NULL,
    total_amount DOUBLE NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

INSERT INTO movies (movie_name, language, genre, duration, ticket_price, available_seats)
VALUES
('Avengers', 'English', 'Action', 150, 180.00, 100),
('Premalu', 'Malayalam', 'Romance', 145, 150.00, 80),
('Interstellar', 'English', 'Sci-Fi', 169, 200.00, 60);
