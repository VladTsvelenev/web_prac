package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.ShowTimeDAO;
import ru.web.tsvelenev.WEB.models.ShowTime;
import ru.web.tsvelenev.WEB.models.Performance;

import java.sql.Date;
import java.util.List;

@Repository
@Transactional
public class ShowTimeDAOImpl extends CommonDAOImpl<ShowTime, Long> implements ShowTimeDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ShowTimeDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, ShowTime.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowTime> getByPerformanceId(Long performanceId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
        Root<ShowTime> root = cq.from(ShowTime.class);

        cq.select(root)
                .where(cb.equal(root.get("performance").get("id"), performanceId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowTime> getByDate(Date date) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional(readOnly = true)
    public List<ShowTime> getByDateRange(Date startDate, Date endDate) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional(readOnly = true)
    public List<ShowTime> getByPerformanceTitle(String title) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ShowTime> cq = cb.createQuery(ShowTime.class);
        Root<ShowTime> root = cq.from(ShowTime.class);
        Join<ShowTime, Performance> performanceJoin = root.join("performance");

        cq.select(root)
                .where(cb.like(performanceJoin.get("title"), "%" + title + "%"));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowTime> getByPerformanceAndDate(Long performanceId, Date date) {
        Session session = sessionFactory.getCurrentSession();
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