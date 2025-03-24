package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.PerformanceDAO;
import ru.web.tsvelenev.WEB.models.Performance;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PerformanceDAOImpl extends CommonDAOImpl<Performance, Long> implements PerformanceDAO {

    public PerformanceDAOImpl() {
        super(Performance.class);
    }

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Override
    public List<Performance> getAllPerformancesByTitle(String title) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Performance> criteriaQuery = builder.createQuery(Performance.class);
            Root<Performance> root = criteriaQuery.from(Performance.class);

            criteriaQuery.select(root).where(builder.equal(root.get("title"), title));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public Performance getSinglePerformanceByTitle(String title) {
        List<Performance> performances = getAllPerformancesByTitle(title);
        return performances.isEmpty() ? null : performances.get(0);
    }

    @Override
    public List<Performance> getPerformancesByDirector(Long directorId) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Performance> criteriaQuery = builder.createQuery(Performance.class);
            Root<Performance> root = criteriaQuery.from(Performance.class);

            criteriaQuery.select(root).where(builder.equal(root.get("director").get("id"), directorId));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public List<Performance> getPerformancesByHall(Long hallId) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Performance> criteriaQuery = builder.createQuery(Performance.class);
            Root<Performance> root = criteriaQuery.from(Performance.class);

            criteriaQuery.select(root).where(builder.equal(root.get("hall").get("id"), hallId));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public List<Performance> getByFilter(Filter filter) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Performance> criteriaQuery = builder.createQuery(Performance.class);
            Root<Performance> root = criteriaQuery.from(Performance.class);

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null) {
                predicates.add(builder.like(root.get("title"), "%" + filter.getTitle() + "%"));
            }
            if (filter.getDirectorId() != null) {
                predicates.add(builder.equal(root.get("director").get("id"), filter.getDirectorId()));
            }
            if (filter.getHallId() != null) {
                predicates.add(builder.equal(root.get("hall").get("id"), filter.getHallId()));
            }
            if (filter.getMinDuration() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("duration"), filter.getMinDuration()));
            }
            if (filter.getMaxDuration() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("duration"), filter.getMaxDuration()));
            }

            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }

            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}