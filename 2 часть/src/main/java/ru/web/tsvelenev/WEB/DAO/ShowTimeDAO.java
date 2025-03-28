package ru.web.tsvelenev.WEB.DAO;

import ru.web.tsvelenev.WEB.models.ShowTime;
import java.sql.Date;
import java.util.List;

public interface ShowTimeDAO extends CommonDAO<ShowTime, Long> {
    List<ShowTime> getByPerformanceId(Long performanceId);
    List<ShowTime> getByDate(Date date);
    List<ShowTime> getByDateRange(Date startDate, Date endDate);
    List<ShowTime> getByPerformanceTitle(String title);
    List<ShowTime> getByPerformanceAndDate(Long performanceId, Date date);
}