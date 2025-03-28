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
public class PerformanceActor implements CommonEntity<PerformanceActorId> {
    @EmbeddedId
    private PerformanceActorId id;

    @ManyToOne
    @MapsId("performanceId")
    @NonNull
    private Performance performance;

    @ManyToOne
    @MapsId("actorId")
    @NonNull
    private Actor actor;

    @Override
    public PerformanceActorId getId() {
        return id;
    }

    @Override
    public void setId(PerformanceActorId id) {
        this.id = id;
    }
}