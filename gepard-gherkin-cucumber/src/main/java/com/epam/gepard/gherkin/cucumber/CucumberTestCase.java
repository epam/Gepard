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

import java.io.IOException;
import java.util.Properties;

import com.epam.gepard.gherkin.cucumber.helper.CucumberError;
import junit.framework.AssertionFailedError;
import junit.framework.TestResult;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.epam.gepard.common.Environment;
import com.epam.gepard.common.NATestCaseException;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.generic.GenericListTestSuite;
import com.epam.gepard.generic.CommonTestCase;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * Serves as a parent class for Cucumber based tests.
 * @author tkohegyi
 */
@CucumberOptions(strict = true, monochrome = true)
public abstract class CucumberTestCase extends CommonTestCase {

    /**
     * When any failure happens, this should be set to true by the connector class.
     * Also Error messages should be set.
     */
    private boolean isFailed;
    private String consoleErrorMessage;
    private int step = 1; // used to count scenarios executed within this Test Class execution


    /**
     * Constructs a new instance of {@link CucumberTestCase}.
     */
    public CucumberTestCase() {
        super("gherkin-cucumber");
    }

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        super.setUp2();
    }

    /**
     * jUnit tearDown is empty now.
     *
     * @throws Exception in case of trouble
     */
    @Override
    protected final void tearDown() throws Exception {
        super.tearDown2();
        super.tearDown();
    }

    @Override
    public void run(final TestResult result) {
        TestClassExecutionData o = getClassData();

        setTcase(Environment.createTestCase(getName(), o.getTestCaseSet()));

        setUpLogger();
        Properties props = new Properties();
        props.setProperty("ID", getTestID());
        props.setProperty("Name", getTestName());
        props.setProperty("TestCase", getName());
        props.setProperty("ScriptNameRow", getClassData().getID());
        getMainTestLogger().insertBlock("Header", props);
        try {
            runSuper(result);
        } finally {
            getMainTestLogger().insertBlock("Footer", null);
            getMainTestLogger().close();
            setMainTestLogger(null);
            getTcase().updateStatus();
            setTcase(null);
        }
    }

    /**
     * We override the run method in order to support HTML logs.
     * @param result Test result
     */
    protected void runSuper(final TestResult result) {
        super.run(result);
    }

    /**
     * We redeclared the runTest method in order to throw only AssertionFailedErrors and
     * to create the Failure logs.
     *
     * @throws Exception AssertionFailedError or ThreadDeath
     */
    @Override
    protected void runTest() throws Exception {
        try {
            //wc.logComment("test started");
            super.runTest();
            if (isFailed) {
                //test failed
                throw new AssertionFailedError(consoleErrorMessage);
            } else {
                //test passed
                logEvent("<font color=\"#00AA00\"><b>All Scenarios passed.</b></font>");
                systemOutPrintLn("All Scenario passed.");
            }
        } catch (CucumberError e) { //we rethrow the CucumberError as AssertionFailedError
            systemOutPrintLn("ERROR: " + e.getMessage());
            logResult("<font color=\"#AA0000\"><b>At least one of the Scenarios failed.</b></font><br>\nMessage: " + e.getMessage(), "<code><small><br><pre>"
                    + getFullStackTrace(e) + "</pre></small></code>");
            throw new AssertionFailedError(e.getMessage());
        } catch (AssertionFailedError e) { //we rethrow the AssertionFailedError
            String stackTrace = getFullStackTrace(e);
            systemOutPrintLn("ERROR: " + e.getMessage());
            logResult("<font color=\"#AA0000\"><b>At least one of the Scenarios failed.</b></font><br>\nMessage: " + e.getMessage(), "<code><small><br><pre>" + stackTrace
                    + "</pre></small></code>");
            throw e;
        } catch (NATestCaseException e) { //we rethrow the Exception as AssertionFailedError
            logEvent("<font color=\"#0000AA\"><b>N/A</b></font><br>\nMessage: " + e.getMessage());
            systemOutPrintLn("Test is N/A: " + e.getMessage());
        } catch (Throwable e) { //we rethrow the Exception as AssertionFailedError
            systemOutPrintLn("ERROR: " + e.getMessage());
            logResult("<font color=\"#AA0000\"><b>At least one of the Scenarios failed.</b></font><br>\nMessage: " + e.getMessage(), "<code><small><br><pre>"
                    + getFullStackTrace(e) + "</pre></small></code>");
            throw new AssertionFailedError(e.getMessage());
        }

    }

    /**
     * Write "Scenario finished" message at the end of the scenario.
     *
     */
    public void logScenarioEnded() {
        String testPassed = "Scenario finished.";
        getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#a0d0a0\">" + testPassed + "</td></tr>");
        systemOutPrintLn(testPassed);
    }

    /**
     * Write "Scenario" and "Example" information.
     *
     * @param scenario is the running scenario name, based on the annotation.
     * @param example is the content of the example row.
     */
    public void logScenarioStarted(final String scenario, final String example) {
        String innerScenario = scenario.replace('\uFF5F', '(').replace('\uFF60', ')'); //Unicode to Console (partial transfer)
        String consoleInfo = "Scenario: \"" + innerScenario + "\"";
        if (example != null) {
            consoleInfo += " with Example row: " + example;
        }
        systemOutPrintLn(step + ". " + consoleInfo);
        getMainTestLogger().insertText("<tr><td align=\"center\">&nbsp;&nbsp;" + step + ".&nbsp;&nbsp;</td><td bgcolor=\"#a0d0a0\"> " + consoleInfo
                + "</td></tr>\n");
        step++;
    }

    /**
     * The entry point to the Cucumber test. Sets up the test case and runs it.
     */
    public void testRunCucumber() {
        try {
            Cucumber cucumber = new Cucumber(this.getClass());
            RunNotifier notifier = new RunNotifier();
            notifier.addFirstListener(new CucumberEventListener(this));
            GenericListTestSuite.getGlobalDataStorage().put(this.getClass().toString(), this);
            cucumber.run(notifier);
        } catch (InitializationError | IOException e) {
            naTestCase("Cucumber Run Initialization Error: " + e.getMessage());
        }
    }

    /**
     * Set isFailed indicator.
     * @param isFailed true if failed.
     * @param consoleErrorMessage is the error message to be logged.
     */
    public void setFailed(final boolean isFailed, final String consoleErrorMessage) {
        this.isFailed = isFailed;
        this.consoleErrorMessage = consoleErrorMessage;
    }
}
