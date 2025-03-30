package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.SeatType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class SeatTypeDAOTest {

    @Autowired
    private SeatTypeDAO seatTypeDAO;

    private SeatType standardType;
    private SeatType vipType;
    private SeatType premiumType;

    @BeforeAll
    void setup() {
        // Создаем тестовые типы мест
        standardType = new SeatType("Standard");
        vipType = new SeatType("VIP");
        premiumType = new SeatType("Premium Seat");

        seatTypeDAO.save(standardType);
        seatTypeDAO.save(vipType);
        seatTypeDAO.save(premiumType);
    }

    @AfterAll
    void tearDown() {
        seatTypeDAO.delete(standardType);
        seatTypeDAO.delete(vipType);
        seatTypeDAO.delete(premiumType);
    }

    @Test
    void testGetById() {
        SeatType found = seatTypeDAO.getById(standardType.getId());
        assertNotNull(found);
        assertEquals("Standard", found.getName());
    }

    @Test
    void testGetById_NotFound() {
        SeatType found = seatTypeDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<SeatType> seatTypes = (List<SeatType>) seatTypeDAO.getAll();
        assertTrue(seatTypes.size() >= 3);
    }

    @Test
    void testSave() {
        SeatType newType = new SeatType("New Type");
        seatTypeDAO.save(newType);

        SeatType found = seatTypeDAO.getById(newType.getId());
        assertNotNull(found);
        assertEquals("New Type", found.getName());

        seatTypeDAO.delete(newType);
    }

    @Test
    void testUpdate() {
        standardType.setName("Standard Updated");
        seatTypeDAO.update(standardType);

        SeatType updated = seatTypeDAO.getById(standardType.getId());
        assertEquals("Standard Updated", updated.getName());

        standardType.setName("Standard");
        seatTypeDAO.update(standardType);
    }

    @Test
    void testDelete() {
        SeatType tempType = new SeatType("Temp Type");
        seatTypeDAO.save(tempType);

        seatTypeDAO.delete(tempType);

        SeatType found = seatTypeDAO.getById(tempType.getId());
        assertNull(found);
    }

    @Test
    void testGetByName() {
        List<SeatType> found = seatTypeDAO.getByName("VIP");
        assertEquals(1, found.size());
        assertEquals("VIP", found.get(0).getName());
    }

    @Test
    void testGetByName_NotFound() {
        List<SeatType> found = seatTypeDAO.getByName("Unknown");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetSingleByName() {
        SeatType found = seatTypeDAO.getSingleByName("VIP");
        assertNotNull(found);
        assertEquals("VIP", found.getName());
    }

    @Test
    void testGetSingleByName_NotFound() {
        SeatType found = seatTypeDAO.getSingleByName("Unknown");
        assertNull(found);
    }

    @Test
    void testGetByNameContaining() {
        List<SeatType> found = seatTypeDAO.getByNameContaining("eat");
        assertTrue(found.size() >= 1);
        assertTrue(found.stream().anyMatch(t -> t.getName().contains("eat")));
    }

    @Test
    void testGetByNameContaining_NotFound() {
        List<SeatType> found = seatTypeDAO.getByNameContaining("xyz");
        assertTrue(found.isEmpty());
    }

    @Test
    void testSeatTypeConstructorValidation() {
        assertThrows(NullPointerException.class, () -> new SeatType(null));
    }
}