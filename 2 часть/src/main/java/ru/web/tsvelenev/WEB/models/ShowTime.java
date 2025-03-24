package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

import java.sql.Date;


@Entity
@Table(name = "showtime")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class ShowTime implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    @NonNull
    private Performance performance;

    @Column(name = "show_datetime", nullable = false)
    @NonNull
    private Data showDatetime;
}