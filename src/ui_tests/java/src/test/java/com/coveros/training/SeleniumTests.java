package com.coveros.training;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeleniumTests {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        // Force WebDriverManager to download the driver matching Chrome version 134.
        WebDriverManager.chromedriver()
                .browserVersion("134")
                .forceDownload()
                .setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        // This option is deprecated; remove it if not needed.
        options.setExperimentalOption("useAutomationExtension", false);
        
        driver = new ChromeDriver(options);
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Tests the entire process of lending - registers a book, a borrower, and lends it.
     */
    @Test
    public void test_shouldLendBook() {
        driver.get("http://demo-app:8080/demo/flyway");
        driver.get("http://demo-app:8080/demo/library.html");
        
        // Register a book.
        driver.findElement(By.id("register_book")).sendKeys("some book");
        driver.findElement(By.id("register_book_submit")).click();
        // Wait for and click the "Return" link.
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Return"))).click();
        
        // Register a borrower.
        driver.findElement(By.id("register_borrower")).sendKeys("some borrower");
        driver.findElement(By.id("register_borrower_submit")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Return"))).click();
        
        // Lend the book.
        driver.findElement(By.id("lend_book")).sendKeys("some book");
        driver.findElement(By.id("lend_borrower")).sendKeys("some borrower");
        driver.findElement(By.id("lend_book_submit")).click();

        // Wait for the result element to appear and verify its text.
        WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String result = resultElement.getText();
        assertEquals("SUCCESS", result);
    }

    /**
     * Expects an exception when interacting with a locked input.
     */
    @Test(expected = org.openqa.selenium.ElementNotInteractableException.class)
    public void test_shouldShowLockedInput() {
        driver.get("http://demo-app:8080/demo/flyway");
        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("lend_book")).sendKeys("some book");
    }

    /**
     * With one book and one borrower, a dropdown should appear.
     */
    @Test
    public void test_shouldShowDropdowns() {
        driver.get("http://demo-app:8080/demo/flyway");
        ApiCalls.registerBook("some book");
        ApiCalls.registerBorrowers("some borrower");

        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("lend_book"))
              .findElement(By.xpath("//option[contains(.,'some book')]"))
              .click();
        driver.findElement(By.id("lend_borrower"))
              .findElement(By.xpath("//option[contains(.,'some borrower')]"))
              .click();
        driver.findElement(By.id("lend_book_submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String result = resultElement.getText();
        assertEquals("SUCCESS", result);
    }

    /**
     * With 10 books and one borrower, an autocomplete should appear.
     */
    @Test
    public void test_shouldShowAutocomplete() {
        driver.get("http://demo-app:8080/demo/flyway");
        ApiCalls.registerBook("a");
        ApiCalls.registerBook("b");
        ApiCalls.registerBook("c");
        ApiCalls.registerBook("d");
        ApiCalls.registerBook("e");
        ApiCalls.registerBook("f");
        ApiCalls.registerBook("g");
        ApiCalls.registerBook("h");
        ApiCalls.registerBook("i");
        ApiCalls.registerBook("j");
        ApiCalls.registerBorrowers("some borrower");

        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("lend_book")).sendKeys("f");
        driver.findElement(By.xpath("//li[contains(.,'f')]")).click();
        driver.findElement(By.id("lend_borrower"))
              .findElement(By.xpath("//option[contains(.,'some borrower')]"))
              .click();
        driver.findElement(By.id("lend_book_submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String result = resultElement.getText();
        assertEquals("SUCCESS", result);
    }

    /**
     * Tests that quotes in a book or borrower name are handled correctly.
     */
    @Test
    public void test_ShouldHandleQuotesInBookOrBorrowerValue() {
        driver.get("http://demo-app:8080/demo/flyway");
        ApiCalls.registerBook("some \"book");
        ApiCalls.registerBorrowers("some \"borrower");

        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("lend_book"))
              .findElement(By.xpath("//option[contains(.,'some \"book')]"))
              .click();
        driver.findElement(By.id("lend_borrower"))
              .findElement(By.xpath("//option[contains(.,'some \"borrower')]"))
              .click();
        driver.findElement(By.id("lend_book_submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String result = resultElement.getText();
        assertEquals("SUCCESS", result);
    }

    /**
     * Tests user registration and login.
     */
    @Test
    public void test_ShouldRegisterAndLoginUser() {
        driver.get("http://demo-app:8080/demo/flyway");
        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("register_username")).sendKeys("some user");
        driver.findElement(By.id("register_password")).sendKeys("lasdfj;alsdkfjasdf");
        driver.findElement(By.id("register_submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement registerResultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String registerResult = registerResultElement.getText();
        assertTrue("result was " + registerResult,
                   registerResult.contains("status: SUCCESSFULLY_REGISTERED"));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Return"))).click();
        driver.findElement(By.id("login_username")).sendKeys("some user");
        driver.findElement(By.id("login_password")).sendKeys("lasdfj;alsdkfjasdf");
        driver.findElement(By.id("login_submit")).click();

        WebElement loginResultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
        final String loginResult = loginResultElement.getText();
        assertTrue("result was " + loginResult,
                   loginResult.contains("access granted"));
    }
}
