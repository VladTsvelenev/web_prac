package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.SeatType;
import java.util.List;

public interface SeatTypeDAO extends CommonDAO<SeatType, Long> {
    List<SeatType> getByName(String name);
    SeatType getSingleByName(String name);
    List<SeatType> getByNameContaining(String namePart);
}