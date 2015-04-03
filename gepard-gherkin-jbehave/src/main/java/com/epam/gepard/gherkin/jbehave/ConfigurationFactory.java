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

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;

import com.epam.gepard.gherkin.jbehave.helper.PendingStepStrategyFactory;
import com.epam.gepard.gherkin.jbehave.helper.StoryParserFactory;
import com.epam.gepard.gherkin.jbehave.helper.StoryReporterBuilderFactory;

/**
 * Creates the {@link Configuration} for the Embedder.
 * @author Adam_Csaba_Kiraly
 */
public class ConfigurationFactory {
    private final StoryParserFactory storyParserFactory = new StoryParserFactory();
    private final StoryReporterBuilderFactory storyReporterBuilderFactory = new StoryReporterBuilderFactory();
    private final PendingStepStrategyFactory pendingStepStrategyFactory = new PendingStepStrategyFactory();

    /**
     * Creates the {@link Configuration} for the Embedder.
     * @param testCase the testCase that will be run by the Embedder.
     * @return the configuration
     */
    public Configuration create(final JBehaveTestCase testCase) {
        Configuration configuration = new MostUsefulConfiguration();
        GherkinStoryParser gherkinStoryParser = storyParserFactory.createGherkinStoryParser();
        configuration.useStoryParser(gherkinStoryParser);
        StoryReporterBuilder storyReporterBuilder = storyReporterBuilderFactory.createForTestCase(testCase);
        configuration.useStoryReporterBuilder(storyReporterBuilder);
        FailingUponPendingStep pendingStepStrategy = pendingStepStrategyFactory.createFailingUponPendingStep();
        configuration.usePendingStepStrategy(pendingStepStrategy);
        return configuration;
    }
}
