package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.Theater;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class TheaterDAOTest {

    @Autowired
    private TheaterDAO theaterDAO;

    private Theater theater1;
    private Theater theater2;
    private Theater theater3;

    @BeforeAll
    void setup() {
        theater1 = new Theater();
        theater1.setName("Bolshoi Theater");
        theater1.setAddress("Theatre Square, 1");
        theater1.setInfo("Historic theater in Moscow");

        theater2 = new Theater();
        theater2.setName("Mariinsky Theater");
        theater2.setAddress("Theatre Square, 1");
        theater2.setInfo("Historic theater in Saint Petersburg");

        theater3 = new Theater();
        theater3.setName("Alexandrinsky Theater");
        theater3.setAddress("Ostrovsky Square, 6");
        theater3.setInfo("Oldest national theater in Russia");

        theaterDAO.save(theater1);
        theaterDAO.save(theater2);
        theaterDAO.save(theater3);
    }

    @AfterAll
    void tearDown() {
        theaterDAO.delete(theater1);
        theaterDAO.delete(theater2);
        theaterDAO.delete(theater3);
    }

    @Test
    void testGetById() {
        Theater found = theaterDAO.getById(theater1.getId());
        assertNotNull(found);
        assertEquals(theater1.getName(), found.getName());
        assertEquals(theater1.getAddress(), found.getAddress());
    }

    @Test
    void testGetById_NotFound() {
        Theater found = theaterDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Theater> theaters = (List<Theater>) theaterDAO.getAll();
        assertTrue(theaters.size() >= 3);
        assertTrue(theaters.stream().anyMatch(t -> t.getName().equals("Bolshoi Theater")));
        assertTrue(theaters.stream().anyMatch(t -> t.getName().equals("Mariinsky Theater")));
        assertTrue(theaters.stream().anyMatch(t -> t.getName().equals("Alexandrinsky Theater")));
    }

    @Test
    void testSave() {
        Theater newTheater = new Theater();
        newTheater.setName("New Theater");
        newTheater.setAddress("New Address");
        theaterDAO.save(newTheater);

        Theater found = theaterDAO.getById(newTheater.getId());
        assertNotNull(found);
        assertEquals("New Theater", found.getName());
        assertEquals("New Address", found.getAddress());

        theaterDAO.delete(newTheater);
    }

    @Test
    void testSaveCollection() {
        Theater theater4 = new Theater();
        theater4.setName("Theater Four");
        theater4.setAddress("Address Four");

        Theater theater5 = new Theater();
        theater5.setName("Theater Five");
        theater5.setAddress("Address Five");

        List<Theater> theaters = List.of(theater4, theater5);

        theaterDAO.saveCollection(theaters);

        List<Theater> allTheaters = (List<Theater>) theaterDAO.getAll();

        theaterDAO.delete(theater4);
        theaterDAO.delete(theater5);
    }

    @Test
    void testUpdate() {
        theater1.setName("Bolshoi Theater Updated");
        theater1.setAddress("Updated Address");
        theaterDAO.update(theater1);

        Theater updated = theaterDAO.getById(theater1.getId());
        assertEquals("Bolshoi Theater Updated", updated.getName());
        assertEquals("Updated Address", updated.getAddress());
        theater1.setName("Bolshoi Theater");
        theater1.setAddress("Theatre Square, 1");
        theaterDAO.update(theater1);
    }

    @Test
    void testDelete() {
        Theater tempTheater = new Theater();
        tempTheater.setName("Temporary Theater");
        tempTheater.setAddress("Temp Address");
        theaterDAO.save(tempTheater);

        theaterDAO.delete(tempTheater);

        Theater found = theaterDAO.getById(tempTheater.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Theater tempTheater = new Theater();
        tempTheater.setName("Temporary Theater 2");
        tempTheater.setAddress("Temp Address 2");
        theaterDAO.save(tempTheater);

        theaterDAO.deleteById(tempTheater.getId());

        Theater found = theaterDAO.getById(tempTheater.getId());
        assertNull(found);
    }

    @Test
    void testGetByName() {
        Theater duplicateTheater = new Theater();
        duplicateTheater.setName("Mariinsky Theater");
        duplicateTheater.setAddress("Different Address");
        theaterDAO.save(duplicateTheater);

        List<Theater> found = theaterDAO.getByName("Mariinsky Theater");
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().allMatch(t -> t.getName().equals("Mariinsky Theater")));

        // Cleanup
        theaterDAO.delete(duplicateTheater);
    }

    @Test
    void testGetByName_NotFound() {
        List<Theater> found = theaterDAO.getByName("Unknown Theater");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetSingleByName() {
        Theater found = theaterDAO.getSingleByName("Alexandrinsky Theater");
        assertNotNull(found);
        assertEquals("Alexandrinsky Theater", found.getName());
    }

    @Test
    void testGetSingleByName_NotFound() {
        Theater found = theaterDAO.getSingleByName("Unknown Theater");
        assertNull(found);
    }

    @Test
    void testGetByNameContaining() {
        List<Theater> found = theaterDAO.getByNameContaining("insky");
        assertTrue(found.size() >= 2); // Mariinsky и Alexandrinsky
        assertTrue(found.stream().anyMatch(t -> t.getName().contains("insky")));
    }

    @Test
    void testGetByNameContaining_NotFound() {
        List<Theater> found = theaterDAO.getByNameContaining("xyz");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByAddressContaining() {
        List<Theater> found = theaterDAO.getByAddressContaining("Square");
        assertTrue(found.size() >= 3); // Все три театра имеют адрес с "Square"
        assertTrue(found.stream().allMatch(t -> t.getAddress().contains("Square")));
    }

    @Test
    void testGetByAddressContaining_NotFound() {
        List<Theater> found = theaterDAO.getByAddressContaining("xyz");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByNameAndAddress() {
        List<Theater> found = theaterDAO.getByNameAndAddress("Mariinsky Theater", "Theatre Square, 1");
        assertFalse(found.isEmpty());
        assertEquals("Mariinsky Theater", found.get(0).getName());
        assertEquals("Theatre Square, 1", found.get(0).getAddress());
    }

    @Test
    void testGetByNameAndAddress_NotFound() {
        List<Theater> found = theaterDAO.getByNameAndAddress("Unknown", "Address");
        assertTrue(found.isEmpty());
    }
}