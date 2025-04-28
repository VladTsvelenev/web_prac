package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Users;

import java.util.List;
import java.util.Optional;

public interface UsersDAO extends CommonDAO<Users, Long> {
    List<Users> getByJsonField(String fieldName, String value);
    List<Users> getByNameContaining(String namePart);
    List<Users> getByJsonFieldContaining(String fieldName, String valuePart);
    List<Users> getByEmail(String email);
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}