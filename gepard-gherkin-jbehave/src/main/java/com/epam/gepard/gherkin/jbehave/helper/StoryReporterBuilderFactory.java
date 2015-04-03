package com.epam.gepard.gherkin.jbehave.helper;

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

import java.net.URL;

import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.reporters.StoryReporterBuilder;

import com.epam.gepard.gherkin.jbehave.JBehaveStoryReporter;
import com.epam.gepard.gherkin.jbehave.JBehaveTestCase;

/**
 * Factory class for {@link StoryReporterBuilder}.
 * @author Adam_Csaba_Kiraly
 */
public class StoryReporterBuilderFactory {

    /**
     * Creates a new {@link StoryReporterBuilder} using {@link com.epam.gepard.gherkin.jbehave.JBehaveStoryReporter}.
     * @param testCase the test case to use for reporting
     * @return the new instance
     */
    public StoryReporterBuilder createForTestCase(final JBehaveTestCase testCase) {
        URL codeLocation = CodeLocations.codeLocationFromPath("");
        return new StoryReporterBuilder().withCodeLocation(codeLocation).withRelativeDirectory("").withReporters(new JBehaveStoryReporter(testCase));
    }
}
