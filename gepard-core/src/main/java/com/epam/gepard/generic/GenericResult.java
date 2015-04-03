package com.epam.gepard.generic;

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

/**
 * This class holds the results of a test.
 *
 * @author dora.gal, Tamas Kohegyi
 */
public class GenericResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericResult.class);
    private final String testID;
    private final String testName;
    private int passed;
    private int failed;
    private int error;
    private int notapplicable;
    private int notcompleted;

    /**
     * Constuctor.
     *
     * @param id   Test ID
     * @param name Test name
     */
    public GenericResult(final String id, final String name) {
        testID = id;
        testName = name;
    }

    /**
     * Increments the number of passed tests by one.
     */
    public void incPassed() {
        LOGGER.debug("incPassed");
        passed++;
    }

    /**
     * Increments the number of failed tests by one.
     */
    public void incFailed() {
        LOGGER.debug("incFailed");
        failed++;
    }

    /**
     * Increments the number of errored tests by one.
     */
    public void incError() {
        LOGGER.debug("incError");
        error++;
    }

    /**
     * Increments the number of N/A tests by one.
     */
    public void incNotApplicable() {
        LOGGER.debug("incNotApplicable");
        notapplicable++;
    }

    /**
     * Increments the number of N/A tests by one.
     */
    public void incNotCompleted() {
        LOGGER.debug("incNotCompleted");
        notcompleted++;
    }

    /**
     * Returns test ID.
     *
     * @return test ID
     */
    public String getID() {
        return testID;
    }

    /**
     * Returns test name.
     *
     * @return test name
     */
    public String getName() {
        return testName;
    }

    /**
     * Returns number of passed tests.
     *
     * @return number of passed tests
     */
    public int getPassed() {
        LOGGER.debug("getPassed");
        return passed;
    }

    /**
     * Returns number of failed tests.
     *
     * @return number of failed tests
     */
    public int getFailed() {
        LOGGER.debug("getFailed");
        return failed;
    }

    /**
     * Returns number of errored tests.
     *
     * @return number of errored tests
     */
    public int getError() {
        LOGGER.debug("getError");
        return error;
    }

    /**
     * Returns number of N/A tests.
     *
     * @return number of N/A tests
     */
    public int getNotApplicable() {
        LOGGER.debug("getNotApplicable");
        return notapplicable;
    }

    /**
     * Returns number of Not Completed tests.
     *
     * @return number of Not Completed tests
     */
    public int getNotCompleted() {
        LOGGER.debug("getNotCompleted");
        return notcompleted;
    }

}
