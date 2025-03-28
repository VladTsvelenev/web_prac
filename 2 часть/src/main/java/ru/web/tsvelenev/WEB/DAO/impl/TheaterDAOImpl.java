package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.TheaterDAO;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

@Repository
public class TheaterDAOImpl extends CommonDAOImpl<Theater, Long> implements TheaterDAO {

    public TheaterDAOImpl() {
        super(Theater.class);
    }

    @Override
    public List<Theater> getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
            Root<Theater> root = cq.from(Theater.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public Theater getSingleByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
            Root<Theater> root = cq.from(Theater.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).setMaxResults(1).uniqueResult();
        }
    }

    @Override
    public List<Theater> getByNameContaining(String namePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
            Root<Theater> root = cq.from(Theater.class);

            cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Theater> getByAddressContaining(String addressPart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
            Root<Theater> root = cq.from(Theater.class);

            cq.select(root).where(cb.like(root.get("address"), "%" + addressPart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Theater> getByNameAndAddress(String name, String address) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
            Root<Theater> root = cq.from(Theater.class);

            Predicate namePredicate = cb.like(root.get("name"), "%" + name + "%");
            Predicate addressPredicate = cb.like(root.get("address"), "%" + address + "%");
            cq.select(root).where(cb.and(namePredicate, addressPredicate));

            return session.createQuery(cq).getResultList();
        }
    }
}