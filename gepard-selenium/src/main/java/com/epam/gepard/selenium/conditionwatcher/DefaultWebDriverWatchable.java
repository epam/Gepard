package com.epam.gepard.selenium.conditionwatcher;

import com.epam.gepard.selenium.conditionwatcher.selectors.Selector;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.openqa.selenium.WebDriver;

/**
 * Default implementation of @AbstractWebDriverWatchable.
 */
public final class DefaultWebDriverWatchable extends AbstractWebDriverWatchable {
    private Selector conditionSelector;
    private Optional<Selector> detailedMessageSelector;
    private Optional<Selector> actionSelector;

    private DefaultWebDriverWatchable(Builder builder) {
        super(builder.message);

        this.conditionSelector = builder.conditionSelector;
        this.detailedMessageSelector = builder.detailedMessageSelector;
        this.actionSelector = builder.actionSelector;
    }

    @Override
    protected boolean evaluate(WebDriver webDriver) {
        return isVisible(webDriver, conditionSelector);
    }

    @Override
    protected String getDetailedMessage(WebDriver webDriver) {
        return detailedMessageSelector.isPresent() ? getText(webDriver, detailedMessageSelector.get()) : "";
    }

    @Override
    protected void handleCondition(WebDriver webDriver) {
        if (actionSelector.isPresent()) {
            click(webDriver, actionSelector.get());
        }
    }

    public Selector getConditionSelector() {
        return conditionSelector;
    }

    public Optional<Selector> getDetailedMessageSelector() {
        return detailedMessageSelector;
    }

    public Optional<Selector> getActionSelector() {
        return actionSelector;
    }

    /**
     * Static Builder class to create a new @DefaultWebDriverWatchable class.
     */
    public static class Builder {
        private String message;
        private Selector conditionSelector;
        private Optional<Selector> detailedMessageSelector = Optional.absent();
        private Optional<Selector> actionSelector = Optional.absent();

        /**
         * Specify the message for the @Selector.
         *
         * @param message is the message
         * @return with itself
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Specify the conditionSelector for the @Selector.
         *
         * @param conditionSelector is the conditionSelector
         * @return with itself
         */
        public Builder conditionSelector(Selector conditionSelector) {
            this.conditionSelector = conditionSelector;
            return this;
        }

        /**
         * Specify the detailedMessageSelector optional @Selector.
         *
         * @param detailedMessageSelector is the Selector
         * @return with itself
         */
        public Builder detailedMessageSelector(Selector detailedMessageSelector) {
            this.detailedMessageSelector = Optional.of(detailedMessageSelector);
            return this;
        }

        /**
         * Specify the actionSelector optional @Selector.
         *
         * @param actionSelector is the Selector
         * @return with itself
         */
        public Builder actionSelector(Selector actionSelector) {
            this.actionSelector = Optional.of(actionSelector);
            return this;
        }

        /**
         * Final call to get the built @DefaultWebDriverWatchable object.
         *
         * @return with the @DefaultWebDriverWatchable object.
         */
        public DefaultWebDriverWatchable build() {
            Preconditions.checkArgument(message != null);
            Preconditions.checkArgument(conditionSelector != null);

            return new DefaultWebDriverWatchable(this);
        }
    }

}
