package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class HallDAOTest {

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private TheaterDAO theaterDAO;

    private Theater theater1;
    private Theater theater2;

    private Hall hall1;
    private Hall hall2;
    private Hall hall3;

    @BeforeAll
    void setup() {
        theater1 = new Theater();
        theater1.setName("Bolshoi Theater");
        theater1.setAddress("Theatre Square, 1");
        theaterDAO.save(theater1);

        theater2 = new Theater();
        theater2.setName("Mariinsky Theater");
        theater2.setAddress("Theatre Square, 1");
        theaterDAO.save(theater2);

        hall1 = new Hall("Main Hall", theater1);
        hall2 = new Hall("Small Hall", theater1);
        hall3 = new Hall("Main Hall", theater2);

        hallDAO.save(hall1);
        hallDAO.save(hall2);
        hallDAO.save(hall3);
    }

    @AfterAll
    void tearDown() {
        hallDAO.delete(hall1);
        hallDAO.delete(hall2);
        hallDAO.delete(hall3);

        theaterDAO.delete(theater1);
        theaterDAO.delete(theater2);
    }

    @Test
    void testGetById() {
        Hall found = hallDAO.getById(hall1.getId());
        assertNotNull(found);
        assertEquals(hall1.getName(), found.getName());
        assertEquals(hall1.getTheater().getId(), found.getTheater().getId());
    }

    @Test
    void testGetById_NotFound() {
        Hall found = hallDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Hall> halls = (List<Hall>) hallDAO.getAll();
        assertTrue(halls.size() >= 3);
        assertTrue(halls.stream().anyMatch(h -> h.getName().equals("Main Hall") && h.getTheater().getId().equals(theater1.getId())));
        assertTrue(halls.stream().anyMatch(h -> h.getName().equals("Small Hall")));
        assertTrue(halls.stream().anyMatch(h -> h.getName().equals("Main Hall") && h.getTheater().getId().equals(theater2.getId())));
    }

    @Test
    void testSave() {
        Hall newHall = new Hall("New Hall", theater1);
        hallDAO.save(newHall);

        Hall found = hallDAO.getById(newHall.getId());
        assertNotNull(found);
        assertEquals("New Hall", found.getName());
        assertEquals(theater1.getId(), found.getTheater().getId());

        hallDAO.delete(newHall);
    }

    @Test
    void testSaveCollection() {
        Hall hall4 = new Hall("Hall Four", theater1);
        Hall hall5 = new Hall("Hall Five", theater2);
        List<Hall> halls = List.of(hall4, hall5);

        hallDAO.saveCollection(halls);

        List<Hall> allHalls = (List<Hall>) hallDAO.getAll();

        hallDAO.delete(hall4);
        hallDAO.delete(hall5);
    }

    @Test
    void testUpdate() {
        hall1.setName("Main Hall Updated");
        hall1.setTheater(theater2);
        hallDAO.update(hall1);

        Hall updated = hallDAO.getById(hall1.getId());
        assertEquals("Main Hall Updated", updated.getName());
        assertEquals(theater2.getId(), updated.getTheater().getId());

        hall1.setName("Main Hall");
        hall1.setTheater(theater1);
        hallDAO.update(hall1);
    }

    @Test
    void testDelete() {
        Hall tempHall = new Hall("Temp Hall", theater1);
        hallDAO.save(tempHall);

        hallDAO.delete(tempHall);

        Hall found = hallDAO.getById(tempHall.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Hall tempHall = new Hall("Temp Hall 2", theater2);
        hallDAO.save(tempHall);

        hallDAO.deleteById(tempHall.getId());

        Hall found = hallDAO.getById(tempHall.getId());
        assertNull(found);
    }

    @Test
    void testGetByName() {
        Hall duplicateHall = new Hall("Main Hall", theater1);
        hallDAO.save(duplicateHall);

        List<Hall> found = hallDAO.getByName("Main Hall");
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().allMatch(h -> h.getName().equals("Main Hall")));

        hallDAO.delete(duplicateHall);
    }

    @Test
    void testGetByName_NotFound() {
        List<Hall> found = hallDAO.getByName("Unknown Hall");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetSingleByName() {
        Hall found = hallDAO.getSingleByName("Small Hall");
        assertNotNull(found);
        assertEquals("Small Hall", found.getName());
    }

    @Test
    void testGetSingleByName_NotFound() {
        Hall found = hallDAO.getSingleByName("Unknown Hall");
        assertNull(found);
    }

    @Test
    void testGetByTheaterId() {
        List<Hall> found = hallDAO.getByTheaterId(theater1.getId());
        assertEquals(3, found.size()); // Main Hall и Small Hall
        assertTrue(found.stream().allMatch(h -> h.getTheater().getId().equals(theater1.getId())));
    }

    @Test
    void testGetByTheaterId_NotFound() {
        List<Hall> found = hallDAO.getByTheaterId(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByNameAndTheater() {
        List<Hall> found = hallDAO.getByNameAndTheater("Main Hall", theater1.getId());
        assertEquals(1, found.size());
        assertEquals("Main Hall", found.get(0).getName());
        assertEquals(theater1.getId(), found.get(0).getTheater().getId());
    }

    @Test
    void testGetByNameAndTheater_NotFound() {
        List<Hall> found = hallDAO.getByNameAndTheater("Unknown", theater1.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByTheaterName() {
        List<Hall> found = hallDAO.getByTheaterName("Bolshoi");
        assertFalse(found.isEmpty());
        assertTrue(found.stream().allMatch(h -> h.getTheater().getName().contains("Bolshoi")));
    }

    @Test
    void testGetByTheaterName_NotFound() {
        List<Hall> found = hallDAO.getByTheaterName("Unknown Theater");
        assertTrue(found.isEmpty());
    }
}