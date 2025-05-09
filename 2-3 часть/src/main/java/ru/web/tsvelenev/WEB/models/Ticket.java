package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
public class Ticket implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с показом спектакля (ShowTime), а не с Performance напрямую
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private ShowTime showTime;

    // Связь с пользователем (покупателем билета)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // Связь с местом в зале
    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "is_sold", nullable = false)
    private Boolean isSold = false;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    // Конструктор для создания нового билета
    public Ticket(ShowTime showTime, Seat seat, Integer price) {
        this.showTime = showTime;
        this.seat = seat;
        this.price = price;
    }
}
