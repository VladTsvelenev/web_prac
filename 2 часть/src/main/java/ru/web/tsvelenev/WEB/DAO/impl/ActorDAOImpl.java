package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.ActorDAO;
import ru.web.tsvelenev.WEB.models.Actor;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ActorDAOImpl extends CommonDAOImpl<Actor, Long> implements ActorDAO {

    public ActorDAOImpl() {
        super(Actor.class);
    }

    @Override
    public List<Actor> findByCriteria(String name, String nameContains) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Actor> query = builder.createQuery(Actor.class);
            Root<Actor> root = query.from(Actor.class);

            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(builder.equal(root.get("name"), name));
            }
            if (nameContains != null) {
                predicates.add(builder.like(root.get("name"), likeExpr(nameContains)));
            }

            if (!predicates.isEmpty()) {
                query.where(predicates.toArray(new Predicate[0]));
            }

            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public Actor getSingleActorByName(String actorName) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Actor> query = builder.createQuery(Actor.class);
            Root<Actor> root = query.from(Actor.class);

            query.select(root)
                    .where(builder.equal(root.get("name"), actorName));

            return session.createQuery(query).setMaxResults(1).uniqueResult();
        }
    }
    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}