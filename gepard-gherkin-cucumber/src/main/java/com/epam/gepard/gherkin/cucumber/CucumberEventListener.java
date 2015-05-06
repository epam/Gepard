package com.epam.gepard.gherkin.cucumber;

/*==========================================================================
 Copyright 2004-2015 EPAM Systems

 This file is part of Gepard.

 Gepard is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gepard is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.PendingException;

/**
 * Listener class optimized for use with Cucumber.
 */
public class CucumberEventListener extends RunListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberEventListener.class);
    private CucumberTestCase testCase;

    /**
     * Constructor to use Cucumber notification hooks.
     * @param testCase is the runner Test Class, that provides the logging framework.
     */
    public CucumberEventListener(final CucumberTestCase testCase) {
        this.testCase = testCase;
    }

    /**
     * Called before any tests have been run.
     *
     * @param description describes the tests to be run
     */
    public void testRunStarted(Description description) {
        testCase.logComment("testRunStarted: " + description.toString());
    }

    /**
     * Called when all tests have finished.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    public void testRunFinished(Result result) {
        testCase.logComment("testRunFinished: " + result.toString());
    }

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    public void testStarted(Description description) {
        if (description.isSuite()) {
            testCase.logScenarioStarted(testCase.getTestName(), description.toString());
        } else if (!description.isTest()) {
            LOGGER.error("UNDISCOVERED PATH: testStarted/???:  " + description.toString());
        }
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    public void testFinished(Description description) {
        if (description.isSuite()) {
            testCase.logScenarioEnded();
        } else if (!description.isTest()) {
            LOGGER.error("UNDISCOVERED PATH: testFinished/???:  " + description.toString());
        }
    }

    /**
     * Called when an atomic test fails.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    public void testFailure(Failure failure) {
        Description d = failure.getDescription();
        if (d.isTest()) {
            try {
                Throwable t = failure.getException();
                if (t != null) {
                    throw t;
                } else {
                    String consoleMessage = "FAILURE: " + d.toString();
                    //String htmlMessage = u.alertText("FAILURE: ") + u.escapeHTML(d.toString());
                    testCase.setFailed(true, consoleMessage);
                    testCase.logComment(consoleMessage);
                }
            } catch (PendingException e) {
                //testCase.naTestCase("missing glue code."); this does not work, if we do this, the scenario passes
                String cause = e.toString();
                String consoleMessage = "N/A: " + cause;
                //String htmlMessage = u.alertText("N/A: ") + u.escapeHTML(cause);
                testCase.setFailed(true, cause);
                testCase.logComment(consoleMessage);
            } catch (Throwable t) {
                //something still wrong
                String cause = t.toString();
                String consoleMessage = "FAILURE: " + cause;
                //String htmlMessage = u.alertText("FAILURE: ") + u.escapeHTML(cause);
                testCase.setFailed(true, consoleMessage);
                testCase.logComment(consoleMessage);
            }
        }
    }

    /**
     * Called when an atomic test flags that it assumes a condition that is false.
     *
     * @param failure describes the test that failed.
     */
    public void testAssumptionFailure(Failure failure) {
        LOGGER.error("UNDISCOVERED PATH: testAssumptionFailure/???:  " + failure.toString());
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated, or a path is not identified.
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    public void testIgnored(Description description) {
        if (description.isSuite()) {
            String message = "UNDISCOVERED SCENARIO PATH: " + description.toString();
            LOGGER.error(message);
            testCase.logComment(message);
        } else if (description.isTest()) {
            String message = "UNDISCOVERED TEST PATH: " + description.toString();
            LOGGER.error(message);
            testCase.logComment(message);
        } else {
            String message = "UNDISCOVERED ??? PATH: " + description.toString();
            LOGGER.error(message);
            testCase.logComment(message);
        }
    }
}
