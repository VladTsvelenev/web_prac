package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
public class Ticket implements CommonEntity<Long> {
    public Ticket(ShowTime showTime, Seat seat, Users user, Integer price, Boolean isSold) {
        this.showTime = showTime;
        this.seat = seat;
        this.user = user;
        this.price = price;
        this.isSold = isSold;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq")
    @SequenceGenerator(name = "ticket_seq", sequenceName = "ticket_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_sold", nullable = false)
    private Boolean isSold;
}