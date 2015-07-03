package com.epam.gepard.examples.gherkin.jbehave;

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

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.common.Environment;
import com.epam.gepard.exception.SimpleGepardException;
import com.epam.gepard.gherkin.jbehave.JBehaveTestCase;
import com.epam.gepard.logger.HtmlRunReporter;
import com.epam.gepard.rest.jira.JiraSiteHandler;
import com.epam.gepard.selenium.browsers.WebDriverUtil;
import com.epam.gepard.util.FileUtil;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Test in order to show how to use JIRA Description field as source of BDD feature information,
 * and run such scenario with JBehave. It requires running Selenium Server/grid, and proper preparation regarding:
 * - in FeatureFileFromJiraTicket.csv should contain an accessible JIRA ticket with Description field with this content:
 * Well, this is an example feature file within a JIRA description.
 * The only thing you nee to take care is the marker that shows exactly where the Feature file is starting.
 * {noformat}
 * Feature: Connect JIRA and BDD test together
 * Scenario: Check Single Title
 * Given we are at page: "PAGE_START"
 * When open "PAGE_NEW"
 * Then actual page title is "PAGE_TITLE"
 * Examples:
 * |  PAGE_START | PAGE_NEW | PAGE_TITLE |
 * |  http://www.epam.com | http://www.epam.com | EPAM &#x7c; Software Product Development Services |
 * |  http://www.epam.com |http://epam.github.io/Gepard/ | Gepard |
 * {noformat}
 * - also jira.site.* properties in gepard.properties file should be properly set (valid JIRA url, username and password)
 *
 * @author tkohegyi
 */
@TestClass(id = "JBehave Test", name = "Feature file is coming from JIRA ticket")
public class FeatureFileFromJiraTicket extends JBehaveTestCase {

    private WebDriverUtil webDriverUtil;
    private FileUtil fileUtil;
    private JiraSiteHandler jiraSiteHandler;
    private String jiraTicket = getDataDrivenTestParameter(0);

    /**
     * Be aware that this is the JUnit @Before method.
     */
    @Before
    public void buildSupportEnvironment() {
        fileUtil = new FileUtil();
        jiraSiteHandler = new JiraSiteHandler(this, getTestClassExecutionData().getEnvironment());
        webDriverUtil = new WebDriverUtil(this);
        webDriverUtil.buildWebDriverInstance("http://www.epam.com");
    }

    /**
     * Be aware that this is the JUnit @After method.
     */
    @After
    public void destroySupportEnvironment() {
        webDriverUtil.destroyWebDriverInstance();
    }

    @Override
    protected String getStoryPath() {
        Environment e = getTestClassExecutionData().getEnvironment();
        HtmlRunReporter htmlRunReporter = getTestClassExecutionData().getHtmlRunReporter();
        String fieldValue;
        String featureFilename;
        try {
            //get feature info
            fieldValue = jiraSiteHandler.getTicketFieldValue(this, jiraTicket, "description");
            int startPos = fieldValue.indexOf("Feature:");
            int endPos = fieldValue.lastIndexOf("{noformat}");
            if (startPos < 0 || endPos <= 0) {
                throw new SimpleGepardException("Cannot identify feature part in ticket: " + jiraTicket);
            }
            fieldValue = fieldValue.substring(startPos, endPos);
            //save it to file
            String featureFullFilename = htmlRunReporter.formatPathName(new java.io.File(".").getCanonicalPath())
                    + "/" + e.getProperty(Environment.GEPARD_HTML_RESULT_PATH)
                    + "/" + getTestClassExecutionData().getHtmlRunReporter().getTestURL() + "." + jiraTicket + ".feature";
            featureFilename = featureFullFilename.substring(featureFullFilename.lastIndexOf("/") + 1);
            String featurePath = featureFullFilename.substring(0, featureFullFilename.lastIndexOf("/"));
            File file = new File(featureFullFilename);
            File path = new File(featurePath);
            fileUtil.writeToFile(fieldValue, file);
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{path.toURI().toURL()});
        } catch (JSONException | IOException ex) {
            throw new SimpleGepardException("Connect to JIRA failed.", ex);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            throw new SimpleGepardException("Transferring Feature info to JBehave failed.", ex);
        }
        return featureFilename;
    }

    /**
     * Generic test method, must present in every JBehave test.
     */
    @Test
    public void runJBehaveExampleTest() {
        testRunJBehave();
    }

    /**
     * Code for: Given we are at page [PAGE_START].
     * @param pageStart is the first web page to be loaded.
     */
    @Given("we are at page $PAGE_START")
    public void givenGoToPage(@Named("PAGE_START") final String pageStart) {
        openPage(pageStart);
    }

    /**
     * Code for: When open [PAGE_NEW].
     * @param pageNew is the new url to be opened
     */
    @When("open $PAGE_NEW")
    public void openPage(@Named("PAGE_NEW") final String pageNew) {
        logComment("Open URL: " + pageNew);
        webDriverUtil.getWebDriver().get(pageNew);
        webDriverUtil.logEvent("Page loaded", true);
    }

    /**
     * Code for: Then actual page title is [PAGE_TITLE].
     * @param pageTitle is the expected title of the page
     */
    @Then("actual page title is $PAGE_TITLE")
    public void checkActualPageTitle(@Named("PAGE_TITLE") final String pageTitle) {
        String title = webDriverUtil.getWebDriver().getTitle();
        Assert.assertTrue("Page title differs from what expected: " + pageTitle + ", meanwhile found:" + title, title.contains(pageTitle));
    }

}
