package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.CommonDAO;
import ru.web.tsvelenev.WEB.models.CommonEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public abstract class CommonDAOImpl<T extends CommonEntity<ID>, ID extends Serializable> implements CommonDAO<T, ID> {

    protected final SessionFactory sessionFactory;
    protected final Class<T> persistentClass;

    @Autowired
    public CommonDAOImpl(SessionFactory sessionFactory, Class<T> persistentClass) {
        this.sessionFactory = sessionFactory;
        this.persistentClass = persistentClass;
    }

    @Override
    @Transactional(readOnly = true)
    public T getById(ID id) {
        return sessionFactory.getCurrentSession().get(persistentClass, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(persistentClass);
        criteriaQuery.from(persistentClass);
        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public T save(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(entity);
        return entity;
    }

    @Override
    public void saveCollection(Collection<T> entities) {
        Session session = sessionFactory.getCurrentSession();
        entities.forEach(session::persist);
    }

    @Override
    public T update(T entity) {
        Session session = sessionFactory.getCurrentSession();
        return (T) session.merge(entity);
    }

    @Override
    public void delete(T entity) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        Session session = sessionFactory.getCurrentSession();
        T entity = getById(id);
        if (entity != null) {
            session.delete(entity);
        }
    }
}