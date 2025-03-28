package ru.web.tsvelenev.WEB.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.Hall;
import ru.web.tsvelenev.WEB.models.Seat;
import ru.web.tsvelenev.WEB.models.SeatType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations="classpath:application.properties")
public class SeatDAOTest {

    @Autowired
    private SeatDAO seatDAO;

    @Autowired
    private HallDAO hallDAO;

    @Autowired
    private SeatTypeDAO seatTypeDAO;

    @Autowired
    private SessionFactory sessionFactory;

    private SeatType standardType;
    private SeatType vipType;
    private Hall mainHall;
    private Hall vipHall;

    @Test
    void testGetByHallId() {
        List<Seat> seats = seatDAO.getByHallId(mainHall.getId());
        assertEquals(10, seats.size());

        for (Seat seat : seats) {
            assertEquals(mainHall.getId(), seat.getHall().getId());
        }
    }

    @Test
    void testGetBySeatTypeId() {
        List<Seat> seats = seatDAO.getBySeatTypeId(vipType.getId());
        assertEquals(5, seats.size());

        for (Seat seat : seats) {
            assertEquals(vipType.getId(), seat.getSeatType().getId());
        }
    }

    @Test
    void testGetByRowNumber() {
        List<Seat> seats = seatDAO.getByRowNumber(1);
        assertEquals(10, seats.size()); // 5 standard + 5 vip

        for (Seat seat : seats) {
            assertEquals(1, seat.getRowNumber());
        }
    }

    @Test
    void testGetByHallAndRow() {
        List<Seat> seats = seatDAO.getByHallAndRow(mainHall.getId(), 1);
        assertEquals(5, seats.size());

        for (Seat seat : seats) {
            assertEquals(mainHall.getId(), seat.getHall().getId());
            assertEquals(1, seat.getRowNumber());
        }
    }

    @Test
    void testGetByHallAndType() {
        List<Seat> seats = seatDAO.getByHallAndType(mainHall.getId(), standardType.getId());
        assertEquals(10, seats.size());

        for (Seat seat : seats) {
            assertEquals(mainHall.getId(), seat.getHall().getId());
            assertEquals(standardType.getId(), seat.getSeatType().getId());
        }
    }

    @BeforeEach
    void beforeEach() {
        cleanup();

        // Создаем типы мест (без цены)
        standardType = new SeatType();
        standardType.setName("Standard");
        seatTypeDAO.save(standardType);

        vipType = new SeatType();
        vipType.setName("VIP");
        seatTypeDAO.save(vipType);

        // Создаем залы
        mainHall = new Hall();
        mainHall.setName("Main Hall");
        hallDAO.save(mainHall);

        vipHall = new Hall();
        vipHall.setName("VIP Hall");
        hallDAO.save(vipHall);
        for (int row = 1; row <= 2; row++) {
            for (int num = 1; num <= 5; num++) {
                Seat seat = new Seat(mainHall, standardType, row, num);
                seatDAO.save(seat);
            }
        }

        for (int num = 1; num <= 5; num++) {
            Seat seat = new Seat(vipHall, vipType, 1, num);
            seatDAO.save(seat);
        }
    }

    @AfterEach
    void cleanup() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createNativeQuery("DELETE FROM seat").executeUpdate();
            session.createNativeQuery("DELETE FROM hall").executeUpdate();
            session.createNativeQuery("DELETE FROM seattype").executeUpdate();
            session.getTransaction().commit();
        }
    }
}