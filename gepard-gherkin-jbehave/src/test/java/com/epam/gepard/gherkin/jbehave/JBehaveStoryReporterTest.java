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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jbehave.core.model.Story;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.epam.gepard.gherkin.PendingException;
import com.epam.gepard.gherkin.jbehave.helper.IOUtils;
import com.epam.gepard.gherkin.jbehave.helper.ResourceProvider;

/**
 * Unit test for {@link JBehaveStoryReporter}.
 * @author Adam_Csaba_Kiraly
 */
public class JBehaveStoryReporterTest {

    private static final String AFTER_STORIES_PATH = "AfterStories";
    private static final String BEFORE_STORIES_PATH = "BeforeStories";
    @Mock
    private JBehaveTestCase jBehaveTestCase;
    @Mock
    private IOUtils ioUtils;
    @Mock
    private ResourceProvider resourceProvider;

    @InjectMocks
    private JBehaveStoryReporter underTest;

    @Mock
    private InputStream inputStream;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "ioUtils", ioUtils);
        Whitebox.setInternalState(underTest, "resourceProvider", resourceProvider);
        given(ioUtils.toString(Mockito.any(InputStream.class))).willReturn("something");
    }

    @Test
    public void testBeforeStoryShouldNotLogAfterStories() {
        //GIVEN
        Story story = new Story(AFTER_STORIES_PATH);
        //WHEN
        underTest.beforeStory(story, false);
        //THEN
        verify(jBehaveTestCase, never()).logComment(Mockito.anyString());
    }

    @Test
    public void testBeforeStoryShouldNotLogBeforeStories() {
        //GIVEN
        Story story = new Story(BEFORE_STORIES_PATH);
        //WHEN
        underTest.beforeStory(story, false);
        //THEN
        verify(jBehaveTestCase, never()).logComment(Mockito.anyString());
    }

    @Test
    public void testBeforeStoryShouldLogStoryAsComment() {
        //GIVEN
        Story story = new Story("path");
        given(resourceProvider.getResourceAsStream("path")).willReturn(inputStream);
        //WHEN
        underTest.beforeStory(story, false);
        //THEN
        verify(jBehaveTestCase).logComment("Using story file: path", "<pre>something</pre>");
    }

    @Test
    public void testBeforeStoryShouldReportWhenStoryDoesNotExist() {
        //GIVEN
        Story story = new Story("fakepath");
        //WHEN
        underTest.beforeStory(story, false);
        //THEN
        verify(jBehaveTestCase).logComment("Using story file: fakepath", "<pre>File not found: fakepath</pre>");
    }

    @Test
    public void testBeforeStoryShouldReporttWhenIOExceptionOccurs() throws IOException {
        //GIVEN
        Story story = new Story("path");
        given(ioUtils.toString(Mockito.any(InputStream.class))).willThrow(new IOException());
        given(resourceProvider.getResourceAsStream("path")).willReturn(inputStream);
        //WHEN
        underTest.beforeStory(story, false);
        //THEN
        verify(jBehaveTestCase).logComment("Using story file: path", "<pre>Could not read file: path</pre>");
    }

    @Test
    public void testBeforeScenarioShouldLogToHtml() {
        //GIVEN
        String scenarioTitle = "title";
        //WHEN
        underTest.beforeScenario(scenarioTitle);
        //THEN
        verify(jBehaveTestCase).logComment("Scenario: title");
    }

    @Test
    public void testExampleShouldLogToHtml() {
        //GIVEN
        Map<String, String> tableRow = new HashMap<String, String>();
        tableRow.put("a", "1");
        //WHEN
        underTest.example(tableRow);
        //THEN
        verify(jBehaveTestCase).logExampleRow("{a=1}");
    }

    @Test
    public void testSuccessfulShouldLogToHtml() {
        //GIVEN
        String step = "step";
        //WHEN
        underTest.successful(step);
        //THEN
        verify(jBehaveTestCase).logStep("Step successful: step");
    }

    @Test
    public void testNotPerformedShouldLogToHtml() {
        //GIVEN
        String step = "step";
        //WHEN
        underTest.notPerformed(step);
        //THEN
        verify(jBehaveTestCase).logComment("Step not performed: step");
    }

    @Test
    public void testFailedShouldLogToHtml() {
        //GIVEN
        String step = "step";
        //WHEN
        underTest.failed(step, new IOException(new PendingException("bad stuff")));
        //THEN
        verify(jBehaveTestCase).logStep("Step failed: step");
        verify(jBehaveTestCase).systemOutPrintLn("FAILURE: com.epam.gepard.gherkin.PendingException: bad stuff");
        verify(jBehaveTestCase).logEvent("<font color=\"#AA0000\"><b>FAILURE: </b></font>com.epam.gepard.gherkin.PendingException: bad stuff");
    }

    @Test
    public void testPendingShouldLogToHtml() {
        //GIVEN
        String step = "step";
        //WHEN
        underTest.pending(step);
        //THEN
        verify(jBehaveTestCase).logPendingRow("Test is N/A: Step pending: step");
        verify(jBehaveTestCase).naTestCase("Test is N/A: Step pending: step");
    }

    @Test
    public void testPendingShouldSetTestCaseResultToNotApplicable() {
        //GIVEN
        String step = "step";
        //WHEN
        underTest.pending(step);
        //THEN
        verify(jBehaveTestCase).logPendingRow("Test is N/A: Step pending: step");
        verify(jBehaveTestCase).naTestCase("Test is N/A: Step pending: step");
    }

}
