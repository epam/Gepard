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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.epam.gepard.logger.HtmlRunReporter;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.gherkin.jbehave.helper.IOUtils;
import com.epam.gepard.gherkin.jbehave.helper.ResourceProvider;
import com.epam.gepard.util.Util;

/**
 * Used for logging the events of the test case.
 * @author Adam_Csaba_Kiraly
 */
public class JBehaveStoryReporter extends NullStoryReporter {

    private static final String COULD_NOT_READ_FILE = "Could not read file: ";
    private static final String FILE_NOT_FOUND = "File not found: ";
    private static final String AFTER_STORIES_PATH = "AfterStories";
    private static final String BEFORE_STORIES_PATH = "BeforeStories";
    private static final Logger LOGGER = LoggerFactory.getLogger(JBehaveStoryReporter.class);
    private final JBehaveTestCase jBehaveTestCase;
    private final Util util = new Util();
    private final IOUtils ioUtils = new IOUtils();
    private final ResourceProvider resourceProvider = new ResourceProvider();

    /**
     * Constructs a new instance of {@link JBehaveStoryReporter}.
     * @param jBehaveTestCase the test case used for logging the events
     */
    public JBehaveStoryReporter(final JBehaveTestCase jBehaveTestCase) {
        this.jBehaveTestCase = jBehaveTestCase;
    }

    @Override
    public void beforeStory(final Story story, final boolean givenStory) {
        String path = story.getPath();
        if (!path.equals(BEFORE_STORIES_PATH) && !path.equals(AFTER_STORIES_PATH)) {
            String message = createFileContentsMessage(path);
            jBehaveTestCase.logComment("Using story file: " + path, message);
        }
    }

    private String createFileContentsMessage(final String path) {
        String fileContents = FILE_NOT_FOUND + path;
        try {
            InputStream inputStream = resourceProvider.getResourceAsStream(path);
            if (inputStream != null) {
                fileContents = ioUtils.toString(inputStream);
            }
        } catch (IOException e) {
            LOGGER.info(COULD_NOT_READ_FILE, e);
            fileContents = COULD_NOT_READ_FILE + path;
        }
        return "<pre>" + util.escapeHTML(fileContents) + "</pre>";
    }

    @Override
    public void beforeScenario(final String scenarioTitle) {
        jBehaveTestCase.logComment("Scenario: " + scenarioTitle);
    }

    @Override
    public void example(final Map<String, String> tableRow) {
        jBehaveTestCase.logExampleRow(tableRow.toString());
    }

    @Override
    public void successful(final String step) {
        jBehaveTestCase.logStep("Step successful: " + step);
    }

    @Override
    public void notPerformed(final String step) {
        String message = "Step not performed: " + step;
        jBehaveTestCase.logComment(message);
    }

    @Override
    public void failed(final String step, final Throwable cause) {
        jBehaveTestCase.logStep("Step failed: " + step);
        String htmlMessage = util.alertText("FAILURE: ") + util.escapeHTML(cause.getCause().toString());
        String consoleMessage = cause.getCause().toString();
        HtmlRunReporter reporter = jBehaveTestCase.getTestClassExecutionData().getHtmlRunReporter();
        reporter.systemOutPrintLn("FAILURE: " + consoleMessage);
        jBehaveTestCase.logEvent(htmlMessage);
    }

    @Override
    public void pending(final String step) {
        String message = "Step is pending: " + step;
        jBehaveTestCase.logPendingRow(message);
        jBehaveTestCase.naTestCase(message);
    }

}
