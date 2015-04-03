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

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.NullEmbedderMonitor;
import org.jbehave.core.steps.InstanceStepsFactory;

import com.epam.gepard.gherkin.jbehave.helper.InstanceStepsFactoryCreator;

/**
 * Wrapper for the {@link Embedder} class.
 * @author Adam_Csaba_Kiraly
 */
public class GepardEmbedder {

    private final Embedder embedder;
    private final ConfigurationFactory configurationFactory;
    private final InstanceStepsFactoryCreator instanceStepsFactoryCreator;

    /**
     * Creates a new instance of {@link GepardEmbedder}.
     * @param embedder the {@link Embedder} to configure
     * @param configurationFactory the {@link ConfigurationFactory} to use
     * @param instanceStepsFactoryCreator the {@link InstanceStepsFactoryCreator} to use
     */
    public GepardEmbedder(final Embedder embedder, final ConfigurationFactory configurationFactory,
            final InstanceStepsFactoryCreator instanceStepsFactoryCreator) {
        this.embedder = embedder;
        this.configurationFactory = configurationFactory;
        this.instanceStepsFactoryCreator = instanceStepsFactoryCreator;
    }

    /**
     * Sets up the {@link Embedder} for use with the given {@link JBehaveTestCase}.
     * @param testCase the given {@link JBehaveTestCase}, this should contain the glue code
     */
    public void setupJBehaveEmbedder(final JBehaveTestCase testCase) {
        embedder.embedderControls().doVerboseFailures(true).doGenerateViewAfterStories(false).useThreads(1);
        embedder.useEmbedderMonitor(new NullEmbedderMonitor());
        Configuration configuration = configurationFactory.create(testCase);
        InstanceStepsFactory instanceStepsFactory = instanceStepsFactoryCreator.createForTestCase(configuration, testCase);
        embedder.useStepsFactory(instanceStepsFactory);
        embedder.useConfiguration(configuration);
    }

    /**
     * Runs the story with the configured {@link Embedder}.
     * @param storyPath the path to the story on classpath
     */
    public void runTest(final String storyPath) {
        try {
            embedder.runStoriesAsPaths(asList(storyPath));
        } finally {
            embedder.generateCrossReference();
        }
    }

}
