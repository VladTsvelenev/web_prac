package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.PerformanceActorDAO;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

@Repository
public class PerformanceActorDAOImpl extends CommonDAOImpl<PerformanceActor, PerformanceActorId>
        implements PerformanceActorDAO {

    public PerformanceActorDAOImpl() {
        super(PerformanceActor.class);
    }

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Override
    public List<PerformanceActor> getByPerformance(Performance performance) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PerformanceActor> criteriaQuery = builder.createQuery(PerformanceActor.class);
            Root<PerformanceActor> root = criteriaQuery.from(PerformanceActor.class);

            criteriaQuery.select(root)
                    .where(builder.equal(root.get("performance"), performance));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public List<PerformanceActor> getByActor(Actor actor) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PerformanceActor> criteriaQuery = builder.createQuery(PerformanceActor.class);
            Root<PerformanceActor> root = criteriaQuery.from(PerformanceActor.class);

            criteriaQuery.select(root)
                    .where(builder.equal(root.get("actor"), actor));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public boolean existsByPerformanceAndActor(Performance performance, Actor actor) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
            Root<PerformanceActor> root = criteriaQuery.from(PerformanceActor.class);

            criteriaQuery.select(builder.count(root))
                    .where(builder.and(
                            builder.equal(root.get("performance"), performance),
                            builder.equal(root.get("actor"), actor)
                    ));

            return session.createQuery(criteriaQuery).getSingleResult() > 0;
        }
    }

    @Override
    public void deleteByPerformanceAndActor(Performance performance, Actor actor) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<PerformanceActor> delete = builder.createCriteriaDelete(PerformanceActor.class);
            Root<PerformanceActor> root = delete.from(PerformanceActor.class);

            delete.where(builder.and(
                    builder.equal(root.get("performance"), performance),
                    builder.equal(root.get("actor"), actor)
            ));

            session.createMutationQuery(delete).executeUpdate();
        }
    }
}