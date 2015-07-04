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

import com.epam.gepard.util.Util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class CookieHandler. This is the place for utility methods related to cookie handling.
 * <p/>
 * Keep in mind the: Same Origin Policy. Javascript can only manipulate cookies, those are from the same
 * (current) domain. So you can only delete / create cookies for the actual domain.
 *
 * @author Istvan_Pamer, Robert_Ambrus, Tamas_Kohegyi
 */
public class CookieHandler {

    private static final String COOKIE_SEPARATOR = "; ";

    private String[] domains;
    private Util util = new Util();

    /**
     * Instantiates a new cookie handler.
     */
    CookieHandler(final String[] domains) {
        setDomains(domains);
    }

    /**
     * Delete all cookies on domain.
     *
     * @param wdu parameter specifies the Web Driver Util object
     */
    public void deleteAllCookiesOnDomain(final WebDriverUtil wdu) {
        wdu.getSelenium().deleteAllVisibleCookies();
        wdu.getGepardTestClass().logComment("[CookieHandler] All the cookies deleted for the current domain.");
    }

    public void setDomains(final String[] domains) {
        this.domains = domains;
    }

    /**
     * Delete cookie on domain.
     *
     * @param wdu  parameter specifies the Test Case object
     * @param name the name
     */
    public void deleteCookieOnDomain(final WebDriverUtil wdu, String name) {
        wdu.getSelenium().deleteCookie(name, "path=/, domain=" + getDomain(wdu));
    }

    private String getDomain(final WebDriverUtil wdu) {
        return wdu.getSelenium().getLocation().replaceAll(".*://[^.]{2,3}", "").replaceAll("\\/.*", "");
    }

    /**
     * Updates a cookie. First calls the {@link #deleteCookieOnDomain(WebDriverUtil wdu, String name)},
     * then the {@link #createCookie(WebDriverUtil, String, String)} method with the given parameters.
     *
     * @param wdu   the TestCase
     * @param name  the name
     * @param value the value
     */
    public void updateCookie(final WebDriverUtil wdu, String name, String value) {
        deleteCookieOnDomain(wdu, name);
        createCookie(wdu, name, value);
    }

    /**
     * Creates the cookie.
     *
     * @param wdu     the TestCase
     * @param name   the name
     * @param value  the value
     * @param maxAge the max age
     */
    public void createCookie(final WebDriverUtil wdu, String name, String value, String maxAge) {
        wdu.getSelenium().createCookie(name + "=" + value, MessageFormat.format("path=/, max_age={0}, domain={1}", maxAge, getDomain(wdu)));
    }

    /**
     * Creates the cookie.
     *
     * @param wdu   the TestCase
     * @param name  the name
     * @param value the value
     */
    public void createCookie(final WebDriverUtil wdu, String name, String value) {
        createCookie(wdu, name, value, "3600");
    }

    /**
     * Gets the cookie.
     *
     * @param wdu the tc
     * @return the cookie
     */
    public String getCookie(final WebDriverUtil wdu) {
        return wdu.getSelenium().getCookie();
    }

    /**
     * Gets all the cookies for the opened page.
     *
     * @param wdu is the test case object
     * @return with the cookies
     */
    public Map<String, String> getAllCookies(WebDriverUtil wdu) {
        Map<String, String> retval = new HashMap<String, String>();

        try {
            String[] cookies = wdu.getSelenium().getCookie().split(COOKIE_SEPARATOR);
            for (String cookie : cookies) {

                String key = util.parseData(cookie, "^(.*?)=");
                String value = util.parseData(cookie, "=\"?(.*?)\"?$");
                retval.put(key, value);
            }
        } catch (Exception e) {
            wdu.getGepardTestClass().logWarning("Unable to get cookies!");
        }

        return retval;
    }

    /**
     * Gets the cookie by name.
     *
     * @param wdu   the tc
     * @param name the name
     * @return the cookie by name
     */
    public static String getCookieByName(final WebDriverUtil wdu, String name) {
        String cookie = "";
        try {
            cookie = wdu.getSelenium().getCookieByName(name);
        } catch (Exception e) {
            wdu.getGepardTestClass().logWarning("[CookieHandler][getCookieByName] No such cookie: " + name);
        }
        return cookie;
    }
}
