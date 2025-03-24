package ru.web.tsvelenev.WEB.DAO;

import lombok.Builder;
import lombok.Getter;
import ru.web.tsvelenev.WEB.models.Performance;

import java.util.List;

public interface PerformanceDAO extends CommonDAO<Performance, Long> {

    List<Performance> getAllPerformancesByTitle(String title);
    Performance getSinglePerformanceByTitle(String title);
    List<Performance> getPerformancesByDirector(Long directorId);
    List<Performance> getPerformancesByHall(Long hallId);
    List<Performance> getByFilter(Filter filter);

    @Builder
    @Getter
    class Filter {
        private String title;
        private Long directorId;
        private Long hallId;
        private Integer minDuration;
        private Integer maxDuration;
    }

    static Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}