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
import com.epam.gepard.selenium.SeleniumTestCase;
import com.epam.gepard.selenium.annotation.GepardSeleniumTestClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

/**
 * This is an example test case for Selenium extension of Gepard.
 *
 * @author tkohegyi
 */
@TestClass(id = "SELENIUM", name = "Basic Selenium Test")
@GepardSeleniumTestClass(baseUrl = "http://www.google.com", browser = "*firefox")
public class BasicSeleniumTest extends SeleniumTestCase {

    @Test
    public void testGoogleMainPage() {
        String seleniumHost = getEnvironmentHelper().getProperty(SeleniumTestCase.SELENIUM_HOST);
        String baseUrl = getBaseURL();
        logComment("Using Selenium Host:" + seleniumHost);
        logComment("Using Default Base Url:" + baseUrl);
        getSeleniumUtil().gotoUrl(this, "http://google.hu");
        WebDriver wd = getWebDriver();
        logComment("We are at: " + wd.getTitle());
    }

}
