package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.TheaterDAO;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TheaterDAOImpl extends CommonDAOImpl<Theater, Long> implements TheaterDAO {

    public TheaterDAOImpl() {
        super(Theater.class);
    }

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Override
    public List<Theater> getAllTheatersByName(String theaterName) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Theater> criteriaQuery = builder.createQuery(Theater.class);
            Root<Theater> root = criteriaQuery.from(Theater.class);

            criteriaQuery.select(root).where(builder.equal(root.get("name"), theaterName));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    @Override
    public Theater getSingleTheaterByName(String theaterName) {
        List<Theater> theaters = getAllTheatersByName(theaterName);
        return theaters.isEmpty() ? null : theaters.get(0);
    }

    @Override
    public List<Theater> getByFilter(Filter filter) {
        try (Session session = entityManager.unwrap(Session.class)) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Theater> criteriaQuery = builder.createQuery(Theater.class);
            Root<Theater> root = criteriaQuery.from(Theater.class);

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null) {
                predicates.add(builder.like(root.get("name"), "%" + filter.getName() + "%"));
            }
            if (filter.getAddress() != null) {
                predicates.add(builder.like(root.get("address"), "%" + filter.getAddress() + "%"));
            }

            if (!predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }

            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}