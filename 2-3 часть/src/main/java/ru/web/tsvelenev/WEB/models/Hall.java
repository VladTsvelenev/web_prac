package ru.web.tsvelenev.WEB.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "hall")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Hall implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hall_seq")
    @SequenceGenerator(name = "hall_seq", sequenceName = "hall_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    @NonNull
    @ToString.Exclude
    private Theater theater;

    @Column(name = "seat_count")
    private Integer seatCount;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Seat> seats;
}
