DROP TABLE IF EXISTS Performance;

CREATE TABLE Performance (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    hall_id INT,
    director_id INT,
    duration INTERVAL,
    FOREIGN KEY (hall_id) REFERENCES Hall(id),
    FOREIGN KEY (director_id) REFERENCES Director(id)
);