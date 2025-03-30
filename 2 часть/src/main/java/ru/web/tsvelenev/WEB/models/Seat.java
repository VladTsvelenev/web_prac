package ru.web.tsvelenev.WEB.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
public class Seat implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_seq")
    @SequenceGenerator(name = "seat_seq", sequenceName = "seat_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "seat_type_id")
    private SeatType seatType;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "seat_number")
    private Integer seatNumber;

    // Explicit constructor for required fields
    public Seat(Hall hall, SeatType seatType, Integer rowNumber, Integer seatNumber) {
        this.hall = hall;
        this.seatType = seatType;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
    }
}