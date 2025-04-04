package com.coveros.training;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;  // For @BeforeClass

public class SeleniumTests {
    private static WebDriver driver;

   @BeforeClass
    public static void setUp() {
        // Let WebDriverManager automatically detect the installed Chrome version
        // and download the appropriate ChromeDriver.
        // forceDownload() can still be useful to ensure no stale cache is used.
       WebDriverManager.chromedriver()
            .browserVersion("134")    // force the major version to 134
            .forceDownload()          // ensure a fresh download is performed
            .setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        // This option is often unnecessary now and can cause warnings. Remove if safe.
        // options.setExperimentalOption("useAutomationExtension", false);

        // Add logging to see which chromedriver executable is being used
        String driverPath = System.getProperty("webdriver.chrome.driver");
        if (driverPath != null) {
            System.out.println("Attempting to use ChromeDriver at: " + driverPath);
        } else {
            System.out.println("webdriver.chrome.driver system property not set. Relying on PATH or Selenium default.");
        }

        try {
            driver = new ChromeDriver(options);
            System.out.println("ChromeDriver started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start ChromeDriver: " + e.getMessage());
            // You might want to print the stack trace for more detail during debugging
            e.printStackTrace();
            // Re-throw or handle appropriately so the test setup fails clearly
            throw e;
        }
    }


    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ... rest of your test methods remain the same ...
    @Test
    public void test_shouldLendBook() {
        // Add a null check in case setup failed
        Assume.assumeNotNull("Driver could not be initialized", driver);
        driver.get("http://demo-app:8080/demo/flyway");
        driver.get("http://demo-app:8080/demo/library.html");
        // ... rest of test ...
        driver.findElement(By.id("register_book")).sendKeys("some book");
        driver.findElement(By.id("register_book_submit")).click();
        driver.findElement(By.linkText("Return")).click();
        driver.findElement(By.id("register_borrower")).sendKeys("some borrower");
        driver.findElement(By.id("register_borrower_submit")).click();
        driver.findElement(By.linkText("Return")).click();
        driver.findElement(By.id("lend_book")).sendKeys("some book");
        driver.findElement(By.id("lend_borrower")).sendKeys("some borrower");
        driver.findElement(By.id("lend_book_submit")).click();
        final String result = driver.findElement(By.id("result")).getText();
        assertEquals("SUCCESS", result);
    }

    @Test(expected = org.openqa.selenium.ElementNotInteractableException.class)
    public void test_shouldShowLockedInput() {
        Assume.assumeNotNull("Driver could not be initialized", driver);
        // clear the database...
        driver.get("http://demo-app:8080/demo/flyway");

        driver.get("http://demo-app:8080/demo/library.html");
        driver.findElement(By.id("lend_book")).sendKeys("some book");
    }

    @Test
    public void test_shouldShowDropdowns() {
        Assume.assumeNotNull("Driver could not be initialized", driver);
        // clear the database...
        driver.get("http://demo-app:8080/demo/flyway");
        // Assume ApiCalls works independently or is set up correctly
        ApiCalls.registerBook("some book");
        ApiCalls.registerBorrowers("some borrower");

        driver.get("http://demo-app:8080/demo/library.html");

        driver.findElement(By.id("lend_book")).findElement(By.xpath("//option[contains(.,'some book')]")).click();
        driver.findElement(By.id("lend_borrower")).findElement(By.xpath("//option[contains(.,'some borrower')]")).click();
        driver.findElement(By.id("lend_book_submit")).click();
        final String result = driver.findElement(By.id("result")).getText();
        assertEquals("SUCCESS", result);
    }

    @Test
    public void test_shouldShowAutocomplete() {
        Assume.assumeNotNull("Driver could not be initialized", driver);
        // clear the database...
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
        // Wait strategy might be needed here if autocomplete is slow
        driver.findElement(By.xpath("//li[contains(.,'f')]")).click();
        driver.findElement(By.id("lend_borrower")).findElement(By.xpath("//option[contains(.,'some borrower')]")).click();
        driver.findElement(By.id("lend_book_submit")).click();
        final String result = driver.findElement(By.id("result")).getText();
        assertEquals("SUCCESS", result);
    }

     @Test
     public void test_ShouldHandleQuotesInBookOrBorrowerValue() {
         Assume.assumeNotNull("Driver could not be initialized", driver);
         // clear the database...
         driver.get("http://demo-app:8080/demo/flyway");
         ApiCalls.registerBook("some \"book");
         ApiCalls.registerBorrowers("some \"borrower");

         driver.get("http://demo-app:8080/demo/library.html");

         driver.findElement(By.id("lend_book")).findElement(By.xpath("//option[contains(.,'some \"book')]")).click();
         driver.findElement(By.id("lend_borrower")).findElement(By.xpath("//option[contains(.,'some \"borrower')]")).click();
         driver.findElement(By.id("lend_book_submit")).click();
         final String result = driver.findElement(By.id("result")).getText();
         assertEquals("SUCCESS", result);
     }

     @Test
     public void test_ShouldRegisterAndLoginUser() {
         Assume.assumeNotNull("Driver could not be initialized", driver);
         driver.get("http://demo-app:8080/demo/flyway");
         driver.get("http://demo-app:8080/demo/library.html");
         driver.findElement(By.id("register_username")).sendKeys("some user");
         driver.findElement(By.id("register_password")).sendKeys("lasdfj;alsdkfjasdf");
         driver.findElement(By.id("register_submit")).click();

         final String registerResult = driver.findElement(By.id("result")).getText();
         assertTrue("result was " + registerResult,
                 registerResult.contains("status: SUCCESSFULLY_REGISTERED"));

         driver.findElement(By.linkText("Return")).click();
         driver.findElement(By.id("login_username")).sendKeys("some user");
         driver.findElement(By.id("login_password")).sendKeys("lasdfj;alsdkfjasdf");
         driver.findElement(By.id("login_submit")).click();

         final String loginResult = driver.findElement(By.id("result")).getText();
         assertTrue("result was " + loginResult,
                 loginResult.contains("access granted"));
     }
}