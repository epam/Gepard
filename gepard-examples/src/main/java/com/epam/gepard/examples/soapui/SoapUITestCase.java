package com.epam.gepard.examples.soapui;

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
import com.epam.gepard.common.TestFailedError;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.logger.LogFileWriter;
import com.epam.gepard.util.Util;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.support.AbstractTestRunner;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlLoader;
import com.eviware.soapui.tools.SoapUITestCaseRunner;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a TestCase, which supports HTML logs, and beforeTestCaseSet
 * and afterTestCaseSet event.
 * <p>
 * Pls. read documentation at GenericTestSuite.
 *
 * @author Tamas Kohegyi
 */
public abstract class SoapUITestCase implements GepardTestClass {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SoapUITestCase.class);
    private static final String UTF8 = "UTF-8";
    /**
     * The final log entry will contain the substring "[FINISHED]" unless the test encounters problems in which case it will contain "[FAILED]".
     */
    private static final String SOAPUI_LOGROOT_PROPERTY = "soapui.logroot";
    /**
     * String that represents new line (\n) in HTML code.
     */
    private static final String HTML_NEW_LINE = "<br/>";
    /**
     * We will stream a copy of every important log channel into this StringWriter.
     */
    private final StringWriter stringWriter = new StringWriter();
    private final PatternLayout patternLayout = new PatternLayout();
    /**
     * Directory where the SoapUI logs should be stored.
     */
    private final String soapUILogPath;
    /**
     * Log filename used by SoapUI.
     */
    private final String logFileName;
    /**
     * The File object representing the Log file used by SoapUI.
     */
    private final File logFile;
    /**
     * Error Log filename used by SoapUI.
     */
    private final String errorLogFileName;
    /**
     * The File object representing the Error Log file used by SoapUI.
     */
    private final File errorLogFile;
    /**
     * This is where we’ll store every line of log entry, once the SoapUI test has finished running.
     */
    private List<String> logEntries;
    /**
     * Main SoapUI object that runs the tests.
     */
    private SoapUITestCaseRunner soapUITestCaseRunner;

    /**
     * Constructor, new approach.
     */
    public SoapUITestCase() {
        soapUILogPath = getTestClassExecutionData().getEnvironment().getProperty(Environment.GEPARD_HTML_RESULT_PATH) + "/" + this.getClass().getName().replace('.', '/')
                + getTestClassExecutionData().getDrivenDataRowNo() + "/";
        logFileName = soapUILogPath + "soapui.log";
        logFile = new File(logFileName);
        errorLogFileName = soapUILogPath + "soapui-errors.log";
        errorLogFile = new File(errorLogFileName);
    }

    /**
     * Create a console appender that will catch the SoapUI messages.
     *
     * @return with the appender.
     */
    public ConsoleAppender getConsoleAppender() {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setWriter(stringWriter);
        appender.setLayout(patternLayout);
        return appender;
    }

    /**
     * Transfer log entries into Gepard html log entries.
     */
    public void processLogEntries() {
        final String innerLogPattern = "exporting to [";
        for (String logEntry : logEntries) {
            if (logEntry.contains("running step")) { // "running step" is always present in a SoapUI log entry that performs the first part of a test step
                logStep(logEntry);
            } else if (logEntry.contains(innerLogPattern)) {
                String someLogFileName = logEntry.substring(logEntry.indexOf(innerLogPattern) + innerLogPattern.length(), logEntry.length() - 2);
                StringBuilder text = new StringBuilder();
                try {
                    LineNumberReader listreader = new LineNumberReader(new InputStreamReader(new FileInputStream(someLogFileName), UTF8));
                    String line; //to hold the line actually loaded
                    while ((line = listreader.readLine()) != null) {
                        line = line.trim();
                        text.append(line).append(HTML_NEW_LINE);
                    }
                } catch (IOException e) {
                    String message = "Error occurred meanwhile reading SoapUI log.";
                    text.append(message);
                    LOGGER.debug(message, e);
                }
                logResult("<font color=\"#AA0000\"><b>Test case failed.</b></font><br>\n", "<code><small><br><pre>" + text.toString()
                        + "</pre></small></code>");

            } else {
                logComment(logEntry);
            }
        }

        //finally refer to the original soapui log
        processLogfile(logFile, logFileName, "SoapUI log", getDivStep());

        //finally refer to the original soapui ERROR log
        processLogfile(errorLogFile, errorLogFileName, "SoapUI ERROR log", getDivStep());
    }

    private void processLogfile(final File logFile, final String logFileName, final String logName, final int divStep) {
        Util util = new Util();
        if (logFile.exists() && logFile.length() > 0) {
            //now we ensured that SoapUI log is available
            String addStr = " <small>[<a href=\"javascript:showhide('div_" + divStep + "');\">details</a>]</small>";
            LogFileWriter mainTestLogger = getTestClassExecutionData().getHtmlRunReporter().getTestMethodHtmlLog();
            mainTestLogger.insertText(
                    "<tr><td>&nbsp;</td><td bgcolor=\"#F0F0E0\">" + "See original " + logName + "." + addStr + "<div id=\"div_" + divStep
                            + "\" style=\"display:none\"><br>\n");
            try {
                LineNumberReader listreader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFileName), UTF8));
                String line; //to hold the line actually loaded
                while ((line = listreader.readLine()) != null) {
                    line = util.escapeHTML(line.trim());
                    mainTestLogger.insertText(line + HTML_NEW_LINE);
                }
            } catch (IOException e) {
                String message = "Error occurred meanwhile reading " + logName + ".";
                mainTestLogger.insertText(message);
                LOGGER.debug(message, e);
            }
            mainTestLogger.insertText("</div></td></tr>\n");
            increaseDivStep();
        }
    }

    @Before
    public final void setUp() throws Exception {
        //clean up log files
        logFile.delete(); //need to clean it up
        errorLogFile.delete(); //need to clean it up
        //Setup new SoapUI runner instance
        System.getProperties().setProperty(SOAPUI_LOGROOT_PROPERTY, soapUILogPath);
        // Create a new instance of soapUITestCaseRunner
        soapUITestCaseRunner = new SoapUITestCaseRunner();
        // SoapUI’s own reports will be generated at this path
        soapUITestCaseRunner.setOutputFolder(soapUILogPath);

        //Add a new appender for the most important loggers
        Logger.getLogger(WsdlLoader.class).addAppender(getConsoleAppender());
        Logger.getLogger(SoapUI.class).addAppender(getConsoleAppender());
        Logger.getLogger(AbstractTestRunner.class).addAppender(getConsoleAppender());
        soapUITestCaseRunner.getLog().addAppender(getConsoleAppender());
        Logger.getLogger("groovy").addAppender(getConsoleAppender());
    }

    /**
     * Specify test project XML local file path, can use URL instead.
     *
     * @param projectFile to be used by SoapUI.
     */
    void setProjectFile(final String projectFile) {
        soapUITestCaseRunner.setProjectFile(projectFile);
    }

    /**
     * Specify test suite (that contains the test case specified below).
     *
     * @param testSuite to be used by SoapUI.
     */
    void setTestSuite(final String testSuite) {
        soapUITestCaseRunner.setTestSuite(testSuite);
    }

    /**
     * Specify test case (that is in the test suite specified above).
     *
     * @param testCase to be used by SoapUI.
     */
    void setTestCase(final String testCase) {
        soapUITestCaseRunner.setTestCase(testCase);
    }

    /**
     * Override service endpoint (Optional).
     *
     * @param endpoint to be used by SoapUI.
     */
    void setEndpoint(final String endpoint) {
        soapUITestCaseRunner.setProjectFile(endpoint);
    }

    /**
     * Get SoapUI object if you would liek to manipulate it further.
     *
     * @return with the SoapUI object.
     */
    SoapUITestCaseRunner getSoapUITestCaseRunner() {
        return soapUITestCaseRunner;
    }

    /**
     * Runs the set SoapUI test.
     */
    void runSoapUITest() {
        TestFailedError ex = null;
        try {
            soapUITestCaseRunner.run();
        } catch (Exception e) {
            ex = new TestFailedError(e);
        }
        //transfer log entries to Gepard results.
        logEntries = Arrays.asList(stringWriter.getBuffer().toString().split("\n"));
        processLogEntries();
        if (ex != null) {
            throw ex;
        }
    }

}
