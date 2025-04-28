package ru.web.tsvelenev.WEB.controllers;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.DAO.PerformanceDAO;
import ru.web.tsvelenev.WEB.DAO.TheaterDAO;
import ru.web.tsvelenev.WEB.models.Performance;
import ru.web.tsvelenev.WEB.models.Theater;
import ru.web.tsvelenev.WEB.models.Hall;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class TheaterController {

    private final TheaterDAO theaterDAO;
    private final PerformanceDAO performanceDAO;

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
        return "addTheater";
    }

    @PostMapping("/add")
    @Transactional
    public String addTheater(@ModelAttribute Theater theater,
                             @RequestParam(required = false) List<String> hallsName,
                             @RequestParam(required = false) List<Integer> hallsSeatCount,
                             Model model) {
        try {
            theaterDAO.save(theater);
            if (hallsName != null && hallsSeatCount != null) {
                for (int i = 0; i < hallsName.size(); i++) {
                    Hall hall = new Hall();
                    hall.setName(hallsName.get(i));
                    hall.setSeatCount(hallsSeatCount.get(i));
                    hall.setTheater(theater);
                    theaterDAO.saveHall(hall); // добавь этот метод в DAO
                }
            }
            return "redirect:/theaters";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении театра или залов: " + e.getMessage());
            model.addAttribute("theater", theater);
            return "addTheater";
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
}