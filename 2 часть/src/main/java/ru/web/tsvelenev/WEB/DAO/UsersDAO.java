package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Users;
import java.util.List;

public interface UsersDAO extends CommonDAO<Users, Long> {
    List<Users> getByJsonField(String fieldName, String value);
    List<Users> getByNameContaining(String namePart);
    List<Users> getByJsonFieldContaining(String fieldName, String valuePart);
}