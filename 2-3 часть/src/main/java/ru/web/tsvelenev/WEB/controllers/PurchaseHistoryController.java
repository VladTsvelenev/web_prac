package ru.web.tsvelenev.WEB.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.web.tsvelenev.WEB.DAO.TicketDAO;
import ru.web.tsvelenev.WEB.DAO.UsersDAO;
import ru.web.tsvelenev.WEB.models.Ticket;
import ru.web.tsvelenev.WEB.models.Users;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Controller
@RequestMapping("/purchase-history")
public class PurchaseHistoryController {

    private final TicketDAO ticketDAO;
    private final UsersDAO usersDAO;

    public PurchaseHistoryController(TicketDAO ticketDAO, UsersDAO usersDAO) {
        this.ticketDAO = ticketDAO;
        this.usersDAO = usersDAO;
    }

    @GetMapping
    public String getPurchaseHistory(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login?from=purchase-history";
        }

        Optional<Users> userOpt = usersDAO.findByEmail(username);
        if (userOpt.isEmpty()) {
            session.removeAttribute("username");
            return "redirect:/login";
        }

        // Добавляем список билетов
        model.addAttribute("tickets", ticketDAO.findPurchasedTicketsByUserWithDetails(userOpt.get()));

        // Добавляем функции для форматирования даты
        model.addAttribute("formatDate", (Function<Date, String>) date ->
                date != null ? new SimpleDateFormat("dd.MM.yyyy").format(date) : "");

        model.addAttribute("formatTime", (Function<Date, String>) date ->
                date != null ? new SimpleDateFormat("HH:mm").format(date) : "");

        return "purchaseHistory";
    }

    // Вспомогательный метод для проверки данных (можно использовать для отладки)
    private void validateTicketsData(List<Ticket> tickets) {
        tickets.forEach(ticket -> {
            if (ticket.getShowTime() == null) {
                throw new IllegalStateException(
                        String.format("Ticket ID %d has null showTime", ticket.getId()));
            }
            if (ticket.getShowTime().getShowDatetime() == null) {
                throw new IllegalStateException(
                        String.format("ShowTime ID %d has null datetime", ticket.getShowTime().getId()));
            }
        });
    }
}