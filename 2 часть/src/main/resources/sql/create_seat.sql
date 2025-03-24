DROP TABLE IF EXISTS Seat;

CREATE TABLE Seat (
    id INT PRIMARY KEY,
    hall_id INT,
    seat_type_id INT,
    row_number INT,
    seat_number INT,
    FOREIGN KEY (hall_id) REFERENCES Hall(id),
    FOREIGN KEY (seat_type_id) REFERENCES SeatType(id)
);