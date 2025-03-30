package ru.web.tsvelenev.WEB.models;

import java.beans.Transient;
import java.time.Duration;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "performance")
@Getter
@Setter
@NoArgsConstructor
public class Performance implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_seq")
    @SequenceGenerator(name = "performance_seq", sequenceName = "performance_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes; // Храним продолжительность в минутах

    // Конструктор
    public Performance(String title, Hall hall, Director director, Integer durationMinutes) {
        this.title = title;
        this.hall = hall;
        this.director = director;
        this.durationMinutes = durationMinutes;
    }

    // Метод для получения Duration (вычисляемое поле)
    @Transient
    public Duration getDuration() {
        return Duration.ofMinutes(durationMinutes);
    }
}