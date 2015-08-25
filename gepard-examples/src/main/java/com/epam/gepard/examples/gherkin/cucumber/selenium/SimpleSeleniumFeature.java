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
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.selenium.browsers.WebDriverUtil;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.junit.Assert;

//import com.epam.gepard.gherkin.cucumber.ParentCucumberTestCase;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Glue code for the parent cucumber test case.
 * Both feature and glue code is ok, test should pass.
 */
public class SimpleSeleniumFeature implements GepardTestClass {

    private WebDriverUtil webDriverUtil = new WebDriverUtil(this);

    /**
     * Be aware that this is the cucumber @Before method, not the JUnit @Before method.
     */
    @Before
    public void buildWebDriverInstance() {
        webDriverUtil.buildWebDriverInstance("http://www.epam.com");
    }

    /**
     * Be aware that this is the cucumber @After method, not the JUnit @After method.
     */
    @After
    public void destroyWebDriverInstance() {
        webDriverUtil.destroyWebDriverInstance();
    }

    /**
     * Sample Cucumber glue code, this part pass.
     */
    @Given("^I have access to Selenium$")
    public void iHaveAccessToSelenium() {
        Assert.assertNotNull(webDriverUtil);
    }

    /**
     * Glue code for: I visit page: xxx.
     * @param url is the xxx
     */
    @When("^I visit page: '(.+)'$")
    public void iVisitPageUrl(String url) {
        logComment("Open URL: " + url);
        webDriverUtil.getWebDriver().get(url);
        webDriverUtil.logEvent("Page loaded", true);
    }

    /**
     * Glue code for: I should find in title: xxx.
     * @param titlePart is the xxx
     */
    @Then("^I should find in title: '(.+)'$")
    public void iShouldFindInTitlePart(String titlePart) {
        String title = webDriverUtil.getWebDriver().getTitle();
        Assert.assertTrue("Page title differs from what expected: " + titlePart + ", meanwhile found:" + title, title.contains(titlePart));
    }

}
