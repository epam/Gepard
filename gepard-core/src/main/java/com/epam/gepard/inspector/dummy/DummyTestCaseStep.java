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

import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DummyTestCaseStep object which redirects its reports into a log file.
 */

public class DummyTestCaseStep implements TestCaseStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyTestCaseStep.class);
    private static final int HASH_MULTIPLIER = 31;
    private final String name;
    private final int status;

    /**
     * Construct a new DummyTestCaseStep.
     *
     * @param name   name of the new TestCaseStep
     * @param parent is the parent TC
     * @param status the result of the TestCaseStep
     * @throws Exception if the parent contains a TestCaseStep with this name
     */

    protected DummyTestCaseStep(String name, TestCase parent, int status) throws Exception {
        this.status = status;
        this.name = name;
        if (!((DummyTestCase) parent).add(this)) {
            throw new Exception("Couldn't define TestCaseStep: " + name);
        }
        LOGGER.debug(name);
    }

    /**
     * Adds a description to the test case step.
     *
     * @return boolean
     */
    @Override
    public boolean addDesc(String desc, String ref) {
        LOGGER.debug(name + ".addDesc(" + desc + ", " + ref + ")");
        return true;
    }

    /**
     * Returns the name of current TestCaseStep.
     *
     * @return name of current TestCaseStep
     */

    public String getName() {
        return name;
    }

    /**
     * Compares the name of specified object, if it is an instance of DummyTestCaseStep.
     *
     * @param obj the object which have to compare to
     * @return result of comparison
     */
    public int compareTo(Object obj) {
        int retval = -1;
        if (obj instanceof DummyTestCaseStep) {
            retval = name.compareTo(((DummyTestCaseStep) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object. It is based on the comparison method.
     *
     * @param obj the object which have to compare to
     * @return true if equals the name of the object to the name of this TestCaseStep
     */

    @Override
    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = HASH_MULTIPLIER * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = HASH_MULTIPLIER * hashCode + status;
        return hashCode;
    }

    /**
     * Returns the status of the TestCaseStep.
     *
     * @return status
     */

    @Override
    public int getStatus() {
        return status;
    }

}
