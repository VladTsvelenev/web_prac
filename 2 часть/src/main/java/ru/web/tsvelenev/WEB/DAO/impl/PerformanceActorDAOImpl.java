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

    @Override
    public List<PerformanceActor> findByPerformanceId(Long performanceId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PerformanceActor> cq = cb.createQuery(PerformanceActor.class);
            Root<PerformanceActor> root = cq.from(PerformanceActor.class);

            cq.select(root).where(cb.equal(root.get("performance").get("id"), performanceId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<PerformanceActor> findByActorId(Long actorId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PerformanceActor> cq = cb.createQuery(PerformanceActor.class);
            Root<PerformanceActor> root = cq.from(PerformanceActor.class);

            cq.select(root).where(cb.equal(root.get("actor").get("id"), actorId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public boolean existsByPerformanceAndActor(Performance performance, Actor actor) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<PerformanceActor> root = cq.from(PerformanceActor.class);

            cq.select(cb.count(root))
                    .where(cb.and(
                            cb.equal(root.get("performance"), performance),
                            cb.equal(root.get("actor"), actor)
                    ));
            return session.createQuery(cq).getSingleResult() > 0;
        }
    }

    @Override
    public void deleteByPerformanceAndActor(Performance performance, Actor actor) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<PerformanceActor> cd = cb.createCriteriaDelete(PerformanceActor.class);
            Root<PerformanceActor> root = cd.from(PerformanceActor.class);

            cd.where(cb.and(
                    cb.equal(root.get("performance"), performance),
                    cb.equal(root.get("actor"), actor)
            ));

            session.createMutationQuery(cd).executeUpdate();
            session.getTransaction().commit();
        }
    }
}