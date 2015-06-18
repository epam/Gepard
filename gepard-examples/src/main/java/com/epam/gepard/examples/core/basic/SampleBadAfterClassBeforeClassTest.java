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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This TC is to test behavior of Gepard.
 * This test should fail as we don't use static methods for @BeforeClass and @AfterClass methods.
 *
 * @author tkohegyi
 */

@TestClass(id = "DEMO-6", name = "Basic AfterClass/BeforeClass Test Sample - incorrect BeforeClass/AfterClass methods")
public class SampleBadAfterClassBeforeClassTest implements GepardTestClass {

    @BeforeClass
    public void beforeClass() {
        logComment(this, "This Test should fail, as the method annotated with @BeforeClass is not static.");
    }

    @AfterClass
    public void afterClass() {
        logComment(this, "This Test should fail, as the method annotated with @BeforeClass is not static.");
    }

    @Test
    public void testMustPass() {
        naTestCase(this, "Something is wrong if you can see this in the log...");
    }

}
