package com.epam.gepard.examples.gherkin.cucumber.selenium;

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

import com.epam.gepard.generic.GenericListTestSuite;
import com.epam.gepard.gherkin.cucumber.ParentCucumberTestCase;
import com.epam.gepard.gherkin.cucumber.helper.CucumberError;

/**
 * This class is the base class of every glue code, and ensures connectivity between Gepard and the glue code.
 */
public class CucumberAndSeleniumTestCaseConnector {

    private CucumberWrappedSeleniumTestCase testCase;

    /**
     * Constructor for the connector class.
     * It established connection between the called JUnit test method and the glue code executed by Cucumber.
     */
    public CucumberAndSeleniumTestCaseConnector() {
        Class parentClass = this.getClass();
        String searchingForParentClass = parentClass.getCanonicalName(); // this is wrong anyway

        if (parentClass.isAnnotationPresent(ParentCucumberTestCase.class)) { //if not, we should fail the test! (N/A)
            searchingForParentClass = ((ParentCucumberTestCase) parentClass.getAnnotation(ParentCucumberTestCase.class)).name();
        } else {
            throw new CucumberError("Test Code ERROR: Missing ParentCucumberTestCase annotation at " + searchingForParentClass);
        }
        testCase = (CucumberWrappedSeleniumTestCase) GenericListTestSuite.getGlobalDataStorage().get("class " + searchingForParentClass);
        if (testCase == null) {
            throw new CucumberError("Test Code ERROR: Cucumber connection between " + searchingForParentClass
                    + " and CucumberTestCaseConnector class failed.");
        }
    }

    /**
     * Write a comment message to the log, without the step number.
     *
     * @param comment Comment message
     */
    public void logComment(final String comment) {
        testCase.logComment(comment);
    }

    public CucumberWrappedSeleniumTestCase getTestCase() {
        return testCase;
    }
}
