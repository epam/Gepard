package com.epam.gepard.gherkin.jbehave;

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

import com.epam.gepard.common.Environment;
import com.epam.gepard.common.NATestCaseException;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.generic.CommonTestCase;
import com.epam.gepard.gherkin.jbehave.helper.InstanceStepsFactoryCreator;
import junit.framework.AssertionFailedError;
import junit.framework.TestResult;
import org.jbehave.core.embedder.Embedder;

import java.util.Properties;

/**
 * Serves as a parent class for Jbehave based tests.
 *
 * @author Adam_Csaba_Kiraly
 */
public abstract class JBehaveTestCase extends CommonTestCase {

    private final GepardEmbedder gepardEmbedder;
    private String htmlErrorMessage;
    private String consoleErrorMessage;

    /**
     * Constructs a new instance of {@link JBehaveTestCase}.
     */
    public JBehaveTestCase() {
        super("gherkin-jbehave");
        gepardEmbedder = new GepardEmbedder(new Embedder(), new ConfigurationFactory(), new InstanceStepsFactoryCreator());
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

    public void setHtmlErrorMessage(final String htmlErrorMessage) {
        this.htmlErrorMessage = htmlErrorMessage;
    }

    public void setConsoleErrorMessage(final String consoleErrorMessage) {
        this.consoleErrorMessage = consoleErrorMessage;
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
     *
     * @param result Test result
     */
    protected void runSuper(final TestResult result) {
        super.run(result);
    }

    /**
     * We redeclared the runTest method in order to throw only AssertionFailedErrors and
     * to create the Failure logs.
     */
    @Override
    protected void runTest() {
        try {
            super.runTest();
            logEvent("<font color=\"#00AA00\"><b>Test passed.</b></font>");
            systemOutPrintLn("Test passed.");
        } catch (AssertionFailedError e) { //we rethrow the AssertionFailedError
            String stackTrace = getFullStackTrace(e);
            systemOutPrintLn("ERROR: " + e.getMessage());
            logResult("<font color=\"#AA0000\"><b>Test failed.</b></font><br>\nMessage: " + e.getMessage(), "<code><small><br><pre>" + stackTrace
                    + "</pre></small></code>");
            throw e;
        } catch (NATestCaseException e) { //we rethrow the Exception as AssertionFailedError
            logEvent("<font color=\"#0000AA\"><b>N/A</b></font><br>\nMessage: " + e.getMessage());
            systemOutPrintLn("Test is N/A: " + e.getMessage());
        } catch (Embedder.RunningStoriesFailed e) {
            if (consoleErrorMessage != null) {
                //test failed
                systemOutPrintLn("Test Failed.");
                logResult("<font color=\"#AA0000\"><b>Test failed.</b></font><br>\nMessage: " + htmlErrorMessage, "<code><small><br><pre>"
                        + getFullStackTrace(e) + "</pre></small></code>");
                throw new AssertionFailedError(consoleErrorMessage);
            } else {
                // test is N/A
                logEvent("<font color=\"#0000AA\"><b>N/A</b></font><br>");
            }
        } catch (Throwable e) { //we rethrow the Exception as AssertionFailedError
            systemOutPrintLn("ERROR: " + e.getMessage());
            logResult("<font color=\"#AA0000\"><b>Test failed.</b></font><br>\nMessage: " + e.getMessage(), "<code><small><br><pre>"
                    + getFullStackTrace(e) + "</pre></small></code>");
            throw new AssertionFailedError(e.getMessage());
        }
    }

    /**
     * Write an event message to the log.
     *
     * @param text Event message
     */
    public void logEvent(final String text) {
        if (!text.startsWith("<font")) {
            systemOutPrintLn(text);
        }
        if (getMainTestLogger() != null) {
            getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + "</td></tr>\n");
        }
    }

    /**
     * Write an event message to the log.
     *
     * @param text        Event message
     * @param description Event description/info
     */
    private void logResult(final String text, final String description) {
        if (getMainTestLogger() != null) {
            increaseStep();
            String addStr = " <small>[<a href=\"javascript:showhide('div_" + getStep() + "');\">details</a>]</small>";
            getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0F0\">" + text + addStr + "<div id=\"div_" + getStep()
                    + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        }
    }

    /**
     * Write a comment message to the log, without the step number, but with a description.
     * Can be used to dump stack trace for example.
     *
     * @param comment     Comment message
     * @param description is a multi-row string description for the comment.
     */
    public void logComment(final String comment, final String description) {
        if (getMainTestLogger() != null) {
            increaseDivStep();
            String addStr = " <small>[<a href=\"javascript:showhide('div_" + getDivStep() + "');\">details</a>]</small>";

            getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + comment + addStr + "<div id=\"div_" + getDivStep()
                    + "\" style=\"display:none\"><br>\n" + description + "</div></td></tr>\n");
        }
    }

    /**
     * Write "Example" information.
     *
     * @param message Example message
     */
    public void logExampleRow(final String message) {
        systemOutPrintLn("Example: " + message);
        getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#a0d0a0\">Example:<br/> " + message + "</td></tr>");
    }

    /**
     * Write "Pending" information.
     *
     * @param message Pending message
     */
    public void logPendingRow(final String message) {
        systemOutPrintLn(message);
        getMainTestLogger().insertText("<tr><td>&nbsp;</td><td bgcolor=\"#f0b0b0\">" + message + "</td></tr>");
    }

    /**
     * The entry point to the test. Sets up the test case and runs it.
     */
    public void testRunJBehave() {
        gepardEmbedder.setupJBehaveEmbedder(this);
        gepardEmbedder.runTest(getStoryPath());
    }

    /**
     * This method should return the path to the test case's story file.
     *
     * @return path to test case's story file
     */
    protected abstract String getStoryPath();

}
