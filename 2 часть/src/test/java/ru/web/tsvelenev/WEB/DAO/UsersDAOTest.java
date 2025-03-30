package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.Users;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class UsersDAOTest {

    @Autowired
    private UsersDAO usersDAO;

    private Users user1;
    private Users user2;
    private Users user3;

    @BeforeAll
    void setup() {
        // Инициализация тестовых данных
        user1 = new Users("{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30}");
        user2 = new Users("{\"name\":\"Jane Smith\",\"email\":\"jane@example.com\",\"age\":25}");
        user3 = new Users("{\"name\":\"Bob Johnson\",\"email\":\"bob@example.com\",\"age\":40}");

        usersDAO.save(user1);
        usersDAO.save(user2);
        usersDAO.save(user3);
    }

    @AfterAll
    void tearDown() {
        usersDAO.delete(user1);
        usersDAO.delete(user2);
        usersDAO.delete(user3);
    }

    @Test
    void testGetById() {
        Users found = usersDAO.getById(user1.getId());
        assertNotNull(found);
        assertTrue(found.getUserInfo().contains("John Doe"));
    }

    @Test
    void testGetById_NotFound() {
        Users found = usersDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Users> users = (List<Users>) usersDAO.getAll();
        assertTrue(users.size() >= 3);
        assertTrue(users.stream().anyMatch(u -> u.getUserInfo().contains("John Doe")));
        assertTrue(users.stream().anyMatch(u -> u.getUserInfo().contains("Jane Smith")));
        assertTrue(users.stream().anyMatch(u -> u.getUserInfo().contains("Bob Johnson")));
    }

    @Test
    void testSave() {
        Users newUser = new Users("{\"name\":\"Alice Brown\",\"email\":\"alice@example.com\"}");
        usersDAO.save(newUser);

        Users found = usersDAO.getById(newUser.getId());
        assertNotNull(found);
        assertTrue(found.getUserInfo().contains("Alice Brown"));
        usersDAO.delete(newUser);
    }

    @Test
    void testSaveCollection() {
        Users user4 = new Users("{\"name\":\"Mike Davis\",\"email\":\"mike@example.com\"}");
        Users user5 = new Users("{\"name\":\"Sarah Wilson\",\"email\":\"sarah@example.com\"}");
        List<Users> users = List.of(user4, user5);

        usersDAO.saveCollection(users);

        List<Users> allUsers = (List<Users>) usersDAO.getAll();
        assertTrue(allUsers.containsAll(users));

        // Cleanup
        usersDAO.delete(user4);
        usersDAO.delete(user5);
    }

    @Test
    void testUpdate() {
        String updatedInfo = "{\"name\":\"John Updated\",\"email\":\"john@example.com\",\"age\":31}";
        user1.setUserInfo(updatedInfo);
        usersDAO.update(user1);

        Users updated = usersDAO.getById(user1.getId());
        assertEquals(updatedInfo, updated.getUserInfo());

        // Restore original info
        user1.setUserInfo("{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"age\":30}");
        usersDAO.update(user1);
    }

    @Test
    void testDelete() {
        Users tempUser = new Users("{\"name\":\"Temp User\",\"email\":\"temp@example.com\"}");
        usersDAO.save(tempUser);

        usersDAO.delete(tempUser);

        Users found = usersDAO.getById(tempUser.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Users tempUser = new Users("{\"name\":\"Temp User 2\",\"email\":\"temp2@example.com\"}");
        usersDAO.save(tempUser);

        usersDAO.deleteById(tempUser.getId());

        Users found = usersDAO.getById(tempUser.getId());
        assertNull(found);
    }

    @Test
    void testGetByJsonField() {
        List<Users> found = usersDAO.getByJsonField("email", "john@example.com");
        assertFalse(found.isEmpty());
        assertTrue(found.get(0).getUserInfo().contains("John Doe"));
    }

    @Test
    void testGetByJsonField_NotFound() {
        List<Users> found = usersDAO.getByJsonField("email", "nonexistent@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByNameContaining() {
        List<Users> found = usersDAO.getByNameContaining("John");
        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(u -> u.getUserInfo().contains("John Doe")));
        assertTrue(found.stream().anyMatch(u -> u.getUserInfo().contains("Bob Johnson")));
    }

    @Test
    void testGetByNameContaining_NotFound() {
        List<Users> found = usersDAO.getByNameContaining("Nonexistent");
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetByJsonFieldContaining() {
        List<Users> found = usersDAO.getByJsonFieldContaining("email", "example");
        assertFalse(found.isEmpty());
        assertTrue(found.size() >= 3);
        assertTrue(found.stream().anyMatch(u -> u.getUserInfo().contains("john@example.com")));
        assertTrue(found.stream().anyMatch(u -> u.getUserInfo().contains("jane@example.com")));
        assertTrue(found.stream().anyMatch(u -> u.getUserInfo().contains("bob@example.com")));
    }

    @Test
    void testGetByJsonFieldContaining_NotFound() {
        List<Users> found = usersDAO.getByJsonFieldContaining("email", "nonexistent");
        assertTrue(found.isEmpty());
    }
}