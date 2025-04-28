package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.TheaterDAO;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

@Repository
public class TheaterDAOImpl extends CommonDAOImpl<Theater, Long> implements TheaterDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public TheaterDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Theater.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theater> getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
        Root<Theater> root = cq.from(Theater.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Theater getSingleByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
        Root<Theater> root = cq.from(Theater.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theater> getByNameContaining(String namePart) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
        Root<Theater> root = cq.from(Theater.class);

        cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theater> getByAddressContaining(String addressPart) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
        Root<Theater> root = cq.from(Theater.class);

        cq.select(root).where(cb.like(root.get("address"), "%" + addressPart + "%"));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Theater> getByNameAndAddress(String name, String address) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Theater> cq = cb.createQuery(Theater.class);
        Root<Theater> root = cq.from(Theater.class);

        Predicate namePredicate = cb.like(root.get("name"), "%" + name + "%");
        Predicate addressPredicate = cb.like(root.get("address"), "%" + address + "%");
        cq.select(root).where(cb.and(namePredicate, addressPredicate));

        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional
    public void saveHall(Hall hall) {
        Session session = sessionFactory.getCurrentSession();
        session.save(hall);
    }
    @Override
    @Transactional(readOnly = true)
    public Hall getHallById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Hall.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getAllHalls() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);

        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getHallsByTheaterId(Long theaterId) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Hall> cq = cb.createQuery(Hall.class);
        Root<Hall> root = cq.from(Hall.class);

        cq.select(root)
                .where(cb.equal(root.get("theater").get("id"), theaterId));
        return session.createQuery(cq).getResultList();
    }
}
