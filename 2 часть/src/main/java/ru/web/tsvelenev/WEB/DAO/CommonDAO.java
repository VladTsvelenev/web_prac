package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.CommonEntity;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.Collection;

public interface CommonDAO<T extends CommonEntity<ID>, ID> {
    T getById(ID id);

    Collection<T> getAll();

    Theater save(T entity);

    void saveCollection(Collection<T> entities);

    void delete(T entity);

    void deleteById(ID id);

    void update(T entity);
}