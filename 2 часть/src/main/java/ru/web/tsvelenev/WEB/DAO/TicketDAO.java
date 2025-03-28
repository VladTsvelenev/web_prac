package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Ticket;
import java.util.List;

public interface TicketDAO extends CommonDAO<Ticket, Long> {
    List<Ticket> getByShowTimeId(Long showTimeId);
    List<Ticket> getByUserId(Long userId);
    List<Ticket> getAvailableByShowTime(Long showTimeId);
    List<Ticket> getSoldByShowTime(Long showTimeId);
    List<Ticket> getBySeatId(Long seatId);
    List<Ticket> getByPriceRange(Integer minPrice, Integer maxPrice);
    List<Ticket> getByShowTimeAndStatus(Long showTimeId, Boolean isSold);
}