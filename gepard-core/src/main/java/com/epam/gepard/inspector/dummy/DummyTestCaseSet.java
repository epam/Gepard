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

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.Statusable;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestScript;

/**
 * DummyTestCaseSet object which redirects its reports into a log file.
 */

public class DummyTestCaseSet implements TestCaseSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyTestCaseSet.class);
    private static final int INITIAL_HASH_VALUE = 1;
    private static final int HASH_MULTIPLIER = 31;
    private final String name;
    private int status = Constants.FAILED;
    private final DummyChildren dummyChildren;

    /**
     * Construct a new DummyTestCaseSet.
     *
     * @param name   of the new TestCaseSet
     * @param parent TestScript
     * @throws Exception if the parent contains a TestCaseSet with this name
     */
    protected DummyTestCaseSet(final String name, final TestScript parent) throws Exception {
        this.name = name;
        this.dummyChildren = new DummyChildren(new CopyOnWriteArrayList<Statusable>());
        if (!((DummyTestScript) parent).add(this)) {
            throw new Exception("Couldn't define TestCaseSet: " + name);
        }
        LOGGER.debug(name + " created");
    }

    /**
     * Updates the status of the test case set.
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
     * Returns the name of the TestCaseSet.
     *
     * @return name
     */

    public String getName() {
        return name;
    }

    /**
     * Compares the name of specified object, if it is an instance of DummyTestCaseSet.
     *
     * @param obj the object which have to compare to
     * @return result of comparison
     */
    public int compareTo(final Object obj) {
        int retval = -1;
        if (obj instanceof DummyTestCaseSet) {
            retval = name.compareTo(((DummyTestCaseSet) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object. It is based on the comparison method.
     *
     * @param obj the object which have to compare to
     * @return true if equals the name of the object to the name of this TestCaseSet
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
     * Adds a child TestCase to this TestCaseSet if there is no child object with such name.
     *
     * @param child TestCase to add
     * @return true if the child has added otherwise it returns false
     */
    boolean add(final DummyTestCase child) {
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
