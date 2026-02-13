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
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-setuid-sandbox");
        }
    }

    @Test
    void testCreateProductIsSuccessful(ChromeDriver driver) throws InterruptedException {
        // Increase wait time significantly for CI
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        // Give the application extra time to stabilize in CI
        if (System.getenv("GITHUB_ACTIONS") != null) {
            Thread.sleep(5000);
        }

        try {
            // Navigate to create product page
            driver.get(baseUrl + "/product/create");
            System.out.println("Navigated to: " + driver.getCurrentUrl());

            // Wait for the page to be fully loaded
            wait.until(driver1 ->
                    ((org.openqa.selenium.JavascriptExecutor) driver1)
                            .executeScript("return document.readyState").equals("complete"));

            // Wait for name input to be visible and interactable
            WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameInput")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nameInput")));
            nameInput.clear();
            nameInput.sendKeys("Sampo Cap Bambang");
            System.out.println("Entered product name");

            // Wait for quantity input
            WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quantityInput")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("quantityInput")));
            quantityInput.clear();
            quantityInput.sendKeys("100");
            System.out.println("Entered quantity");

            // Wait for submit button and click
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            submitButton.click();
            System.out.println("Clicked submit button");

            // Wait for redirect with longer timeout
            wait.until(ExpectedConditions.urlContains("/product/list"));
            System.out.println("Redirected to: " + driver.getCurrentUrl());

            // Wait for page to be fully loaded again
            wait.until(driver1 ->
                    ((org.openqa.selenium.JavascriptExecutor) driver1)
                            .executeScript("return document.readyState").equals("complete"));

            // Extra wait for table to render in CI
            if (System.getenv("GITHUB_ACTIONS") != null) {
                Thread.sleep(2000);
            }

            // Wait for the table cell with retry
            WebElement tableCell = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[contains(text(), 'Sampo Cap Bambang')]")));

            // Scroll to element to ensure visibility
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tableCell);

            // Wait for it to be visible
            wait.until(ExpectedConditions.visibilityOf(tableCell));
            System.out.println("Found product in table");

            assertTrue(tableCell.isDisplayed(), "Product 'Sampo Cap Bambang' should be displayed in the list");

        } catch (Exception e) {
            System.err.println("=== TEST FAILED ===");
            System.err.println("Current URL: " + driver.getCurrentUrl());
            System.err.println("Page Title: " + driver.getTitle());
            System.err.println("Page Source:");
            System.err.println(driver.getPageSource());
            throw e;
        }
    }
}