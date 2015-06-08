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

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper for org.apache.commons.io.IOUtils.
 * @author Adam_Csaba_Kiraly
 */
public class IOUtils {

    /**
     * Get the contents of an InputStream as a String using the default character encoding of the platform.
     * This method buffers the input internally, so there is no need to use a BufferedInputStream.
     * @param inputStream the InputStream to read from
     * @return the contents of the inputStream
     * @throws IOException - if an I/O error occurs
     */
    public String toString(final InputStream inputStream) throws IOException {
        return org.apache.commons.io.IOUtils.toString(inputStream);
    }
}
