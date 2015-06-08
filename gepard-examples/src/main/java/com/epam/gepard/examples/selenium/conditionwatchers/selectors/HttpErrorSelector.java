package com.epam.gepard.examples.selenium.conditionwatchers.selectors;

import com.epam.gepard.selenium.conditionwatcher.selectors.Selector;
import org.openqa.selenium.By;

/**
 * Http Error Selector with two types of errors, and selectors to these errors.
 */
public enum HttpErrorSelector implements Selector {
    ERROR_HTTP_500("body[class='page common 500errorpage articlepage ']"),
    ERROR_HTTP_404("body[class='page common 404errorpage articlepage ']");

    private String selector;

    private HttpErrorSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String getSelector() {
        return selector;
    }

    @Override
    public By getBy() {
        return By.cssSelector(selector);
    }
}
