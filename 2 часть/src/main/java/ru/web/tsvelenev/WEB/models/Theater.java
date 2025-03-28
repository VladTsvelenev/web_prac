package ru.web.tsvelenev.WEB.models;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "theater")
@Getter // Оставляем только необходимые аннотации
@Setter
@NoArgsConstructor
@AllArgsConstructor // Заменяем @RequiredArgsConstructor
public class Theater implements CommonEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String info;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Hall> halls;
}