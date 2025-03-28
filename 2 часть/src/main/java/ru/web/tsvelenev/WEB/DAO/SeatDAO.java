package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Seat;
import java.util.List;

public interface SeatDAO extends CommonDAO<Seat, Long> {
    List<Seat> getByHallId(Long hallId);
    List<Seat> getBySeatTypeId(Long seatTypeId);
    List<Seat> getByRowNumber(Integer rowNumber);
    List<Seat> getBySeatNumber(Integer seatNumber);
    List<Seat> getByHallAndRow(Long hallId, Integer rowNumber);
    List<Seat> getByHallAndType(Long hallId, Long seatTypeId);
}