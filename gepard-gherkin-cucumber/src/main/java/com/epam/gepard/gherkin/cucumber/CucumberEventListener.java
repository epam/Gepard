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

import com.epam.gepard.exception.SimpleGepardException;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.logger.HtmlRunReporter;
import com.epam.gepard.logger.LogFileWriter;
import gherkin.formatter.model.Scenario;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.PendingException;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Listener class optimized for use with Cucumber.
 */
public class CucumberEventListener extends RunListener implements GepardTestClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(CucumberEventListener.class);
    private boolean failedScenario;
    private int step = 1; // used to count scenarios executed within this Test Class execution

    /**
     * Called before any tests have been run.
     *
     * @param description describes the tests to be run
     */
    public void testRunStarted(Description description) {
        logComment("testRunStarted: " + description.toString());
    }

    /**
     * Called when all tests have finished.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    public void testRunFinished(Result result) {
        logComment("testRunFinished: " + result.toString());
    }

    /**
     * Called when an atomic test is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    public void testStarted(Description description) {
        if (description.isSuite()) {
            failedScenario = false;
            logScenarioStarted(description);
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
            logScenarioEnded(failedScenario);
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
                    failedScenario = true;
                    logComment(consoleMessage);
                    setFailed(consoleMessage);
                }
            } catch (PendingException e) {
                //testCase.naTestCase("missing glue code."); this does not work, if we do this, the scenario passes
                String cause = e.toString();
                String consoleMessage = "N/A: " + cause;
                failedScenario = true;
                logComment(consoleMessage);
                setFailed(cause);
            } catch (Throwable t) {
                //something still wrong
                String cause = t.toString();
                String consoleMessage = "FAILURE: " + cause;
                failedScenario = true;
                logComment(consoleMessage);
                setFailed(consoleMessage);
            }
        }
    }

    /**
     * Set isFailed indicator.
     * @param consoleErrorMessage is the error message to be logged.
     */
    public void setFailed(final String consoleErrorMessage) {
        String naCase = "cucumber.api.PendingException: TODO: implement me";
        if (consoleErrorMessage.contains(naCase)) {
            naTestCase("cucumber.api.PendingException: TODO: implement me");
        } else {
            getTestClassExecutionData().getHtmlRunReporter().setTestFailed(new SimpleGepardException(consoleErrorMessage));
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
            logComment(message);
        } else if (description.isTest()) {
            String message = "UNDISCOVERED TEST PATH: " + description.toString();
            LOGGER.error(message);
            logComment(message);
        } else {
            String message = "UNDISCOVERED ??? PATH: " + description.toString();
            LOGGER.error(message);
            logComment(message);
        }
    }

    /**
     * Write "Scenario" and "Example" information.
     *
     * @param example is the content of the example row.
     */
    public void logScenarioStarted(final Description example) {
        String innerScenario;
        try {
            Field privateSerializableField = Description.class.getDeclaredField("fUniqueId");
            privateSerializableField.setAccessible(true);
            Serializable scenarioCandidate = (Serializable) privateSerializableField.get(example);
            Scenario scenario = (Scenario) scenarioCandidate;
            innerScenario = scenario.getName();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Cannot access Scenario object at: " + example.toString(), e);
            innerScenario = getTestClassExecutionData().getTestStriptName();
        }
        innerScenario = innerScenario.replace('\uFF5F', '(').replace('\uFF60', ')'); //Unicode to Console (partial transfer)
        String consoleInfo = "Scenario: \"" + innerScenario + "\"";
        if (example != null) {
            consoleInfo += " with Example row: " + example.toString();
        }
        HtmlRunReporter reporter = getTestClassExecutionData().getHtmlRunReporter();
        LogFileWriter htmlLog = reporter.getTestMethodHtmlLog();
        if (htmlLog != null) {
            htmlLog.insertText(
                    "<tr><td align=\"center\">&nbsp;&nbsp;" + step + ".&nbsp;&nbsp;</td><td bgcolor=\"#b0e0b0\"> " + consoleInfo + "</td></tr>\n");
        }
        reporter.systemOutPrintLn(step + ". " + consoleInfo);
        step++;
    }

    /**
     * Write "Scenario finished" message at the end of the scenario.
     *
     */
    public void logScenarioEnded(final boolean isFailedScenario) {
        HtmlRunReporter reporter = getTestClassExecutionData().getHtmlRunReporter();
        LogFileWriter htmlLog = reporter.getTestMethodHtmlLog();
        String testPassed = "bgcolor=\"#b0e0b0\">Scenario finished.";
        if (isFailedScenario) {
            testPassed = "bgcolor=\"#e0b0b0\">Scenario finished with failure.";
        }
        if (htmlLog != null) {
            htmlLog.insertText("<tr><td>&nbsp;</td><td " + testPassed + "</td></tr>");
        }
        reporter.systemOutPrintLn(testPassed);
    }

}
