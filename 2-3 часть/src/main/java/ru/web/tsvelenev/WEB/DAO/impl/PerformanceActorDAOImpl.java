package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.PerformanceActorDAO;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

@Repository
@Transactional
public class PerformanceActorDAOImpl extends CommonDAOImpl<PerformanceActor, PerformanceActorId>
        implements PerformanceActorDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public PerformanceActorDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, PerformanceActor.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceActor> findByPerformanceId(Long performanceId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PerformanceActor> cq = cb.createQuery(PerformanceActor.class);
        Root<PerformanceActor> root = cq.from(PerformanceActor.class);

        cq.select(root).where(cb.equal(root.get("performance").get("id"), performanceId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceActor> findByActorId(Long actorId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PerformanceActor> cq = cb.createQuery(PerformanceActor.class);
        Root<PerformanceActor> root = cq.from(PerformanceActor.class);

        cq.select(root).where(cb.equal(root.get("actor").get("id"), actorId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPerformanceAndActor(Performance performance, Actor actor) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional
    public void deleteByPerformanceAndActor(Performance performance, Actor actor) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<PerformanceActor> cd = cb.createCriteriaDelete(PerformanceActor.class);
        Root<PerformanceActor> root = cd.from(PerformanceActor.class);

        cd.where(cb.and(
                cb.equal(root.get("performance"), performance),
                cb.equal(root.get("actor"), actor)
        ));

        session.createMutationQuery(cd).executeUpdate();
    }
}