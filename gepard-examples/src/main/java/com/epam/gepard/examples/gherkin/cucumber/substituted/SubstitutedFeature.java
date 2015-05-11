package com.epam.gepard.examples.gherkin.cucumber.substituted;
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
import cucumber.api.java.en.When;

/**
 * Glue code for the parent cucumber test case.
 */
@ParentCucumberTestCase(name = "com.epam.gepard.examples.gherkin.cucumber.substituted.SubstitutedGherkinTest")
public class SubstitutedFeature extends CucumberTestCaseConnector {

    /**
     * Sample Cucumber glue code.
     * @param nameSource is the name parameter
     */
    @Given("^I have a user account with my name \"([^\"]*)\"")
    public void iHaveAUserAccountWithMy(String nameSource) {
        logComment("(Given) I have a user account with param = " + nameSource);
    }

    /**
     * Sample Cucumber glue code.
     */
    @Given("valami")
    public void iHaveAUserAccountWithMy() {
        logComment("(Given) valami");
    }

    /**
     * Sample Cucumber glue code.
     * @param nameSource is the name parameter
     */
    @When("^an Admin grants me ([^\"]*) rights$")
    public void whenAnAdminGrantsMe(String nameSource) {
        logComment("(When) Admin grants param = " + nameSource);
    }

    /**
     * Sample Cucumber glue code.
     * @param markdown is the body parameter
     */
    @Then("^I should receive an email with the body:$")
    public void iShouldReceiveAnEmail(String markdown) {
        logComment("(Then) I should receive mail: " + markdown);
    }
}
