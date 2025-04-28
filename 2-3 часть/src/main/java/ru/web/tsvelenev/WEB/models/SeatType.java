package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "seat_type")
@Getter @Setter @NoArgsConstructor
public class SeatType implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price; // Добавьте это поле

    public SeatType(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}