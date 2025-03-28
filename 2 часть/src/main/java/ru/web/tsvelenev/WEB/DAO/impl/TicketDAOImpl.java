package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.TicketDAO;
import ru.web.tsvelenev.WEB.models.Ticket;

import java.util.List;

@Repository
public class TicketDAOImpl extends CommonDAOImpl<Ticket, Long> implements TicketDAO {

    public TicketDAOImpl() {
        super(Ticket.class);
    }

    @Override
    public List<Ticket> getByShowTimeId(Long showTimeId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
            Root<Ticket> root = cq.from(Ticket.class);

            cq.select(root)
                    .where(cb.equal(root.get("showTime").get("id"), showTimeId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Ticket> getByUserId(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
            Root<Ticket> root = cq.from(Ticket.class);

            cq.select(root)
                    .where(cb.equal(root.get("user").get("id"), userId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Ticket> getAvailableByShowTime(Long showTimeId) {
        return getByShowTimeAndStatus(showTimeId, false);
    }

    @Override
    public List<Ticket> getSoldByShowTime(Long showTimeId) {
        return getByShowTimeAndStatus(showTimeId, true);
    }

    @Override
    public List<Ticket> getBySeatId(Long seatId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
            Root<Ticket> root = cq.from(Ticket.class);

            cq.select(root)
                    .where(cb.equal(root.get("seat").get("id"), seatId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Ticket> getByPriceRange(Integer minPrice, Integer maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
            Root<Ticket> root = cq.from(Ticket.class);

            Predicate minPredicate = cb.ge(root.get("price"), minPrice);
            Predicate maxPredicate = cb.le(root.get("price"), maxPrice);

            cq.select(root).where(cb.and(minPredicate, maxPredicate));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Ticket> getByShowTimeAndStatus(Long showTimeId, Boolean isSold) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
            Root<Ticket> root = cq.from(Ticket.class);

            Predicate showTimePredicate = cb.equal(root.get("showTime").get("id"), showTimeId);
            Predicate statusPredicate = cb.equal(root.get("isSold"), isSold);

            cq.select(root).where(cb.and(showTimePredicate, statusPredicate));
            return session.createQuery(cq).getResultList();
        }
    }
}