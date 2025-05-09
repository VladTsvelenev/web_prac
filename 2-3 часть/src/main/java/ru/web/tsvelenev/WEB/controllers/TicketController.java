package ru.web.tsvelenev.WEB.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.web.tsvelenev.WEB.DAO.TicketDAO;
import ru.web.tsvelenev.WEB.DAO.UsersDAO;
import ru.web.tsvelenev.WEB.models.Ticket;
import ru.web.tsvelenev.WEB.models.Users;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketDAO ticketDAO;
    private final UsersDAO usersDAO;

    public TicketController(TicketDAO ticketDAO, UsersDAO usersDAO) {
        this.ticketDAO = ticketDAO;
        this.usersDAO = usersDAO;
    }

    @PostMapping("/buy/{id}")
    @Transactional
    public String buyTicket(@PathVariable Long id, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login?from=/tickets/buy/" + id;
        }

        Optional<Users> userOpt = usersDAO.findByEmail(username);
        if (userOpt.isEmpty()) {
            session.removeAttribute("username");
            return "redirect:/login";
        }

        Ticket ticket = ticketDAO.getById(id);
        if (ticket == null || ticket.getIsSold()) {
            model.addAttribute("error", "Билет не найден или уже продан");
            return "error";
        }

        ticket.setIsSold(true);
        ticket.setUser(userOpt.get());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticketDAO.save(ticket);

        return "redirect:/purchase-history?purchased=" + id;
    }
}