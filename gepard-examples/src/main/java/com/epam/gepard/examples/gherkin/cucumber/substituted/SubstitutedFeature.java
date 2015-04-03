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
     */
    @Given("^I have a user account with my name \"([^\"]*)\"")
    //CHECKSTYLE.OFF
    public void I_have_a_user_account_with_my(String nameSource) {
        //CHECKSTYLE.ON
        logComment("(Given) I have a user account with param = " + nameSource);
    }

    /**
     * Sample Cucumber glue code.
     */
    @Given("valami")
    //CHECKSTYLE.OFF
    public void I_have_a_user_account_with_my() {
        //CHECKSTYLE.ON
        logComment("(Given) valami");
    }

    /**
     * Sample Cucumber glue code.
     */
    @When("^an Admin grants me ([^\"]*) rights$")
    //CHECKSTYLE.OFF
    public void When_an_Admin_grants_me(String nameSource) {
        //CHECKSTYLE.ON
        logComment("(When) Admin grants param = " + nameSource);
    }

    /**
     * Sample Cucumber glue code.
     */
    @Then("^I should receive an email with the body:$")
    //CHECKSTYLE.OFF
    public void I_should_receive_an_email(String markdown) {
        //CHECKSTYLE.ON
        logComment("(Then) I should receive mail: " + markdown);
    }
}
