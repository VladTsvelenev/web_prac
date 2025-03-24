package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Ticket implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    @NonNull
    private ShowTime showTime;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    @NonNull
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "price", nullable = false)
    @NonNull
    private Integer price;

    @Column(name = "is_sold", nullable = false)
    @NonNull
    private Boolean isSold;
}