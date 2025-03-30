package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.PerformanceDAO;
import ru.web.tsvelenev.WEB.models.Performance;

import java.time.Duration;
import java.util.List;

@Repository
public class PerformanceDAOImpl extends CommonDAOImpl<Performance, Long> implements PerformanceDAO {

    public PerformanceDAOImpl() {
        super(Performance.class);
    }

    @Override
    public List<Performance> getByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
            Root<Performance> root = cq.from(Performance.class);

            cq.select(root).where(cb.equal(root.get("title"), title));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public Performance getSingleByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
            Root<Performance> root = cq.from(Performance.class);

            cq.select(root).where(cb.equal(root.get("title"), title));
            return session.createQuery(cq).setMaxResults(1).uniqueResult();
        }
    }

    @Override
    public List<Performance> getByDirectorId(Long directorId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
            Root<Performance> root = cq.from(Performance.class);

            cq.select(root).where(cb.equal(root.get("director").get("id"), directorId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Performance> getByHallId(Long hallId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
            Root<Performance> root = cq.from(Performance.class);

            cq.select(root).where(cb.equal(root.get("hall").get("id"), hallId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Performance> getByTitleContaining(String titlePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Performance> cq = cb.createQuery(Performance.class);
            Root<Performance> root = cq.from(Performance.class);

            cq.select(root).where(cb.like(root.get("title"), "%" + titlePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Performance> getByDurationBetween(Integer minMinutes, Integer maxMinutes) {
        try (Session session = sessionFactory.openSession()) {
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
    }
}