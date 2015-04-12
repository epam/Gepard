package com.epam.gepard.selenium.helper;
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

import com.epam.gepard.common.Environment;

/**
 * This class extends Environment class with methods useful for Web page tests.
 *
 * predefined property names are:
 * - env.TEID.url
 * - env.TEID.url.secure
 * - env.TEID.targetbrowser
 *
 * @author tkohegyi
 */
public class EnvironmentHelper {
    private Environment environment;

    /**
     * Default constructor.
     * @param environment is the parent environment class of Gepard.
     */
    public EnvironmentHelper(Environment environment) {
        this.environment = environment;
    }

    public String getTestEnvironmentURL() {
        return environment.getProperty("env." + environment.getTestEnvironmentID() + ".url");
    }

    public String getTestEnvironmentURLSecure() {
        return environment.getProperty("env." + environment.getTestEnvironmentID() + ".url.secure");
    }

    /**
     * Gets the browser that is specified to thi specific TSID.
     * @return with the TSID specific browser string.
     */
    public String getTestEnvironmentBrowser() {
        return environment.getProperty("env." + environment.getTestEnvironmentID() + ".browser");
    }

    /**
     * Simple wrapper ofor getting environment properties.
     * @param propertyKey is the key of the property.
     * @return with property value.
     */
    public String getProperty(final String propertyKey) {
        return environment.getProperty(propertyKey);
    }

    /**
     *
     * Simple wrapper ofor getting environment properties.
     * @param propertyKey is the key of the property.
     * @param defaultValue is the default value, if parameter was not found.
     * @return with property value.
     */
    public String getProperty(final String propertyKey, final String defaultValue) {
        return environment.getProperty(propertyKey, defaultValue);
    }
}
