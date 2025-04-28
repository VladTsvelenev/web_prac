package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "showtime")
@Getter
@Setter
@NoArgsConstructor
public class ShowTime implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "showtime_seq")
    @SequenceGenerator(name = "showtime_seq", sequenceName = "showtime_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Column(name = "show_datetime")
    private Date showDatetime;

    public ShowTime(Performance performance, Date showDatetime) {
        if (performance == null) {
            throw new IllegalArgumentException("Performance cannot be null");
        }
        if (showDatetime == null) {
            throw new IllegalArgumentException("Show datetime cannot be null");
        }
        this.performance = performance;
        this.showDatetime = showDatetime;
    }

    public ShowTime(Performance performance, java.util.Date showDatetime) {
        this(performance, new Date(showDatetime.getTime()));
    }
}