package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.ActorDAO;
import ru.web.tsvelenev.WEB.models.Actor;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ActorDAOImpl extends CommonDAOImpl<Actor, Long> implements ActorDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ActorDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Actor.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actor> findByCriteria(String name, String nameContains) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional(readOnly = true)
    public List<Actor> findActorsByPerformanceId(Long performanceId) {
        Session session = sessionFactory.getCurrentSession();

        // Используем нативный SQL, так как связи ManyToMany нет в моделях
        String sql = "SELECT a.* FROM actor a " +
                "JOIN performance_actor pa ON a.id = pa.actor_id " +
                "WHERE pa.performance_id = :performanceId";

        return session.createNativeQuery(sql, Actor.class)
                .setParameter("performanceId", performanceId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Actor getSingleActorByName(String actorName) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Actor> query = builder.createQuery(Actor.class);
        Root<Actor> root = query.from(Actor.class);

        query.select(root)
                .where(builder.equal(root.get("name"), actorName));

        return session.createQuery(query).setMaxResults(1).uniqueResult();
    }

    private String likeExpr(String param) {
        return "%" + param + "%";
    }
}