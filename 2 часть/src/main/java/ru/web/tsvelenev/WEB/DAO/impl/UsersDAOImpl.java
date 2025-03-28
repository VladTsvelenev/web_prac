package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.web.tsvelenev.WEB.DAO.UsersDAO;
import ru.web.tsvelenev.WEB.models.Users;

import java.util.List;

@Repository
public class UsersDAOImpl extends CommonDAOImpl<Users, Long> implements UsersDAO {

    public UsersDAOImpl() {
        super(Users.class);
    }

    @Override
    public List<Users> getByJsonField(String fieldName, String value) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Users> cq = cb.createQuery(Users.class);
            Root<Users> root = cq.from(Users.class);

            Expression<String> jsonExtract = cb.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get("userInfo"),
                    cb.literal(fieldName)
            );

            cq.select(root).where(cb.equal(jsonExtract, value));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Users> getByNameContaining(String namePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Users> cq = cb.createQuery(Users.class);
            Root<Users> root = cq.from(Users.class);

            Expression<String> nameExtract = cb.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get("userInfo"),
                    cb.literal("name")
            );

            cq.select(root).where(cb.like(nameExtract, "%" + namePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<Users> getByJsonFieldContaining(String fieldName, String valuePart) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Users> cq = cb.createQuery(Users.class);
            Root<Users> root = cq.from(Users.class);

            Expression<String> jsonExtract = cb.function(
                    "jsonb_extract_path_text",
                    String.class,
                    root.get("userInfo"),
                    cb.literal(fieldName)
            );

            cq.select(root).where(cb.like(jsonExtract, "%" + valuePart + "%"));
            return session.createQuery(cq).getResultList();
        }
    }
}