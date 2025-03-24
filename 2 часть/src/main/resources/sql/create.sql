DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS ShowTime;
DROP TABLE IF EXISTS PerformanceActor;
DROP TABLE IF EXISTS Actor;
DROP TABLE IF EXISTS Performance;
DROP TABLE IF EXISTS Director;
DROP TABLE IF EXISTS Seat;
DROP TABLE IF EXISTS SeatType;
DROP TABLE IF EXISTS Hall;
DROP TABLE IF EXISTS Theater;

CREATE TABLE Theater (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    info VARCHAR(255)
);

CREATE TABLE Hall (
    id INT PRIMARY KEY,
    theater_id INT,
    name VARCHAR(255),
    FOREIGN KEY (theater_id) REFERENCES Theater(id)
);

CREATE TABLE SeatType (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE Seat (
    id INT PRIMARY KEY,
    hall_id INT,
    seat_type_id INT,
    row_number INT,
    seat_number INT,
    FOREIGN KEY (hall_id) REFERENCES Hall(id),
    FOREIGN KEY (seat_type_id) REFERENCES SeatType(id)
);

CREATE TABLE Director (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE Performance (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    hall_id INT,
    director_id INT,
    duration INTERVAL,
    FOREIGN KEY (hall_id) REFERENCES Hall(id),
    FOREIGN KEY (director_id) REFERENCES Director(id)
);

CREATE TABLE Actor (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE PerformanceActor (
    performance_id INT,
    actor_id INT,
    PRIMARY KEY (performance_id, actor_id),
    FOREIGN KEY (performance_id) REFERENCES Performance(id),
    FOREIGN KEY (actor_id) REFERENCES Actor(id)
);

CREATE TABLE ShowTime (
    id INT PRIMARY KEY,
    performance_id INT,
    show_datetime TIMESTAMP,
    FOREIGN KEY (performance_id) REFERENCES Performance(id)
);

CREATE TABLE Users (
    id INT PRIMARY KEY,
    user_info JSONB
);

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
