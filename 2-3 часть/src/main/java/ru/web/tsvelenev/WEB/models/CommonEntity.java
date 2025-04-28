package ru.web.tsvelenev.WEB.models;

public interface CommonEntity<ID> {
    ID getId();
    void setId(ID id);
}