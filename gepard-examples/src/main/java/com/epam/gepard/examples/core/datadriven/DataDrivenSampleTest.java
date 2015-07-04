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
import com.epam.gepard.generic.GepardTestClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * The presented approach below shown the lowest level access to data parameters.
 * Usually you don't need to use this approach.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-11", name = "Data Driven Test Sample")
public class DataDrivenSampleTest implements GepardTestClass {

    @Test
    public void testDoNotUseData() {
        logComment("This TC does not use data at all.");
    }

    @Test
    public void testDoUseData() {
        String[] parameters = getTestClassExecutionData().getDrivenData().getParameters();
        logComment("First check if we have data available as we expect...");
        assertTrue("Test is missing parameters!", parameters != null); //we need parameters
        assertTrue("Test is missing correct number of parameters!", parameters.length >= 3); //we need 2 parameters for this TC, so check it now
        logComment("Ok. We have at least 3 params.");
        logStep("Test if we can use the params:");
        logEvent("Param 1:" + parameters[0] + ", Param 2:" + parameters[1] + ", Param 3:" + parameters[2]);
        logStep("Test fails, if Param 2 is 'WILLFAIL'");
        assertTrue("This need to be failed.", parameters[2].compareTo("WILLFAIL") != 0);
    }

}
