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

/**
 * This browser utility class contains all the Chrome specific methods.
 *
 * @author tkohegyi
 */
public class ChromeBrowserHandler extends BrowserHandlerBase {
    /**
     * Constructor of the class.
     *
     * @param tc parameter specifies the Test Class object
     * @param wdu parameter specifies the WebDriver Util object
     */
    public ChromeBrowserHandler(final GepardTestClass tc, final WebDriverUtil wdu) {
        super(tc, wdu);
    }
}
