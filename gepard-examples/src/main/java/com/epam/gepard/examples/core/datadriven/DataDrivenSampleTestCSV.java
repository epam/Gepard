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
 * Sample data driven test with data in CSV.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-2", name = "Data Driven Test Sample - CVS")
public class DataDrivenSampleTestCSV extends OtherTestCase {

    private final int threeTestDataIsExpected = 3;
    private final int fourthRow = 4;
    /**
     * This test method do not rely on any test data,
     * but the result depends on the which row is actually called.
     */
    public void testDoNotUseData() {
        logComment("This TC does not use data at all.");
        if (getClassData().getDrivenDataRowNo() == fourthRow) {  //in case we are at row 4 (that is the 5th execution), set this TC as N/A
            naTestCase("To test this as well");
        }
    }

    /**
     * This test shows a special way of accessing the test data.
     */
    public void testDoUseData() {
        String[] parameters = getClassData().getDrivenData().getParameters();
        logComment("First check if we have data available as we expect...");
        assertTrue("Test is missing parameters!", parameters != null); //we need parameters
        assertTrue("Test is missing correct number of parameters!", parameters.length == threeTestDataIsExpected); //we need 2 parameters for this TC, so check it now
        logComment("Ok. We have exactly 3 params.");
        logStep("Test if we can use the params:");
        logEvent("Param 1:" + parameters[0] + ", Param 2:" + parameters[1] + ", Param 3:" + parameters[2]);
        logStep("Test fails, if Param 2 is 'WILLFAIL'");
        if (getClassData().getDrivenDataRowNo() == 2) {
            dummyTestCase(); //in case we are at row 2 (that is the 3th execution), set this TC as Dummy one
        }
        assertTrue("This need to be failed.", parameters[2].compareTo("WILLFAIL") != 0); //fail the TC if the specific parameter has special value
    }

}
