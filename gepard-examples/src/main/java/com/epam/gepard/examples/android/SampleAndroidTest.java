package com.epam.gepard.examples.android;
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
import org.junit.Test;

/**
 * This test class demonstrates the capabilities of Android extension of Gepard.
 * Or at least - will demonstrate it.
 *
 * @author tkohegyi
 */
@TestClass(id = "ANDROID", name = "Sample Android Test")
public class SampleAndroidTest implements GepardTestClass {

    @Test
    public void testTestMustPass() throws Exception {
        dummyTestCase(); //
        naTestCase("This Gepard feature is not-yet implemented");
    }

}
