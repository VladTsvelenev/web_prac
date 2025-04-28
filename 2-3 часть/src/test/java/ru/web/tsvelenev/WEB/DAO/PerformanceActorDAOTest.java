package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.models.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class PerformanceActorDAOTest {

    @Autowired
    private PerformanceActorDAO performanceActorDAO;

    @Autowired
    private PerformanceDAO performanceDAO;

    @Autowired
    private ActorDAO actorDAO;

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private DirectorDAO directorDAO;

    @Autowired
    private TheaterDAO theaterDAO;

    private Performance performance;
    private Actor actor1;
    private Actor actor2;
    private PerformanceActor link1;
    private PerformanceActor link2;
    private Hall hall;
    private Director director;
    private Theater theater;

    @BeforeAll
    void setup() {
        theater = new Theater();
        theater.setName("Test Theater");
        theater.setAddress("Test Address");
        theater.setInfo("Test Info");
        theaterDAO.save(theater);
        assertNotNull(theater.getId(), "Theater should be saved");

        director = new Director("Test Director");
        directorDAO.save(director);
        assertNotNull(director.getId(), "Director should be saved");

        hall = new Hall("Main Hall", theater);
        hallDAO.save(hall);
        assertNotNull(hall.getId(), "Hall should be saved");
        assertEquals(theater.getId(), hall.getTheater().getId(), "Hall should reference Theater");

        performance = new Performance(
                "Test Performance",
                hall,
                director,
                120
        );
        performanceDAO.save(performance);
        assertNotNull(performance.getId(), "Performance should be saved");
        assertEquals(hall.getId(), performance.getHall().getId(), "Performance should reference Hall");
        assertEquals(director.getId(), performance.getDirector().getId(), "Performance should reference Director");

        actor1 = new Actor("John Doe 1");
        actorDAO.save(actor1);
        actor2 = new Actor("Jane Smith 2");
        actorDAO.save(actor2);
        assertNotNull(actor1.getId(), "Actor1 should be saved");
        assertNotNull(actor2.getId(), "Actor2 should be saved");

        link1 = new PerformanceActor(new PerformanceActorId(performance.getId(), actor1.getId()), performance, actor1);
        link2 = new PerformanceActor(new PerformanceActorId(performance.getId(), actor2.getId()), performance, actor2);

        performanceActorDAO.save(link1);
        performanceActorDAO.save(link2);
        assertNotNull(link1.getId(), "Link1 should be saved");
        assertNotNull(link2.getId(), "Link2 should be saved");
    }

    @AfterAll
    void tearDown() {
        performanceActorDAO.delete(link1);
        performanceActorDAO.delete(link2);
        performanceDAO.delete(performance);
        actorDAO.delete(actor1);
        actorDAO.delete(actor2);
        hallDAO.delete(hall);
        directorDAO.delete(director);
        theaterDAO.delete(theater);
    }

    @Test
    void testGetById() {
        PerformanceActor found = performanceActorDAO.getById(link1.getId());
        assertNotNull(found);
        assertEquals(performance.getId(), found.getPerformance().getId());
        assertEquals(actor1.getId(), found.getActor().getId());
    }

    @Test
    void testGetById_NotFound() {
        PerformanceActorId nonExistentId = new PerformanceActorId(999L, 999L);
        PerformanceActor found = performanceActorDAO.getById(nonExistentId);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<PerformanceActor> allLinks = (List<PerformanceActor>) performanceActorDAO.getAll();
        assertTrue(allLinks.size() >= 2);
        assertTrue(allLinks.contains(link1));
        assertTrue(allLinks.contains(link2));
    }

    @Test
    void testSave() {
        Actor newActor = new Actor("New Actor");
        actorDAO.save(newActor);

        PerformanceActor newLink = new PerformanceActor(
                new PerformanceActorId(performance.getId(), newActor.getId()),
                performance,
                newActor
        );
        performanceActorDAO.save(newLink);

        PerformanceActor found = performanceActorDAO.getById(newLink.getId());
        assertNotNull(found);
        assertEquals(performance.getId(), found.getPerformance().getId());
        assertEquals(newActor.getId(), found.getActor().getId());

        // Cleanup
        performanceActorDAO.delete(newLink);
        actorDAO.delete(newActor);
    }

    @Test
    void testUpdate() {
        Performance newPerformance = new Performance(
                "Updated Performance",
                hall,
                director,
                90
        );
        performanceDAO.save(newPerformance);

        link1.setPerformance(newPerformance);
        performanceActorDAO.update(link1);

        PerformanceActor updated = performanceActorDAO.getById(link1.getId());
        assertEquals(newPerformance.getId(), updated.getPerformance().getId());

        link1.setPerformance(performance);
        performanceActorDAO.update(link1);
        performanceDAO.delete(newPerformance);
    }

    @Test
    void testDelete() {
        Actor tempActor = new Actor("Temp Actor");
        actorDAO.save(tempActor);

        PerformanceActor tempLink = new PerformanceActor(
                new PerformanceActorId(performance.getId(), tempActor.getId()),
                performance,
                tempActor
        );
        performanceActorDAO.save(tempLink);

        performanceActorDAO.delete(tempLink);

        PerformanceActor found = performanceActorDAO.getById(tempLink.getId());
        assertNull(found);

        actorDAO.delete(tempActor);
    }

    @Test
    void testDeleteById() {
        Actor tempActor = new Actor("Temp Actor 2");
        actorDAO.save(tempActor);

        PerformanceActor tempLink = new PerformanceActor(
                new PerformanceActorId(performance.getId(), tempActor.getId()),
                performance,
                tempActor
        );
        performanceActorDAO.save(tempLink);

        performanceActorDAO.deleteById(tempLink.getId());

        PerformanceActor found = performanceActorDAO.getById(tempLink.getId());
        assertNull(found);

        actorDAO.delete(tempActor);
    }

    @Test
    void testFindByPerformanceId() {
        List<PerformanceActor> found = performanceActorDAO.findByPerformanceId(performance.getId());
        assertEquals(2, found.size());
        assertTrue(found.stream()
                .allMatch(pa -> pa.getPerformance().getId().equals(performance.getId())));
    }

    @Test
    void testFindByPerformanceId_NotFound() {
        List<PerformanceActor> found = performanceActorDAO.findByPerformanceId(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByActorId() {
        List<PerformanceActor> found = performanceActorDAO.findByActorId(actor1.getId());
        assertEquals(1, found.size());
        assertEquals(actor1.getId(), found.get(0).getActor().getId());
    }

    @Test
    void testFindByActorId_NotFound() {
        List<PerformanceActor> found = performanceActorDAO.findByActorId(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void testExistsByPerformanceAndActor() {
        boolean exists = performanceActorDAO.existsByPerformanceAndActor(performance, actor1);
        assertTrue(exists);

        Actor newActor = new Actor("Non-linked Actor");
        actorDAO.save(newActor);
        boolean notExists = performanceActorDAO.existsByPerformanceAndActor(performance, newActor);
        assertFalse(notExists);

        actorDAO.delete(newActor);
    }

    @Test
    void testDeleteByPerformanceAndActor() {
        Actor tempActor = new Actor("Actor to delete");
        actorDAO.save(tempActor);

        PerformanceActor tempLink = new PerformanceActor(
                new PerformanceActorId(performance.getId(), tempActor.getId()),
                performance,
                tempActor
        );
        performanceActorDAO.save(tempLink);

        performanceActorDAO.deleteByPerformanceAndActor(performance, tempActor);

        boolean exists = performanceActorDAO.existsByPerformanceAndActor(performance, tempActor);
        assertFalse(exists);

        actorDAO.delete(tempActor);
    }

    @Test
    void testPerformanceDuration() {
        PerformanceActor link = performanceActorDAO.getById(link1.getId());
        Duration duration = link.getPerformance().getDuration();
        assertEquals(2, duration.toHours());
        assertEquals(120, link.getPerformance().getDurationMinutes());
    }

    @Test
    void testPerformanceRelations() {
        PerformanceActor link = performanceActorDAO.getById(link1.getId());
        Performance p = link.getPerformance();

        assertEquals("Test Performance", p.getTitle());
        assertEquals(120, p.getDurationMinutes());
        assertEquals("Main Hall", p.getHall().getName());
        assertEquals("Test Director", p.getDirector().getName());
        assertEquals("Test Theater", p.getHall().getTheater().getName());
    }
}