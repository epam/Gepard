package com.epam.gepard.examples.selenium;
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
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.selenium.browsers.WebDriverUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 * This is an example test case for Selenium extension of Gepard.
 *
 * @author tkohegyi
 */
@TestClass(id = "SELENIUM", name = "Basic Selenium Test")
public class BasicSeleniumTest implements GepardTestClass {

    private WebDriverUtil webDriverUtil = new WebDriverUtil(this);

    @Before
    public void buildWebDriverInstance() {
        webDriverUtil.buildWebDriverInstance("http://www.epam.com");
    }

    @After
    public void destroyWebDriverInstance() {
        webDriverUtil.destroyWebDriverInstance();
    }

    @Test
    public void testGoogleMainPage() {
        Environment e = getTestClassExecutionData().getEnvironment();
        String seleniumHostPort = e.getProperty(WebDriverUtil.SELENIUM_HOST) + ":" + e.getProperty(WebDriverUtil.SELENIUM_PORT);
        logComment("Using Selenium at: " + seleniumHostPort);
        webDriverUtil.gotoUrl("http://google.hu");
        WebDriver wd = webDriverUtil.getWebDriver();
        logComment("We are at: " + wd.getTitle());
    }

}
