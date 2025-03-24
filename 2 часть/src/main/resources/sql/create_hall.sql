DROP TABLE IF EXISTS Hall;

CREATE TABLE Hall (
    id INT PRIMARY KEY,
    theater_id INT,
    name VARCHAR(255),
    FOREIGN KEY (theater_id) REFERENCES Theater(id)
);