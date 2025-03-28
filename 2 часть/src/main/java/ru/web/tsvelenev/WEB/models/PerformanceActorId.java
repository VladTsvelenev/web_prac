package ru.web.tsvelenev.WEB.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceActorId implements Serializable {
    private Long performanceId;
    private Long actorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerformanceActorId that = (PerformanceActorId) o;
        return Objects.equals(performanceId, that.performanceId) &&
                Objects.equals(actorId, that.actorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(performanceId, actorId);
    }
}
