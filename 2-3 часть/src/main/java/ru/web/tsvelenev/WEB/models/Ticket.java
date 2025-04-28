package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter @Setter @NoArgsConstructor
public class Ticket implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false)
    private SeatType seatType;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "is_sold", nullable = false)
    private Boolean isSold = false;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    public Ticket(Performance performance, SeatType seatType, Integer seatNumber, Integer price) {
        this.performance = performance;
        this.seatType = seatType;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}