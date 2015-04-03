package com.epam.gepard.gherkin.jbehave.helper;

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

import java.io.InputStream;

/**
 * Utility class for returning resources from classpath.
 * @author Adam_Csaba_Kiraly
 */
public class ResourceProvider {

    /**
     * Returns the resource with the given path from the classpath.
     * @param path the given path of the resource
     * @return an {@link InputStream} of the resource, or null if not found
     */
    public InputStream getResourceAsStream(final String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

}
