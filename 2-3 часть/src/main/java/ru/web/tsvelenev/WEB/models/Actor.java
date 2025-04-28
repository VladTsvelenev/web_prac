package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actor")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Actor implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actor_seq")
    @SequenceGenerator(name = "actor_seq", sequenceName = "actor_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NonNull
    private String name;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerformanceActor> performances = new HashSet<>();

    // Переопределение equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor)) return false;
        return id != null && id.equals(((Actor) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}