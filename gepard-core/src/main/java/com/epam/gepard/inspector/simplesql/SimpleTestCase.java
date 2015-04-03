package com.epam.gepard.inspector.simplesql;

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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestCaseStep;

/**
 * Creates a new test case in the database.
 */

public class SimpleTestCase implements TestCase {

    private static final String ID_NAME = "TCID";
    private static final String PARENT_ID_NAME = "TCSID";
    private static final String DATABASE_NAME = "Inspector.TestCase";
    private static final String PARENT_DATABASE_NAME = "Inspector.TestCaseSet";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);
    private static final int HASH_MULTIPLIER = 31;
    private static final int INITIAL_HASH_VALUE = 1;
    private final String name;
    private int status = Constants.FAILED;
    private final List<TestCaseStep> children = new ArrayList<>();
    private final int tcId;

    /**
     * The constructor creates a test case in the database.
     *
     * @param name   name of the test case.
     * @param parent class of the test case.
     * @throws Exception if the parent contains a TestCase with this name.
     */

    protected SimpleTestCase(final String name, final TestCaseSet parent) throws Exception {
        this.name = name;
        if (!((SimpleTestCaseSet) parent).add(this)) {
            throw new Exception("Couldn't define " + name);
        }
        String parentName = ((SimpleTestCaseSet) parent).getName();
        SimpleTestIdCreator simpleTestCaseIdCreator = new SimpleTestIdCreator(PARENT_DATABASE_NAME, DATABASE_NAME, PARENT_ID_NAME, ID_NAME);
        tcId = simpleTestCaseIdCreator.createTestId(parentName, name, status);
    }

    /**
     * Updates the status of the test case.
     *
     * @return boolean
     */

    @Override
    public boolean updateStatus() {
        boolean retval;
        status = Constants.SUCCEEDED;
        for (TestCaseStep aChildren : children) {
            if (status == aChildren.getStatus() && status == Constants.SUCCEEDED) {
                status = Constants.SUCCEEDED;
            } else {
                status = Constants.FAILED;
            }
        }
        try {
            updateStatusInDatabase();
            retval = true;
        } catch (SQLException ex) {
            LOGGER.debug("SQLException: ", ex);
            retval = false;
        } catch (IOException ex) {
            LOGGER.debug("IOException: ", ex);
            retval = false;
        }
        return retval;
    }

    private void updateStatusInDatabase() throws SQLException, IOException {
        Connection con = SimpleFactory.getConnection();
        PreparedStatement pStat = con.prepareStatement("UPDATE ? SET status = ? WHERE ? = ?;");
        int paramCounter = 1;
        pStat.setString(paramCounter++, DATABASE_NAME);
        pStat.setInt(paramCounter++, status);
        pStat.setString(paramCounter++, ID_NAME);
        pStat.setInt(paramCounter++, tcId);
        LOGGER.debug(String.valueOf(pStat));
        pStat.executeUpdate();
        pStat.close();
        con.close();
        LOGGER.info("Status of TestCaseSet " + name + " has updated.");
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

    /**
     * Compares the object if that is an instance of SimpleTestCase.
     * @param obj is the object to compare.
     * @return retval
     */

    public int compareTo(final Object obj) {
        int retval = -1;
        if (obj instanceof SimpleTestCase) {
            retval = name.compareTo(((SimpleTestCase) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object.
     *
     * @return boolean
     */

    @Override
    public boolean equals(final Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(INITIAL_HASH_VALUE, HASH_MULTIPLIER).append(name).append(children).append(status).append(tcId).toHashCode();
    }

    /**
     * Returns the name of the test case.
     *
     * @return name
     */

    public String getName() {
        return name;
    }

    /**
     * Adds a child TestCaseStep to this TestCase.
     * @param child  to add Test Case Step as a child of the Test Case.
     * @return boolean
     */

    public boolean add(final SimpleTestCaseStep child) {
        boolean retval = false;
        if (!children.contains(child)) {
            children.add(child);
            retval = true;
        }
        return retval;
    }

}
