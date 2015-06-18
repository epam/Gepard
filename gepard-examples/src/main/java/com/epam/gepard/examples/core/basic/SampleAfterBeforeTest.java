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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This TC is to test behavior of Gepard.
 * In case parameter 0 is true, that cause forced failure at @Before
 * In case parameter 1 is true, that cause forced N/A at @Before
 * In case parameter 3 is true, that cause forced failure at @After
 * In case parameter 4 is true, that cause forced N/A at @after
 * <p/>
 * The trick is that failure or setting N/A during @Before or @After should not influence the result of the test case.
 *
 * @author tkohegyi
 */

@TestClass(id = "DEMO-4", name = "Basic After/Before Test Sample")
public class SampleAfterBeforeTest implements GepardTestClass {

    @Before
    public void before() {
        logComment(this, "We started the Before method.");
        Boolean b;
        b = Boolean.valueOf(getTestClassExecutionData(this).getDrivenData().getParameters()[1]);
        if (b) {
            Assert.fail("forced fail at beforeTestCase");
        }
        b = Boolean.valueOf(getTestClassExecutionData(this).getDrivenData().getParameters()[2]);
        if (b) {
            naTestCase(this, "forced N/A at beforeTestCase");
        }
        logComment(this, "We finished the Before method.");
    }

    @After
    public void after() {
        logComment(this, "We started the After method.");
        Boolean b;
        b = Boolean.valueOf(getTestClassExecutionData(this).getDrivenData().getParameters()[3]);
        if (b) {
            Assert.fail("forced fail at afterTestCase");
        }
        b = Boolean.valueOf(getTestClassExecutionData(this).getDrivenData().getParameters()[4]);
        if (b) {
            naTestCase(this, "forced N/A at afterTestCase");
        }
        logComment(this, "We finished the After method.");
    }

    @Test
    public void testTestMustPass() {
        logComment(this, "Par0:" + getTestClassExecutionData(this).getDrivenData().getParameters()[0]
                + ", Par1:" + getTestClassExecutionData(this).getDrivenData().getParameters()[1]
                + ", Par2:" + getTestClassExecutionData(this).getDrivenData().getParameters()[2]
                + ", Par3:" + getTestClassExecutionData(this).getDrivenData().getParameters()[3]
                + ", Par4:" + getTestClassExecutionData(this).getDrivenData().getParameters()[4]);
    }

    @Test
    public void testTestMustNA() {
        naTestCase(this, "Set to N/A");
    }

    @Test
    public void testTestMustFail() {
        Assert.fail("Set to FAILED");
    }

}
