package com.epam.gepard.inspector;
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

/**
 * Test factory interface for escalated test step element creation.
 */
public interface TestFactory {

    /**
     * Creates a new TestCase with the given name in the given parent TestCaseSet object.
     * @param name is the name of the test class.
     * @param testCaseSet is the test class object.
     * @return the new TestCase object
     */
    TestCase createTestCase(String name, TestCaseSet testCaseSet);

    /**
     * Creates a new TestCaseSet object with the given name in the given parent TestScript object.
     * @param name is the name of the test class.
     * @param testScript is the test script object.
     * @return the new TestCaseSet
     */
    TestCaseSet createTestCaseSet(String name, TestScript testScript);

    /**
     * Creates a new TestScript object with the given name.
     * @param name is the name.
     * @return the new TestScript
     */
    TestScript createTestScript(String name);

}
