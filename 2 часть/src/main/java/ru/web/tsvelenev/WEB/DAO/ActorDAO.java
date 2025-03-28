package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Actor;
import java.util.List;

public interface ActorDAO extends CommonDAO<Actor, Long> {
    List<Actor> findByCriteria(String name, String nameContains);
    Actor getSingleActorByName(String actorName);
    List<Actor> findTopActors(int limit);
}