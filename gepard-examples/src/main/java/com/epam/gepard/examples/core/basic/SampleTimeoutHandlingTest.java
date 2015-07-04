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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/**
 * This test shows different timeout error handling mechanisms within Gepard.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-4", name = "Timeout Handling, Sample")
public class SampleTimeoutHandlingTest implements GepardTestClass {

    //CHECKSTYLE.OFF
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);
    //CHECKSTYLE.ON

    @Test(timeout = 2000)
    public void simpleMethodTimeoutTest() throws InterruptedException {
        logStep("Test the built in JUnit timeout for this class. Timeout is 2 sec meanwhile this test runs (or would run) for 4 secs.");
        Thread.sleep(4000); //4 sec
        org.junit.Assert.fail("THIS IS BAD, TIMEOUT HAS ELAPSED, STILL WE ARE LIVING!");
    }

    @Test
    public void simpleClassLevelTimeoutTest() throws InterruptedException {
        logStep("Test the built in class level timeout. Class level Timeout is 5 sec meanwhile this test runs (or would run) for 10 secs.");
        Thread.sleep(10000); //10 sec
        org.junit.Assert.fail("THIS IS BAD, TIMEOUT HAS ELAPSED, STILL WE ARE LIVING!");
    }

}
