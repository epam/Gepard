package com.epam.gepard.selenium.conditionwatcher.selectors;

import org.openqa.selenium.By;

/**
 * Selector interface, in order to have dynamic selectors available for WebDriver.
 */
public interface Selector {
    /**
     * Gets the selector part of the object.
     * @return with the selector string,
     */
    String getSelector();

    /**
     * Define method of how the selector should work.
     * @return with the selected object (implementation will decide how).
     */
    By getBy();
}
