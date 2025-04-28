package ru.web.tsvelenev.WEB.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.*;
import ru.web.tsvelenev.WEB.models.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceDAO performanceDAO;
    private final TheaterDAO theaterDAO;
    private final ActorDAO actorDAO;
    private final DirectorDAO directorDAO;
    private final PerformanceActorDAO performanceActorDAO;
    private final TicketDAO ticketDAO;
    private final SeatTypeDAO seatTypeDAO;

    @GetMapping
    @Transactional
    public String listPerformances(Model model) {
        try {
            List<Performance> performances = performanceDAO.getAll();

            // Инициализируем необходимые связанные объекты
            performances.forEach(performance -> {
                Hibernate.initialize(performance.getHall());
                Hibernate.initialize(performance.getDirector());

                // Если нужно получить театр через зал
                if (performance.getHall() != null) {
                    Hibernate.initialize(performance.getHall().getTheater());
                }
            });

            model.addAttribute("performances", performances);
            return "performance";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке спектаклей: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    @Transactional
    public String viewPerformance(@PathVariable Long id, Model model) {
        try {
            Performance performance = performanceDAO.getById(id);
            if (performance == null) {
                model.addAttribute("error", "Спектакль с ID " + id + " не найден.");
                return "error";
            }

            // Явно инициализируем связанные объекты
            Hibernate.initialize(performance.getDirector());
            Hibernate.initialize(performance.getHall());
            Hibernate.initialize(performance.getHall().getTheater());
            Hibernate.initialize(performance.getShowTimes());

            // Получаем актеров через PerformanceActor
            Set<Actor> actors = performance.getActors().stream()
                    .map(PerformanceActor::getActor)
                    .collect(Collectors.toSet());

            model.addAttribute("performance", performance);
            model.addAttribute("actors", actors);
            model.addAttribute("showTimes", performance.getShowTimes());

            return "performanceDetails";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке спектакля: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add")
    @Transactional
    public String showAddForm(Model model) {
        model.addAttribute("performance", new Performance());
        model.addAttribute("theaters", theaterDAO.getAll());

        List<Hall> halls = theaterDAO.getAll().stream()
                .flatMap(theater -> theater.getHalls().stream())
                .toList();
        model.addAttribute("halls", halls);
        model.addAttribute("directors", directorDAO.getAll());
        model.addAttribute("actors", actorDAO.getAll());

        return "addPerformance";
    }

    @PostMapping("/add")
    @Transactional
    public String addPerformance(@ModelAttribute Performance performance,
                                 @RequestParam Long directorId,
                                 @RequestParam Long hallId,
                                 @RequestParam List<Long> actorIds,
                                 Model model) {
        try {
            Director director = directorDAO.getById(directorId);
            if (director == null) {
                throw new IllegalArgumentException("Режиссёр не найден");
            }

            Hall hall = theaterDAO.getHallById(hallId);
            if (hall == null) {
                throw new IllegalArgumentException("Зал не найден");
            }

            // 2. Устанавливаем связи
            performance.setDirector(director);
            performance.setHall(hall);

            // 3. Сохраняем спектакль (это важно сделать до создания связей)
            Performance savedPerformance = performanceDAO.save(performance);

            // 4. Создаём связи с актёрами
            for (Long actorId : actorIds) {
                Actor actor = actorDAO.getById(actorId);
                if (actor != null) {
                    PerformanceActor pa = new PerformanceActor();
                    pa.setId(new PerformanceActorId(savedPerformance.getId(), actor.getId()));
                    pa.setPerformance(savedPerformance);
                    pa.setActor(actor);
                    performanceActorDAO.save(pa);  // Сохраняем связь
                }
            }

            return "redirect:/performances?success";
        } catch (Exception e) {
            // При ошибке заполняем модель для повторного отображения формы
            model.addAttribute("theaters", theaterDAO.getAll());
            model.addAttribute("halls", theaterDAO.getAllHalls());
            model.addAttribute("directors", directorDAO.getAll());
            model.addAttribute("actors", actorDAO.getAll());
            model.addAttribute("error", "Ошибка сохранения: " + e.getMessage());
            return "addPerformance";
        }
    }

    private void prepareModelForError(Model model, Performance performance) {
        model.addAttribute("theaters", theaterDAO.getAll());
        model.addAttribute("halls", theaterDAO.getAllHalls());
        model.addAttribute("directors", directorDAO.getAll());
        model.addAttribute("actors", actorDAO.getAll());
        model.addAttribute("performance", performance);
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String deletePerformance(@PathVariable Long id, Model model) {
        try {
            // Каскадное удаление должно обработать PerformanceActor автоматически
            performanceDAO.deleteById(id);
            return "redirect:/performances";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при удалении спектакля: " + e.getMessage());
            return "performance";
        }
    }


    @PostMapping("/{id}/generate-tickets")
    @Transactional
    public String generateTickets(@PathVariable Long id,
                                  @RequestParam Map<String, String> prices,
                                  Model model) {
        try {
            Performance performance = performanceDAO.getById(id);
            if (performance == null) {
                model.addAttribute("error", "Спектакль не найден");
                return "error";
            }

            // Удаляем старые билеты для этого спектакля
            ticketDAO.deleteByPerformance(performance);

            // Получаем зал и инициализируем места
            Hall hall = performance.getHall();
            if (hall == null) {
                model.addAttribute("error", "Зал не найден");
                return "error";
            }

            // Явно загружаем места, так как связь LAZY
            Hibernate.initialize(hall.getSeats());

            if (hall.getSeats() == null || hall.getSeats().isEmpty()) {
                model.addAttribute("error", "В зале нет мест");
                return "error";
            }

            // Создаем новые билеты
            for (Seat seat : hall.getSeats()) {
                SeatType seatType = seat.getSeatType();
                String priceStr = prices.get(seatType.getId().toString());

                if (priceStr == null || priceStr.isEmpty()) {
                    continue; // или обработать ошибку
                }

                try {
                    Integer price = Integer.parseInt(priceStr);
                    Ticket ticket = new Ticket();
                    ticket.setPerformance(performance);
                    ticket.setSeatType(seatType);
                    ticket.setSeatNumber(seat.getSeatNumber());
                    ticket.setPrice(price);
                    ticket.setIsSold(false);

                    ticketDAO.save(ticket);
                } catch (NumberFormatException e) {
                    // обработка ошибки преобразования цены
                    continue;
                }
            }

            return "redirect:/performances/" + id + "?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка генерации билетов: " + e.getMessage());
            return "error";
        }
    }
}