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

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.NullEmbedderMonitor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.epam.gepard.gherkin.jbehave.helper.InstanceStepsFactoryCreator;

/**
 * Unit test for {@link GepardEmbedder}.
 * @author Adam_Csaba_Kiraly
 *
 */
public class GepardEmbedderTest {

    @Mock
    private Embedder embedder;
    @Mock
    private ConfigurationFactory configurationFactory;
    @InjectMocks
    private GepardEmbedder underTest;

    @Mock
    private EmbedderControls embedderControls;
    @Mock
    private JBehaveTestCase testCase;
    @Mock
    private InstanceStepsFactoryCreator instanceStepsFactoryCreator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        given(embedder.embedderControls()).willReturn(embedderControls);
        given(embedderControls.doVerboseFailures(Mockito.anyBoolean())).willReturn(embedderControls);
        given(embedderControls.doGenerateViewAfterStories(Mockito.anyBoolean())).willReturn(embedderControls);
    }

    @Test
    public void testSetupJBehaveEmbedderShouldSetupEmbedderControls() {
        //GIVEN in setup
        //WHEN
        underTest.setupJBehaveEmbedder(testCase);
        //THEN
        verify(embedderControls).doVerboseFailures(true);
        verify(embedderControls).doGenerateViewAfterStories(false);
    }

    @Test
    public void testSetupJBehaveEmbedderShouldSetEmbedderToUseNullEmbedderMonitor() {
        //GIVEN in setup
        //WHEN
        underTest.setupJBehaveEmbedder(testCase);
        //THEN
        verify(embedder).useEmbedderMonitor(Mockito.any(NullEmbedderMonitor.class));
    }

    @Test
    public void testSetupJBehaveEmbedderShouldConfigureInstanceStepsFactory() {
        //GIVEN in setup
        Configuration configuration = Mockito.mock(Configuration.class);
        given(configurationFactory.create(testCase)).willReturn(configuration);
        //WHEN
        underTest.setupJBehaveEmbedder(testCase);
        //THEN
        verify(instanceStepsFactoryCreator).createForTestCase(configuration, testCase);
    }

    @Test
    public void testSetupJBehaveEmbedderConfigureEmbedder() {
        //GIVEN in setup
        Configuration configuration = Mockito.mock(Configuration.class);
        given(configurationFactory.create(testCase)).willReturn(configuration);
        //WHEN
        underTest.setupJBehaveEmbedder(testCase);
        //THEN
        verify(embedder).useConfiguration(configuration);
    }

    @Test
    public void testRunTestShouldRunStoryWithGivenPath() {
        //GIVEN
        String storyPath = "storyPath";
        //WHEN
        underTest.runTest(storyPath);
        //THEN
        verify(embedder).runStoriesAsPaths(asList(storyPath));
    }

    @Test
    public void testRunTestShouldGenerateCrossReference() {
        //GIVEN
        String storyPath = "storyPath";
        //WHEN
        underTest.runTest(storyPath);
        //THEN
        verify(embedder).generateCrossReference();
    }

}
