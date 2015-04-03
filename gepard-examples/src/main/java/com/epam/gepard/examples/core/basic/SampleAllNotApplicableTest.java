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
import com.epam.gepard.generic.OtherTestCase;

/**
 * This tests the different circumstances when a test is Not applicable.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-1", name = "Basic Results, Sample, Every Test is NotApplicable")
public class SampleAllNotApplicableTest extends OtherTestCase {

    public void testTestMustPass() {
        logComment("This is empty so must pass...");
        naTestCase("But it is a forced NA");
    }

    public void testTestPassedWithTwoSteps() {
        logStep("Step 1");
        logStep("Step 2");
        logComment("And so on...");
        naTestCase("But it is a forced NA");
    }

    public void testFailedTest() {
        logComment("Test: failed test case");
        naTestCase("But it is a forced NA");
        fail("Forced TC failure.");
    }

    public void testSimpleNotApplicableTest() {
        logStep("Test: N/A test case");
        naTestCase("test N/A purpose");
    }

    public void testTestCaseIsUnderConstruction() {
        logStep("Test: Dummy Test Case, passed result");
        dummyTestCase();
        naTestCase("But it is a forced NA");
    }

    public void testTestCaseIsUnderConstructionAndNotappplicableAndFailed() {
        logStep("Test: Dummy Test Case, failed result");
        dummyTestCase();
        naTestCase("But it is a forced NA");
        fail("Ups.");
    }

    public void testTestCaseIsUnderConstructionAndNotApplicable() {
        logStep("Test: Dummy Test Case, N/A");
        dummyTestCase();
        naTestCase("To test dummy and N/A pair");
    }

}
