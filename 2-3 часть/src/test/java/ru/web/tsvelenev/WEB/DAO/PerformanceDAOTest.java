package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.*;
import java.util.List;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class PerformanceDAOTest {

    @Autowired
    private PerformanceDAO performanceDAO;

    @Autowired
    private DirectorDAO directorDAO;

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private TheaterDAO theaterDAO;

    private Director director1;
    private Director director2;
    private Theater theater;
    private Hall hall1;
    private Hall hall2;
    private Performance performance1;
    private Performance performance2;
    private Performance performance3;

    @BeforeAll
    void setup() {
        director1 = new Director("Christopher Nolan");
        director2 = new Director("Quentin Tarantino");
        directorDAO.save(director1);
        directorDAO.save(director2);

        theater = new Theater();
        theater.setName("Bolshoi Theater");
        theater.setAddress("Theatre Square, 1");
        theaterDAO.save(theater);

        hall1 = new Hall("Main Hall", theater);
        hall2 = new Hall("Small Hall", theater);
        hallDAO.save(hall1);
        hallDAO.save(hall2);

        performance1 = new Performance("Hamlet", hall1, director1, 120);  // 2 часа
        performance2 = new Performance("Romeo and Juliet", hall1, director2, 150); // 2.5 часа
        performance3 = new Performance("The Cherry Orchard", hall2, director1, 90); // 1.5 часа

        performanceDAO.save(performance1);
        performanceDAO.save(performance2);
        performanceDAO.save(performance3);
    }

    @AfterAll
    void tearDown() {
        performanceDAO.delete(performance1);
        performanceDAO.delete(performance2);
        performanceDAO.delete(performance3);
        hallDAO.delete(hall1);
        hallDAO.delete(hall2);
        theaterDAO.delete(theater);
        directorDAO.delete(director1);
        directorDAO.delete(director2);
    }

    @Test
    void testGetById() {
        Performance found = performanceDAO.getById(performance1.getId());
        assertNotNull(found);
        assertEquals("Hamlet", found.getTitle());
        assertEquals(120, found.getDurationMinutes());
        assertEquals(director1.getId(), found.getDirector().getId());
        assertEquals(hall1.getId(), found.getHall().getId());
    }

    @Test
    void testGetById_NotFound() {
        Performance found = performanceDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Performance> performances = (List<Performance>) performanceDAO.getAll();
        assertTrue(performances.size() >= 3);
        assertTrue(performances.stream().anyMatch(p -> p.getTitle().equals("Hamlet")));
        assertTrue(performances.stream().anyMatch(p -> p.getTitle().equals("Romeo and Juliet")));
        assertTrue(performances.stream().anyMatch(p -> p.getTitle().equals("The Cherry Orchard")));
    }

    @Test
    void testSave() {
        Performance newPerformance = new Performance("New Play", hall2, director2, 180);
        performanceDAO.save(newPerformance);

        Performance found = performanceDAO.getById(newPerformance.getId());
        assertNotNull(found);
        assertEquals("New Play", found.getTitle());
        assertEquals(180, found.getDurationMinutes());
        assertEquals(Duration.ofHours(3), found.getDuration());

        performanceDAO.delete(newPerformance);
    }

    @Test
    void testUpdate() {
        String newTitle = "Hamlet Updated";
        int newDuration = 180;

        performance1.setTitle(newTitle);
        performance1.setDurationMinutes(newDuration);
        performanceDAO.update(performance1);

        Performance updated = performanceDAO.getById(performance1.getId());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(newDuration, updated.getDurationMinutes());

        performance1.setTitle("Hamlet");
        performance1.setDurationMinutes(120);
        performanceDAO.update(performance1);
    }

    @Test
    void testDelete() {
        Performance temp = new Performance("Temp Performance", hall1, director1, 60);
        performanceDAO.save(temp);

        performanceDAO.delete(temp);

        assertNull(performanceDAO.getById(temp.getId()));
    }

    @Test
    void testGetByTitle() {
        List<Performance> found = performanceDAO.getByTitle("Hamlet");
        assertFalse(found.isEmpty());
        assertEquals("Hamlet", found.get(0).getTitle());
    }

    @Test
    void testGetByTitle_NotFound() {
        List<Performance> found = performanceDAO.getByTitle("Nonexistent Title");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetSingleByTitle() {
        Performance found = performanceDAO.getSingleByTitle("Romeo and Juliet");
        assertNotNull(found);
        assertEquals("Romeo and Juliet", found.getTitle());
    }

    @Test
    void testGetSingleByTitle_NotFound() {
        Performance found = performanceDAO.getSingleByTitle("Nonexistent Title");
        assertNull(found);
    }

    @Test
    void testGetByDirectorId() {
        List<Performance> found = performanceDAO.getByDirectorId(director1.getId());
        assertEquals(2, found.size()); // Hamlet и The Cherry Orchard
        assertTrue(found.stream().allMatch(p -> p.getDirector().getId().equals(director1.getId())));
    }

    @Test
    void testGetByHallId() {
        List<Performance> found = performanceDAO.getByHallId(hall1.getId());
        assertEquals(2, found.size()); // Hamlet и Romeo and Juliet
        assertTrue(found.stream().allMatch(p -> p.getHall().getId().equals(hall1.getId())));
    }

    @Test
    void testGetByTitleContaining() {
        List<Performance> found = performanceDAO.getByTitleContaining("and");
        assertEquals(1, found.size());
        assertEquals("Romeo and Juliet", found.get(0).getTitle());
    }

    @Test
    void testGetByDurationBetween() {
        // Проверяем диапазон от 100 до 160 минут
        List<Performance> found = performanceDAO.getByDurationBetween(100, 160);
        assertEquals(2, found.size()); // Hamlet (120) и Romeo and Juliet (150)

        // Проверяем граничные значения
        List<Performance> exactMatch = performanceDAO.getByDurationBetween(120, 120);
        assertEquals(1, exactMatch.size());
        assertEquals("Hamlet", exactMatch.get(0).getTitle());
    }

    @Test
    void testGetByDurationBetween_NotFound() {
        List<Performance> found = performanceDAO.getByDurationBetween(200, 300);
        assertTrue(found.isEmpty());
    }

    @Test
    void testNullTitle() {
        Performance invalid = new Performance(null, hall1, director1, 120);
        assertThrows(DataIntegrityViolationException.class, () -> {
            performanceDAO.save(invalid);
        });
    }

    @Test
    void testZeroDuration() {
        Performance invalid = new Performance("Zero Duration", hall2, director2, 0);
        assertThrows(DataIntegrityViolationException.class, () -> {
            performanceDAO.save(invalid);
        });
    }

    @Test
    void testNegativeDuration() {
        Performance invalid = new Performance("Negative Duration", hall1, director1, -10);
        assertThrows(DataIntegrityViolationException.class, () -> {
            performanceDAO.save(invalid);
        });
    }

    @Test
    void testTransientDurationField() {
        Performance test = performanceDAO.getById(performance1.getId());
        assertEquals(Duration.ofMinutes(120), test.getDuration());
    }
}