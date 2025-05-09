package ru.web.tsvelenev.WEB.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.web.tsvelenev.WEB.DAO.*;
import ru.web.tsvelenev.WEB.models.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final ShowTimeDAO showTimeDAO;
    private final SeatDAO seatDAO;

    @GetMapping
    @Transactional
    public String listPerformances(Model model) {
        try {
            List<Performance> performances = performanceDAO.getAll();

            performances.forEach(performance -> {
                Hibernate.initialize(performance.getHall());
                Hibernate.initialize(performance.getDirector());
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
        Performance performance = performanceDAO.getById(id);
        if (performance == null) {
            model.addAttribute("error", "Спектакль с ID " + id + " не найден.");
            return "error";
        }

        Hibernate.initialize(performance.getDirector());
        Hibernate.initialize(performance.getHall());
        Hibernate.initialize(performance.getHall().getTheater());
        Hibernate.initialize(performance.getShowTimes());

        Set<Actor> actors = performance.getActors().stream()
                .map(PerformanceActor::getActor)
                .collect(Collectors.toSet());

        List<ShowTime> showTimes = new ArrayList<>(performance.getShowTimes());

        List<Ticket> availableTickets = new ArrayList<>();
        for (ShowTime showTime : showTimes) {
            availableTickets.addAll(ticketDAO.findByShowTimeAndIsSoldFalse(showTime));
        }

        model.addAttribute("performance", performance);
        model.addAttribute("actors", actors);
        model.addAttribute("showTimes", showTimes);
        model.addAttribute("availableTickets", availableTickets);

        return "performanceDetails";
    }

    @GetMapping("/add")
    @Transactional
    public String showAddForm(Model model) {
        model.addAttribute("theaters", theaterDAO.getAll());
        model.addAttribute("halls", theaterDAO.getAllHalls());
        model.addAttribute("directors", directorDAO.getAll());
        model.addAttribute("actors", actorDAO.getAll());
        return "addPerformance";
    }

    @GetMapping("/api/halls/{hallId}/seat-types")
    @ResponseBody
    @Transactional
    public List<SeatType> getSeatTypesForHall(@PathVariable Long hallId) {
        return seatDAO.getByHallId(hallId).stream()
                .map(Seat::getSeatType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    @Transactional
    public String addPerformance(
            @RequestParam String title,
            @RequestParam Integer durationMinutes,
            @RequestParam Long directorId,
            @RequestParam Long hallId,
            @RequestParam List<Long> actorIds,
            @RequestParam List<String> showTimes,
            @RequestParam Map<String, String> seatPrices,
            Model model) {

        try {
            // Получаем зависимости
            Director director = directorDAO.getById(directorId);
            Hall hall = theaterDAO.getHallById(hallId);

            if (director == null) {
                throw new IllegalArgumentException("Режиссер не найден");
            }
            if (hall == null) {
                throw new IllegalArgumentException("Зал не найден");
            }

            // Создаем и сохраняем спектакль
            Performance performance = new Performance();
            performance.setTitle(title);
            performance.setDurationMinutes(durationMinutes);
            performance.setDirector(director);
            performance.setHall(hall);

            Performance savedPerformance = performanceDAO.save(performance);

            // Добавляем актеров
            addActorsToPerformance(savedPerformance, actorIds);

            // Преобразуем цены в Map<Long, Integer>
            Map<Long, Integer> seatTypePrices = new HashMap<>();
            seatPrices.forEach((seatTypeId, price) -> {
                try {
                    seatTypePrices.put(Long.parseLong(seatTypeId), Integer.parseInt(price));
                } catch (NumberFormatException e) {
                    // Пропускаем невалидные значения
                }
            });

            // Создаем расписание показов и билеты
            createShowTimesWithTickets(savedPerformance, showTimes, seatTypePrices);

            return "redirect:/performances/" + savedPerformance.getId() + "?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании спектакля: " + e.getMessage());
            prepareAddFormModel(model);
            return "addPerformance";
        }
    }

    private Map<Long, Integer> parseSeatTypePrices(String jsonPrices) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<Long, Integer>> typeRef = new TypeReference<>() {};
        return mapper.readValue(jsonPrices, typeRef);
    }

    private void addActorsToPerformance(Performance performance, List<Long> actorIds) {
        for (Long actorId : actorIds) {
            Actor actor = actorDAO.getById(actorId);
            if (actor != null) {
                PerformanceActor pa = new PerformanceActor();
                pa.setId(new PerformanceActorId(performance.getId(), actor.getId()));
                pa.setPerformance(performance);
                pa.setActor(actor);
                performanceActorDAO.save(pa);
            }
        }
    }

    private void createShowTimesWithTickets(Performance performance,
                                            List<String> showTimes,
                                            Map<Long, Integer> seatTypePrices) {
        for (String showTimeStr : showTimes) {
            if (showTimeStr == null || showTimeStr.isEmpty()) continue;

            LocalDateTime showDateTime = LocalDateTime.parse(showTimeStr.replace(" ", "T"));
            Date date = Date.from(showDateTime.atZone(ZoneId.systemDefault()).toInstant());

            ShowTime showTime = new ShowTime(performance, date);
            ShowTime savedShowTime = showTimeDAO.save(showTime);

            generateTicketsForShowTime(savedShowTime, performance.getHall(), seatTypePrices);
        }
    }

    private void generateTicketsForShowTime(ShowTime showTime,
                                            Hall hall,
                                            Map<Long, Integer> seatTypePrices) {
        List<Seat> seats = seatDAO.getByHallId(hall.getId());

        for (Seat seat : seats) {
            Integer price = seatTypePrices.get(seat.getSeatType().getId());
            if (price != null) {
                Ticket ticket = new Ticket();
                ticket.setShowTime(showTime);
                ticket.setSeat(seat);
                ticket.setPrice(price);
                ticket.setIsSold(false);
                ticketDAO.save(ticket);
            }
        }
    }

    private void prepareAddFormModel(Model model) {
        model.addAttribute("theaters", theaterDAO.getAll());
        model.addAttribute("halls", theaterDAO.getAllHalls());
        model.addAttribute("directors", directorDAO.getAll());
        model.addAttribute("actors", actorDAO.getAll());
        model.addAttribute("allSeatTypes", seatTypeDAO.getAll());
    }

    @PostMapping("/{id}/delete")
    @Transactional
    public String deletePerformance(@PathVariable Long id, Model model) {
        try {
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

            List<ShowTime> showTimes = new ArrayList<>(performance.getShowTimes());
            if (showTimes.isEmpty()) {
                model.addAttribute("error", "Нет доступных показов для спектакля");
                return "error";
            }

            Hall hall = performance.getHall();
            Hibernate.initialize(hall.getSeats());

            if (hall.getSeats() == null || hall.getSeats().isEmpty()) {
                model.addAttribute("error", "В зале нет мест");
                return "error";
            }

            for (ShowTime showTime : showTimes) {
                for (Seat seat : hall.getSeats()) {
                    SeatType seatType = seat.getSeatType();
                    String priceStr = prices.get(seatType.getId().toString());

                    if (priceStr == null || priceStr.isEmpty()) continue;

                    try {
                        Integer price = Integer.parseInt(priceStr);
                        Ticket ticket = new Ticket(showTime, seat, price);
                        ticket.setIsSold(false);
                        ticketDAO.save(ticket);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }

            return "redirect:/performances/" + id + "?success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка генерации билетов: " + e.getMessage());
            return "error";
        }
    }
}