package ru.web.tsvelenev.WEB.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "theater")
@Getter
@Setter
@NoArgsConstructor
public class Theater implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "theater_seq")
    @SequenceGenerator(name = "theater_seq", sequenceName = "theater_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String info;

    @OneToMany(mappedBy = "theater",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Hall> halls;
}