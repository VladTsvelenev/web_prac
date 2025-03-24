DROP TABLE IF EXISTS PerformanceActor;

CREATE TABLE PerformanceActor (
    performance_id INT,
    actor_id INT,
    PRIMARY KEY (performance_id, actor_id),
    FOREIGN KEY (performance_id) REFERENCES Performance(id),
    FOREIGN KEY (actor_id) REFERENCES Actor(id)
);