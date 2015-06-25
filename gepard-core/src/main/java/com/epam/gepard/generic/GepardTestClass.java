package com.epam.gepard.generic;

import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.TestClassExecutionThread;
import com.epam.gepard.datadriven.DataDrivenParameters;
import com.epam.gepard.exception.SimpleGepardException;

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

    default void logComment(final String comment, final String description) {
        getTestClassExecutionData().getHtmlRunReporter().logComment(comment, description);
    }

    default void logStep(final String comment) {
        getTestClassExecutionData().getHtmlRunReporter().logStep(comment);
    }

    default void logEvent(final String comment) {
        getTestClassExecutionData().getHtmlRunReporter().logEvent(comment);
    }

    default void logWarning(final String comment) { getTestClassExecutionData().getHtmlRunReporter().logWarning(comment); }

    default void logResult(final String comment, final String description) { getTestClassExecutionData().getHtmlRunReporter().logResult(comment, description); }

    default int getDivStep() { return getTestClassExecutionData().getHtmlRunReporter().getDivStep(); }
    default void increaseDivStep() { getTestClassExecutionData().getHtmlRunReporter().increaseDivStep(); }

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

    default String getDataDrivenTestParameter(final int byPosition) {
        String value;
        DataDrivenParameters parameters = getTestClassExecutionData().getDrivenData();
        if (parameters != null) {
            value = parameters.getTestParameter(parameters.getParameterName(byPosition));
        } else {
            throw new SimpleGepardException("Try to access to a data driven parameter is failed, as it is not data driven test class.");
        }
        return value;
    }

    default String getDataDrivenTestParameter(final String byParameterName) {
        String value;
        DataDrivenParameters parameters = getTestClassExecutionData().getDrivenData();
        if (parameters != null) {
            value = parameters.getTestParameter(byParameterName);
        } else {
            throw new SimpleGepardException("Try to access to a data driven parameter is failed, as it is not data driven test class.");
        }
        return value;
    }

}
