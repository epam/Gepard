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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.epam.gepard.gherkin.jbehave.helper.PendingStepStrategyFactory;
import com.epam.gepard.gherkin.jbehave.helper.StoryParserFactory;
import com.epam.gepard.gherkin.jbehave.helper.StoryReporterBuilderFactory;

/**
 * Unit test for {@link ConfigurationFactory}.
 * @author Adam_Csaba_Kiraly
 */
public class ConfigurationFactoryTest {

    @Mock
    private StoryParserFactory storyParserFactory;
    @Mock
    private StoryReporterBuilderFactory storyReporterBuilderFactory;
    @Mock
    private PendingStepStrategyFactory pendingStepStrategyFactory;

    private ConfigurationFactory underTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        underTest = new ConfigurationFactory();
        Whitebox.setInternalState(underTest, "storyParserFactory", storyParserFactory);
        Whitebox.setInternalState(underTest, "storyReporterBuilderFactory", storyReporterBuilderFactory);
        Whitebox.setInternalState(underTest, "pendingStepStrategyFactory", pendingStepStrategyFactory);
    }

    @Test
    public void testCreateShouldCreateConfiguration() {
        //GIVEN
        JBehaveTestCase testCase = new JBehaveTestCase() {
            @Override
            protected String getStoryPath() {
                return "something";
            }
        };
        GherkinStoryParser gherkinStoryParser = new GherkinStoryParser();
        StoryReporterBuilder storyReporterBuilder = new StoryReporterBuilder().withReporters(new JBehaveStoryReporter(testCase));
        FailingUponPendingStep pendingStepStrategy = new FailingUponPendingStep();
        given(storyParserFactory.createGherkinStoryParser()).willReturn(gherkinStoryParser);
        given(pendingStepStrategyFactory.createFailingUponPendingStep()).willReturn(pendingStepStrategy);
        given(storyReporterBuilderFactory.createForTestCase(testCase)).willReturn(storyReporterBuilder);
        //WHEN
        Configuration result = underTest.create(testCase);
        //THEN
        assertEquals(gherkinStoryParser, result.storyParser());
        assertEquals(pendingStepStrategy, result.pendingStepStrategy());
        assertEquals(storyReporterBuilder, result.storyReporterBuilder());
    }
}
