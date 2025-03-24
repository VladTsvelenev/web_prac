package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "seat")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Seat implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    @NonNull
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false)
    @NonNull
    private SeatType seatType;

    @Column(name = "row_number", nullable = false)
    @NonNull
    private Integer rowNumber;

    @Column(name = "seat_number", nullable = false)
    @NonNull
    private Integer seatNumber;
}