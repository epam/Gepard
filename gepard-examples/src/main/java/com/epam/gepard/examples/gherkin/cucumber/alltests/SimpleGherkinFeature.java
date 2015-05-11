package com.epam.gepard.examples.gherkin.cucumber.alltests;
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
import com.epam.gepard.gherkin.cucumber.CucumberTestCaseConnector;
import com.epam.gepard.gherkin.cucumber.ParentCucumberTestCase;
import cucumber.api.java.en.Given;

/**
 * Glue code for the parent cucumber test case.
 * Both feature and glue code is ok, test should pass.
 */
@ParentCucumberTestCase(name = "com.epam.gepard.examples.gherkin.cucumber.alltests.CombinedCucumberTest")
public class SimpleGherkinFeature extends CucumberTestCaseConnector {

    /**
     * Sample Cucumber glue code, this part pass.
     * @param cukes is the number of cukes
     */
    @Given("I have (\\d+) cukes in my belly")
    public void iHaveCukesInMyBelly(int cukes) {
        logComment("(Given) given I have " + cukes + " cukes in my belly");
    }

}
