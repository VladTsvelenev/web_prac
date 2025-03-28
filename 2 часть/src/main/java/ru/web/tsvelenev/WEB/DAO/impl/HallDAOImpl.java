package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.HallDAO;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

@Repository
public class HallDAOImpl extends CommonDAOImpl<Hall, Long> implements HallDAO {

    public HallDAOImpl() {
        super(Hall.class);
    }

    @Override
    public List<Hall> getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
            Root<Hall> root = cq.from(Hall.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public Hall getSingleByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
            Root<Hall> root = cq.from(Hall.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).setMaxResults(1).uniqueResult();
        }
    }

    @Override
    public List<Hall> getByTheaterId(Long theaterId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
            Root<Hall> root = cq.from(Hall.class);

            cq.select(root).where(cb.equal(root.get("theater").get("id"), theaterId));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Hall> getByNameAndTheater(String name, Long theaterId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
            Root<Hall> root = cq.from(Hall.class);

            Predicate namePredicate = cb.equal(root.get("name"), name);
            Predicate theaterPredicate = cb.equal(root.get("theater").get("id"), theaterId);
            cq.select(root).where(cb.and(namePredicate, theaterPredicate));

            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Hall> getByTheaterName(String theaterName) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
            Root<Hall> root = cq.from(Hall.class);
            Join<Hall, Theater> theaterJoin = root.join("theater");

            cq.select(root).where(cb.like(theaterJoin.get("name"), "%" + theaterName + "%"));
            return session.createQuery(cq).getResultList();
        }
    }
}