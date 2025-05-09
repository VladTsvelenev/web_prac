package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

public interface TicketDAO {
    Ticket save(Ticket ticket);
    List<Ticket> findByUser(Users user);
    Ticket findFirstByPerformanceAndSeatTypeAndIsSoldFalse(Performance performance, SeatType seatType);
    List<Ticket> getByShowTimeId(Long showTimeId);
    List<Ticket> getAvailableByShowTime(Long showTimeId);
    List<Ticket> getSoldByShowTime(Long showTimeId);
    List<Ticket> getBySeatId(Long seatId);
    List<Ticket> getByPriceRange(Integer minPrice, Integer maxPrice);
    List<Ticket> getByShowTimeAndStatus(Long showTimeId, Boolean isSold);
    List<Ticket> findAvailableByPerformanceId(Long performanceId);
    List<Ticket> findAvailableTickets(Long performanceId);
    public void deleteByPerformance(Performance performance);
    public List<Ticket> findByShowTimeAndIsSoldFalse(ShowTime showTime);
    public List<Ticket> findPurchasedTicketsByUserWithDetails(Users user);
    public Ticket getById(Long id);
}