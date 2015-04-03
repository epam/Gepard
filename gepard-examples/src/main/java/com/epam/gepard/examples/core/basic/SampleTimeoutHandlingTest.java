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

import java.util.Date;

/**
 * This test shows different timeout error handling mechanisms within Gepard.
 *
 * @author tkohegyi
 */
@TestClass(id = "DEMO-1", name = "Timeout Handling, Sample")
public class SampleTimeoutHandlingTest extends OtherTestCase {

    public void testSimpleTimeoutTest() throws InterruptedException {
        logStep("Test the build in timeout. In testprj.properties, the timeout was set to 2 secs for this class.");
        Thread.sleep(30000); //30 sec
        fail("THIS IS BAD, TIMEOUT HAS ELAPSED, STILL WE ARE LIVING!");
    }

    public void testDoNotLetTimeoutTest() throws InterruptedException {
        logStep("Test the build in timeout. In testprj.properties, the timeout was set to 2 secs for this class, but we keep this thread alive.");
        logComment("So this test must pass");
        for (int i = 0; i < 5; i++) {
            logStep("We may sleep, TC still not fail, count: " + i);
            Thread.sleep(1000); //1 sec, but 5 times, it is far enough to test its activity reliability
        }
        logComment("We WON!");
    }

    public void testLetTimeoutTest() throws InterruptedException {
        logStep("Test the build in timeout. In testprj.properties, the timeout was set to 2 secs for this class, but we keep this thread alive.");
        logComment("So this test must pass");
        for (int i = 0; i < 5; i++) {
            logStep("We may sleep, TC still not fail, count: " + i);
            Thread.sleep(1000); //1 sec, but 20 time, it is far enough to test its activity reliability
        }
        logComment("We WON!");
        logStep("But now, we continue and let this TC fail with timeout.");
        Thread.sleep(10000); //10 sec
        fail("THIS IS BAD, TIMEOUT HAS ELAPSED, STILL WE ARE LIVING!");
    }

    public void testTimeoutWithoutInterruptPossibilityTest() {
        logStep("Test the build in timeout. In testprj.properties, the timeout was set to 2 secs for this class.");
        logComment("But now we do not let this test interrupted");
        Date start = new Date();
        Date end = new Date(start.getTime() + 10000);
        while (true) { //10 sec activity without interrupt possibility
            Date now = new Date();
            if (now.getTime() > end.getTime()) {
                break;
            }
        }
        fail("THIS IS BAD, TIMEOUT HAS ELAPSED, STILL WE ARE LIVING!");
    }

}
