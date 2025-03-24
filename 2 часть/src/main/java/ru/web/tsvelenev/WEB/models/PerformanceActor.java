package ru.web.tsvelenev.WEB.models;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "performance_actor")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class PerformanceActor {
    @EmbeddedId
    private PerformanceActorId id;

    @ManyToOne
    @MapsId("performanceId")
    private Performance performance;

    @ManyToOne
    @MapsId("actorId")
    private Actor actor;

    // Геттеры и сеттеры
}