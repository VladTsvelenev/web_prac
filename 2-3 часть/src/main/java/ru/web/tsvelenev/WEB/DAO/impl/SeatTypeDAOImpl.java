package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.SeatTypeDAO;
import ru.web.tsvelenev.WEB.models.SeatType;

import java.util.List;

@Repository
@Transactional
public class SeatTypeDAOImpl extends CommonDAOImpl<SeatType, Long> implements SeatTypeDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public SeatTypeDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, SeatType.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatType> getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
        Root<SeatType> root = cq.from(SeatType.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatType getSingleByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
        Root<SeatType> root = cq.from(SeatType.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatType> getByNameContaining(String namePart) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SeatType> cq = cb.createQuery(SeatType.class);
        Root<SeatType> root = cq.from(SeatType.class);

        cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
        return session.createQuery(cq).getResultList();
    }
}