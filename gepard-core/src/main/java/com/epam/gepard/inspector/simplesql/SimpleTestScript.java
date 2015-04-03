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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.Constants;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestScript;

/**
 * SimpleTestScript object which creates a test script in the database.
 */
public class SimpleTestScript implements TestScript {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestScript.class);
    private final String name;
    private final List<TestCaseSet> children = new CopyOnWriteArrayList<>();
    private int status = Constants.FAILED;
    private int tsId;

    /**
     * Construct a new SimpleTestScript.
     * @param name is the name of the Test Script.
     */
    protected SimpleTestScript(final String name) {
        this.name = name;
        try {
            Connection con = SimpleFactory.getConnection();
            PreparedStatement pStat = con
                    .prepareStatement("INSERT INTO Inspector.TestScript VALUES(UNIQUEKEY('Inspector.TestScript'), null, ?, ?, DATEOB())");
            pStat.setString(1, "'" + name + "'");
            pStat.setInt(2, status);
            LOGGER.debug(String.valueOf(pStat));
            pStat.execute();
            LOGGER.debug("pStat: " + pStat);
            pStat = con.prepareStatement("SELECT TSID FROM Inspector.TestScript WHERE name = ?;");
            pStat.setString(1, "'" + name + "'");
            LOGGER.debug(String.valueOf(pStat));
            ResultSet rs = pStat.executeQuery();
            while (rs.next()) {
                tsId = rs.getInt(1);
            }
            pStat.close();
            con.close();
            LOGGER.info("New TestScript created with name: " + name);
        } catch (IOException ex) {
            LOGGER.error("IOException ", ex);
        } catch (SQLException ex) {
            LOGGER.error("SQLException ", ex);
        }
    }

    /**
     * Updates the status of the test case.
     * @return retval
     */
    @Override
    public boolean updateStatus() {
        boolean retval;
        status = Constants.SUCCEEDED;
        for (Object aChildren : children) {
            if (status == ((TestCaseSet) aChildren).getStatus() && status == Constants.SUCCEEDED) {
                status = Constants.SUCCEEDED;
            } else {
                status = Constants.FAILED;
            }

        }
        try {
            Connection con = SimpleFactory.getConnection();
            PreparedStatement pStat = con.prepareStatement("UPDATE Inspector.TestScript SET status = ? WHERE TSID = ?;");
            pStat.setInt(1, status);
            pStat.setInt(2, tsId);
            LOGGER.debug(String.valueOf(pStat));
            pStat.executeUpdate();
            pStat.close();
            con.close();
            LOGGER.info("Status of TestScript " + name + " has updated.");
            retval = true;
        } catch (SQLException ex) {
            LOGGER.error("SQLException ", ex);
            retval = false;
        } catch (IOException ex) {
            LOGGER.error("IOException ", ex);
            retval = false;
        }
        return retval;
    }

    /**
     * Returns with the name of the test script.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a child TestCaseSet to this TestScript if there is no child object with such name.
     * @param child is the child Test Case Set.
     * @return retval
     */
    protected boolean add(final SimpleTestCaseSet child) {
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
