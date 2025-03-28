package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.ShowTimeDAO;
import ru.web.tsvelenev.WEB.models.ShowTime;
import ru.web.tsvelenev.WEB.models.Performance;

import java.sql.Date;
import java.util.List;

@Repository
public class ShowTimeDAOImpl extends CommonDAOImpl<ShowTime, Long> implements ShowTimeDAO {

    public ShowTimeDAOImpl() {
        super(ShowTime.class);
    }

    @Override
    public List<ShowTime> getByPerformanceId(Long performanceId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
            Root<ShowTime> root = cq.from(ShowTime.class);

            cq.select(root)
                    .where(cb.equal(root.get("performance").get("id"), performanceId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<ShowTime> getByDate(Date date) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
            Root<ShowTime> root = cq.from(ShowTime.class);

            cq.select(root)
                    .where(cb.equal(
                            cb.function("date", Date.class, root.get("showDatetime")),
                            date
                    ));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<ShowTime> getByDateRange(Date startDate, Date endDate) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
            Root<ShowTime> root = cq.from(ShowTime.class);

            Predicate datePredicate = cb.between(
                    cb.function("date", Date.class, root.get("showDatetime")),
                    startDate,
                    endDate
            );

            cq.select(root).where(datePredicate);
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<ShowTime> getByPerformanceTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
            Root<ShowTime> root = cq.from(ShowTime.class);
            Join<ShowTime, Performance> performanceJoin = root.join("performance");

            cq.select(root)
                    .where(cb.like(performanceJoin.get("title"), "%" + title + "%"));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<ShowTime> getByPerformanceAndDate(Long performanceId, Date date) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
            Root<ShowTime> root = cq.from(ShowTime.class);

            Predicate perfPredicate = cb.equal(root.get("performance").get("id"), performanceId);
            Predicate datePredicate = cb.equal(
                    cb.function("date", Date.class, root.get("showDatetime")),
                    date
            );

            cq.select(root).where(cb.and(perfPredicate, datePredicate));
            return session.createQuery(cq).getResultList();
        }
    }
}