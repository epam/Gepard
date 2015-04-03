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

import com.epam.gepard.inspector.TestCase;
import com.epam.gepard.inspector.TestCaseSet;
import com.epam.gepard.inspector.TestFactory;
import com.epam.gepard.inspector.TestScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Logging is targeted to SQL - support class.
 */
public class SimpleFactory implements TestFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFactory.class);

    /**
     * The constructor configures the log4j property file for the logging.
     */
    public SimpleFactory() {
    }

    /**
     * Creates a new TestCase with a given name and parent TestCaseSet.
     *
     * @param name is the name of the test Case.
     * @param testCaseSet is the parent Test Case Set.
     * @return TestCase
     */

    @Override
    public TestCase createTestCase(String name, TestCaseSet testCaseSet) {
        try {
            return testCaseSet == null ? null : new SimpleTestCase(name, testCaseSet);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Creates a new TestCaseSet with name and parent TestScript.
     *
     * @param name is the name of the Test Case Set.
     * @param testScript is the parent Test Script.
     * @return TestCaseSet
     */

    @Override
    public TestCaseSet createTestCaseSet(String name, TestScript testScript) {
        try {
            return testScript == null ? null : new SimpleTestCaseSet(name, testScript);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Creates a new TestScript with name.
     *
     * @param name is the name of the Test Script.
     * @return TestScript
     */
    @Override
    public TestScript createTestScript(String name) {
        return new SimpleTestScript(name);
    }

    /**
     * Creates the connection to the existing database.
     *
     * @return DriverManager.getConnection(url, usr, pwd)
     * @throws SQLException in case of sql error.
     * @throws IOException in case of IO error.
     */
    public static Connection getConnection() throws SQLException, IOException {
        Properties props = new Properties();
        LOGGER.debug("Opening 'database.properties' file for reading.");
        FileInputStream fis = new FileInputStream("database.properties");
        props.load(fis);
        fis.close();
        LOGGER.debug("Reading in the properties.");
        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        }
        String url = props.getProperty("jdbc.url");
        String usr = props.getProperty("jdbc.username");
        String pwd = props.getProperty("jdbc.password");
        LOGGER.debug("Done.");

        return DriverManager.getConnection(url, usr, pwd);
    }

}
