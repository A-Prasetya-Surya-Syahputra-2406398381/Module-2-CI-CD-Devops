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
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
        }
    }

    @Test
    void testCreateProductIsSuccessful(ChromeDriver driver) {
        // Remove implicit wait - use only explicit waits
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Navigate to create product page
        driver.get(baseUrl + "/product/create");

        // Wait for page to load by checking a unique element
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput")));
        nameInput.clear();
        nameInput.sendKeys("Sampo Cap Bambang");

        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quantityInput")));
        quantityInput.clear();
        quantityInput.sendKeys("100");

        // Wait for submit button to be clickable
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        submitButton.click();

        // Wait for redirect to list page
        wait.until(ExpectedConditions.urlContains("/product/list"));

        // Wait for the table to load and find the product
        WebElement tableCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'Sampo Cap Bambang')]")));

        assertTrue(tableCell.isDisplayed(), "Product 'Sampo Cap Bambang' should be visible in the product list");
    }
}   