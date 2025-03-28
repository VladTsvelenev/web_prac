package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Director;
import java.util.List;

public interface DirectorDAO extends CommonDAO<Director, Long> {
    List<Director> getByName(String name);
    Director getSingleByName(String name);
    List<Director> getByNameContaining(String namePart);
}