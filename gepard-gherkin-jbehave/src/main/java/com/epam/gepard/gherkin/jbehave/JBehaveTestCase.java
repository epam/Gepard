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

import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.gherkin.jbehave.helper.InstanceStepsFactoryCreator;
import com.epam.gepard.logger.HtmlRunReporter;
import com.epam.gepard.logger.LogFileWriter;
import org.jbehave.core.embedder.Embedder;

/**
 * Serves as a parent class for Jbehave based tests.
 *
 * @author Adam_Csaba_Kiraly
 */
public abstract class JBehaveTestCase implements GepardTestClass {

    private final GepardEmbedder gepardEmbedder;

    /**
     * Constructs a new instance of {@link JBehaveTestCase}.
     */
    public JBehaveTestCase() {
        gepardEmbedder = new GepardEmbedder(new Embedder(), new ConfigurationFactory(), new InstanceStepsFactoryCreator());
    }

    /**
     * Write "Example" information.
     *
     * @param message Example message
     */
    public void logExampleRow(final String message) {
        HtmlRunReporter reporter = getTestClassExecutionData().getHtmlRunReporter();
        reporter.systemOutPrintLn("Example: " + message);
        LogFileWriter htmlLog = reporter.getTestMethodHtmlLog();
        if (htmlLog != null) {
            htmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#a0d0a0\">Example:<br/> " + message + "</td></tr>");
        }
    }

    /**
     * Write "Pending" information.
     *
     * @param message Pending message
     */
    public void logPendingRow(final String message) {
        HtmlRunReporter reporter = getTestClassExecutionData().getHtmlRunReporter();
        reporter.systemOutPrintLn(message);
        LogFileWriter htmlLog = reporter.getTestMethodHtmlLog();
        if (htmlLog != null) {
            htmlLog.insertText("<tr><td>&nbsp;</td><td bgcolor=\"#f0b0b0\">" + message + "</td></tr>");
        }
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
