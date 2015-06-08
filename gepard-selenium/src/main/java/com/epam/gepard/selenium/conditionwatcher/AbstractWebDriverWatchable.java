package com.epam.gepard.selenium.conditionwatcher;

import com.epam.gepard.inspector.conditionwatcher.Watchable;
import com.epam.gepard.selenium.conditionwatcher.selectors.Selector;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

/**
 * This class provides a Template Method pattern implementation for {@link Watchable} objects.
 * You must override at least the evaluate() method.
 * If that returns true, the detailed message logged and the conditionAction is executed.
 *
 * @author Robert_Ambrus
 */
public abstract class AbstractWebDriverWatchable implements Watchable<WebDriver> {
    private final String message;

    /**
     * Constructor for this simple implementation.
     *
     * @param message The message you want to see in the log.
     */
    public AbstractWebDriverWatchable(String message) {
        this.message = message;
    }

    @Override
    public final boolean checkCondition(WebDriver webDriver) {
        boolean retval = false;

        if (evaluate(webDriver)) {
            retval = true;
            handleCondition(webDriver);
        }

        return retval;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * This method should evaluate whether the watched condition is actually true.
     *
     * @param webDriver is the webDriver object.
     * @return Returns true if the watched condition is true.
     */
    protected abstract boolean evaluate(WebDriver webDriver);

    /**
     * Return detailed message to include in the log when this event is happening.
     *
     * @param webDriver is the webDriver object.
     * @return empty string by default, override if you want a detailed message logged.
     */
    protected String getDetailedMessage(WebDriver webDriver) {
        return "";
    }

    /**
     * This method is called when the condition is true.
     *
     * @param webDriver is the webDriver object.
     */
    protected void handleCondition(WebDriver webDriver) {
    }

    /**
     * Checks Visibility of an element based on selector.
     *
     * @param webDriver is the web driver object
     * @param selector  of the element
     * @return true if visible
     */
    protected boolean isVisible(WebDriver webDriver, Selector selector) {
        boolean retval;

        try {
            retval = webDriver.findElement(selector.getBy()).isDisplayed();
        } catch (NoSuchElementException e) {
            retval = false;
        }

        return retval;
    }

    /**
     * Safe getText method. If the element is not visible, returns with empty string.
     *
     * @param webDriver the webdriver object
     * @param selector  is the element selector
     * @return with the element.getText() result, or empty string if element is not visible
     */
    protected String getText(WebDriver webDriver, Selector selector) {
        String retval = "";

        if (isVisible(webDriver, selector)) {
            retval = webDriver.findElement(selector.getBy()).getText();
        }

        return retval;
    }

    /**
     * Safe click on element. If the element is not visible, then @IllegalStateException thrown.
     *
     * @param webDriver is the webdriver object
     * @param selector  is the element selector
     */

    protected void click(WebDriver webDriver, Selector selector) {
        if (isVisible(webDriver, selector)) {
            webDriver.findElement(selector.getBy()).click();
        } else {
            throw new IllegalStateException("Action element not found !");
        }
    }
}
