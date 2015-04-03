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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestScript;

/**
 * Creates a new test case set in the database.
 */
public class SimpleTestCaseSet implements TestCaseSet {

    private static final String ID_NAME = "TCSID";
    private static final String PARENT_ID_NAME = "TSID";
    private static final String PARENT_DATABASE_NAME = "Inspector.TestScript";
    private static final String DATABASE_NAME = "Inspector.TestCaseSet";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCaseSet.class);
    private static final int HASH_MULTIPLIER = 31;
    private static final int INITIAL_HASH_VALUE = 1;
    private final int tcsId;
    private final List<TestCase> children = new CopyOnWriteArrayList<>();
    private final String name;
    private int status = Constants.FAILED;

    /**
     * Construct a new SimpleTestCaseSet.
     * @param name name of the test case set.
     * @param parent class of the test case set.
     *
     * @throws Exception in case of error.
     */

    protected SimpleTestCaseSet(final String name, final TestScript parent) throws Exception {
        this.name = name;
        if (!((SimpleTestScript) parent).add(this)) {
            throw new Exception("Couldn't define TestCaseSet: " + name);
        }
        String parentName = ((SimpleTestScript) parent).getName();
        SimpleTestIdCreator simpleTestCaseSetIdCreator = new SimpleTestIdCreator(PARENT_DATABASE_NAME, DATABASE_NAME, PARENT_ID_NAME, ID_NAME);
        tcsId = simpleTestCaseSetIdCreator.createTestId(parentName, name, status);
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
        for (TestCase aChildren : children) {
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
            LOGGER.error("SQLException: ", ex);
            retval = false;
        } catch (IOException ex) {
            LOGGER.error("IOException: ", ex);
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
        pStat.setInt(paramCounter++, tcsId);
        LOGGER.debug(String.valueOf(pStat));
        pStat.executeUpdate();
        pStat.close();
        con.close();
        LOGGER.info("Status of TestCaseSet " + name + " has updated.");
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
     * Compares the object if that is an instance of SimpleTestCaseSet.
     * @param obj is the object to compare.
     * @return retval
     */

    public int compareTo(final Object obj) {
        int retval = -1;
        if (obj instanceof SimpleTestCaseSet) {
            retval = name.compareTo(((SimpleTestCaseSet) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object.
     *
     * @return booelan
     */

    @Override
    public boolean equals(final Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(INITIAL_HASH_VALUE, HASH_MULTIPLIER).append(name).append(children).append(status).append(tcsId).toHashCode();
    }

    /**
     * Adds a child TestCase to the TestCaseSet.
     * @param child the test case to be added.
     * @return boolean
     */

    public boolean add(final SimpleTestCase child) {
        boolean retval = false;
        if (!children.contains(child)) {
            retval = children.add(child);
        }
        return retval;
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
