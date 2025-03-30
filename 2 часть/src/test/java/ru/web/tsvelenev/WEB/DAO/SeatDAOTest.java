package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class SeatDAOTest {

    @Autowired
    private SeatDAO seatDAO;

    @Autowired
    private TheaterDAO theaterDAO;

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private SeatTypeDAO seatTypeDAO;

    private Theater theater;
    private Hall hall;
    private SeatType seatTypes;
    private Seat seat1;
    private Seat seat2;
    private Seat seat3;

    @BeforeAll
    void setup() {
        theater = new Theater();
        theater.setName("Test Theater");
        theater.setAddress("Test Address");
        theater.setInfo("Test Info");
        theaterDAO.save(theater);

        seatTypes = new SeatType("Standard");
        seatTypeDAO.save(seatTypes);

        hall = new Hall("Main Hall", theater);
        hallDAO.save(hall);

        seat1 = new Seat(hall, null, 1, 1);
        seat2 = new Seat(hall, null, 1, 2);
        seat3 = new Seat(hall, null, 2, 1);

        seatDAO.save(seat1);
        seatDAO.save(seat2);
        seatDAO.save(seat3);
    }

    @AfterAll
    void tearDown() {
        seatDAO.delete(seat1);
        seatDAO.delete(seat2);
        seatDAO.delete(seat3);
        hallDAO.delete(hall);
        seatTypeDAO.delete(seatTypes);
        theaterDAO.delete(theater);
    }

    @Test
    @Transactional
    void testGetById() {
        Seat found = seatDAO.getById(seat1.getId());
        assertNotNull(found);
        assertEquals(1, found.getRowNumber());
        assertEquals(1, found.getSeatNumber());
        assertEquals(hall.getId(), found.getHall().getId());
        assertEquals(seatTypes.getId(), found.getSeatType().getId());
    }

    @Test
    void testGetById_NotFound() {
        Seat found = seatDAO.getById(999L);
        assertNull(found);
    }

    @Test
    @Transactional
    void testGetAll() {
        List<Seat> seats = (List<Seat>) seatDAO.getAll();
        assertTrue(seats.size() >= 3);
        assertTrue(seats.stream().anyMatch(s -> s.getId().equals(seat1.getId())));
        assertTrue(seats.stream().anyMatch(s -> s.getId().equals(seat2.getId())));
        assertTrue(seats.stream().anyMatch(s -> s.getId().equals(seat3.getId())));
    }

    @Test
    @Transactional
    void testSave() {
        Seat newSeat = new Seat(hall, seatTypes, 3, 1);
        seatDAO.save(newSeat);

        Seat found = seatDAO.getById(newSeat.getId());
        assertNotNull(found);
        assertEquals(3, found.getRowNumber());
        assertEquals(1, found.getSeatNumber());
        assertEquals(hall.getId(), found.getHall().getId());
        assertEquals(seatTypes.getId(), found.getSeatType().getId());

        seatDAO.delete(newSeat);
    }

    @Test
    @Transactional
    void testSaveCollection() {
        Seat seat4 = new Seat(hall, seatTypes, 3, 2);
        Seat seat5 = new Seat(hall, seatTypes, 3, 3);
        List<Seat> seats = List.of(seat4, seat5);

        seatDAO.saveCollection(seats);

        List<Seat> allSeats = (List<Seat>) seatDAO.getAll();
        assertTrue(allSeats.containsAll(seats));

        seatDAO.delete(seat4);
        seatDAO.delete(seat5);
    }

    @Test
    @Transactional
    void testUpdate() {
        int originalRow = seat1.getRowNumber();
        seat1.setRowNumber(10);
        seatDAO.update(seat1);

        Seat updated = seatDAO.getById(seat1.getId());
        assertEquals(10, updated.getRowNumber());

        seat1.setRowNumber(originalRow);
        seatDAO.update(seat1);
    }

    @Test
    @Transactional
    void testDelete() {
        Seat tempSeat = new Seat(hall, seatTypes, 4, 1);
        seatDAO.save(tempSeat);

        seatDAO.delete(tempSeat);

        Seat found = seatDAO.getById(tempSeat.getId());
        assertNull(found);
    }

    @Test
    @Transactional
    void testDeleteById() {
        Seat tempSeat = new Seat(hall, seatTypes, 4, 2);
        seatDAO.save(tempSeat);

        seatDAO.deleteById(tempSeat.getId());

        Seat found = seatDAO.getById(tempSeat.getId());
        assertNull(found);
    }

    @Test
    @Transactional
    void testGetByHallId() {
        List<Seat> seats = seatDAO.getByHallId(hall.getId());
        assertFalse(seats.isEmpty());
        assertTrue(seats.stream().allMatch(s -> s.getHall().getId().equals(hall.getId())));
    }

    @Test
    @Transactional
    void testGetBySeatTypeId() {
        List<Seat> seats = seatDAO.getBySeatTypeId(seatTypes.getId());
        assertFalse(seats.isEmpty());
        assertTrue(seats.stream().allMatch(s -> s.getSeatType().getId().equals(seatTypes.getId())));
    }

    @Test
    @Transactional
    void testGetByRowNumber() {
        List<Seat> seats = seatDAO.getByRowNumber(1);
        assertEquals(2, seats.size());
        assertTrue(seats.stream().allMatch(s -> s.getRowNumber() == 1));
    }

    @Test
    @Transactional
    void testGetBySeatNumber() {
        List<Seat> seats = seatDAO.getBySeatNumber(1);
        assertTrue(seats.size() >= 2); // seat1 and seat3
        assertTrue(seats.stream().allMatch(s -> s.getSeatNumber() == 1));
    }

    @Test
    @Transactional
    void testGetByHallAndRowAndSeat() {
        Seat found = seatDAO.getByHallAndRowAndSeat(hall.getId(), 1, 1);
        assertNotNull(found);
        assertEquals(1, found.getRowNumber());
        assertEquals(1, found.getSeatNumber());
        assertEquals(hall.getId(), found.getHall().getId());
    }

    @Test
    @Transactional
    void testGetByHallAndRowAndSeat_NotFound() {
        Seat found = seatDAO.getByHallAndRowAndSeat(hall.getId(), 99, 99);
        assertNull(found);
    }
}