package ru.web.tsvelenev.WEB.controllers;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.PerformanceDAO;
import ru.web.tsvelenev.WEB.DAO.SeatTypeDAO;
import ru.web.tsvelenev.WEB.DAO.TheaterDAO;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class TheaterController {

    private final TheaterDAO theaterDAO;
    private final PerformanceDAO performanceDAO;
    private final SeatTypeDAO seatTypeDAO;

    @GetMapping
    public String listTheaters(Model model) {
        try {
            model.addAttribute("theaters", theaterDAO.getAll());
            return "theaters";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке театров: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    @Transactional
    public String viewTheater(@PathVariable Long id, Model model) {
        try {
            Theater theater = theaterDAO.getById(id);
            if (theater == null) {
                model.addAttribute("error", "Театр с ID " + id + " не найден.");
                return "error";
            }

            // Инициализируем связанные объекты
            Hibernate.initialize(theater.getHalls());

            // Получаем спектакли и инициализируем связанные сущности
            List<Performance> performances = performanceDAO.findByTheaterId(id);
            performances.forEach(performance -> {
                Hibernate.initialize(performance.getDirector());
                Hibernate.initialize(performance.getHall());
            });

            model.addAttribute("theater", theater);
            model.addAttribute("performances", performances);
            return "theaterDetails";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке театра: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("theater", new Theater());
        model.addAttribute("seatTypes", seatTypeDAO.getAll());
        return "addTheater";
    }

    @PostMapping("/add")
    @Transactional
    public String addTheater(@ModelAttribute Theater theater,
                             @RequestParam List<String> hallsName,
                             @RequestParam List<Long> seatTypeId,
                             @RequestParam List<Integer> seatTypeCount,
                             @RequestParam(required = false) List<Integer> rows,
                             @RequestParam(required = false) List<Integer> seatsPerRow,
                             Model model) {

        try {
            theaterDAO.save(theater);

            if (hallsName != null && seatTypeId != null && seatTypeCount != null) {
                int typeIndex = 0;

                for (int hallIndex = 0; hallIndex < hallsName.size(); hallIndex++) {
                    Hall hall = new Hall();
                    hall.setName(hallsName.get(hallIndex));
                    hall.setTheater(theater);
                    theaterDAO.saveHall(hall);

                    while (typeIndex < seatTypeId.size()) {
                        Long typeId = seatTypeId.get(typeIndex);
                        SeatType seatType = seatTypeDAO.getById(typeId);

                        if (seatType == null) {
                            model.addAttribute("error", "Тип места с ID " + typeId + " не найден!");
                            model.addAttribute("seatTypes", seatTypeDAO.getAll());
                            return "addTheater";
                        }

                        int count = seatTypeCount.get(typeIndex);
                        Integer rowCount = (rows != null && rows.size() > typeIndex) ? rows.get(typeIndex) : null;
                        Integer seatsInRow = (seatsPerRow != null && seatsPerRow.size() > typeIndex) ? seatsPerRow.get(typeIndex) : null;

                        createSeats(hall, seatType, count, rowCount, seatsInRow);
                        typeIndex++;
                    }
                }
            }

            return "redirect:/theaters";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            model.addAttribute("seatTypes", seatTypeDAO.getAll());
            return "addTheater";
        }
    }


    private void createSeatsForHall(Hall hall, SeatType seatType, int seatCount, Integer rows, Integer seatsPerRow) {
        if (rows != null && seatsPerRow != null && rows * seatsPerRow == seatCount) {
            // Создаем места с нумерацией рядов и мест
            for (int row = 1; row <= rows; row++) {
                for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                    Seat seat = new Seat();
                    seat.setHall(hall);
                    seat.setSeatType(seatType);
                    seat.setRowNumber(row);
                    seat.setSeatNumber(seatNum);
                    theaterDAO.saveSeat(seat);
                }
            }
        } else {
            // Создаем места без указания рядов (просто последовательные номера)
            for (int i = 1; i <= seatCount; i++) {
                Seat seat = new Seat();
                seat.setHall(hall);
                seat.setSeatType(seatType);
                seat.setSeatNumber(i);
                theaterDAO.saveSeat(seat);
            }
        }
    }
    @PostMapping("/{id}/delete")
    @Transactional
    public String deleteTheater(@PathVariable Long id, Model model) {
        try {
            theaterDAO.deleteById(id);
            return "redirect:/theaters";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при удалении театра: " + e.getMessage());
            return "theaters";
        }
    }

    private void createSeats(Hall hall, SeatType seatType, int count, Integer rows, Integer seatsPerRow) {
        if (rows != null && seatsPerRow != null) {
            for (int row = 1; row <= rows; row++) {
                for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                    Seat seat = new Seat();
                    seat.setHall(hall);
                    seat.setSeatType(seatType);
                    seat.setRowNumber(row);
                    seat.setSeatNumber(seatNum);
                    theaterDAO.saveSeat(seat);
                }
            }
        } else {
            for (int i = 1; i <= count; i++) {
                Seat seat = new Seat();
                seat.setHall(hall);
                seat.setSeatType(seatType);
                seat.setSeatNumber(i);
                theaterDAO.saveSeat(seat);
            }
        }
    }
}