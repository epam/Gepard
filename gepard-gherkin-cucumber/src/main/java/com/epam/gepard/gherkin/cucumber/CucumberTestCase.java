package com.epam.gepard.gherkin.cucumber;

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

import com.epam.gepard.generic.GepardTestClass;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import com.epam.gepard.generic.GenericListTestSuite;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * Serves as a parent class for Cucumber based tests.
 * @author tkohegyi
 */
@CucumberOptions(strict = true, monochrome = true)
public abstract class CucumberTestCase implements GepardTestClass {

    /**
     * The entry point to the Cucumber test. Sets up the test case and runs it.
     */
    @Test
    public void testRunCucumber() {
        try {
            Cucumber cucumber = new Cucumber(this.getClass());
            RunNotifier notifier = new RunNotifier();
            notifier.addFirstListener(new CucumberEventListener());
            GenericListTestSuite.getGlobalDataStorage().put(this.getClass().toString(), this);
            cucumber.run(notifier);
        } catch (InitializationError | IOException e) {
            naTestCase("Cucumber Run Initialization Error: " + e.getMessage());
        }
    }

}
