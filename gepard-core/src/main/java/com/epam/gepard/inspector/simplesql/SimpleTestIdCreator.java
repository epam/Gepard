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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used for creating simple sql test ids.
 * @author Adam_Csaba_Kiraly
 */
public class SimpleTestIdCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestIdCreator.class);
    private final String parentDatabaseName;
    private final String databaseName;
    private final String parentId;
    private final String id;

    /**
     * Constructs a new instance of {@link SimpleTestIdCreator}.
     * @param parentDatabaseName the name of the parent database
     * @param databaseName the name of the database
     * @param parentId the column name of the parent's id in the database
     * @param id the column name of the id in the database
     */
    public SimpleTestIdCreator(final String parentDatabaseName, final String databaseName, final String parentId, final String id) {
        super();
        this.parentDatabaseName = parentDatabaseName;
        this.databaseName = databaseName;
        this.parentId = parentId;
        this.id = id;
    }

    /**
     * Creates the test id for the simple sql tests.
     * @param parentName the name of the parent test
     * @param name the name of the test
     * @param status the status of the test
     * @return the test case id or zero if an error occurs
     */
    public int createTestId(final String parentName, final String name, final int status) {
        int result = 0;
        try {
            Connection con = SimpleFactory.getConnection();
            PreparedStatement pStat = createSelectParentIdStatement(name, con);
            int parentID = selectParentID(parentName, pStat);
            pStat = createInsertValuesStatement(name, status, con, parentID);
            insertValues(pStat);
            pStat = createSelectIdStatement(parentName, con);
            ResultSet rs = selectId(pStat);
            while (rs.next()) {
                result = rs.getInt(1);
            }
            pStat.close();
            con.close();
            LOGGER.info("New TestCase created with name: " + name);
        } catch (SQLException ex) {
            LOGGER.debug("SQLException: ", ex);
        } catch (IOException ex) {
            LOGGER.debug("IOException: ", ex);
        }
        return result;
    }

    private ResultSet selectId(final PreparedStatement pStat) throws SQLException {
        LOGGER.debug(String.valueOf(pStat));
        return pStat.executeQuery();
    }

    private PreparedStatement createSelectIdStatement(final String name, final Connection con) throws SQLException {
        PreparedStatement pStat = con.prepareStatement("SELECT ? FROM ? WHERE name = ?;");
        int paramCounter = 1;
        pStat.setString(paramCounter++, id);
        pStat.setString(paramCounter++, databaseName);
        pStat.setString(paramCounter++, "'" + name + "'");
        return pStat;
    }

    private void insertValues(final PreparedStatement pStat) throws SQLException {
        LOGGER.debug(String.valueOf(pStat));
        pStat.execute();
    }

    private PreparedStatement createInsertValuesStatement(final String name, final int status, final Connection con, final int parentID)
        throws SQLException {
        PreparedStatement pStat;
        pStat = con.prepareStatement("INSERT INTO ? VALUES(UNIQUEKEY('?'), ?, ?, ?);");
        int paramCounter = 1;
        pStat.setString(paramCounter, databaseName);
        paramCounter++;
        pStat.setString(paramCounter, databaseName);
        paramCounter++;
        pStat.setInt(paramCounter, parentID);
        paramCounter++;
        pStat.setString(paramCounter, "'" + name + "'");
        paramCounter++;
        pStat.setInt(paramCounter, status);
        return pStat;
    }

    private int selectParentID(final String parentName, final PreparedStatement pStat) throws SQLException {
        LOGGER.debug(String.valueOf(pStat));
        ResultSet parentNameRs = pStat.executeQuery();
        parentNameRs.next();
        return parentNameRs.getInt(1);
    }

    private PreparedStatement createSelectParentIdStatement(final String parentName, final Connection con) throws SQLException {
        PreparedStatement pStat = con.prepareStatement("SELECT ? FROM ? WHERE name = ?;");
        int paramCounter = 1;
        pStat.setString(paramCounter++, parentId);
        pStat.setString(paramCounter++, parentDatabaseName);
        pStat.setString(paramCounter++, "'" + parentName + "'");
        return pStat;
    }

}
