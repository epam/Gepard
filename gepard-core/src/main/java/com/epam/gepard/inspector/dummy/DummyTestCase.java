package com.epam.gepard.inspector.dummy;

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

import java.util.ArrayList;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.Statusable;
import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseSet;

/**
 * DummyTestCase object which redirects its reports into a log file.
 */
public class DummyTestCase implements TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyTestCase.class);
    private static final int INITIAL_HASH_VALUE = 1;
    private static final int HASH_MULTIPLIER = 31;
    private final String name;
    private int status = Constants.FAILED;
    private final DummyChildren dummyChildren;

    /**
     * Construct a new dummyTestCase.
     *
     * @param name   of the new TestCase
     * @param parent TestCaseSet
     * @throws Exception if the parent contains a TestCase with this name
     */
    DummyTestCase(final String name, final TestCaseSet parent) throws Exception {
        this.name = name;
        this.dummyChildren = new DummyChildren(new ArrayList<Statusable>());
        if (!((DummyTestCaseSet) parent).add(this)) {
            throw new Exception("Couldn't define TestCase: " + name);
        }
        LOGGER.debug(name + " created");
    }

    /**
     * Updates the status of the test case.
     *
     * @return boolean
     */
    @Override
    public boolean updateStatus() {
        status = dummyChildren.determineNewStatus(status);
        LOGGER.debug(name + ".updateStatus() "
                + (status == Constants.SUCCEEDED ? "SUCCEDED" : (status == Constants.FAILED ? "FAILED" : "NOT CHECKED")));
        return true;
    }

    /**
     * Returns the name of TestCase.
     *
     * @return name of current TestCase
     */
    String getName() {
        return name;
    }

    /**
     * Compares the name of specified object, if it is an instance of DummyTestCase.
     *
     * @param obj the object which have to compare to
     * @return result of comparison
     */
    int compareTo(final Object obj) {
        int retval = -1;
        if (obj instanceof DummyTestCase) {
            retval = name.compareTo(((DummyTestCase) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object. It is based on the comparison method.
     *
     * @param obj the object which have to compare to
     * @return true if equals the name of the object to the name of this TestCase
     */
    @Override
    public boolean equals(final Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(INITIAL_HASH_VALUE, HASH_MULTIPLIER).append(name).append(dummyChildren).append(status).toHashCode();
    }

    /**
     * Adds a child TestCaseStep to this TestCase if there is no child object with such name.
     *
     * @param child TestCaseStep
     * @return true if the child has added
     */
    boolean add(final DummyTestCaseStep child) {
        return dummyChildren.add(child);
    }

    /**
     * Returns with the status of the test case.
     *
     * @return status
     */

    @Override
    public int getStatus() {
        return status;
    }

}
