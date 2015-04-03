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
 * This test is prepared to demonstrate dependency handling between test cases.
 * Not yet finalized.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-3", name = "Dependency Sample (b)")
public class DependencyBTest extends OtherTestCase {

    public void testNeedToPass() {
        logComment("This is empty so must pass...");
    }

    public void testNeedToFail() {
        logComment("Test: failed test case");
        fail("Forced TC failure.");
    }

    public void testNeedToNA() {
        naTestCase("test N/A purpose");
    }

    public void testNeedToWaitALotThenPass() throws InterruptedException {
        Thread.sleep(10000);
    }

}
