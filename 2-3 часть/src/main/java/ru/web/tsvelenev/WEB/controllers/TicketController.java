package ru.web.tsvelenev.WEB.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.web.tsvelenev.WEB.DAO.*;
import ru.web.tsvelenev.WEB.models.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {
    private final TicketDAO ticketDAO;
    private final PerformanceDAO performanceDAO;
    private final UsersDAO userDAO;
    private final SeatTypeDAO seatTypeDAO;

    @PostMapping("/buy")
    public String buyTicket(@RequestParam Long performanceId,
                            @RequestParam Long seatTypeId,
                            @SessionAttribute(required = false) Long userId,
                            Model model) {
        if (userId == null) {
            return "redirect:/login";
        }

        Performance performance = performanceDAO.getById(performanceId);
        Users user = userDAO.getById(userId);
        SeatType seatType = seatTypeDAO.getById(seatTypeId);

        if (performance == null || user == null || seatType == null) {
            model.addAttribute("error", "Ошибка при покупке билета: неверные данные");
            return "error";
        }

        // Находим первый доступный билет данного типа
        Ticket availableTicket = ticketDAO.findFirstByPerformanceAndSeatTypeAndIsSoldFalse(performance, seatType);

        if (availableTicket == null) {
            model.addAttribute("error", "Нет доступных билетов данного типа");
            return "error";
        }

        // Покупка билета
        availableTicket.setUser(user);
        availableTicket.setIsSold(true);
        availableTicket.setPurchaseDate(LocalDateTime.now());
        ticketDAO.save(availableTicket);

        return "redirect:/tickets/my";
    }

    @GetMapping("/my")
    public String getUserTickets(@SessionAttribute(required = false) Long userId, Model model) {
        if (userId == null) {
            return "redirect:/login";
        }

        Users user = userDAO.getById(userId);
        List<Ticket> tickets = ticketDAO.findByUser(user);

        model.addAttribute("tickets", tickets);
        return "userTickets";
    }
}