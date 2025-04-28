DROP TABLE IF EXISTS Ticket;

CREATE TABLE Ticket (
    id INT PRIMARY KEY,
    showtime_id INT,
    seat_id INT,
    price INT,
    is_sold BOOLEAN,
    user_id INT,
    FOREIGN KEY (showtime_id) REFERENCES ShowTime(id),
    FOREIGN KEY (seat_id) REFERENCES Seat(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
