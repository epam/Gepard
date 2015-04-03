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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestScript;

/**
 * DummyTestScript object which redirects its reports into a LOGGER file.
 */
public class DummyTestScript implements TestScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyTestScript.class);
    private final String name;
    private final List<TestCaseSet> children = new CopyOnWriteArrayList<>();
    private int status = Constants.FAILED;

    /**
     * Construct a new DummyTestScript.
     *
     * @param name name of the new TestScript
     */
    protected DummyTestScript(final String name) {
        this.name = name;
        LOGGER.debug(name + " created");
    }

    /**
     * Updates the status of the test case.
     *
     * @return retval
     */
    @Override
    public boolean updateStatus() {
        if (!children.isEmpty()) {
            status = Constants.SUCCEEDED;
        }
        for (TestCaseSet aChildren : children) {
            if (status == aChildren.getStatus() && status == Constants.SUCCEEDED) {
                status = Constants.SUCCEEDED;
            } else {
                status = Constants.FAILED;
            }
        }
        return true;
    }

    /**
     * Returns the name of current TestScript.
     *
     * @return name of current TestScript
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a child TestCaseSet to this TestScript if there is no child object with such name.
     *
     * @param child TestCaseSet to add
     * @return true if the child has added otherwise it returns false
     */
    protected boolean add(final DummyTestCaseSet child) {
        boolean retval = false;
        if (!children.contains(child)) {
            retval = children.add(child);
        }
        return retval;
    }

    /**
     * Gets the status of the test case step.
     *
     * @return status
     */
    @Override
    public int getStatus() {
        return status;
    }

}
