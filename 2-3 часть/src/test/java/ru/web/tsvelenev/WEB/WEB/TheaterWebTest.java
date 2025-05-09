package ru.web.tsvelenev.WEB.WEB;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TheaterWebTest {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8082";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\vlad_c\\Desktop\\web_t\\drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testTheatersListPage() {
        driver.get(BASE_URL + "/theaters");

        // Проверка заголовка
        assertEquals("Список театров - Театральная система", driver.getTitle());

        // Проверка наличия кнопки "Добавить театр"
        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(text(), 'Добавить театр')]")
                )
        );
        assertTrue(addButton.isDisplayed());

        // Проверка наличия карточек театров
        List<WebElement> theaterCards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className("theater-card")
                )
        );
        assertFalse(theaterCards.isEmpty(), "Должна быть хотя бы одна карточка театра");

        // Проверка структуры карточки театра
        WebElement firstCard = theaterCards.get(0);
        assertTrue(firstCard.findElement(By.className("theater-name")).isDisplayed());
        assertTrue(firstCard.findElement(By.className("theater-address")).isDisplayed());
        assertTrue(firstCard.findElement(By.xpath(".//a[contains(text(), 'Подробнее')]")).isDisplayed());
    }

    @Test
    void testAddTheaterPage() {
        driver.get(BASE_URL + "/theaters/add");

        // Проверка заголовка
        assertEquals("Добавить Театр - Театральная система", driver.getTitle());

        // Проверка наличия формы
        WebElement form = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("form")
                )
        );
        assertTrue(form.isDisplayed());

        // Проверка полей формы
        assertTrue(driver.findElement(By.id("name")).isDisplayed());
        assertTrue(driver.findElement(By.id("address")).isDisplayed());
        assertTrue(driver.findElement(By.id("info")).isDisplayed());

        // Проверка блока залов
        WebElement hallCard = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("hall-card")
                )
        );
        assertTrue(hallCard.isDisplayed());

        // Проверка кнопок управления
        assertTrue(driver.findElement(By.xpath("//button[contains(text(), 'Добавить ещё зал')]")).isDisplayed());
        assertTrue(driver.findElement(By.xpath("//button[contains(text(), 'Добавить тип мест')]")).isDisplayed());
    }


    @Test
    void testAddTheaterFormValidation() {
        driver.get(BASE_URL + "/theaters/add");

        // Попытка отправить пустую форму
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка сообщений об ошибках
        try {
            WebElement errorElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//input[@id='name'][@required]")
                    )
            );
            // Проверяем, что поле подсвечено как невалидное
            assertNotNull(errorElement);
        } catch (TimeoutException e) {
            fail("Не сработала валидация обязательных полей");
        }
    }
}