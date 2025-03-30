package ru.web.tsvelenev.WEB.models;

import lombok.*;
import jakarta.persistence.*;

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

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Performance performance;

    @ManyToOne(cascade = {CascadeType.MERGE})
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