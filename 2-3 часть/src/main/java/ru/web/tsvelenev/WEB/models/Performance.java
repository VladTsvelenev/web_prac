package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "performance")
@Getter
@Setter
@NoArgsConstructor
public class Performance implements CommonEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_seq")
    @SequenceGenerator(name = "performance_seq", sequenceName = "performance_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false) // или @NonNull
    private Director director;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShowTime> showTimes = new HashSet<>();

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerformanceActor> actors = new HashSet<>();

    // Конструктор без id
    public Performance(String title, Hall hall, Director director, Integer durationMinutes) {
        this.title = title;
        this.hall = hall;
        this.director = director;
        this.durationMinutes = durationMinutes;
    }

    // Метод для получения Duration
    @Transient
    public Duration getDuration() {
        return durationMinutes != null ? Duration.ofMinutes(durationMinutes) : null;
    }

    // Методы для управления связями
    public void addActor(Actor actor) {
        PerformanceActor performanceActor = new PerformanceActor();
        performanceActor.setPerformance(this);
        performanceActor.setActor(actor);
        performanceActor.setId(new PerformanceActorId(this.id, actor.getId()));
        this.actors.add(performanceActor);
        actor.getPerformances().add(performanceActor);
    }

    public void removeActor(Actor actor) {
        PerformanceActor performanceActor = new PerformanceActor();
        performanceActor.setPerformance(this);
        performanceActor.setActor(actor);
        performanceActor.setId(new PerformanceActorId(this.id, actor.getId()));
        this.actors.remove(performanceActor);
        actor.getPerformances().remove(performanceActor);
    }
}