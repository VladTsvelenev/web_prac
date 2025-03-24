package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "hall")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Hall implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    @NonNull
    private Theater theater;
}
