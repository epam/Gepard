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

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.selenium.annotation.GepardSeleniumTestClass;

/**
 * Wrapper Junit test for Cucumber glue code placed next to this class.
 * Many such glue code presents next to this class.
 */
@GepardSeleniumTestClass(baseUrl = "http://www.google.com", browser = "*firefox")
@TestClass(id = "Cuke + Selenium", name = "Combined Cucumber and Selenium Tests")
public class CucumberWithSeleniumTest extends CucumberWrappedSeleniumTestCase {
}
