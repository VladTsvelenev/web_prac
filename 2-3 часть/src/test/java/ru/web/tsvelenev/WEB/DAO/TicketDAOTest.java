package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class TicketDAOTest {

    @Autowired
    private TicketDAO ticketDAO;

    @Autowired
    private ShowTimeDAO showTimeDAO;

    @Autowired
    private SeatDAO seatDAO;

    @Autowired
    private UsersDAO usersDAO;

    private ShowTime testShowTime;
    private Seat testSeat;
    private Users testUser;
    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;

    @BeforeAll
    void setup() {
        // Создаем тестовые данные
        testShowTime = new ShowTime(/* параметры для показа */);
        testSeat = new Seat(/* параметры для места */);
        testUser = new Users(/* параметры пользователя */);

        showTimeDAO.save(testShowTime);
        seatDAO.save(testSeat);
        usersDAO.save(testUser);

        ticket1 = new Ticket(testShowTime, testSeat, testUser, 500, true);
        ticket2 = new Ticket(testShowTime, testSeat, testUser, 300, false);
        ticket3 = new Ticket(testShowTime, testSeat, testUser, 400, true);

        ticketDAO.save(ticket1);
        ticketDAO.save(ticket2);
        ticketDAO.save(ticket3);
    }

    @AfterAll
    void tearDown() {
        ticketDAO.delete(ticket1);
        ticketDAO.delete(ticket2);
        ticketDAO.delete(ticket3);
        showTimeDAO.delete(testShowTime);
        seatDAO.delete(testSeat);
        usersDAO.delete(testUser);
    }

    @Test
    void testGetById() {
        Ticket found = ticketDAO.getById(ticket1.getId());
        assertNotNull(found);
        assertEquals(ticket1.getPrice(), found.getPrice());
    }

    @Test
    void testGetById_NotFound() {
        Ticket found = ticketDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Ticket> tickets = (List<Ticket>) ticketDAO.getAll();
        assertTrue(tickets.size() >= 3);
        assertTrue(tickets.stream().anyMatch(t -> t.getId().equals(ticket1.getId())));
        assertTrue(tickets.stream().anyMatch(t -> t.getId().equals(ticket2.getId())));
        assertTrue(tickets.stream().anyMatch(t -> t.getId().equals(ticket3.getId())));
    }

    @Test
    void testSave() {
        Ticket newTicket = new Ticket(testShowTime, testSeat, testUser, 450, false);
        ticketDAO.save(newTicket);

        Ticket found = ticketDAO.getById(newTicket.getId());
        assertNotNull(found);
        assertEquals(450, found.getPrice());

        ticketDAO.delete(newTicket);
    }

    @Test
    void testSaveCollection() {
        Ticket ticket4 = new Ticket(testShowTime, testSeat, testUser, 350, false);
        Ticket ticket5 = new Ticket(testShowTime, testSeat, testUser, 550, true);
        List<Ticket> tickets = List.of(ticket4, ticket5);

        ticketDAO.saveCollection(tickets);

        List<Ticket> allTickets = (List<Ticket>) ticketDAO.getAll();
        assertTrue(allTickets.containsAll(tickets));

        // Cleanup
        ticketDAO.delete(ticket4);
        ticketDAO.delete(ticket5);
    }

    @Test
    void testUpdate() {
        ticket1.setPrice(600);
        ticketDAO.update(ticket1);

        Ticket updated = ticketDAO.getById(ticket1.getId());
        assertEquals(600, updated.getPrice());
        ticket1.setPrice(500);
        ticketDAO.update(ticket1);
    }

    @Test
    void testDelete() {
        Ticket tempTicket = new Ticket(testShowTime, testSeat, null, 200, false);
        ticketDAO.save(tempTicket);

        ticketDAO.delete(tempTicket);

        Ticket found = ticketDAO.getById(tempTicket.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Ticket tempTicket = new Ticket(testShowTime, testSeat, testUser, 250, true);
        ticketDAO.save(tempTicket);

        ticketDAO.deleteById(tempTicket.getId());

        Ticket found = ticketDAO.getById(tempTicket.getId());
        assertNull(found);
    }

    @Test
    void testGetByShowTimeId() {
        List<Ticket> found = ticketDAO.getByShowTimeId(testShowTime.getId());
        assertFalse(found.isEmpty());
        assertTrue(found.size() >= 3);
    }

    @Test
    void testGetByUserId() {
        List<Ticket> found = ticketDAO.getByUserId(testUser.getId());
        assertFalse(found.isEmpty());
        assertTrue(found.stream().allMatch(t -> t.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void testGetAvailableByShowTime() {
        List<Ticket> available = ticketDAO.getAvailableByShowTime(testShowTime.getId());
        assertFalse(available.isEmpty());
        assertTrue(available.stream().noneMatch(Ticket::getIsSold));
    }

    @Test
    void testGetSoldByShowTime() {
        List<Ticket> sold = ticketDAO.getSoldByShowTime(testShowTime.getId());
        assertFalse(sold.isEmpty());
        assertTrue(sold.stream().allMatch(Ticket::getIsSold));
    }

    @Test
    void testGetBySeatId() {
        List<Ticket> found = ticketDAO.getBySeatId(testSeat.getId());
        assertFalse(found.isEmpty());
        assertTrue(found.stream().allMatch(t -> t.getSeat().getId().equals(testSeat.getId())));
    }

    @Test
    void testGetByPriceRange() {
        List<Ticket> found = ticketDAO.getByPriceRange(300, 500);
        assertFalse(found.isEmpty());
        assertTrue(found.stream().allMatch(t -> t.getPrice() >= 300 && t.getPrice() <= 500));
    }

    @Test
    void testGetByShowTimeAndStatus() {
        List<Ticket> soldTickets = ticketDAO.getByShowTimeAndStatus(testShowTime.getId(), true);
        assertFalse(soldTickets.isEmpty());
        assertTrue(soldTickets.stream().allMatch(Ticket::getIsSold));

        List<Ticket> availableTickets = ticketDAO.getByShowTimeAndStatus(testShowTime.getId(), false);
        assertFalse(availableTickets.isEmpty());
        assertTrue(availableTickets.stream().noneMatch(Ticket::getIsSold));
    }
}