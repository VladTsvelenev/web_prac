package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Seat;

import java.util.List;

public interface HallDAO extends CommonDAO<Hall, Long> {
    List<Hall> getByName(String name);
    Hall getSingleByName(String name);
    List<Hall> getByTheaterId(Long theaterId);
    List<Hall> getByNameAndTheater(String name, Long theaterId);
    List<Hall> getByTheaterName(String theaterName);
    public List<Seat> getSeatsByHallId(Long hallId);
}