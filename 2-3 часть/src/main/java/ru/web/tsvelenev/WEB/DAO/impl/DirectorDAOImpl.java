package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.DirectorDAO;
import ru.web.tsvelenev.WEB.models.Director;

import java.util.List;

@Repository
@Transactional
public class DirectorDAOImpl extends CommonDAOImpl<Director, Long> implements DirectorDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public DirectorDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Director.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Director> getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Director> cq = cb.createQuery(Director.class);
        Root<Director> root = cq.from(Director.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Director getSingleByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Director> cq = cb.createQuery(Director.class);
        Root<Director> root = cq.from(Director.class);

        cq.select(root).where(cb.equal(root.get("name"), name));
        return session.createQuery(cq).setMaxResults(1).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Director> getByNameContaining(String namePart) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Director> cq = cb.createQuery(Director.class);
        Root<Director> root = cq.from(Director.class);

        cq.select(root).where(cb.like(root.get("name"), "%" + namePart + "%"));
        return session.createQuery(cq).getResultList();
    }
}