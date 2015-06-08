package com.epam.gepard.examples.selenium.conditionwatchers.watchconditions;

import com.epam.gepard.examples.selenium.conditionwatchers.selectors.HttpErrorSelector;
import com.epam.gepard.inspector.conditionwatcher.Watchable;
import com.epam.gepard.selenium.conditionwatcher.DefaultWebDriverWatchable;
import org.openqa.selenium.WebDriver;

/**
 * This enum is used to group similar watchables on a page.
 */
public enum HttpErrorWatchCondition implements Watchable<WebDriver> {
    HTTP_500(new DefaultWebDriverWatchable.Builder().message("HTTP 500 - Internal Server Error").conditionSelector(HttpErrorSelector.ERROR_HTTP_500).build()),
    HTTP_404(new DefaultWebDriverWatchable.Builder().message("HTTP 404 - Page Not Found").conditionSelector(HttpErrorSelector.ERROR_HTTP_404).build());

    private final Watchable<WebDriver> watchable;

    HttpErrorWatchCondition(Watchable<WebDriver> watchable) {
        this.watchable = watchable;
    }

    @Override
    public boolean checkCondition(WebDriver webDriver) {
        return watchable.checkCondition(webDriver);
    }

    @Override
    public String getMessage() {
        return watchable.getMessage();
    }

}
