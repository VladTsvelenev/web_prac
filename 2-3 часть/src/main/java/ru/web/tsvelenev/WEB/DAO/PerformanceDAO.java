package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.Performance;
import java.util.List;

public interface PerformanceDAO extends CommonDAO<Performance, Long> {
    List<Performance> getByTitle(String title);
    Performance getSingleByTitle(String title);
    List<Performance> getByDirectorId(Long directorId);
    List<Performance> getByHallId(Long hallId);
    List<Performance> getByTitleContaining(String titlePart);
    List<Performance> getByDurationBetween(Integer minDuration, Integer maxDuration);
    List<Performance> findByTheaterId(Long theaterId);
}