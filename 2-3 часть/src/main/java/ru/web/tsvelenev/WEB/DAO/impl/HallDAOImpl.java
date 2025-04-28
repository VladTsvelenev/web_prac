package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.HallDAO;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Seat;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

@Repository
public class HallDAOImpl extends CommonDAOImpl<Hall, Long> implements HallDAO {

    @Autowired
    public HallDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Hall.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);
        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Hall getSingleByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);
        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getByTheaterId(Long theaterId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);
        cq.select(root).where(cb.equal(root.get("theater").get("id"), theaterId));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getByNameAndTheater(String name, Long theaterId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);
        Predicate namePredicate = cb.equal(root.get("name"), name);
        Predicate theaterPredicate = cb.equal(root.get("theater").get("id"), theaterId);
        cq.select(root).where(cb.and(namePredicate, theaterPredicate));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getByTheaterName(String theaterName) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);
        Join<Hall, Theater> theaterJoin = root.join("theater");
        cq.select(root).where(cb.like(theaterJoin.get("name"), "%" + theaterName + "%"));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getSeatsByHallId(Long hallId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Seat> cq = cb.createQuery(Seat.class);
        Root<Hall> root = cq.from(Hall.class);
        Join<Hall, Seat> seatsJoin = root.join("seats");

        cq.select(seatsJoin)
                .where(cb.equal(root.get("id"), hallId))
                .orderBy(
                        cb.asc(seatsJoin.get("rowNumber")),
                        cb.asc(seatsJoin.get("seatNumber"))
                );

        return session.createQuery(cq).getResultList();
    }
}
