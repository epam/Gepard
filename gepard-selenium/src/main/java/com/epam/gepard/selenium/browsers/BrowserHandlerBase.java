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
import org.openqa.selenium.Dimension;

/**
 * This abstract browser utility class provides the default implementation of the browser specific methods.
 *
 * @author robert_ambrus, tamas_kohegyi
 */
public abstract class BrowserHandlerBase {
    private SeleniumTestCase tc;

    /**
     * Constructor of the class.
     *
     * @param tc parameter specifies the Test Case object
     */
    public BrowserHandlerBase(SeleniumTestCase tc) {
        this.tc = tc;
    }

    /**
     * Restart the browser with the default settings (clear cookies, enable javascript support).
     * @param domains are the list of domains from where we should take care about the cookies.
     */
    public void restartBrowser(final String[] domains) {
        CookieHandler cookieHandler = new CookieHandler(domains);
        restartBrowser(cookieHandler, true, true);
    }

    /**
     * Restart the browser with the specified settings.
     *
     * @param clearCookies      parameter specifies whether clear cookies
     * @param javascriptSupport parameter specifies whether enable javascript support
     * @param cookieHandler parameter specifies the handler class of the cookies
     */
    public void restartBrowser(final CookieHandler cookieHandler, final boolean clearCookies, final boolean javascriptSupport) {
        //clear domain cookies if necessary
        if (clearCookies) {
            cookieHandler.deleteAllCookiesOnDomain(tc);
        }

        //set javascript support
        setJavascriptSupport(javascriptSupport);

        //restart browser
        tc.restartBrowser();
    }

    /**
     * Get the JavaScript support status in the browser.
     *
     * @return with the current status
     */
    public boolean getJavascriptSupport() {
        tc.logComment("Javascript support adjustment is not available on this browser.");
        return false;
    }

    /**
     * Set the JavaScript support in the browser.
     *
     * @param status parameter specifies the status to set
     */
    protected void setJavascriptSupport(boolean status) {
        tc.logComment("Cannot set Javascript support to '" + status + "', adjustment is not available on this browser.");
    }

    /**
     * Sets the browser width and height according to the argument dimension.
     *
     * @param dimension the dimension to set
     */
    public void setBrowserSize(Dimension dimension) {
        if (dimension == null) {
            tc.getSelenium().windowMaximize();
        } else {
            tc.getSelenium().getEval("window.resizeTo(" + dimension.getWidth() + "," + dimension.getHeight() + "); window.moveTo(0,0);");
        }
    }

    SeleniumTestCase getTc() {
        return tc;
    }
}
