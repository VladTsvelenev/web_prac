package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "performance_actor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceActor implements CommonEntity<PerformanceActorId> {
    @EmbeddedId
    @NonNull
    private PerformanceActorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("performanceId")
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actorId")
    @JoinColumn(name = "actor_id")
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