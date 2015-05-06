package com.epam.gepard.selenium.browsers;

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

import com.epam.gepard.selenium.SeleniumTestCase;
import com.thoughtworks.selenium.Selenium;

/**
 * Utility functions for Selenium.
 * @author Tamas_Kohegyi
 */

public class SeleniumUtil {

    /**
     * Identify whether the current browser is a firefox instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a firefox browser.
     */
    public boolean isFireFox(final String browserString) {
        return browserString.endsWith("*chrome") || browserString.endsWith("*firefox") || browserString.endsWith("*firefoxproxy");
    }

    /**
     * Identify whether the current browser is a safari instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a safari browser.
     */
    public boolean isSafari(final String browserString) {
        return browserString.endsWith("*safari");
    }

    /**
     * Identify whether the current browser is an Internet Explorer instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is an Internet Explorer browser.
     */
    public boolean isInternetExplorer(final String browserString) {
        return browserString.endsWith("*iexplore") || browserString.endsWith("*iehta") || browserString.endsWith("*iexploreproxy");
    }

    /**
     * Identify whether the current browser is a Google Chrome instance.
     *
     * @param browserString is the string used to identify the browser
     * @return true, if the current browser is a Google Chrome browser.
     */
    public boolean isGoogleChrome(final String browserString) {
        return browserString.endsWith("*googlechrome");
    }

    /**
     * Detect the actual browser type.
     *
     * @param tc is the caller test case
     * @return with the (enumerated) browser type.
     */
    public BrowserEnum getBrowserType(final SeleniumTestCase tc) {
        String browserString = tc.getBrowserString();
        return getBrowserType(browserString);
    }

    /**
     * Detect the actual browser type.
     *
     * @param browserString is the used browser string in the test class
     * @return with the (enumerated) browser type.
     */
    public BrowserEnum getBrowserType(final String browserString) {
        BrowserEnum type = BrowserEnum.Unknown;
        if (isFireFox(browserString)) {
            type = BrowserEnum.FireFox;
        }
        if (isSafari(browserString)) {
            type = BrowserEnum.Safari;
        }
        if (isInternetExplorer(browserString)) {
            type = BrowserEnum.InternetExplorer;
        }
        if (isGoogleChrome(browserString)) {
            type = BrowserEnum.GoogleChrome;
        }
        return type;
    }

    /**
     * Create the BrowserHandlerBase according to the browser type.
     *
     * @param tc The test case class.
     * @return with the BrowserUtil.
     */
    public BrowserHandlerBase getBrowserUtil(SeleniumTestCase tc) {
        BrowserEnum browser = getBrowserType(tc);

        BrowserHandlerBase retVal;
        switch (browser) {
        case FireFox:
            retVal = new FirefoxBrowserHandler(tc);
            break;
        case InternetExplorer:
            retVal = new InternetExplorerBrowserHandler(tc);
            break;
        case Safari:
            retVal = new SafariBrowserHandler(tc);
            break;
        case GoogleChrome:
            retVal = new ChromeBrowserHandler(tc);
            break;
        default:
            throw new IllegalStateException("Unknown browser type [" + browser + "], handler class could not be created.");
        }

        return retVal;
    }

    /**
     * Opens to a specific URL.
     * Uses timeout of fix 30 seconds.
     *
     * @param tc is the caller Test Case
     * @param url is the URL the browser should go.
     */
    public void gotoUrl(final SeleniumTestCase tc, final String url) {
        tc.logComment("Open URL: " + url);
        tc.getSelenium().open(url);
        waitPageLoad(tc.getSelenium());
        tc.logEvent("Page loaded", true);
    }

    /**
     * Click on a specific button on the page.
     *
     * @param tc is the caller Test Case
     * @param path is the path to the button
     */
    public void clickButton(final SeleniumTestCase tc, final String path) {
        tc.logComment("Click Button: " + path);
        tc.getSelenium().focus(path);
        tc.getSelenium().click(path);
        waitPageLoad(tc.getSelenium());
        tc.logEvent("Page updated.", true);
    }

    /**
     * Click on a specific link on the page.
     *
     * @param tc is the caller Test Case
     * @param path is the path to the link
     */
    public void clickLink(final SeleniumTestCase tc, final String path) {
        tc.logComment("Click Link: " + path);
        tc.getSelenium().focus(path);
        tc.getSelenium().click(path);
        waitPageLoad(tc.getSelenium());
        tc.logEvent("Page updated.", true);
    }

    /**
     * Set an input text field by calling keydown/keyup events.
     *
     * @param tc is the caller Test Case
     * @param path  is the path to the text field
     * @param value is the value to be set
     */
    public void setInputFieldWithEvents(final SeleniumTestCase tc, final String path, final String value) {
        tc.logComment("Set text value of input field (keydown/up): " + path + " to:" + value);
        tc.getSelenium().focus(path);
        tc.getSelenium().typeKeys(path, value);
    }

    /**
     * Set an input text field.
     *
     * @param tc is the caller Test Case
     * @param path  is the path to the text field
     * @param value is the value to be set
     */
    public void setInputField(final SeleniumTestCase tc, final String path, final String value) {
        tc.logComment("Set text value of input field: " + path + " to:" + value);
        tc.getSelenium().focus(path);
        tc.getSelenium().type(path, value);
    }

    /**
     * Select a field.
     *
     * @param tc is the caller Test Case
     * @param path  is the path to the text field
     * @param value is the value to be set
     */
    public void setSelectField(final SeleniumTestCase tc, final String path, final String value) {
        tc.logComment("Set select field: " + path + " to:" + value);
        tc.getSelenium().focus(path);
        tc.getSelenium().select(path, value);
    }

    /**
     * Wait for a page to be loaded.
     * Wait is limited with a fix timeout: 30 sec.
     *
     * @param selenium is the test server.
     */
    public void waitPageLoad(final Selenium selenium) {
        selenium.waitForPageToLoad("30000");
    }

    /**
     * Wait for a page to be loaded.
     * Wait is limited with a given timeout.
     *
     * @param selenium  is the test server.
     * @param milliseconds is the timeout in usec.
     */
    public static void waitPageLoadExtended(final Selenium selenium, final String milliseconds) {
        selenium.waitForPageToLoad(milliseconds);
    }

}
