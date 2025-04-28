package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.SeatDAO;
import ru.web.tsvelenev.WEB.models.Seat;

import java.util.List;

@Repository
@Transactional
public class SeatDAOImpl extends CommonDAOImpl<Seat, Long> implements SeatDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public SeatDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Seat.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getByHallId(Long hallId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);

        cq.select(root).where(cb.equal(root.get("hall").get("id"), hallId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getBySeatTypeId(Long seatTypeId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);

        cq.select(root).where(cb.equal(root.get("seatType").get("id"), seatTypeId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getByRowNumber(Integer rowNumber) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);

        cq.select(root).where(cb.equal(root.get("rowNumber"), rowNumber));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getBySeatNumber(Integer seatNumber) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);

        cq.select(root).where(cb.equal(root.get("seatNumber"), seatNumber));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Seat getByHallAndRowAndSeat(Long hallId, Integer rowNumber, Integer seatNumber) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Seat> root = cq.from(Seat.class);

        Predicate hallPredicate = cb.equal(root.get("hall").get("id"), hallId);
        Predicate rowPredicate = cb.equal(root.get("rowNumber"), rowNumber);
        Predicate seatPredicate = cb.equal(root.get("seatNumber"), seatNumber);

        cq.select(root).where(cb.and(hallPredicate, rowPredicate, seatPredicate));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSeatNumber(Long seatId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Seat> root = cq.from(Seat.class);

        cq.select(root.get("seatNumber"))
                .where(cb.equal(root.get("id"), seatId));

        return session.createQuery(cq).getSingleResult();
    }
}