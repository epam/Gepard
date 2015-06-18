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

import com.epam.gepard.AllTestRunner;
import com.epam.gepard.annotations.TestClass;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.util.Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * This sample test class shows basic functions. Like using:
 * - logComment, logStep as logger methods
 * - naTestCase - to set a test case to N/A (not applicable)
 * - how to fail a test case by using fail method
 * - how to set a test case as "dummy" - as it is under construction
 * - how to set AUT - Application Under Test Version value
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-3", name = "Basic Results, Sample")
public class SampleOtherTest implements GepardTestClass {

    @Test
    public void testTestMustPass() {
        logComment("This is empty so must pass...");
    }

    @Test
    public void annotatedTestWithTwoStepsPassed() {
        logStep("Step 1");
        logStep("Step 2");
        logComment("And so on...");
    }

    @Test
    public void testFailedTest() {
        logComment("Test: failed test case");
        Assert.fail("Forced TC failure.");
    }

    @Test
    public void testNotApplicableTest() {
        logStep("Test: N/A test case");
        naTestCase("test N/A purpose");
    }

    @Test
    public void testTestCaseIsUnderConstructionPassed() {
        logStep("Test: Dummy Test Case, passed result");
        dummyTestCase();
    }

    @Test
    public void testTestCaseIsUnderConstructionFailed() {
        logStep("Test: Dummy Test Case, failed result");
        dummyTestCase();
        Assert.fail("Ups.");
    }

    @Test
    public void testTestCaseIsUnderConstructionNotApplicable() {
        logStep("Test: Dummy Test Case, N/A");
        dummyTestCase();
        naTestCase("To test dummy and N/A pair");
    }

    @Test
    public void testSetSystemUnderTestVersion() {
        logComment("This is a sample on how to set the SUT (System Under Test) value, that is visible in the test report.");
        Util util = new Util();
        AllTestRunner.setSystemUnderTestVersion(util.getGepardVersion());
    }

}
