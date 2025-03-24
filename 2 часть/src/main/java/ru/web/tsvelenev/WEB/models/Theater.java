package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "theater")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Theater implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "info")
    private String info;
}