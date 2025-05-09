package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Seat;
import ru.web.tsvelenev.WEB.models.SeatType;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

public interface TheaterDAO extends CommonDAO<Theater, Long> {
    List<Theater> getByName(String name);
    Theater getSingleByName(String name);
    List<Theater> getByNameContaining(String namePart);
    List<Theater> getByAddressContaining(String addressPart);
    List<Theater> getByNameAndAddress(String name, String address);
    void saveHall(Hall hall); // <-- добавил
    Hall getHallById(Long id);
    List<Hall> getAllHalls();
    List<Hall> getHallsByTheaterId(Long theaterId);
    void saveSeat(Seat seat);
    List<SeatType> getAllSeatTypes();
    SeatType getSeatTypeById(Long id);
    List<Seat> getSeatsByHallId(Long hallId);
    List<Seat> getSeatsByHallAndType(Long hallId, Long seatTypeId);
    void saveSeatType(SeatType seatType);
    void deleteSeatType(Long id);
    void deleteSeat(Long id);
    Seat getSeatById(Long id);
}
