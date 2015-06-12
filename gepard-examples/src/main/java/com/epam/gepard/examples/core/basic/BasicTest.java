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
@TestClass(id = "DEMO-2", name = "JUnit4, Sample")
public class BasicTest {

    @Test
    public void annotatedJunit4TestThatNeedPass() {
        Assert.assertNotNull(this);
    }

    @Test
    public void annotatedJunit4TestThatNeedFail() {
        Assert.assertNull(this);
    }

}
