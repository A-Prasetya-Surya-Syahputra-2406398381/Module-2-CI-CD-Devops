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
import java.net.HttpURLConnection;
import java.net.URL;

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
    void setupTest() throws Exception {
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
        }

        // Wait for application to be ready
        waitForApplicationToBeReady();
    }

    private void waitForApplicationToBeReady() throws Exception {
        int maxAttempts = 30;
        int attempt = 0;
        boolean isReady = false;

        while (attempt < maxAttempts && !isReady) {
            try {
                URL url = new URL(baseUrl + "/product/list");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    isReady = true;
                    System.out.println("Application is ready!");
                }
                connection.disconnect();
            } catch (Exception e) {
                attempt++;
                Thread.sleep(1000);
                System.out.println("Waiting for application... Attempt " + attempt);
            }
        }

        if (!isReady) {
            throw new RuntimeException("Application failed to start in time");
        }

        // Extra buffer for CI
        if (System.getenv("GITHUB_ACTIONS") != null) {
            Thread.sleep(3000);
        }
    }

    @Test
    void testCreateProductIsSuccessful(ChromeDriver driver) {
        // Set page load timeout
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

        try {
            System.out.println("Navigating to: " + baseUrl + "/product/create");
            driver.get(baseUrl + "/product/create");
            System.out.println("Current URL: " + driver.getCurrentUrl());

            // Wait for name input with multiple strategies
            WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nameInput")));
            System.out.println("Found nameInput element");

            // Ensure element is interactable
            wait.until(ExpectedConditions.elementToBeClickable(By.id("nameInput")));
            nameInput.clear();
            nameInput.sendKeys("Sampo Cap Bambang");
            System.out.println("Entered product name");

            WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("quantityInput")));
            wait.until(ExpectedConditions.elementToBeClickable(By.id("quantityInput")));
            quantityInput.clear();
            quantityInput.sendKeys("100");
            System.out.println("Entered quantity");

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            submitButton.click();
            System.out.println("Clicked submit");

            // Wait for URL change
            wait.until(ExpectedConditions.urlContains("/product/list"));
            System.out.println("Navigated to list page: " + driver.getCurrentUrl());

            // Wait for table to load
            WebElement tableCell = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[contains(text(), 'Sampo Cap Bambang')]")));
            System.out.println("Found product in table");

            assertTrue(tableCell.isDisplayed());
            System.out.println("Test passed!");

        } catch (Exception e) {
            System.err.println("=== TEST FAILED ===");
            System.err.println("Current URL: " + driver.getCurrentUrl());
            System.err.println("Page Title: " + driver.getTitle());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}