package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.SeatDAO;
import ru.web.tsvelenev.WEB.models.Seat;

import java.util.List;

@Repository
public class SeatDAOImpl extends CommonDAOImpl<Seat, Long> implements SeatDAO {

    public SeatDAOImpl() {
        super(Seat.class);
    }

    @Override
    public List<Seat> getByHallId(Long hallId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            cq.select(root).where(cb.equal(root.get("hall").get("id"), hallId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Seat> getBySeatTypeId(Long seatTypeId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            cq.select(root).where(cb.equal(root.get("seatType").get("id"), seatTypeId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Seat> getByRowNumber(Integer rowNumber) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            cq.select(root).where(cb.equal(root.get("rowNumber"), rowNumber));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Seat> getBySeatNumber(Integer seatNumber) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            cq.select(root).where(cb.equal(root.get("seatNumber"), seatNumber));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Seat> getByHallAndRow(Long hallId, Integer rowNumber) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            Predicate hallPredicate = cb.equal(root.get("hall").get("id"), hallId);
            Predicate rowPredicate = cb.equal(root.get("rowNumber"), rowNumber);
            cq.select(root).where(cb.and(hallPredicate, rowPredicate));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Seat> getByHallAndType(Long hallId, Long seatTypeId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
            Root<Seat> root = cq.from(Seat.class);

            Predicate hallPredicate = cb.equal(root.get("hall").get("id"), hallId);
            Predicate typePredicate = cb.equal(root.get("seatType").get("id"), seatTypeId);
            cq.select(root).where(cb.and(hallPredicate, typePredicate));

            return session.createQuery(cq).getResultList();
        }
    }
}