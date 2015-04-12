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

/**
 * This browser utility class contains all the FireFox specific methods.
 *
 * @author robert_ambrus
 */
public class FirefoxBrowserHandler extends BrowserHandlerBase {
    private static final String SET_JS = "{prefs=Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);"
            + "prefs.setBoolPref('javascript.enabled', " + "%STATUS%" + ");}";
    private static final String GET_JS = "{prefs=Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);"
            + "prefs.getBoolPref('javascript.enabled');}";

    /**
     * Constructor of the class.
     *
     * @param tc parameter specifies the Test Case object
     */
    public FirefoxBrowserHandler(SeleniumTestCase tc) {
        super(tc);
    }

    /**
     * Restart the browser with the specified settings.
     *
     * @param clearCookies      parameter specifies whether clear cookies
     * @param javascriptSupport parameter specifies whether enable javascript support
     */
    @Override
    public void restartBrowser(final CookieHandler cookieHandler, final boolean clearCookies, final boolean javascriptSupport) {
        //clear domain cookies if necessary
        if (clearCookies) {
            cookieHandler.deleteAllCookiesOnDomain(getTc());
        }

        //restart browser
        getTc().restartBrowser();

        //set javascript support
        setJavascriptSupport(javascriptSupport);
    }

    /**
     * Set the JavaScript support in the browser.
     *
     * @param status parameter specifies the status to set
     */
    @Override
    protected void setJavascriptSupport(boolean status) {
        if (getJavascriptSupport() == status) {
            getTc().logComment("Browser JavaScript support status [" + status + "] already set.");
            return;
        }

        getTc().getSelenium().getEval(SET_JS.replace("%STATUS%", Boolean.toString(status)));

        getTc().logComment("Browser Javascript support status [" + status + "] set.");
    }

    /**
     * Get the JavaScript support status in the browser.
     *
     * @return with the current status
     */
    @Override
    public boolean getJavascriptSupport() {
        return Boolean.parseBoolean(getTc().getSelenium().getEval(GET_JS));
    }
}
