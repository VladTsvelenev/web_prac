package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.CommonEntity;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CommonDAO<T extends CommonEntity<ID>, ID extends Serializable> {
    T getById(ID id);
    List<T> getAll();
    T save(T entity);
    void saveCollection(Collection<T> entities);
    T update(T entity);
    void delete(T entity);
    void deleteById(ID id);
}