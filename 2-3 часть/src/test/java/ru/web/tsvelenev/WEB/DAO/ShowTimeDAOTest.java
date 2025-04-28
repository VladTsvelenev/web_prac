package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.*;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class ShowTimeDAOTest {

    @Autowired
    private ShowTimeDAO showTimeDAO;

    @Autowired
    private PerformanceDAO performanceDAO;

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private TheaterDAO theaterDAO;

    @Autowired
    private DirectorDAO directorDAO;

    private Performance performance1;
    private Performance performance2;
    private ShowTime showTime1;
    private ShowTime showTime2;
    private ShowTime showTime3;
    private Hall hall1;
    private Hall hall2;
    private Theater theater1;
    private Theater theater2;
    private Director director1;
    private Director director2;

    @BeforeAll
    void setup() {
        // Создаем театры
        theater1 = new Theater();
        theater1.setName("Bolshoi Theater");
        theater1.setAddress("Theatre Square, 1");
        theater1.setInfo("The oldest theater in Russia");

        theater2 = new Theater();
        theater2.setName("Mariinsky Theater");
        theater2.setAddress("Theatre Square, 1");
        theater2.setInfo("Historic theater of opera and ballet");

        theaterDAO.save(theater1);
        theaterDAO.save(theater2);

        // Создаем залы с привязкой к театрам
        hall1 = new Hall("Main Hall", theater1);
        hall2 = new Hall("Small Hall", theater2);
        hallDAO.save(hall1);
        hallDAO.save(hall2);

        // Обновляем театры с добавленными залами
        theater1 = theaterDAO.getById(theater1.getId());
        theater2 = theaterDAO.getById(theater2.getId());

        // Создаем режиссеров
        director1 = new Director("Peter Brook");
        director2 = new Director("Robert Wilson");
        directorDAO.save(director1);
        directorDAO.save(director2);

        // Создаем спектакли
        performance1 = new Performance("Romeo and Juliet", hall1, director1, 120);
        performance2 = new Performance("Hamlet", hall2, director2, 180);
        performanceDAO.save(performance1);
        performanceDAO.save(performance2);

        // Создаем расписания
        Date today = new Date(System.currentTimeMillis());
        Date tomorrow = new Date(System.currentTimeMillis() + 86400000);
        Date yesterday = new Date(System.currentTimeMillis() - 86400000);

        showTime1 = new ShowTime(performance1, today);
        showTime2 = new ShowTime(performance1, tomorrow);
        showTime3 = new ShowTime(performance2, yesterday);

        showTimeDAO.save(showTime1);
        showTimeDAO.save(showTime2);
        showTimeDAO.save(showTime3);
    }

    @AfterAll
    void tearDown() {
        // Удаляем созданные данные в правильном порядке (из-за foreign key constraints)
        showTimeDAO.delete(showTime1);
        showTimeDAO.delete(showTime2);
        showTimeDAO.delete(showTime3);
        performanceDAO.delete(performance1);
        performanceDAO.delete(performance2);
        directorDAO.delete(director1);
        directorDAO.delete(director2);
        hallDAO.delete(hall1);
        hallDAO.delete(hall2);
        theaterDAO.delete(theater1);
        theaterDAO.delete(theater2);
    }

    @Test
    void testGetById() {
        ShowTime found = showTimeDAO.getById(showTime1.getId());
        assertNotNull(found);
        assertEquals(showTime1.getPerformance().getId(), found.getPerformance().getId());
        assertEquals(showTime1.getShowDatetime().toString(), found.getShowDatetime().toString());
    }

    @Test
    void testGetById_NotFound() {
        ShowTime found = showTimeDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<ShowTime> showTimes = (List<ShowTime>) showTimeDAO.getAll();
        assertTrue(showTimes.size() >= 3);
    }

    @Test
    void testSave() {
        Date newDate = new Date(System.currentTimeMillis() + 172800000); // +2 дня
        ShowTime newShowTime = new ShowTime(performance1, newDate);
        showTimeDAO.save(newShowTime);

        ShowTime found = showTimeDAO.getById(newShowTime.getId());
        assertNotNull(found);
        assertEquals(performance1.getId(), found.getPerformance().getId());
        assertEquals(newDate.toString(), found.getShowDatetime().toString());

        showTimeDAO.delete(newShowTime);
    }

    @Test
    void testUpdate() {
        Date updatedDate = new Date(System.currentTimeMillis() + 259200000); // +3 дня
        showTime1.setShowDatetime(updatedDate);
        showTimeDAO.update(showTime1);

        ShowTime updated = showTimeDAO.getById(showTime1.getId());
        assertEquals(updatedDate.toString(), updated.getShowDatetime().toString());
    }

    @Test
    void testGetByPerformanceId() {
        List<ShowTime> showTimes = showTimeDAO.getByPerformanceId(performance1.getId());
        assertTrue(showTimes.size() >= 2);
        assertTrue(showTimes.stream().allMatch(st -> st.getPerformance().getId().equals(performance1.getId())));
    }

    @Test
    void testGetByDate() {
        Date today = new Date(System.currentTimeMillis());
        List<ShowTime> showTimes = showTimeDAO.getByDate(today);
        assertTrue(showTimes.size() >= 1);
        assertTrue(showTimes.stream().anyMatch(st -> st.getId().equals(showTime1.getId())));
    }

    @Test
    void testGetByDateRange() {
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // Вчера
        Date endDate = new Date(System.currentTimeMillis() + 86400000); // Завтра

        List<ShowTime> showTimes = showTimeDAO.getByDateRange(startDate, endDate);
        assertTrue(showTimes.size() >= 3);
    }

    @Test
    void testGetByPerformanceTitle() {
        List<ShowTime> showTimes = showTimeDAO.getByPerformanceTitle("Romeo");
        assertTrue(showTimes.size() >= 2);
        assertTrue(showTimes.stream().allMatch(st ->
                st.getPerformance().getTitle().contains("Romeo")));
    }

    @Test
    void testGetByPerformanceAndDate() {
        Date today = new Date(System.currentTimeMillis());
        List<ShowTime> showTimes = showTimeDAO.getByPerformanceAndDate(performance1.getId(), today);
        assertEquals(1, showTimes.size());
        assertEquals(performance1.getId(), showTimes.get(0).getPerformance().getId());
        assertEquals(today, showTimes.get(0).getShowDatetime());
    }

    @Test
    void testShowTimeConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new ShowTime(null, new Date(System.currentTimeMillis())));
        assertThrows(IllegalArgumentException.class, () -> new ShowTime(performance1, null));
    }

    @Test
    void testUtilDateConstructor() {
        java.util.Date utilDate = new java.util.Date();
        ShowTime showTime = new ShowTime(performance1, utilDate);
        assertEquals(new Date(utilDate.getTime()), showTime.getShowDatetime());
    }

    @Test
    void testFullEntityRelations() {
        ShowTime showTime = showTimeDAO.getById(showTime1.getId());
        Performance performance = showTime.getPerformance();
        Hall hall = performance.getHall();
        Theater theater = hall.getTheater();
        Director director = performance.getDirector();

        assertNotNull(performance);
        assertEquals("Romeo and Juliet", performance.getTitle());

        assertNotNull(hall);
        assertEquals("Main Hall", hall.getName());

        assertNotNull(theater);
        assertEquals("Bolshoi Theater", theater.getName());
        assertEquals("Theatre Square, 1", theater.getAddress());

        assertNotNull(director);
        assertEquals("Peter Brook", director.getName());
    }

    @Test
    void testTheaterHallsRelation() {
        Theater theater = theaterDAO.getById(theater1.getId());
        assertFalse(theater.getHalls().isEmpty());
        assertEquals("Main Hall", theater.getHalls().get(0).getName());
    }
}