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
import cucumber.api.java.en.Then;
import org.junit.Assert;


/**
 * Glue code for the parent cucumber test case.
 * Glue code is good, test fails on When statement.
 */
@ParentCucumberTestCase(name = "com.epam.gepard.examples.gherkin.cucumber.alltests.CombinedCucumberTest")
public class BrokenFeature extends CucumberTestCaseConnector {

    /**
     * Sample Cucumber glue code, this part pass.
     */
    @Given("Something else")
    public void iHaveCukesInMyBelly() {
        logComment("(Given) Something else");
    }

    /**
     * Sample Cucumber glue code, this part should fail with assertion error.
     */
    @Then("Assertion failure")
    public void iWantToFail() {
        logComment("(Then) I want to fail");
        Assert.assertFalse(true);
    }
}
