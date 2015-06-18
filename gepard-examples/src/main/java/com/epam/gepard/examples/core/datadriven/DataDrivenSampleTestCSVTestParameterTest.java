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
import com.epam.gepard.annotations.TestParameter;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.TestClassExecutionThread;
import com.epam.gepard.generic.GepardTestClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Sample test on how to use CSV based data driven test.
 * See the preferred and suggested use of data parameters below.
 *
 * @author Tamas_Kohegyi
 */
@TestClass(id = "DEMO-12", name = "Data Driven Test Sample - CSV - Recommended TDD approach")
public class DataDrivenSampleTestCSVTestParameterTest implements GepardTestClass {

    @TestParameter(id = "ID")
    private String tcRun = getDataDrivenTestParameter(0);
    @TestParameter(id = "PART1")
    private String tcText = getDataDrivenTestParameter("PART1");
    @TestParameter(id = "PART2")
    private String tcAssertText = getDataDrivenTestParameter("PART2");

    /**
     * Sample test method, do not use data at all, and always passes.
     */
    @Test
    public void testDoNotUseData() {
        logComment("This TC does not use data at all.");
    }

    /**
     * Sample test method, lists all the available data, and passes only if the 3. parameter is 'WILLFAIL'.
     */
    @Test
    public void testDoUseData() {
        logStep("Test if we can use the params:");
        logEvent("Param 1:" + tcRun + ", Param 2:" + tcText + ", Param 3:" + tcAssertText);
        logStep("Test fails, if Param 3 is 'WILLFAIL'");
        assertTrue("This need to be failed.", tcAssertText.compareTo("WILLFAIL") != 0);
    }

}
