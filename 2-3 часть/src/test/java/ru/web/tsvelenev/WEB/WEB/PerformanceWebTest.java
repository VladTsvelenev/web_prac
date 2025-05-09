package ru.web.tsvelenev.WEB.WEB;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PerformanceWebTest {

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
    void testPerformancesListPage() {
        driver.get(BASE_URL + "/performances");

        // Проверка заголовка
        assertEquals("Список спектаклей - Театральная система", driver.getTitle());

        // Проверка наличия кнопки "Добавить спектакль"
        WebElement addButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(text(), 'Добавить спектакль')]")
                )
        );
        assertTrue(addButton.isDisplayed());

        // Проверка наличия карточек спектаклей
        List<WebElement> performanceCards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className("performance-card")
                )
        );
        assertFalse(performanceCards.isEmpty(), "Должна быть хотя бы одна карточка спектакля");

        // Проверка структуры карточки спектакля
        WebElement firstCard = performanceCards.get(0);
        assertTrue(firstCard.findElement(By.className("performance-title")).isDisplayed());
        assertTrue(firstCard.findElement(By.className("performance-info")).isDisplayed());
        assertTrue(firstCard.findElement(By.xpath(".//a[contains(text(), 'Подробнее')]")).isDisplayed());
    }

    @Test
    void testAddPerformancePage() {
        driver.get(BASE_URL + "/performances/add");

        // Проверка заголовка
        assertEquals("Добавить спектакль - Театральная система", driver.getTitle());

        // Проверка наличия формы
        WebElement form = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("form")
                )
        );
        assertTrue(form.isDisplayed());

        // Проверка основных полей формы
        assertTrue(driver.findElement(By.id("title")).isDisplayed());
        assertTrue(driver.findElement(By.id("duration")).isDisplayed());
        assertTrue(driver.findElement(By.id("theater")).isDisplayed());
        assertTrue(driver.findElement(By.id("hall")).isDisplayed());
        assertTrue(driver.findElement(By.id("director")).isDisplayed());
        assertTrue(driver.findElement(By.id("actors")).isDisplayed());

        // Проверка блока дат показов
        WebElement showTimesContainer = driver.findElement(By.id("showTimesContainer"));
        assertTrue(showTimesContainer.isDisplayed());
        assertTrue(showTimesContainer.findElement(By.xpath(".//input[@type='datetime-local']")).isDisplayed());
        assertTrue(showTimesContainer.findElement(By.xpath(".//button[contains(text(), '+')]")).isDisplayed());

        // Проверка блока цен на билеты
        WebElement seatPricesContainer = driver.findElement(By.id("seatPricesContainer"));
        assertTrue(seatPricesContainer.isDisplayed());
        WebElement noSeatTypesMessage = seatPricesContainer.findElement(By.id("noSeatTypesMessage"));
        assertTrue(noSeatTypesMessage.isDisplayed());
    }

    @Test
    void testAddPerformanceFormValidation() {
        driver.get(BASE_URL + "/performances/add");

        // Попытка отправить пустую форму
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка сообщений об ошибках
        try {
            WebElement errorElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//input[@id='title'][@required]")
                    )
            );
            assertNotNull(errorElement);
        } catch (TimeoutException e) {
            fail("Не сработала валидация обязательных полей");
        }
    }

    @Test
    void testNavigationBetweenPerformancePages() {
        driver.get(BASE_URL + "/performances");

        // Клик по кнопке "Добавить спектакль"
        driver.findElement(By.xpath("//a[contains(text(), 'Добавить спектакль')]")).click();
        wait.until(ExpectedConditions.urlContains("/performances/add"));
    }

    @Test
    void testAddShowTimeInForm() {
        driver.get(BASE_URL + "/performances/add");

        // Проверка начального количества полей для дат показов
        List<WebElement> initialShowTimeInputs = driver.findElements(By.name("showTimes"));
        assertEquals(1, initialShowTimeInputs.size());

        // Добавление нового поля для даты показа
        driver.findElement(By.xpath("//button[contains(text(), '+')]")).click();

        // Проверка, что поле добавилось
        List<WebElement> showTimeInputsAfterAdd = wait.until(
                ExpectedConditions.numberOfElementsToBe(
                        By.name("showTimes"), 2
                )
        );
        assertEquals(2, showTimeInputsAfterAdd.size());

        // Удаление поля для даты показа
        driver.findElement(By.xpath("//button[contains(text(), '-')]")).click();

        // Проверка, что поле удалилось
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.name("showTimes"), 1
        ));
    }

    @Test
    void testHallSelectionLogic() {
        driver.get(BASE_URL + "/performances/add");

        // Выбор театра
        Select theaterSelect = new Select(driver.findElement(By.id("theater")));
        theaterSelect.selectByIndex(1); // Выбираем первый непустой вариант

        // Проверка, что список залов обновился
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//select[@id='hall']/option[not(@value='')]"), 0
        ));

        // Выбор зала
        Select hallSelect = new Select(driver.findElement(By.id("hall")));
        hallSelect.selectByIndex(1); // Выбираем первый непустой вариант

        // Проверка, что появились поля для указания цен
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("noSeatTypesMessage")
        ));

        List<WebElement> seatPriceInputs = driver.findElements(By.className("seat-price-input"));
        assertTrue(seatPriceInputs.isEmpty(), "Должны появиться поля для указания цен на билеты");
    }
}