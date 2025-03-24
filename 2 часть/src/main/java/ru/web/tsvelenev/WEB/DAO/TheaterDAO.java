package ru.web.tsvelenev.WEB.DAO;

import lombok.Builder;
import lombok.Getter;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

public interface TheaterDAO extends CommonDAO<Theater, Long> {

    List<Theater> getAllTheatersByName(String theaterName);
    Theater getSingleTheaterByName(String theaterName);
    List<Theater> getByFilter(Filter filter);

    @Builder
    @Getter
    class Filter {
        private String name;
        private String address;
    }

    static Filter.FilterBuilder getFilterBuilder() {
        return Filter.builder();
    }
}