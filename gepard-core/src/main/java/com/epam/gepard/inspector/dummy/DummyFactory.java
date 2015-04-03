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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestFactory;
import com.epam.gepard.inspector.TestScript;

/**
 * This is a basic logger which redirects the log output into a file.
 * You can configure the logging through the log4j.properties file which contains
 * properties in standard case but you can add more parameters by the rules
 * of the Log4J usage.
 */

public class DummyFactory implements TestFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DummyFactory.class);
    private int casesetcounter;
    private int casecounter;
    private int scriptcounter;

    /**
     * The constructor initialize the logger properties.
     */

    public DummyFactory() {
    }

    /**
     * Creates a new TestCase with a given name and parent TestCaseSet.
     */
    @Override
    public TestCase createTestCase(final String name, final TestCaseSet testCaseSet) {
        try {
            return testCaseSet == null ? null : new DummyTestCase(casecounter++ + ". " + name, testCaseSet);
        } catch (Exception e) {
            LOG.error("createTestCase Exception", e);
            return null;
        }
    }

    /**
     * Creates a new TestCaseSet with name and parent TestScript.
     * @return TestCaseSet
     */
    @Override
    public TestCaseSet createTestCaseSet(final String name, final TestScript testScript) {
        try {
            casecounter = 0;
            return testScript == null ? null : new DummyTestCaseSet(casesetcounter++ + ". " + name, testScript);
        } catch (Exception e) {
            LOG.error("createTestCaseSet Exception", e);
            return null;
        }
    }

    /**
     * Creates a new TestScript with name.
     * @return TestScript
     */
    @Override
    public TestScript createTestScript(final String name) {
        casesetcounter = 0;
        return new DummyTestScript(scriptcounter++ + ". " + name);
    }

}
