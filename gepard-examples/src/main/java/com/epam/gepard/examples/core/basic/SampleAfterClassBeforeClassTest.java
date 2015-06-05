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
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * This TC is to test behavior of Gepard.
 * In case parameter 0 is true, that cause forced failure at beforetestcase
 * In case parameter 1 is true, that cause forced N/A at beforetestcase
 * In case parameter 3 is true, that cause forced failure at aftertestcase
 * In case parameter 4 is true, that cause forced N/A at aftertestcase
 * <p/>
 * The trick is that failure or setting N/A during beforetestcase or aftertestcase should not influence the result of the test case.
 *
 * @author tkohegyi
 */

@TestClass(id = "DEMO-1", name = "Basic AfterClass/BeforeClass Test Sample")
public class SampleAfterClassBeforeClassTest extends OtherTestCase {

    @BeforeClass
    public void beforeClass() {
        Boolean b;
        b = Boolean.valueOf(getClassData().getDrivenData().getParameters()[0]);
        if (b) {
            fail("forced fail at beforeClass");
        }
        b = Boolean.valueOf(getClassData().getDrivenData().getParameters()[1]);
        if (b) {
            naTestCase("forced N/A at beforeClass");
        }
    }

    @AfterClass
    public void afterClass() {
        Boolean b;
        b = Boolean.valueOf(getClassData().getDrivenData().getParameters()[2]);
        if (b) {
            fail("forced fail at afterCass");
        }
        b = Boolean.valueOf(getClassData().getDrivenData().getParameters()[3]);
        if (b) {
            naTestCase("forced N/A at afterClass");
        }
    }

    public void testMustPass() {
        logComment("Par0:" + getClassData().getDrivenData().getParameters()[0]
                + ", Par1:" + getClassData().getDrivenData().getParameters()[1]
                + ", Par2:" + getClassData().getDrivenData().getParameters()[2]
                + ", Par3:" + getClassData().getDrivenData().getParameters()[3]);
    }

    public void testMustNA() {
        naTestCase("Set to N/A");
    }

    public void testMustFail() {
        fail("Set to FAILED");
    }

}
