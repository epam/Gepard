package com.epam.gepard.examples.selenium.conditionwatchers;

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.examples.selenium.conditionwatchers.watchconditions.HttpErrorWatchCondition;
import com.epam.gepard.inspector.conditionwatcher.ConditionSeverity;
import com.epam.gepard.inspector.conditionwatcher.ConditionWatcher;
import com.epam.gepard.selenium.SeleniumTestCase;
import com.epam.gepard.selenium.annotation.GepardSeleniumTestClass;
import com.epam.gepard.selenium.conditionwatcher.WebDriverConditionWatcher;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Test class to show the usage of @Before, @After annotations,
 * and example implementation of the core's watchable condition, for detecting E404 page in Selenium test.
 */
@TestClass(id = "SELENIUM", name = "Selenium Test with E404 watched")
@GepardSeleniumTestClass(baseUrl = "http://www.epam.com/zzz", browser = "*firefox")
public class Http404ConditionDemo extends SeleniumTestCase {
    private static final String INVALID_HOMEPAGE_URL = "http://www.epam.com/zzz";

    private ConditionWatcher<WebDriver> conditionWatcher;

    /**
     * Pre-condition for every test: register a condition watcher for E500 and E404.
     * Note that only E404 will be used in this example test.
     */
    @Before
    public void before() {
        logComment("By using @Before annotation, we set up the condition watchers.");
        conditionWatcher = new WebDriverConditionWatcher(getWebDriver());
        conditionWatcher.register(HttpErrorWatchCondition.HTTP_500, ConditionSeverity.ERROR);
        conditionWatcher.register(HttpErrorWatchCondition.HTTP_404, ConditionSeverity.ERROR);
    }

    /**
     * A test without using the condition watcher. Will fail with not really meaningful error message.
     */
    public void testWithoutConditionWatcher() {
        logComment("Without condition watcher, the error is not really meaningful.");
        getWebDriver().get(INVALID_HOMEPAGE_URL);
        getWebDriver().findElement(By.cssSelector("#anynotexisting")).sendKeys("SearchString");
    }


    /**
     * A test with condition watcher. Will fail properly with meaningful error message.
     */
    public void testWithConditionWatcher() {
        logComment("With condition watcher, we know what is the problem immediately.");
        getWebDriver().get(INVALID_HOMEPAGE_URL);
        conditionWatcher.watchForConditions();
        getWebDriver().findElement(By.cssSelector("#anynotexisting")).sendKeys("SearchString");
    }

    /**
     * Post-condition for every test: does actually nothimg, just shows the possibility of using @After annotation.
     */
    @After
    public void after() {
        logComment("Just representing, that using @After annotation it is possible.");
    }

}
