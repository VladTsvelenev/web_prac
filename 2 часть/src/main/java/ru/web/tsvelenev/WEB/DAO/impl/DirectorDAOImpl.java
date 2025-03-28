package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.DirectorDAO;
import ru.web.tsvelenev.WEB.models.Director;

import java.util.List;

@Repository
public class DirectorDAOImpl extends CommonDAOImpl<Director, Long> implements DirectorDAO {

    public DirectorDAOImpl() {
        super(Director.class);
    }

    @Override
    public List<Director> getByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Director> cq = cb.createQuery(Director.class);
            Root<Director> root = cq.from(Director.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public Director getSingleByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Director> cq = cb.createQuery(Director.class);
            Root<Director> root = cq.from(Director.class);

            cq.select(root).where(cb.equal(root.get("name"), name));
            return session.createQuery(cq).setMaxResults(1).uniqueResult();
        }
    }

    @Override
    public List<Director> getByNameContaining(String namePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Director> cq = cb.createQuery(Director.class);
            Root<Director> root = cq.from(Director.class);

            cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }
}