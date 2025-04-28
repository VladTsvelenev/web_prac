package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "director")
@Getter
@Setter
@NoArgsConstructor
public class Director implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "director_seq")
    @SequenceGenerator(name = "director_seq", sequenceName = "director_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Director(String name) {
        this.name = name;
    }

    public String getFullName() {
        return name;
    }
}