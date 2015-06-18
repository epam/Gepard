package com.epam.gepard.examples.core.basic;
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
import org.junit.Assert;
import org.junit.Test;

/**
 * Just to show how Gepard runs tests in parallel.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-5", name = "Basic Parallel Test - C")
public class SampleParallelTestC implements GepardTestClass {

    /**
     * This test actually should always pass.
     */
    @Test
    public void testMustPass() {
        logComment("This is empty so must pass...");
    }

    /**
     * This test actually should always fail.
     */
    @Test
    public void testFailedTest() {
        logComment("Test: failed test case");
        Assert.fail("Forced TC failure.");
    }

}
