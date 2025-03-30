package ru.web.tsvelenev.WEB.DAO;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.web.tsvelenev.WEB.models.Actor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
class ActorDAOTest {

    @Autowired
    private ActorDAO actorDAO;

    private Actor actor1;
    private Actor actor2;
    private Actor actor3;

    @BeforeAll
    void setup() {
        actor1 = new Actor("Leonardo DiCaprio");
        actor2 = new Actor("Tom Hanks");
        actor3 = new Actor("Meryl Streep");

        actorDAO.save(actor1);
        actorDAO.save(actor2);
        actorDAO.save(actor3);
    }

    @AfterAll
    void tearDown() {
        actorDAO.delete(actor1);
        actorDAO.delete(actor2);
        actorDAO.delete(actor3);
    }

    @Test
    void testGetById() {
        Actor found = actorDAO.getById(actor1.getId());
        assertNotNull(found);
        assertEquals(actor1.getName(), found.getName());
    }

    @Test
    void testGetById_NotFound() {
        Actor found = actorDAO.getById(999L);
        assertNull(found);
    }

    @Test
    void testGetAll() {
        List<Actor> actors = (List<Actor>) actorDAO.getAll();
        assertTrue(actors.size() >= 3);
        assertTrue(actors.stream().anyMatch(a -> a.getName().equals("Leonardo DiCaprio")));
        assertTrue(actors.stream().anyMatch(a -> a.getName().equals("Tom Hanks")));
        assertTrue(actors.stream().anyMatch(a -> a.getName().equals("Meryl Streep")));
    }

    @Test
    void testSave() {
        Actor newActor = new Actor("Brad Pitt");
        actorDAO.save(newActor);

        Actor found = actorDAO.getById(newActor.getId());
        assertNotNull(found);
        assertEquals("Brad Pitt", found.getName());

        actorDAO.delete(newActor);
    }

    @Test
    void testSaveCollection() {
        Actor actor4 = new Actor("Johnny Depp");
        Actor actor5 = new Actor("Denzel Washington");
        List<Actor> actors = List.of(actor4, actor5);

        actorDAO.saveCollection(actors);

        List<Actor> allActors = (List<Actor>) actorDAO.getAll();
        assertTrue(allActors.containsAll(actors));

        actorDAO.delete(actor4);
        actorDAO.delete(actor5);
    }

    @Test
    void testUpdate() {
        actor1.setName("Leonardo DiCaprio Updated");
        actorDAO.update(actor1);

        Actor updated = actorDAO.getById(actor1.getId());
        assertEquals("Leonardo DiCaprio Updated", updated.getName());

        actor1.setName("Leonardo DiCaprio");
        actorDAO.update(actor1);
    }

    @Test
    void testDelete() {
        Actor tempActor = new Actor("Temporary Actor");
        actorDAO.save(tempActor);

        actorDAO.delete(tempActor);

        Actor found = actorDAO.getById(tempActor.getId());
        assertNull(found);
    }

    @Test
    void testDeleteById() {
        Actor tempActor = new Actor("Temporary Actor 2");
        actorDAO.save(tempActor);

        actorDAO.deleteById(tempActor.getId());

        Actor found = actorDAO.getById(tempActor.getId());
        assertNull(found);
    }

    @Test
    void testFindByCriteria() {
        // Тест точного совпадения имени
        List<Actor> foundByName = actorDAO.findByCriteria("Tom Hanks", null);
        assertEquals(1, foundByName.size());
        assertEquals("Tom Hanks", foundByName.get(0).getName());

        // Тест поиска по части имени
        List<Actor> foundByNameContains = actorDAO.findByCriteria(null, "Leo");
        assertTrue(foundByNameContains.size() >= 1);
        assertTrue(foundByNameContains.get(0).getName().contains("Leo"));

        // Тест с обоими параметрами (должен учитывать только точное совпадение)
        List<Actor> foundByBoth = actorDAO.findByCriteria("Meryl Streep", "Meryl");
        assertEquals(1, foundByBoth.size());
        assertEquals("Meryl Streep", foundByBoth.get(0).getName());

        // Тест без параметров (должен вернуть всех актеров)
        List<Actor> foundAll = actorDAO.findByCriteria(null, null);
        assertTrue(foundAll.size() >= 3);

        // Тест с несуществующим именем
        List<Actor> foundNonExisting = actorDAO.findByCriteria("Non Existing", null);
        assertTrue(foundNonExisting.isEmpty());

        // Тест с несуществующей частью имени
        List<Actor> foundNonExistingPart = actorDAO.findByCriteria(null, "xyz");
        assertTrue(foundNonExistingPart.isEmpty());
    }

    @Test
    void testGetSingleActorByName() {
        Actor found = actorDAO.getSingleActorByName("Tom Hanks");
        assertNotNull(found);
        assertEquals("Tom Hanks", found.getName());
    }

    @Test
    void testGetSingleActorByName_NotFound() {
        Actor found = actorDAO.getSingleActorByName("Unknown Actor");
        assertNull(found);
    }

    @Test
    void testFindByCriteria_EmptyName() {
        List<Actor> found = actorDAO.findByCriteria("", null);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByCriteria_EmptyNameContains() {
        List<Actor> found = actorDAO.findByCriteria(null, "");
        assertTrue(found.size() >= 3); // Должен вернуть всех, так как "" соответствует любому имени
    }

    @Test
    void testFindByCriteria_CaseSensitivity() {
        // Проверка чувствительности к регистру
        List<Actor> foundLowercase = actorDAO.findByCriteria("tom hanks", null);
        assertTrue(foundLowercase.isEmpty());

        List<Actor> foundUppercase = actorDAO.findByCriteria("TOM HANKS", null);
        assertTrue(foundUppercase.isEmpty());

        List<Actor> foundMixedCase = actorDAO.findByCriteria(null, "leo");
        assertTrue(foundMixedCase.size() >= 1);
    }
}