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

/**
 * This sample test class shows basic functions. Like using.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-2", name = "Sample, basic test, without any test method")
public class BasicNoTest implements GepardTestClass {

    public void nobodyRecognisesThisAsTestMethod() {
        Assert.assertNotNull(this);
    }

}
