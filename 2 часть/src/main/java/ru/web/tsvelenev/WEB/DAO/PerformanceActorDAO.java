package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Actor;
import ru.web.tsvelenev.WEB.models.Performance;
import ru.web.tsvelenev.WEB.models.PerformanceActor;
import ru.web.tsvelenev.WEB.models.PerformanceActorId;

import java.util.List;

public interface PerformanceActorDAO extends CommonDAO<PerformanceActor, PerformanceActorId> {
    List<PerformanceActor> findByPerformanceId(Long performanceId);
    List<PerformanceActor> findByActorId(Long actorId);
    boolean existsByPerformanceAndActor(Performance performance, Actor actor);
    void deleteByPerformanceAndActor(Performance performance, Actor actor);
}