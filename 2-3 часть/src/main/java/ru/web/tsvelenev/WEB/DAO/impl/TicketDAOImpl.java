package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.TicketDAO;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

@Repository
@Transactional
public class TicketDAOImpl extends CommonDAOImpl<Ticket, Long> implements TicketDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public TicketDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Ticket.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        Session session = sessionFactory.getCurrentSession();
        if (ticket.getId() == null) {
            session.persist(ticket);
            return ticket;
        } else {
            return session.merge(ticket);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByUser(Users user) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        // Добавляем JOIN FETCH для связанных сущностей
        Join<Ticket, ShowTime> showTimeJoin = root.join("showTime", JoinType.INNER);
        Join<ShowTime, Performance> performanceJoin = showTimeJoin.join("performance", JoinType.INNER);
        performanceJoin.join("hall", JoinType.INNER).join("theater", JoinType.INNER);
        root.join("seat", JoinType.INNER).join("seatType", JoinType.INNER);

        cq.select(root)
                .where(cb.equal(root.get("user"), user))
                .orderBy(cb.desc(root.get("purchaseDate")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findPurchasedTicketsByUserWithDetails(Users user) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        // Полная загрузка всех связанных сущностей для истории покупок
        Fetch<Ticket, ShowTime> showTimeFetch = root.fetch("showTime", JoinType.INNER);
        Fetch<ShowTime, Performance> performanceFetch = showTimeFetch.fetch("performance", JoinType.INNER);
        performanceFetch.fetch("hall", JoinType.INNER).fetch("theater", JoinType.INNER);
        performanceFetch.fetch("director", JoinType.LEFT);
        root.fetch("seat", JoinType.INNER).fetch("seatType", JoinType.INNER);

        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("user"), user),
                        cb.equal(root.get("isSold"), true)
                ))
                .orderBy(cb.desc(root.get("purchaseDate")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket findFirstByPerformanceAndSeatTypeAndIsSoldFalse(Performance performance, SeatType seatType) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("performance"), performance),
                        cb.equal(root.get("seatType"), seatType),
                        cb.equal(root.get("isSold"), false)
                ))
                .orderBy(cb.asc(root.get("id")));

        return session.createQuery(cq).setMaxResults(1).getSingleResultOrNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getByShowTimeId(Long showTimeId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(root)
                .where(cb.equal(root.get("showTime").get("id"), showTimeId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getAvailableByShowTime(Long showTimeId) {
        return getByShowTimeAndStatus(showTimeId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getSoldByShowTime(Long showTimeId) {
        return getByShowTimeAndStatus(showTimeId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getBySeatId(Long seatId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(root)
                .where(cb.equal(root.get("seat").get("id"), seatId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getByPriceRange(Integer minPrice, Integer maxPrice) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        Predicate minPredicate = cb.ge(root.get("price"), minPrice);
        Predicate maxPredicate = cb.le(root.get("price"), maxPrice);

        cq.select(root).where(cb.and(minPredicate, maxPredicate));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> getByShowTimeAndStatus(Long showTimeId, Boolean isSold) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        Predicate showTimePredicate = cb.equal(root.get("showTime").get("id"), showTimeId);
        Predicate statusPredicate = cb.equal(root.get("isSold"), isSold);

        cq.select(root).where(cb.and(showTimePredicate, statusPredicate));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAvailableByPerformanceId(Long performanceId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("performance").get("id"), performanceId),
                        cb.equal(root.get("isSold"), false)
                ));

        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findAvailableTickets(Long performanceId) {
        return findAvailableByPerformanceId(performanceId);
    }

    @Override
    @Transactional
    public void deleteByPerformance(Performance performance) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Ticket> cd = cb.createCriteriaDelete(Ticket.class);
        Root<Ticket> root = cd.from(Ticket.class);

        cd.where(cb.equal(root.get("performance"), performance));

        session.createMutationQuery(cd).executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findByShowTimeAndIsSoldFalse(ShowTime showTime) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(root)
                .where(cb.and(
                        cb.equal(root.get("showTime"), showTime),
                        cb.equal(root.get("isSold"), false)
                ));

        return session.createQuery(cq).getResultList();
    }
}