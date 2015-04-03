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

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.steps.InstanceStepsFactory;

import com.epam.gepard.gherkin.jbehave.JBehaveTestCase;

/**
 * Factory class for {@link InstanceStepsFactory}.
 * @author Adam_Csaba_Kiraly
 */
public class InstanceStepsFactoryCreator {

    /**
     * Creates an {@link InstanceStepsFactory} with the given {@link Configuration} for the given {@link com.epam.gepard.gherkin.jbehave.JBehaveTestCase}.
     * @param configuration the configuration to use
     * @param testCase the test case which will be used in the step creation process
     * @return a new instance of {@link InstanceStepsFactory}
     */
    public InstanceStepsFactory createForTestCase(final Configuration configuration, final JBehaveTestCase testCase) {
        return new InstanceStepsFactory(configuration, testCase);
    }

}
