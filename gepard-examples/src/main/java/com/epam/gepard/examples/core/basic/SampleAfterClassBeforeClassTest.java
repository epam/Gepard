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
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.TestClassExecutionThread;
import com.epam.gepard.generic.GenericListTestSuite;
import com.epam.gepard.generic.GepardTestClass;
import com.epam.gepard.logger.HtmlRunReporter;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This TC is to test behavior of Gepard.
 * In case parameter 0 is true, that cause forced failure at beforetestcase
 * In case parameter 1 is true, that cause forced N/A at beforetestcase
 * In case parameter 3 is true, that cause forced failure at aftertestcase
 * In case parameter 4 is true, that cause forced N/A at aftertestcase
 * <p>
 * The trick is that failure or setting N/A during beforetestcase or aftertestcase should not influence the result of the test case.
 *
 * @author tkohegyi
 */

@TestClass(id = "DEMO-8", name = "Basic AfterClass/BeforeClass Test Sample")
public class SampleAfterClassBeforeClassTest implements GepardTestClass {

    @BeforeClass
    public static void beforeClass() {
        TestClassExecutionData classData = TestClassExecutionThread.classDataInContext.get();
        HtmlRunReporter reporter = classData.getHtmlRunReporter();
        reporter.beforeClassComment("We started the BeforeClass method.");
        Boolean b;
        b = Boolean.valueOf(classData.getDrivenData().getParameters()[1]);
        if (b) {
            org.junit.Assert.fail("forced fail at beforeClass");
        }
        b = Boolean.valueOf(classData.getDrivenData().getParameters()[2]);
        if (b) {
            reporter.naTestCase("forced N/A at beforeClass");
        }
        reporter.beforeClassComment("We finished the BeforeClass method.");
    }

    @AfterClass
    public static void afterClass() {
        TestClassExecutionData classData = TestClassExecutionThread.classDataInContext.get();
        HtmlRunReporter reporter = classData.getHtmlRunReporter();
        reporter.afterClassComment("We started the AfterClass method.");
        Boolean b;
        b = Boolean.valueOf(classData.getDrivenData().getParameters()[3]);
        if (b) {
            org.junit.Assert.fail("forced fail at AfterClass");
        }
        b = Boolean.valueOf(classData.getDrivenData().getParameters()[4]);
        if (b) {
            reporter.naTestCase("forced N/A at AfterClass");
        }
        reporter.afterClassComment("We finished the AfterClass method.");
    }

    @Test
    public void testMustPass() {
        logComment("Par0:" + getTestClassExecutionData().getDrivenData().getParameters()[0]
                + ", Par1:" + getTestClassExecutionData().getDrivenData().getParameters()[1]
                + ", Par2:" + getTestClassExecutionData().getDrivenData().getParameters()[2]
                + ", Par3:" + getTestClassExecutionData().getDrivenData().getParameters()[3]
                + ", Par4:" + getTestClassExecutionData().getDrivenData().getParameters()[4]);
    }

    @Test
    public void testMustNA() {
        naTestCase("Set to N/A");
    }

    @Test
    public void testMustFail() {
        Assert.fail("Set to FAILED");
    }

}
