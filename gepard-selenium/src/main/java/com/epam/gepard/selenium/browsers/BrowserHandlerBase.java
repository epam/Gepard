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

import com.epam.gepard.generic.GepardTestClass;
import org.openqa.selenium.Dimension;

/**
 * This abstract browser utility class provides the default implementation of the browser specific methods.
 *
 * @author robert_ambrus, tamas_kohegyi
 */
public abstract class BrowserHandlerBase {
    private WebDriverUtil wdu;
    private GepardTestClass tc;

    /**
     * Constructor of the class.
     *
     * @param tc parameter specifies the Test Class object
     * @param wdu parameter specifies the WebDriver Util object
     */
    public BrowserHandlerBase(final GepardTestClass tc, final WebDriverUtil wdu) {
        this.tc = tc;
        this.wdu = wdu;
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
            wdu.getSelenium().windowMaximize();
        } else {
            wdu.getSelenium().getEval("window.resizeTo(" + dimension.getWidth() + "," + dimension.getHeight() + "); window.moveTo(0,0);");
        }
    }

    GepardTestClass getTc() {
        return tc;
    }
    WebDriverUtil getWebDriverUtil() {
        return wdu;
    }
}
