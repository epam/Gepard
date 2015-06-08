package com.epam.gepard.selenium.conditionwatcher;

import com.epam.gepard.inspector.conditionwatcher.ConditionSeverity;
import com.epam.gepard.inspector.conditionwatcher.ConditionWatcher;
import com.epam.gepard.inspector.conditionwatcher.ConditionWatcherErrorException;
import com.epam.gepard.inspector.conditionwatcher.ConditionWatcherWarningException;
import com.epam.gepard.inspector.conditionwatcher.Watchable;
import com.google.common.base.Optional;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * General Condition watcher for Selenium WebDriver.
 */
public class WebDriverConditionWatcher implements ConditionWatcher<WebDriver> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverConditionWatcher.class);
    private final Map<Watchable<WebDriver>, ConditionSeverity> watchList = new LinkedHashMap<>();
    private WebDriver webDriver;

    /**
     * Constructor, for WebDriver.
     *
     * @param webDriver is the used WebDriver object.
     */
    public WebDriverConditionWatcher(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public void register(Watchable<WebDriver> watchable, ConditionSeverity conditionSeverity) {
        LOGGER.info("Register WatchCondition [{}] with Severity [{}]", watchable, conditionSeverity);
        watchList.put(watchable, conditionSeverity);
    }

    @Override
    public void registerAll(Map<Watchable<WebDriver>, ConditionSeverity> watchList) {
        for (Entry<Watchable<WebDriver>, ConditionSeverity> entry : watchList.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void unregister(Watchable<WebDriver> watchable) {
        LOGGER.info("Unregister WatchCondition [{}]", watchable);
        watchList.remove(watchable);
    }

    @Override
    public void unregisterAll(Set<Watchable<WebDriver>> watchables) {
        for (Watchable<WebDriver> watchable : watchables) {
            watchList.remove(watchable);
        }
    }

    @Override
    public void reset() {
        LOGGER.info("Reset ConditionWatcher, unregister all registered WatchConditions");
        watchList.clear();
    }

    @Override
    public void watchForConditions() {
        Optional<Watchable<WebDriver>> conditionMet = evaluateConditions();

        if (conditionMet.isPresent()) {
            ConditionSeverity conditionSeverity = getSeverity(conditionMet.get());
            LOGGER.info("Condition met: [{}], Severity: [{}]", conditionMet.get(), conditionSeverity);
            switch (conditionSeverity) {
            case INFO:
                // Do nothing, warning message has been already logged
                break;
            case WARNING:
                throw new ConditionWatcherWarningException(conditionMet.get().getMessage());
            case ERROR:
                throw new ConditionWatcherErrorException(conditionMet.get().getMessage());
            default:
                // do nothing
            }
        }
    }

    private ConditionSeverity getSeverity(Watchable<WebDriver> watchable) {
        return watchList.get(watchable);
    }

    private Optional<Watchable<WebDriver>> evaluateConditions() {
        Optional<Watchable<WebDriver>> retval = Optional.absent();

        for (Watchable<WebDriver> watchable : watchList.keySet()) {
            if (watchable.checkCondition(webDriver)) {
                retval = Optional.of(watchable);
                break;
            }
        }

        return retval;
    }
}
