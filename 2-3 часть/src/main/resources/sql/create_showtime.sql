DROP TABLE IF EXISTS ShowTime;

CREATE TABLE ShowTime (
    id INT PRIMARY KEY,
    performance_id INT,
    show_datetime TIMESTAMP,
    FOREIGN KEY (performance_id) REFERENCES Performance(id)
);