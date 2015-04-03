package com.epam.gepard.common.threads.handler;

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

import java.util.concurrent.Future;

import com.epam.gepard.AllTestRunner;
import com.epam.gepard.common.TestClassExecutionData;
import com.epam.gepard.common.threads.TestClassExecutionThread;

/**
 * Handler for handling timeout.
 * @author Zsolt Kiss Gere, Laszlo Toth, Tamas Godan, Tamas Kohegyi, Tibor Kovacs
 */
public class TimeoutHandler {

    /**
     * There is a thread that is used to handle class level timeouts, by counting down till timeout occurs.
     * Any little event on test executor thread should reset the timer.
     * If timer count down reaches the predefined level, then the test is considered as "dead", and Gepard tries to stop it.
     */
    public void timeoutHandler() {
        for (TestClassExecutionThread testClassExecutionThread : AllTestRunner.getExecutorThreadManager().getThreads()) {
            if (testClassExecutionThread == null) {
                return; //threads are not-yet created
            }
            if (testClassExecutionThread.isEnabled()) {
                TestClassExecutionData o = testClassExecutionThread.getActiveTest();
                if (o != null) {
                    o.timeoutTick();
                    if (o.getHealth() < -o.getDeathTimeout()) {
                        //ups, timeout at test class executor thread
                        synchronized (o) {
                            Future task = o.getTask();
                            if (task != null) {
                                task.cancel(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
