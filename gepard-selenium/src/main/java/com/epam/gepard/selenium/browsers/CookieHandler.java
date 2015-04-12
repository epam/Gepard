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
     * @param tc parameter specifies the Test Case object
     */
    public void deleteAllCookiesOnDomain(final SeleniumTestCase tc) {
        tc.getSelenium().deleteAllVisibleCookies();
        tc.logComment("[CookieHandler] All the cookies deleted for the current domain.");
    }

    public void setDomains(final String[] domains) {
        this.domains = domains;
    }

    /**
     * Delete cookie on domain.
     *
     * @param tc   parameter specifies the Test Case object
     * @param name the name
     */
    public void deleteCookieOnDomain(final SeleniumTestCase tc, String name) {
        tc.getSelenium().deleteCookie(name, "path=/, domain=" + getDomain(tc));
    }

    private String getDomain(final SeleniumTestCase tc) {
        return tc.getSelenium().getLocation().replaceAll(".*://[^.]{2,3}", "").replaceAll("\\/.*", "");
    }

    /**
     * Updates a cookie. First calls the {@link #deleteCookieOnDomain(SeleniumTestCase tc, String name)},
     * then the {@link #createCookie(SeleniumTestCase, String, String)} method with the given parameters.
     *
     * @param tc    the TestCase
     * @param name  the name
     * @param value the value
     */
    public void updateCookie(final SeleniumTestCase tc, String name, String value) {
        deleteCookieOnDomain(tc, name);
        createCookie(tc, name, value);
    }

    /**
     * Creates the cookie.
     *
     * @param tc     the TestCase
     * @param name   the name
     * @param value  the value
     * @param maxAge the max age
     */
    public void createCookie(final SeleniumTestCase tc, String name, String value, String maxAge) {
        tc.getSelenium().createCookie(name + "=" + value, MessageFormat.format("path=/, max_age={0}, domain={1}", maxAge, getDomain(tc)));
    }

    /**
     * Creates the cookie.
     *
     * @param tc    the TestCase
     * @param name  the name
     * @param value the value
     */
    public void createCookie(final SeleniumTestCase tc, String name, String value) {
        createCookie(tc, name, value, "3600");
    }

    /**
     * Gets the cookie.
     *
     * @param tc the tc
     * @return the cookie
     */
    public String getCookie(final SeleniumTestCase tc) {
        return tc.getSelenium().getCookie();
    }

    /**
     * Gets all the cookies for the opened page.
     *
     * @param tc is the test case object
     * @return with the cookies
     */
    public Map<String, String> getAllCookies(SeleniumTestCase tc) {
        Map<String, String> retval = new HashMap<String, String>();

        try {
            String[] cookies = tc.getSelenium().getCookie().split(COOKIE_SEPARATOR);
            for (String cookie : cookies) {

                String key = util.parseData(cookie, "^(.*?)=");
                String value = util.parseData(cookie, "=\"?(.*?)\"?$");
                retval.put(key, value);
            }
        } catch (Exception e) {
            tc.logWarning("Unable to get cookies!");
        }

        return retval;
    }

    /**
     * Gets the cookie by name.
     *
     * @param tc   the tc
     * @param name the name
     * @return the cookie by name
     */
    public static String getCookieByName(final SeleniumTestCase tc, String name) {
        String cookie = "";
        try {
            cookie = tc.getSelenium().getCookieByName(name);
        } catch (Exception e) {
            tc.logWarning("[CookieHandler][getCookieByName] No such cookie: " + name);
        }
        return cookie;
    }
}
