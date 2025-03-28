package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.SeatTypeDAO;
import ru.web.tsvelenev.WEB.models.SeatType;

import java.util.List;

@Repository
public class SeatTypeDAOImpl extends CommonDAOImpl<SeatType, Long> implements SeatTypeDAO {

    public SeatTypeDAOImpl() {
        super(SeatType.class);
    }

    @Override
    public List<SeatType> getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
            Root<SeatType> root = cq.from(SeatType.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public SeatType getSingleByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
            Root<SeatType> root = cq.from(SeatType.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).setMaxResults(1).uniqueResult();
        }
    }

    @Override
    public List<SeatType> getByNameContaining(String namePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
            Root<SeatType> root = cq.from(SeatType.class);

            cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }
}