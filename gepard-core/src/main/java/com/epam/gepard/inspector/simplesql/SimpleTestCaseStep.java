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
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseStep;

/**
 * Creates a new test case step in the database.
 */
public class SimpleTestCaseStep implements TestCaseStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCaseStep.class);
    private static final int HASH_MULTIPLIER = 31;
    private final String name;
    private final int status;
    private int tcsId;

    /**
     * Construct a new SimpleTestCaseStep.
     *
     * @param name   name of the test case step.
     * @param parent class of the test case step.
     * @param status not used.
     * @throws Exception in case of problem.
     */

    protected SimpleTestCaseStep(final String name, final TestCase parent, final int status) throws Exception {
        this.name = name;
        this.status = status;
        if (!((SimpleTestCase) parent).add(this)) {
            throw new Exception("Couldn't define " + name);
        }
        String parentName = ((SimpleTestCase) parent).getName();
        try {
            Connection con = SimpleFactory.getConnection();
            PreparedStatement pStat = con.prepareStatement("SELECT TCID FROM Inspector.TestCase WHERE name = ?;");
            pStat.setString(1, "'" + parentName + "'");
            LOGGER.debug(String.valueOf(pStat));
            ResultSet parentNameRs = pStat.executeQuery();
            parentNameRs.next();
            int parentID = parentNameRs.getInt(1);
            pStat = con.prepareStatement("INSERT INTO Inspector.TestCaseStep VALUES(UNIQUEKEY('Inspector.TestCaseStep'), ?, ?, ?, ?);");
            int paramCounter = 1;
            pStat.setInt(paramCounter, parentID);
            paramCounter++;
            pStat.setNull(paramCounter, Types.INTEGER);
            paramCounter++;
            pStat.setString(paramCounter, "'" + name + "'");
            paramCounter++;
            pStat.setInt(paramCounter, status);
            LOGGER.debug(String.valueOf(pStat));
            pStat.execute();
            pStat = con.prepareStatement("SELECT tcsId FROM Inspector.TestCaseStep WHERE name = ?;");
            pStat.setString(1, "'" + name + "'");
            LOGGER.debug(String.valueOf(pStat));
            ResultSet rs = pStat.executeQuery();
            while (rs.next()) {
                tcsId = rs.getInt(1);
            }
            pStat.close();
            con.close();
            LOGGER.info("New TestCaseStep created with name: " + name);
        } catch (SQLException ex) {
            LOGGER.error("SQLException: ", ex);
        } catch (IOException ex) {
            LOGGER.error("IOExcpetion: ", ex);
        }
    }

    /**
     * Adds a description to the test case step.
     *
     * @return boolean
     */

    @Override
    public boolean addDesc(final String desc, final String ref) {
        boolean retval;
        try {
            Connection con = SimpleFactory.getConnection();
            PreparedStatement pStat = con.prepareStatement("INSERT INTO Inspector.Description VALUES(UNIQUEKEY('Inspector.Description'), ?, ?);");
            pStat.setString(1, desc);
            pStat.setString(2, ref);
            LOGGER.debug(String.valueOf(pStat));
            pStat.executeUpdate();
            pStat = con.prepareStatement("SELECT DID FROM Inspector.Description WHERE description = ? AND ref = ?;");
            pStat.setString(1, desc);
            pStat.setString(2, ref);
            LOGGER.debug(String.valueOf(pStat));
            ResultSet didRs = pStat.executeQuery();
            didRs.next();
            int did = didRs.getInt(1);
            pStat = con.prepareStatement("UPDATE Inspector.TestCaseStep SET DID = ? WHERE TCSID = ?;");
            pStat.setInt(1, did);
            pStat.setInt(2, tcsId);
            LOGGER.debug(String.valueOf(pStat));
            pStat.executeUpdate();
            pStat.close();
            con.close();
            LOGGER.info("Description has added to TestCaseStep: " + name);
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
     * Gets the status of the test case step.
     *
     * @return status
     */
    @Override
    public int getStatus() {
        return status;
    }

    /**
     * Returns with the name of the test case step.
     *
     * @return name
     */

    public String getName() {
        return name;
    }

    /**
     * Compares the object if that is an instance of SimpleTestCaseStep.
     *
     * @param obj is the object to compare.
     * @return retval
     */

    public int compareTo(final Object obj) {
        int retval = -1;
        if (obj instanceof SimpleTestCaseStep) {
            retval = name.compareTo(((SimpleTestCaseStep) obj).getName());
        }
        return retval;
    }

    /**
     * Checks the equality of name of the object.
     * @param obj is the object to compare.
     * @return boolean
     */

    @Override
    public boolean equals(final Object obj) {
        return compareTo(obj) == 0;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = HASH_MULTIPLIER * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = HASH_MULTIPLIER * hashCode + status;
        hashCode = HASH_MULTIPLIER * hashCode + tcsId;
        return hashCode;
    }
}
