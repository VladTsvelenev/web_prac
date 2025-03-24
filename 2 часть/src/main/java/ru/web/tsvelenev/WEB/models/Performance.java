package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "performance")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Performance implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    @NonNull
    private String title;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    @NonNull
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    @NonNull
    private Director director;

    @Column(name = "duration", nullable = false)
    @NonNull
    private Date duration;
}