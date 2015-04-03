package com.epam.gepard.examples.core.datadriven;
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

import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.generic.OtherTestCase;

/**
 * This test shows the Gepard behavior when no feeder exists, but a test would like to use it.
 *
 * @author tkohegyi
 */
@TestClass(id = "EX.DATA", name = "DataFeeder, Exceptional cases")
public class DataFeederIsMissingTest extends OtherTestCase {

    public void testTestMustPass() {
        logComment("This is empty so must pass...");
    }

    public void testTestUseDataSoShouldThrowException() {
        getTestParameters();
    }

}
