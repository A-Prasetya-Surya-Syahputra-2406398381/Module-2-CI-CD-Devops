package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.bonigarcia.seljup.Options;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.time.Duration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
public class CreateProductFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @Options
    ChromeOptions options = new ChromeOptions();

    @BeforeEach
    void setupTest() {
        String host = testBaseUrl.contains("localhost") ? "http://127.0.0.1" : testBaseUrl;
        baseUrl = String.format("%s:%d", host, serverPort);

        // Check if running in a CI environment (like GitHub Actions)
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
    }

    @Test
    void testCreateProductIsSuccessful(ChromeDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(baseUrl + "/product/create");

        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameInput")));
        nameInput.sendKeys("Sampo Cap Bambang");

        driver.findElement(By.id("quantityInput")).sendKeys("100");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("td")));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/product/list"));

        List<WebElement> productCells = driver.findElements(By.tagName("td"));
        boolean isProductFound = productCells.stream()
                .anyMatch(cell -> cell.getText().equals("Sampo Cap Bambang"));

        assertTrue(isProductFound, "Product name should be visible in the list after creation");
    }
}