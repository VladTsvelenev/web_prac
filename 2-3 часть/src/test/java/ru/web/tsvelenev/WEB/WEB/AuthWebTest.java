package ru.web.tsvelenev.WEB.WEB;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class AuthWebTest {

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
    void testLoginPageElements() {
        driver.get(BASE_URL + "/login");

        // Проверка заголовка
        assertEquals("Вход в систему", driver.getTitle());

        // Проверка логотипа
        WebElement logo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("auth-logo"))
        );
        assertTrue(logo.isDisplayed());

        // Проверка заголовка формы
        WebElement title = driver.findElement(By.className("auth-title"));
        assertEquals("Вход в систему", title.getText());

        // Проверка наличия полей формы
        assertTrue(driver.findElement(By.id("username")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());

        // Проверка ссылки на регистрацию
        WebElement registerLink = driver.findElement(By.xpath("//a[contains(text(), 'Создать аккаунт')]"));
        assertEquals("http://localhost:8082/register", registerLink.getAttribute("href"));
    }

    @Test
    void testRegisterPageElements() {
        driver.get(BASE_URL + "/register");

        // Проверка заголовка
        assertEquals("Регистрация", driver.getTitle());

        // Проверка логотипа
        WebElement logo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("auth-logo"))
        );
        assertTrue(logo.isDisplayed());

        // Проверка заголовка формы
        WebElement title = driver.findElement(By.className("auth-title"));
        assertEquals("Регистрация", title.getText());

        // Проверка наличия полей формы
        assertTrue(driver.findElement(By.id("name")).isDisplayed());
        assertTrue(driver.findElement(By.id("email")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.id("confirmPassword")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());

        // Проверка ссылки на вход
        WebElement loginLink = driver.findElement(By.xpath("//a[contains(text(), 'Войти в аккаунт')]"));
        assertEquals("http://localhost:8082/login", loginLink.getAttribute("href"));
    }

    @Test
    void testSuccessfulRegistration() {
        driver.get(BASE_URL + "/register");

        // Заполнение формы
        driver.findElement(By.id("name")).sendKeys("Тестовый Пользователь");
        driver.findElement(By.id("email")).sendKeys("testuser_" + System.currentTimeMillis() + "@example.com");
        driver.findElement(By.id("password")).sendKeys("Test1234!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Test1234!");

        // Отправка формы
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка редиректа или сообщения об успехе
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/login"),
                ExpectedConditions.visibilityOfElementLocated(By.className("alert-success"))
        ));
    }

    @Test
    void testLoginLogoutFlow() {
        // Регистрация тестового пользователя (можно вынести в отдельный метод)
        String email = "testuser_" + System.currentTimeMillis() + "@example.com";
        registerTestUser(email, "Test1234!");

        // Вход
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys("Test1234!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка успешного входа
        wait.until(ExpectedConditions.urlContains("/"));
        WebElement greeting = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("user-greeting"))
        );
        assertTrue(greeting.getText().contains(email));

        // Выход
        driver.findElement(By.xpath("//button[contains(text(), 'Выход')]")).click();

        // Проверка успешного выхода
        wait.until(ExpectedConditions.urlContains("/"));
        WebElement loginButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(text(), 'Вход')]"))
        );
        assertTrue(loginButton.isDisplayed());
    }

    private void registerTestUser(String email, String password) {
        driver.get(BASE_URL + "/register");
        driver.findElement(By.id("name")).sendKeys("Тестовый Пользователь");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("confirmPassword")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/login"));
    }
}