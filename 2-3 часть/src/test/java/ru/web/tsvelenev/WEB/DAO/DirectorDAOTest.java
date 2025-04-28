package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.DAO.DirectorDAO;
import ru.web.tsvelenev.WEB.models.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class DirectorDAOTest {

    @Autowired
    private DirectorDAO directorDAO;

    private Director director1;
    private Director director2;
    private Director director3;

    @BeforeAll
    void setup() {
        // Инициализация данных один раз для всех тестов
        director1 = new Director("Christopher Nolan");
        director2 = new Director("Quentin Tarantino");
        director3 = new Director("Steven Spielberg");

        directorDAO.save(director1);
        directorDAO.save(director2);
        directorDAO.save(director3);
    }

    @AfterAll
    void tearDown() {
        // Очистка данных после всех тестов
        directorDAO.delete(director1);
        directorDAO.delete(director2);
        directorDAO.delete(director3);
    }

    @Test
    void testGetById() {
        Director found = directorDAO.getById(director1.getId());
        assertNotNull(found);
        assertEquals(director1.getName(), found.getName());
    }

    @Test
    void testGetById_NotFound() {
        Director found = directorDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Director> directors = (List<Director>) directorDAO.getAll();
        assertTrue(directors.size() >= 3);
        assertTrue(directors.stream().anyMatch(d -> d.getName().equals("Christopher Nolan")));
        assertTrue(directors.stream().anyMatch(d -> d.getName().equals("Quentin Tarantino")));
        assertTrue(directors.stream().anyMatch(d -> d.getName().equals("Steven Spielberg")));
    }

    @Test
    void testSave() {
        Director newDirector = new Director("James Cameron");
        directorDAO.save(newDirector);

        Director found = directorDAO.getById(newDirector.getId());
        assertNotNull(found);
        assertEquals("James Cameron", found.getName());

        directorDAO.delete(newDirector);
    }

    @Test
    void testSaveCollection() {
        Director director4 = new Director("Martin Scorsese");
        Director director5 = new Director("Ridley Scott");
        List<Director> directors = List.of(director4, director5);

        directorDAO.saveCollection(directors);

        List<Director> allDirectors = (List<Director>) directorDAO.getAll();

        directorDAO.delete(director4);
        directorDAO.delete(director5);
    }

    @Test
    void testUpdate() {
        director1.setName("Christopher Nolan Updated");
        directorDAO.update(director1);

        Director updated = directorDAO.getById(director1.getId());
        assertEquals("Christopher Nolan Updated", updated.getName());

        director1.setName("Christopher Nolan");
        directorDAO.update(director1);
    }

    @Test
    void testDelete() {
        Director tempDirector = new Director("Temporary Director");
        directorDAO.save(tempDirector);

        directorDAO.delete(tempDirector);

        Director found = directorDAO.getById(tempDirector.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Director tempDirector = new Director("Temporary Director 2");
        directorDAO.save(tempDirector);

        directorDAO.deleteById(tempDirector.getId());

        Director found = directorDAO.getById(tempDirector.getId());
        assertNull(found);
    }

    @Test
    void testGetByName() {
        directorDAO.save(new Director("Quentin Tarantino")); // Добавляем еще одного с таким же именем

        List<Director> found = directorDAO.getByName("Quentin Tarantino");
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().allMatch(d -> d.getName().equals("Quentin Tarantino")));
    }

    @Test
    void testGetByName_NotFound() {
        List<Director> found = directorDAO.getByName("Unknown Director");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetSingleByName() {
        Director found = directorDAO.getSingleByName("Steven Spielberg");
        assertNotNull(found);
        assertEquals("Steven Spielberg", found.getName());
    }

    @Test
    void testGetSingleByName_NotFound() {
        Director found = directorDAO.getSingleByName("Unknown Director");
        assertNull(found);
    }

    @Test
    void testGetByNameContaining() {
        List<Director> found = directorDAO.getByNameContaining("en");
        assertTrue(found.size() >= 2); // Christopher Nolan и Steven Spielberg
        assertTrue(found.stream().anyMatch(d -> d.getName().contains("en")));
    }

    @Test
    void testGetByNameContaining_NotFound() {
        List<Director> found = directorDAO.getByNameContaining("xyz");
        assertTrue(found.isEmpty());
    }
}