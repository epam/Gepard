package com.epam.gepard.generic;

import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.TestClassExecutionThread;

/**
 * @author Tamas_Kohegyi
 */
public interface GepardTestClass {

    /**
     * Detects the Test Class Execution Data object for the running Test Class.
     * @return with Test Class Execution Data object or null, if it was not properly set.
     */
    default TestClassExecutionData getTestClassExecutionData() {
        return TestClassExecutionThread.classDataInContext.get();
    }

    default void logComment(final String comment) {
        getTestClassExecutionData().getHtmlRunReporter().logComment(comment);

    }

    default void logStep(final String comment) {
        getTestClassExecutionData().getHtmlRunReporter().logStep(comment);
    }

    default void logEvent(final String comment) {
        getTestClassExecutionData().getHtmlRunReporter().logEvent(comment);
    }

    /**
     * Sets the testcase. N/A test cases are tests with Not Applicable results.
     *
     * @param reason The reason why this TC is N/A
     */
    default void naTestCase(final String reason) {
        getTestClassExecutionData().getHtmlRunReporter().naTestCase(reason);
    }

    default void dummyTestCase() {
        getTestClassExecutionData().getHtmlRunReporter().dummyTestCase();
    }

}
