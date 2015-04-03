package com.epam.gepard.common.threads;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.common.GepardConstants;
import com.epam.gepard.common.threads.handler.TimeoutHandler;

/**
 * This thread is used to handle class level timeouts, by counting down till timeout occurs.
 * Any little event on test executor thread should reset the timer.
 * If timer count down reaches the predefined level, then the test is considered as "dead", and Gepard tries to stop it.
 */

public class TimeoutHandlerThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutHandlerThread.class);
    private TimeoutHandler handler;

    /**
     * Constructor of the thread.
     * @param handler should be the AllTestRunner (main Gepard) class.
     */
    public TimeoutHandlerThread(final TimeoutHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void run() {
        String me = this.getName();
        //forever loop we have
        while (true) {
            //handling timeout
            handler.timeoutHandler();
            //finally, wait for a sec for the next timeout iteration
            try {
                sleep(GepardConstants.ONE_SECOND_LENGTH.getConstant()); //sleep for a sec, then restart the loop
            } catch (InterruptedException e) {
                //this was not expected, but if happens, then time to exit
                LOGGER.debug("Thread: " + me + " is exiting, as got InterruptedException!");
                return;
            }
        }
    }

}
