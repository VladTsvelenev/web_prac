package ru.web.tsvelenev.WEB.DAO.impl;

import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.UsersDAO;
import ru.web.tsvelenev.WEB.models.Users;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UsersDAOImpl extends CommonDAOImpl<Users, Long> implements UsersDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public UsersDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Users.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getByJsonField(String fieldName, String value) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Users> cq = cb.createQuery(Users.class);
        Root<Users> root = cq.from(Users.class);

        // Для PostgreSQL используем правильный синтаксис
        Expression<String> jsonExtract = cb.function(
                "jsonb_extract_path_text",
                String.class,
                cb.function("to_jsonb", Object.class, root.get("userInfo")),
                cb.literal(fieldName)
        );

        cq.select(root).where(cb.equal(jsonExtract, value));
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getByNameContaining(String namePart) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional(readOnly = true)
    public List<Users> getByJsonFieldContaining(String fieldName, String valuePart) {
        Session session = sessionFactory.getCurrentSession();
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

    @Override
    @Transactional(readOnly = true)
    public List<Users> getByEmail(String email) {
        return getByJsonField("email", email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Users> findByEmail(String email) {
        try {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM Users WHERE userInfo LIKE :emailPattern";
            String emailPattern = "%\"email\":\"" + email + "\"%";

            List<Users> users = session.createQuery(hql, Users.class)
                    .setParameter("emailPattern", emailPattern)
                    .getResultList();

            return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}