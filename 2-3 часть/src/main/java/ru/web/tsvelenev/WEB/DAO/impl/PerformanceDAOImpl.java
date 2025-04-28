package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.PerformanceDAO;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Performance;

import java.util.List;

@Repository
@Transactional
public class PerformanceDAOImpl extends CommonDAOImpl<Performance, Long> implements PerformanceDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public PerformanceDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Performance.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> getByTitle(String title) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.equal(root.get("title"), title));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Performance getSingleByTitle(String title) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.equal(root.get("title"), title));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> getByDirectorId(Long directorId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.equal(root.get("director").get("id"), directorId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> getByHallId(Long hallId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.equal(root.get("hall").get("id"), hallId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> getByTitleContaining(String titlePart) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.like(root.get("title"), "%" + titlePart + "%"));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> getByDurationBetween(Integer minMinutes, Integer maxMinutes) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        cq.select(root).where(cb.between(
                root.get("durationMinutes"),
                minMinutes,
                maxMinutes
        ));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Performance> findByTheaterId(Long theaterId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
        Root<Performance> root = cq.from(Performance.class);

        Join<Performance, Hall> hallJoin = root.join("hall"); // Join с Hall
        Join<Hall, ?> theaterJoin = hallJoin.join("theater"); // Join с Theater через Hall

        cq.select(root)
                .where(cb.equal(theaterJoin.get("id"), theaterId));

        return session.createQuery(cq).getResultList();
    }
}
