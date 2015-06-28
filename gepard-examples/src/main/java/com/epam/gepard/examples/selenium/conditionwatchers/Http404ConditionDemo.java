package com.epam.gepard.examples.selenium.conditionwatchers;

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.examples.selenium.conditionwatchers.watchconditions.HttpErrorWatchCondition;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.inspector.conditionwatcher.ConditionSeverity;
import com.epam.gepard.inspector.conditionwatcher.ConditionWatcher;
import com.epam.gepard.selenium.browsers.WebDriverUtil;
import com.epam.gepard.selenium.conditionwatcher.WebDriverConditionWatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Test class to show the usage of @Before, @After annotations,
 * and example implementation of the core's watchable condition, for detecting E404 page in Selenium test.
 */
@TestClass(id = "SELENIUM", name = "Selenium Test with E404 watched")
public class Http404ConditionDemo implements GepardTestClass {
    private static final String INVALID_HOMEPAGE_URL = "http://www.epam.com/zzz";

    private ConditionWatcher<WebDriver> conditionWatcher;
    private WebDriverUtil webDriverUtil = new WebDriverUtil(this);

    /**
     * Pre-condition for every test: register a condition watcher for E500 and E404.
     * Note that only E404 will be used in this example test.
     */
    @Before
    public void buildWebDriverInstance() {
        webDriverUtil.buildWebDriverInstance("http://www.epam.com");
        logComment("By using @Before annotation, we set up the condition watchers.");
        conditionWatcher = new WebDriverConditionWatcher(webDriverUtil.getWebDriver());
        conditionWatcher.register(HttpErrorWatchCondition.HTTP_500, ConditionSeverity.ERROR);
        conditionWatcher.register(HttpErrorWatchCondition.HTTP_404, ConditionSeverity.ERROR);
    }

    @After
    public void destroyWebDriverInstance() {
        webDriverUtil.destroyWebDriverInstance();
    }

    /**
     * A test without using the condition watcher. Will fail with not really meaningful error message.
     */
    @Test
    public void withoutConditionWatcher() {
        logComment("Without condition watcher, the error is not really meaningful.");
        webDriverUtil.getWebDriver().get(INVALID_HOMEPAGE_URL);
        webDriverUtil.getWebDriver().findElement(By.cssSelector("#anynotexisting")).sendKeys("SearchString");
    }


    /**
     * A test with condition watcher. Will fail properly with meaningful error message.
     */
    @Test
    public void withConditionWatcher() {
        logComment("With condition watcher, we know what is the problem immediately.");
        webDriverUtil.getWebDriver().get(INVALID_HOMEPAGE_URL);
        conditionWatcher.watchForConditions();
        webDriverUtil.getWebDriver().findElement(By.cssSelector("#anynotexisting")).sendKeys("SearchString");
    }

}
